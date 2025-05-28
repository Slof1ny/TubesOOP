// src/game/Store.java (or whatever package you prefer for game-specific classes)
package system;

import item.Item;
import item.Seed;
import item.Food;
import item.Equipment;
import item.Misc;
import core.player.Player;
import npc.NPC; // To reference the NPC owner if needed

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays; // For lists in fish initialization

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
    private NPC owner; // Emily
    private Map<String, Item> itemsForSale; // Map of item name to Item object

    public Store(String name, NPC owner) {
        this.name = name;
        this.owner = owner;
        this.itemsForSale = new HashMap<>();
        initializeStoreInventory();
    }

    /**
     * Populates the store with items available for purchase based on the assignment specification.
     * This method should be comprehensive, including all items with a buy price.
     */
    private void initializeStoreInventory() {
        // Seeds (Name, BuyPrice, Season, DaysToHarvest)
        itemsForSale.put("Parsnip Seeds", Seed.getSeedByName("Parsnip Seeds"));
        itemsForSale.put("Cauliflower Seeds", Seed.getSeedByName("Cauliflower Seeds"));
        itemsForSale.put("Potato Seeds", Seed.getSeedByName("Potato Seeds"));
        itemsForSale.put("Wheat Seeds", Seed.getSeedByName("Wheat Seeds"));
        itemsForSale.put("Blueberry Seeds", Seed.getSeedByName("Blueberry Seeds"));
        itemsForSale.put("Tomato Seeds", Seed.getSeedByName("Tomato Seeds"));
        itemsForSale.put("Hot Pepper Seeds", Seed.getSeedByName("Hot Pepper Seeds"));
        itemsForSale.put("Melon Seeds", Seed.getSeedByName("Melon Seeds"));
        itemsForSale.put("Cranberry Seeds", Seed.getSeedByName("Cranberry Seeds"));
        itemsForSale.put("Pumpkin Seeds", Seed.getSeedByName("Pumpkin Seeds"));
        itemsForSale.put("Grape Seeds", Seed.getSeedByName("Grape Seeds"));

        // Food (Name, BuyPrice, SellPrice, EnergyRestored)
        itemsForSale.put("Fish n' Chips", new Food("Fish n' Chips", 150, 135, 50));
        itemsForSale.put("Baguette", new Food("Baguette", 100, 80, 25));
        itemsForSale.put("Sashimi", new Food("Sashimi", 300, 275, 70));
        // Fugu, Spakbor Salad, The Legends of Spakbor have 0 buy price in spec, implying not bought directly
        itemsForSale.put("Fugu", new Food("Fugu", 0, 135, 50));
        itemsForSale.put("Wine", new Food("Wine", 100, 90, 20));
        itemsForSale.put("Pumpkin Pie", new Food("Pumpkin Pie", 120, 100, 35));
        itemsForSale.put("Veggie Soup", new Food("Veggie Soup", 140, 120, 40));
        itemsForSale.put("Fish Stew", new Food("Fish Stew", 280, 260, 70));
        itemsForSale.put("Spakbor Salad", new Food("Spakbor Salad", 0, 250, 70));
        itemsForSale.put("Fish Sandwich", new Food("Fish Sandwich", 200, 180, 50));
        itemsForSale.put("The Legends of Spakbor", new Food("The Legends of Spakbor", 0, 2000, 100));
        itemsForSale.put("Cooked Pig's Head", new Food("Cooked Pig's Head", 1000, 0, 100)); // Sell price is 0 in spec

        // Equipment (Player starts with these, but store might sell replacements)
        // For demonstration, let's give them a placeholder price if they were to be sold
        itemsForSale.put("Hoe", new Equipment("Hoe", 500, 250));
        itemsForSale.put("Watering Can", new Equipment("Watering Can", 500, 250));
        itemsForSale.put("Pickaxe", new Equipment("Pickaxe", 500, 250));
        itemsForSale.put("Fishing Rod", new Equipment("Fishing Rod", 750, 375));

        // Misc (Name, BuyPrice, SellPrice) - ensure sellPrice < buyPrice
        itemsForSale.put("Coal", new Misc("Coal", 100, 50));
        itemsForSale.put("Firewood", new Misc("Firewood", 50, 20));

        // Example Fish (if store sells fish, which is not explicitly stated in spec for buying)
        // This is just to show how Fish objects would be created if needed.
        // itemsForSale.put("Sardine", new Fish("Sardine", 50, 25, FishType.COMMON,
        //     Arrays.asList(Season.SPRING, Season.SUMMER, Season.FALL, Season.WINTER),
        //     Arrays.asList(GameCalendar.MORNING, GameCalendar.AFTERNOON),
        //     Arrays.asList(Weather.SUNNY, Weather.RAINY),
        //     Arrays.asList(FishingLocation.OCEAN)));
    }

    /**
     * Displays the current items available for sale in the store.
     */
    public void displayStoreMenu() {
        System.out.println("\n--- Welcome to " + name + "! ---");
        System.out.println("Owner: " + owner.getName());
        System.out.println("Items for sale:");
        int i = 1;
        for (Map.Entry<String, Item> entry : itemsForSale.entrySet()) {
            Item item = entry.getValue();
            if (item.getBuyPrice() > 0) { // Only display items that have a buy price
                System.out.printf("%d. %-20s (%s) - %dg%n", i++, item.getName(), item.getCategory(), item.getBuyPrice());
            }
        }
        System.out.println("0. Exit Store");
        System.out.println("----------------------------------");
    }

    /**
     * Handles a player's attempt to purchase an item from the store.
     *
     * @param player The Player attempting the purchase.
     * @param itemName The name of the item to buy.
     * @param quantity The quantity of the item to buy.
     */
    public void handlePurchase(Player player, String itemName, int quantity) {
        if (quantity <= 0) {
            System.out.println("Invalid quantity. Please enter a positive number.");
            return;
        }

        Item itemToBuy = itemsForSale.get(itemName);

        if (itemToBuy == null) {
            System.out.println("Sorry, '" + itemName + "' is not available in this store.");
            return;
        }

        if (itemToBuy.getBuyPrice() <= 0) {
            System.out.println("You cannot buy '" + itemName + "'. It's not for sale.");
            return;
        }

        int totalCost = itemToBuy.getBuyPrice() * quantity;

        if (player.getGold().getAmount() >= totalCost) {
            if (player.getGold().subtract(totalCost)) { // deductGold returns true if successful
                player.getInventory().addItem(itemToBuy, quantity);
                System.out.println("You successfully bought " + quantity + " " + itemName + "(s) for " + totalCost + "g.");
                // The specification does not explicitly state energy or time cost for buying from the store.
                // If it should, add player.deductEnergy() and gameTime.advanceTime() here.
            }
        } else {
            System.out.println("You don't have enough gold to buy " + quantity + " " + itemName + "(s). You need " + totalCost + "g.");
        }
    }
}
