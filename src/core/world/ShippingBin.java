package core.world;

import core.player.Player;
import item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShippingBin {
    private static final int MAX_UNIQUE_SLOTS = 16;
    private Map<Item, Integer> itemsToShip; // Item -> Quantity
    private int uniqueSlotsUsed;

    public ShippingBin() {
        this.itemsToShip = new HashMap<>();
        this.uniqueSlotsUsed = 0;
    }

    public boolean addItem(Player player, Item item, int quantity) {
        if (quantity <= 0) {
            System.out.println("Jumlah item harus positif.");
            return false;
        }

        // Cek apakah item sudah ada di bin atau apakah slot unik penuh
        if (!itemsToShip.containsKey(item) && uniqueSlotsUsed >= MAX_UNIQUE_SLOTS) {
            System.out.println("Shipping Bin penuh. Maksimal " + MAX_UNIQUE_SLOTS + " slot item unik.");
            return false;
        }

        if (!player.getInventory().removeByName(item.getName(), quantity)) {
            System.out.println("Anda tidak memiliki cukup " + item.getName() + " di inventaris.");
            return false;
        }

        if (!itemsToShip.containsKey(item)) {
            uniqueSlotsUsed++;
        }
        itemsToShip.put(item, itemsToShip.getOrDefault(item, 0) + quantity);
        System.out.println(quantity + " " + item.getName() + " ditambahkan ke Shipping Bin.");
        return true;
    }

    public int processSales(Player player) {
        int totalGoldEarned = 0;
        System.out.println("\n--- Memproses Penjualan Shipping Bin ---");
        if (itemsToShip.isEmpty()) {
            System.out.println("Shipping Bin kosong. Tidak ada yang dijual.");
        } else {
            for (Map.Entry<Item, Integer> entry : itemsToShip.entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();
                int itemSaleValue = item.getSellPrice() * quantity;
                totalGoldEarned += itemSaleValue;
                System.out.println("- " + quantity + "x " + item.getName() + " dijual seharga " + itemSaleValue + "g.");
            }
            player.getGold().add(totalGoldEarned);
            // Perbarui total pendapatan di PlayerStats
            player.getStats().addGoldEarned(totalGoldEarned);
            System.out.println("Total emas yang didapat: " + totalGoldEarned + "g.");
        }

        // Kosongkan bin setelah penjualan
        itemsToShip.clear();
        uniqueSlotsUsed = 0;
        System.out.println("--- Penjualan Selesai ---");
        return totalGoldEarned;
    }


    public void displayContents() {
        System.out.println("\n--- Isi Shipping Bin ---");
        if (itemsToShip.isEmpty()) {
            System.out.println("Bin kosong.");
        } else {
            for (Map.Entry<Item, Integer> entry : itemsToShip.entrySet()) {
                System.out.println("- " + entry.getKey().getName() + " x" + entry.getValue() + " (Harga Jual: " + entry.getKey().getSellPrice() + "g)");
            }
            System.out.println("Slot unik terpakai: " + uniqueSlotsUsed + "/" + MAX_UNIQUE_SLOTS);
        }
        System.out.println("------------------------");
    }

    public int getUniqueSlotsUsed() {
        return uniqueSlotsUsed;
    }


    public boolean hasItemsToShip() {
        return !itemsToShip.isEmpty();
    }
}
