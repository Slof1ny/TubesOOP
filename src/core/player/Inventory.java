package core.player;

import item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<Item, Integer> items;
    private PlayerStats playerStats;

    public Inventory(PlayerStats playerStats) {
        items = new HashMap<>();
        this.playerStats = playerStats;
        giveStartingItems();
    }

    private void giveStartingItems(){
        addItem(new Seed("Parsnips Seeds",20, "Spring", 1), 15);
        addItem(new Equipment("Hoe", 0, 0));
        addItem(new Equipment("Watering Can", 0, 0));
        addItem(new Equipment("Pickaxe", 0, 0));
        addItem(new Equipment("Fishing Rod", 0, 0));

    }

    public void addItem(Item item, int quantity){
        items.put(item, items.getOrDefault(item, 0) + quantity);
        if (playerStats != null) {
            playerStats.addItem(item.getName(), quantity);
        }
    }

    public void addItem(Item item){
        addItem(item, 1);
    }

    public boolean removeItem(Item item, int quantity) {
        int currentQuantity = items.getOrDefault(item, 0);
        if (currentQuantity < quantity) return false;
        if (currentQuantity == quantity) items.remove(item);
        else items.put(item, currentQuantity - quantity);
        return true;
    }

    public int getItemCount(Item item) {
        return items.getOrDefault(item, 0);
    }

    public int getItemCount(String name) {
        int total = 0;
        for (var e : items.entrySet()) {
            if (e.getKey().getName().equals(name)) {
                total += e.getValue();
            }
        }
        return total;
    }

    /** Remove up to `quantity` items matching `name`. Returns true if successful. */
    public boolean removeByName(String name, int quantity) {
        int need = quantity;
        List<Item> toRemove = new ArrayList<>();
        for (var e : items.entrySet()) {
            if (need == 0) break;
            Item it = e.getKey();
            int cnt = e.getValue();
            if (it.getName().equals(name)) {
                int take = Math.min(cnt, need);
                for (int i = 0; i < take; i++) toRemove.add(it);
                need -= take;
            }
        }
        if (need > 0) return false;
        for (Item it : toRemove) removeItem(it, 1);
        return true;
    }

    public void showInventory() {
        System.out.println("Inventory:");
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            System.out.println("- " + entry.getKey().getName() + " x" + entry.getValue());
        }
    }
}
