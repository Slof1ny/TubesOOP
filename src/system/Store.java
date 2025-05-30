package system;

import item.Item;
import item.Seed;
import item.SeedRegistry;
import item.Food;
import item.Equipment;
import item.Misc;
import core.player.Player;
import npc.NPC;
import gui.StorePanel;
import gui.GameView;
import gui.PlayerInfoPanel;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

// Placeholder imports for Fish dependencies
import fishing.FishType;
import core.world.Season;
import time.GameCalendar;
import core.world.Weather;
import fishing.FishingLocation;
import item.Fish;


/**
 * Represents the in-game store where players can buy items.
 * Emily is the NPC owner of this store.
 */
public class Store {
    private String name;
    private NPC owner;
    private Map<String, Item> itemsForSale;
    private StorePanel storePanel;

    public Store(String name, NPC owner) {
        this.name = name;
        this.owner = owner;
        this.itemsForSale = new HashMap<>();
        initializeStoreInventory();
    }

    public String getName() { // Added missing getter
        return name;
    }

    public Map<String, Item> getItemsForSale() { // Added missing getter
        return itemsForSale;
    }


    private void initializeStoreInventory() {
        itemsForSale.put("Parsnip Seeds", SeedRegistry.getSeedByName("Parsnip Seeds"));
        itemsForSale.put("Cauliflower Seeds", SeedRegistry.getSeedByName("Cauliflower Seeds"));
        itemsForSale.put("Potato Seeds", SeedRegistry.getSeedByName("Potato Seeds"));
        itemsForSale.put("Wheat Seeds", SeedRegistry.getSeedByName("Wheat Seeds"));
        itemsForSale.put("Blueberry Seeds", SeedRegistry.getSeedByName("Blueberry Seeds"));
        itemsForSale.put("Tomato Seeds", SeedRegistry.getSeedByName("Tomato Seeds"));
        itemsForSale.put("Hot Pepper Seeds", SeedRegistry.getSeedByName("Hot Pepper Seeds"));
        itemsForSale.put("Melon Seeds", SeedRegistry.getSeedByName("Melon Seeds"));
        itemsForSale.put("Cranberry Seeds", SeedRegistry.getSeedByName("Cranberry Seeds"));
        itemsForSale.put("Pumpkin Seeds", SeedRegistry.getSeedByName("Pumpkin Seeds"));
        itemsForSale.put("Grape Seeds", SeedRegistry.getSeedByName("Grape Seeds"));

        itemsForSale.put("Fish n' Chips", new Food("Fish n' Chips", 150, 135, 50));
        itemsForSale.put("Baguette", new Food("Baguette", 100, 80, 25));
        itemsForSale.put("Sashimi", new Food("Sashimi", 300, 275, 70));
        itemsForSale.put("Fugu", new Food("Fugu", 0, 135, 50));
        itemsForSale.put("Wine", new Food("Wine", 100, 90, 20));
        itemsForSale.put("Pumpkin Pie", new Food("Pumpkin Pie", 120, 100, 35));
        itemsForSale.put("Veggie Soup", new Food("Veggie Soup", 140, 120, 40));
        itemsForSale.put("Fish Stew", new Food("Fish Stew", 280, 260, 70));
        itemsForSale.put("Spakbor Salad", new Food("Spakbor Salad", 0, 250, 70));
        itemsForSale.put("Fish Sandwich", new Food("Fish Sandwich", 200, 180, 50));
        itemsForSale.put("The Legends of Spakbor", new Food("The Legends of Spakbor", 0, 2000, 100));
        itemsForSale.put("Cooked Pig's Head", new Food("Cooked Pig's Head", 1000, 0, 100));

        itemsForSale.put("Hoe", new Equipment("Hoe", 500, 250));
        itemsForSale.put("Watering Can", new Equipment("Watering Can", 500, 250));
        itemsForSale.put("Pickaxe", new Equipment("Pickaxe", 500, 250));
        itemsForSale.put("Fishing Rod", new Equipment("Fishing Rod", 750, 375));

        itemsForSale.put("Coal", new item.Item("Coal", 100, 50) {
            @Override public String getCategory() { return "Fuel"; }
        });
        itemsForSale.put("Firewood", new item.Item("Firewood", 50, 20) {
            @Override public String getCategory() { return "Fuel"; }
        });
        
        Item proposalRing = new Item("Proposal Ring", 2500, 0) { // Buy: 2500g, Sell: 0g (or non-sellable)
            @Override
            public String getCategory() {
                return "Special"; // A distinct category
            }
        };
        itemsForSale.put(proposalRing.getName(), proposalRing);
        System.out.println("DEBUG: Proposal Ring added to store for " + proposalRing.getBuyPrice() + "g.");
    }

    public void displayStoreMenu() {
        System.out.println("\n--- Welcome to " + name + "! ---");
        System.out.println("Owner: " + owner.getName());
        System.out.println("Items for sale:");
        int i = 1;
        for (Map.Entry<String, Item> entry : itemsForSale.entrySet()) {
            Item item = entry.getValue();
            if (item.getBuyPrice() > 0) {
                System.out.printf("%d. %-20s (%s) - %dg%n", i++, item.getName(), item.getCategory(), item.getBuyPrice());
            }
        }
        System.out.println("0. Exit Store");
        System.out.println("----------------------------------");
    }

    // public void handlePurchase(Player player, String itemName, int quantity) {
    //     if (quantity <= 0) {
    //         System.out.println("Invalid quantity. Please enter a positive number.");
    //         return;
    //     }

    //     Item itemToBuy = itemsForSale.get(itemName);

    //     if (itemToBuy == null) {
    //         System.out.println("Sorry, '" + itemName + "' is not available in this store.");
    //         return;
    //     }

    //     if (itemToBuy.getBuyPrice() <= 0) {
    //         System.out.println("You cannot buy '" + itemName + "'. It's not for sale.");
    //         return;
    //     }

    //     int totalCost = itemToBuy.getBuyPrice() * quantity;

    //     if (player.getGold().getAmount() >= totalCost) {
    //         if (player.getGold().subtract(totalCost)) {
    //             player.getInventory().addItem(itemToBuy, quantity);
    //             System.out.println("You successfully bought " + quantity + " " + itemName + "(s) for " + totalCost + "g.");
    //         }
    //     } else {
    //         System.out.println("You don't have enough gold to buy " + quantity + " " + itemName + "(s). You need " + totalCost + "g.");
    //     }
    // }

    public boolean handlePurchase(Player player, String itemName, int quantity) { // MODIFIED: Return boolean, removed Season parameter
        if (quantity <= 0) {
            System.out.println("Store: Invalid quantity. Please enter a positive number.");
            // In a GUI context, this message might be better handled by the panel.
            return false;
        }

        Item itemToBuy = itemsForSale.get(itemName);

        if (itemToBuy == null) {
            System.out.println("Store: Sorry, '" + itemName + "' is not available in this store.");
            return false;
        }

        if (itemToBuy.getBuyPrice() <= 0) {
            System.out.println("Store: You cannot buy '" + itemName + "'. It's not for sale.");
            return false;
        }

        int totalCost = itemToBuy.getBuyPrice() * quantity;

        if (player.getGold().getAmount() >= totalCost) {
            if (player.getGold().subtract(totalCost)) { // This returns boolean
                player.getInventory().addItem(itemToBuy, quantity);
                System.out.println("Store: You successfully bought " + quantity + " " + itemToBuy.getName() + "(s) for " + totalCost + "g.");
                // PlayerStats expenditure will be handled by the caller (StorePanel)
                return true; // Purchase successful
            } else {
                // This case should ideally not be reached if getAmount check is correct,
                // but good for robustness.
                System.out.println("Store: Gold subtraction failed unexpectedly for " + itemName);
                return false;
            }
        } else {
            System.out.println("Store: You don't have enough gold to buy " + quantity + " " + itemToBuy.getName() + "(s). You need " + totalCost + "g.");
            return false; // Not enough gold
        }
    }
}
