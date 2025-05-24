package core.player;

import item.*;
import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private Map<Item, Integer> items;

    public Inventory() {
        items = new HashMap<>();
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

    public void showInventory() {
        System.out.println("Inventory:");
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            System.out.println("- " + entry.getKey().getName() + " x" + entry.getValue());
        }
    }
}
