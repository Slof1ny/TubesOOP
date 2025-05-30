package core.world;

import core.player.Player;
import item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ShippingBin extends DeployedObject{
    private static final int MAX_UNIQUE_SLOTS = 16;
    private Map<Item, Integer> itemsToShip; // Item -> Quantity
    private int uniqueSlotsUsed;

    // THIS IS THE CORRECTED NO-ARGUMENT CONSTRUCTOR
    public ShippingBin() {
        // As ShippingBin extends DeployedObject, its no-arg constructor must call super().
        // We use placeholder values (0,0,0,0,' ') because the actual position
        // is set later when FarmMap calls its other constructor during placement.
        super(0, 0, 0, 0, ' ');
        this.itemsToShip = new HashMap<>();
        this.uniqueSlotsUsed = 0;
    }

    public ShippingBin(int x, int y, int w, int h, char symbol) {
            super(x, y, w, h, symbol);
            this.itemsToShip = new HashMap<>();
            this.uniqueSlotsUsed = 0;
    }

    @Override
    public void interact(Player p, FarmMap map) {
        // This method is required by DeployedObject, but the actual interaction
        // logic is handled by the GUI controller.
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

    public int processSales(Player player, Season currentSeason) { // NEW
        int totalGoldEarnedThisSale = 0; // Renamed to avoid confusion with PlayerStats field
        System.out.println("\n--- Memproses Penjualan Shipping Bin ---");
        if (itemsToShip.isEmpty()) {
            System.out.println("Shipping Bin kosong. Tidak ada yang dijual.");
        } else {
            for (Map.Entry<Item, Integer> entry : itemsToShip.entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();
                int itemSaleValue = item.getSellPrice() * quantity;
                totalGoldEarnedThisSale += itemSaleValue;
                System.out.println("- " + quantity + "x " + item.getName() + " dijual seharga " + itemSaleValue + "g.");
            }
            player.getGold().add(totalGoldEarnedThisSale);
            // Perbarui total pendapatan di PlayerStats
            player.getStats().addGoldEarned(totalGoldEarnedThisSale, currentSeason); // MODIFIED: pass currentSeason
            System.out.println("Total emas yang didapat: " + totalGoldEarnedThisSale + "g.");
        }

        // Kosongkan bin setelah penjualan
        itemsToShip.clear();
        uniqueSlotsUsed = 0;
        System.out.println("--- Penjualan Selesai ---");
        return totalGoldEarnedThisSale;
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