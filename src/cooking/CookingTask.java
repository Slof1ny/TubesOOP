package cooking;

import core.player.Player;
import item.Item;
import core.player.Inventory;

public class CookingTask implements Runnable {
    private final Player player;
    private final Item foodToProduce; // Item makanan yang akan dihasilkan
    private final int quantityToProduce; // Jumlah makanan yang akan dihasilkan
    private final Inventory playerInventory; // Referensi langsung ke inventory pemain
    // cookingDurationMillis is no longer strictly needed if using ScheduledExecutorService's delay
    private final long cookingDurationMillis_placeholder;


    public interface CookingCompleteListener {
        void onCookingComplete(Player player, Item itemProduced, int quantity);
        void onCookingFailed(Player player, String recipeName, String reason);
    }
    private CookingCompleteListener listener;


    public CookingTask(Player player, Item foodToProduce, int quantityToProduce, 
                       Inventory playerInventory, long cookingDurationMillis_placeholder, CookingCompleteListener listener) {
        this.player = player;
        this.foodToProduce = foodToProduce;
        this.quantityToProduce = quantityToProduce;
        this.playerInventory = playerInventory;
        this.cookingDurationMillis_placeholder = cookingDurationMillis_placeholder; // Keep for signature, but not used for Thread.sleep
        this.listener = listener;
    }

    @Override
    public void run() {
        String taskName = "CookingTask-" + foodToProduce.getName() + "-for-" + player.getName();
        Thread.currentThread().setName(taskName);
        System.out.println("[" + Thread.currentThread().getName() + "] Processing scheduled cooking completion...");

        try {
            // The delay is handled by ScheduledExecutorService, so no Thread.sleep() needed here.
            // If you still wanted a small internal delay for some reason:
            // if (cookingDurationMillis_placeholder > 0) {
            //     Thread.sleep(cookingDurationMillis_placeholder);
            // }

            // Add item to inventory (synchronized if Inventory is not thread-safe, but usually accessed from game thread)
            // For simplicity here, assuming direct modification is okay as it's called from a single cooking thread.
            // If multiple cooking tasks could run truly concurrently and modify inventory, synchronization would be vital.
            synchronized (playerInventory) { // Good practice if inventory might be accessed elsewhere
                playerInventory.addItem(foodToProduce, quantityToProduce);
            }
            
            System.out.println("[" + Thread.currentThread().getName() + "] Completed. " +
                               foodToProduce.getName() + " x" + quantityToProduce + 
                               " added to inventory for " + player.getName() + ".");
            
            if (listener != null) {
                listener.onCookingComplete(player, foodToProduce, quantityToProduce);
            }

        } catch (Exception e) { // Catch generic exceptions during the task execution
            System.err.println("[" + Thread.currentThread().getName() + "] Failed to complete cooking task: " + e.getMessage());
            e.printStackTrace(); // Log the full stack trace for debugging
             if (listener != null) {
                listener.onCookingFailed(player, foodToProduce.getName(), "Kesalahan sistem saat menyelesaikan masak: " + e.getMessage());
            }
            // No Thread.currentThread().interrupt() unless it was an InterruptedException and re-interrupting is desired.
        }
    }
}