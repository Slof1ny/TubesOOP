package core.player;

import java.util.*;

public class PlayerStats {
    private Map<String, Integer> itemCount;
    private static final Map<String, List<String>> CATEGORY_MAP = new HashMap<>();

    public PlayerStats() {
        itemCount = new HashMap<>();
        initializeAllItems();
        initializeCategories();
    }

    private void initializeAllItems() {
        for(String item : getAllItems()){
            itemCount.put(item, 0);
        }
    }

    private void initializeCategories(){
        CATEGORY_MAP.put("Seeds", Arrays.asList(
            "Parsnip Seeds", "Cauliflower Seeds", "Potato Seeds", "Wheat Seeds",
            "Blueberry Seeds", "Tomato Seeds", "Hot Pepper Seeds", "Melon Seeds",
            "Cranberry Seeds", "Pumpkin Seeds", "Grape Seeds"
        ));

        CATEGORY_MAP.put("Crops", Arrays.asList(
            "Parsnip", "Cauliflower", "Potato", "Wheat", "Blueberry", "Tomato",
            "Hot Pepper", "Melon", "Cranberry", "Pumpkin", "Grape"
        ));

        CATEGORY_MAP.put("Fish", Arrays.asList(
            "Bullhead", "Carp", "Chub", "Largemouth Bass", "Rainbow Trout",
            "Sturgeon", "Midnight Carp", "Flounder", "Halibut", "Octopus",
            "Pufferfish", "Sardine", "Super Cucumber", "Catfish", "Salmon",
            "Angler", "Crimsonfish", "Glacierfish", "Legend"
        ));

        CATEGORY_MAP.put("Food", Arrays.asList(
            "Fish n’ Chips", "Baguette", "Sashimi", "Fugu", "Wine", "Pumpkin Pie",
            "Veggie Soup", "Fish Stew", "Spakbor Salad", "Fish Sandwich",
            "The Legends of Spakbor", "Cooked Pig's Head"
        ));

        CATEGORY_MAP.put("Equipment", Arrays.asList(
            "Hoe", "Watering Can", "Pickaxe", "Fishing Rod"
        ));

        CATEGORY_MAP.put("Misc", Arrays.asList("Coal", "Firewood"));
    }

    private List<String> getAllItems() {
        return CATEGORY_MAP.values()
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .toList();
    }

    public void addItem(String itemName, int amount) {
        itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + amount);
    }

    public int getItemCount(String itemName) {
        return itemCount.getOrDefault(itemName, 0);
    }

    public int getTotalInCategory(String category) {
        List<String> items = CATEGORY_MAP.getOrDefault(category, Collections.emptyList());
        int total = 0;
        for (String item : items){
            total += getItemCount(item);
        }
        return total;
    }

    public void printStats() {
        for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
            System.out.println("Item: " + entry.getKey() + " | Jumlah: " + entry.getValue());
        }
    }

// public class Stats {
//     private Map<String, PlayerStats> playerStats;

//     public Stats() {
//         playerStats = new HashMap<>();
//     }

//     // Tambah item untuk pemain tertentu
//     public void addItemToPlayer(String playerName, String itemName, int amount) {
//         playerStats.putIfAbsent(playerName, new PlayerStats());
//         playerStats.get(playerName).addItem(itemName, amount);
//     }

//     // Dapatkan jumlah item dari pemain tertentu
//     public int getPlayerItemCount(String playerName, String itemName) {
//         PlayerStats stats = playerStats.get(playerName);
//         if (stats == null) return 0;
//         return stats.getItemCount(itemName);
//     }

//     // Print statistik semua pemain
//     public void printAllStats() {
//         for (Map.Entry<String, PlayerStats> entry : playerStats.entrySet()) {
//             System.out.println("Player: " + entry.getKey());
//             entry.getValue().printStats();
//         }
//     }
// }

}