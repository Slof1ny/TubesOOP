package core.player;

import java.util.HashMap;
import java.util.Map;


public class PlayerStats {
    private Map<String, Integer> itemCount;

    public PlayerStats() {
        itemCount = new HashMap<>();
    }

    // Tambah item
    public void addItem(String itemName, int amount) {
        itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + amount);
    }

    // Dapatkan jumlah item
    public int getItemCount(String itemName) {
        return itemCount.getOrDefault(itemName, 0);
    }

    // Print semua item dan jumlahnya
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
