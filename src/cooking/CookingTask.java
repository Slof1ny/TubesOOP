package com.spakborhills.game.tasks;

import com.spakborhills.game.model.Player;
import com.spakborhills.game.model.Item;
import com.spakborhills.game.model.Inventory; // Asumsi kelas Inventory sudah ada

/**
 * Tugas yang berjalan secara asynchronous untuk mensimulasikan proses memasak.
 */
public class CookingTask implements Runnable {
    private final Player player;
    private final Item foodToProduce; // Item makanan yang akan dihasilkan
    private final int quantityToProduce; // Jumlah makanan yang akan dihasilkan
    private final Inventory playerInventory; // Referensi langsung ke inventory pemain
    private final long cookingDurationMillis; // Durasi masak dalam milidetik dunia nyata

    // Callback untuk memberitahu ketika masakan selesai (opsional)
    public interface CookingCompleteListener {
        void onCookingComplete(Player player, Item itemProduced, int quantity);
        void onCookingFailed(Player player, String recipeName, String reason);
    }
    private CookingCompleteListener listener;


    public CookingTask(Player player, Item foodToProduce, int quantityToProduce, 
                       Inventory playerInventory, long cookingDurationMillis, CookingCompleteListener listener) {
        this.player = player;
        this.foodToProduce = foodToProduce;
        this.quantityToProduce = quantityToProduce;
        this.playerInventory = playerInventory;
        this.cookingDurationMillis = cookingDurationMillis;
        this.listener = listener;
    }

    @Override
    public void run() {
        String taskName = "Memasak " + foodToProduce.getName() + " untuk " + player.getName();
        Thread.currentThread().setName(taskName); // Memberi nama thread untuk logging
        System.out.println("[" + Thread.currentThread().getName() + "] Mulai...");

        try {
            Thread.sleep(cookingDurationMillis); // Simulasikan waktu memasak

            // Sinkronisasi diperlukan jika inventory bisa diakses/dimodifikasi oleh thread lain secara bersamaan
            // Jika inventory hanya diubah oleh task ini atau game loop utama secara sekuensial,
            // sinkronisasi mungkin tidak selalu diperlukan, tapi aman untuk menambahkannya.
            synchronized (playerInventory) {
                playerInventory.addItem(foodToProduce, quantityToProduce);
            }
            
            System.out.println("[" + Thread.currentThread().getName() + "] Selesai. " +
                               foodToProduce.getName() + " x" + quantityToProduce + 
                               " telah ditambahkan ke inventory " + player.getName() + ".");
            if (listener != null) {
                listener.onCookingComplete(player, foodToProduce, quantityToProduce);
            }

        } catch (InterruptedException e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Proses memasak terganggu.");
            if (listener != null) {
                listener.onCookingFailed(player, foodToProduce.getName(), "Proses terganggu");
            }
            Thread.currentThread().interrupt(); // Restore interrupted status
        } catch (Exception e) {
            System.err.println("[" + Thread.currentThread().getName() + "] Gagal menyelesaikan proses memasak: " + e.getMessage());
             if (listener != null) {
                listener.onCookingFailed(player, foodToProduce.getName(), "Kesalahan sistem: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }
}
