package core.player;

import item.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    private Map<Item, Integer> items;
    private PlayerStats playerStats;
    private EquipmentManager equipmentManager;

    public Inventory(PlayerStats playerStats, EquipmentManager equipmentManager) {
        items = new HashMap<>();
        this.playerStats = playerStats;
        this.equipmentManager = equipmentManager;
        giveStartingItems();
    }

    private void giveStartingItems(){
        addItem(SeedRegistry.getSeedByName("Parsnip Seeds"), 15);
    }

    public Map<item.Item, Integer> getAllItems() {
        return new HashMap<>(items);
    }


    public void addItem(Item item, int quantity){
        if (item instanceof Equipment) {
            Equipment equipment = (Equipment) item;
            for (int i = 0; i < quantity; i++) {
                equipmentManager.addEquipment(new Equipment(equipment.getName(), 
                                                           equipment.getBuyPrice(), 
                                                           equipment.getSellPrice()));
            }
            return;
        }
        
        items.put(item, items.getOrDefault(item, 0) + quantity);
        if (playerStats != null) {
            playerStats.addItem(item.getName(), quantity);
        }
    }

    public void addItem(Item item){
        addItem(item, 1);
    }

    public boolean removeItem(Item item, int quantity) {
        if (item instanceof Equipment) {
            Equipment equipment = (Equipment) item;
            for (int i = 0; i < quantity; i++) {
                if (!equipmentManager.removeEquipment(equipment.getName())) {
                    return false;
                }
            }
            return true;
        }
        
        int currentQuantity = items.getOrDefault(item, 0);
        if (currentQuantity < quantity) return false;
        if (currentQuantity == quantity) items.remove(item);
        else items.put(item, currentQuantity - quantity);
        return true;
    }

    public int getItemCount(Item item) {
        if (item instanceof Equipment) {
            return equipmentManager.hasEquipment(item.getName()) ? 1 : 0;
        }
        
        return items.getOrDefault(item, 0);
    }

    public int getItemCount(String name) {
        if (equipmentManager.hasEquipment(name)) {
            return 1;
        }
        
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
        if (equipmentManager.hasEquipment(name)) {
            if (quantity > 1) return false;
            return equipmentManager.removeEquipment(name);
        }
        
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
        System.out.println("=== INVENTORY ===");
        
        System.out.println("Items:");
        for (Map.Entry<Item, Integer> entry : items.entrySet()) {
            System.out.println("- " + entry.getKey().getName() + " x" + entry.getValue());
        }
        
        equipmentManager.showEquipmentStatus();
    }

    public Item findItemByName(String name){
        for(Item item : items.keySet()){
            if(item.getName().equalsIgnoreCase(name)){
                return item;
            }
        }

        if (equipmentManager.hasEquipment(name)) {
            for(Equipment eq : equipmentManager.getOwnedEquipment().values()){
                if(eq.getName().equalsIgnoreCase(name)){
                    return eq;
                }
            }
        }
        return null;
    }
}