package core.player;

import java.util.HashMap;
import java.util.Map;


public class PlayerStats {
    private Map<String, Integer> itemCount;

    public PlayerStats() {
        itemCount = new HashMap<>();
        initializeAllItems();
    }

    private void initializeAllItems() {
        // Seeds
        String[] seeds = {
            "Parsnip Seeds", "Cauliflower Seeds", "Potato Seeds", "Wheat Seeds",
            "Blueberry Seeds", "Tomato Seeds", "Hot Pepper Seeds", "Melon Seeds",
            "Cranberry Seeds", "Pumpkin Seeds", "Grape Seeds"
        };

        // Crops
        String[] crops = {
            "Parsnip", "Cauliflower", "Potato", "Wheat", "Blueberry", "Tomato",
            "Hot Pepper", "Melon", "Cranberry", "Pumpkin", "Grape"
        };

        // Fish (common, regular, legendary)
        String[] fish = {
            "Bullhead", "Carp", "Chub",
            "Largemouth Bass", "Rainbow Trout", "Sturgeon", "Midnight Carp",
            "Flounder", "Halibut", "Octopus", "Pufferfish", "Sardine", "Super Cucumber",
            "Catfish", "Salmon",
            "Angler", "Crimsonfish", "Glacierfish", "Legend"
        };

        // Food
        String[] food = {
            "Fish n’ Chips", "Baguette", "Sashimi", "Fugu", "Wine",
            "Pumpkin Pie", "Veggie Soup", "Fish Stew", "Spakbor Salad",
            "Fish Sandwich", "The Legends of Spakbor", "Cooked Pig's Head"
        };

        // Equipment
        String[] equipment = {
            "Hoe", "Watering Can", "Pickaxe", "Fishing Rod"
        };

        // Misc
        String[] misc = {
            "Coal", "Firewood"
        };

        // Combine all
        for (String item : combineArrays(seeds, crops, fish, food, equipment, misc)) {
            itemCount.put(item, 0);
        }
    }

    private String[] combineArrays(String[]... arrays) {
        return java.util.Arrays.stream(arrays)
                .flatMap(java.util.Arrays::stream)
                .toArray(String[]::new);
    }

    public void addItem(String itemName, int amount) {
        itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + amount);
    }

    public int getItemCount(String itemName) {
        return itemCount.getOrDefault(itemName, 0);
    }

    public void printStats() {
        for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
            System.out.println("Item: " + entry.getKey() + " | Jumlah: " + entry.getValue());
        }
    }

public class Stats {
    private Map<String, PlayerStats> playerStats;

    public Stats() {
        playerStats = new HashMap<>();
    }

    // Tambah item untuk pemain tertentu
    public void addItemToPlayer(String playerName, String itemName, int amount) {
        playerStats.putIfAbsent(playerName, new PlayerStats());
        playerStats.get(playerName).addItem(itemName, amount);
    }

    // Dapatkan jumlah item dari pemain tertentu
    public int getPlayerItemCount(String playerName, String itemName) {
        PlayerStats stats = playerStats.get(playerName);
        if (stats == null) return 0;
        return stats.getItemCount(itemName);
    }

    // Print statistik semua pemain
    public void printAllStats() {
        for (Map.Entry<String, PlayerStats> entry : playerStats.entrySet()) {
            System.out.println("Player: " + entry.getKey());
            entry.getValue().printStats();
        }
    }
}

}