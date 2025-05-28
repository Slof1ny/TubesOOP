package test;

import java.util.ArrayList;
import java.util.List;

import core.player.Inventory;
import core.player.PlayerStats;
import core.world.Season;
import item.EquipmentManager;
import item.Food;
import item.Misc;
import item.Seed;
import item.Crop;

public class InventoryTester {
    public static void main(String[] args) {
        System.out.println("--- Testing Inventory Class ---");

        // 1. Create an Inventory object
        System.out.println("\n1. Creating a new Inventory object (should have starting items).");
        Inventory playerInventory = new Inventory(new PlayerStats(), new EquipmentManager());
        playerInventory.showInventory();

        // 2. Test addItem() with different item types
        System.out.println("\n2. Adding 5 'Copper Ore' (Misc item).");
        Misc copperOre = new Misc("Copper Ore", 50, 25); // Example prices
        playerInventory.addItem(copperOre, 5);
        playerInventory.showInventory();

        System.out.println("\n3. Adding a 'Cooked Meat' (Food item).");
        Food cookedMeat = new Food("Cooked Meat", 150, 100, 10); // Example prices and energy
        playerInventory.addItem(cookedMeat);
        playerInventory.showInventory();

        System.out.println("\n4. Adding 10 'Wheat' (Crop item).");
        Crop wheat = new Crop("Wheat", 10, 8, 5); // Example prices and quantity
        playerInventory.addItem(wheat, 10);
        playerInventory.showInventory();

        System.out.println("\n5. Adding 2 more 'Parsnips Seeds' (existing Seed item).");
        // Ensure the Seed object matches the one added initially for HashMap key equality
        List<Season> parsnipSeasons = new ArrayList<>();
        parsnipSeasons.add(Season.SPRING);
        Seed parsnipsSeed = new Seed("Parsnips Seeds", 20, parsnipSeasons , 1, 1); // Uses buyPrice
        playerInventory.addItem(parsnipsSeed, 2);
        playerInventory.showInventory(); // Should be 15 + 2 = 17

        // 6. Test getItemCount()
        System.out.println("\n6. Getting count of 'Hoe': " + playerInventory.getItemCount("Hoe")); // Should be 1
        System.out.println("7. Getting count of 'Parsnips Seeds' (by name): " + playerInventory.getItemCount("Parsnips Seeds")); // Should be 17
        System.out.println("8. Getting count of 'Parsnips Seeds' (by object): " + playerInventory.getItemCount(parsnipsSeed)); // Should be 17
        System.out.println("9. Getting count of 'NonExistentItem': " + playerInventory.getItemCount("NonExistentItem")); // Should be 0

        // 10. Test removeItem()
        System.out.println("\n11. Removing 2 'Copper Ore'.");
        boolean removed = playerInventory.removeItem(copperOre, 2);
        System.out.println("Removed successfully: " + removed); // Should be true
        playerInventory.showInventory(); // Copper Ore x3

        System.out.println("\n12. Attempting to remove more 'Wheat' than available (15).");
        removed = playerInventory.removeItem(wheat, 15); // Only 10 left
        System.out.println("Removed successfully: " + removed); // Should be false
        playerInventory.showInventory(); // Wheat x10 (unchanged)

        System.out.println("\n13. Removing all 'Wheat' (10).");
        removed = playerInventory.removeItem(wheat, 10);
        System.out.println("Removed successfully: " + removed); // Should be true
        playerInventory.showInventory(); // Wheat should be gone

        // 14. Test removeByName()
        System.out.println("\n15. Removing 5 'Parsnips Seeds' by name.");
        removed = playerInventory.removeByName("Parsnips Seeds", 5);
        System.out.println("Removed by name successfully: " + removed); // Should be true
        playerInventory.showInventory(); // Should be 17 - 5 = 12

        System.out.println("\n16. Attempting to remove 20 'Parsnips Seeds' by name (only 12 left).");
        removed = playerInventory.removeByName("Parsnips Seeds", 20);
        System.out.println("Removed by name successfully: " + removed); // Should be false
        playerInventory.showInventory(); // Should remain x12

        System.out.println("\n--- Inventory Class Testing Complete ---");
    }
}

/*
 * COMMAND UNTUK NGERUN
 * javac -d . src/core/player/Inventory.java src/item/*.java test/InventoryTester.java
 * java -cp . test.InventoryTester
 */