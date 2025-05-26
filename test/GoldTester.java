package test;

import core.player.Gold;

public class GoldTester {
    public static void main(String[] args) {
        System.out.println("--- Testing Gold Class ---");

        // 1. Create a Gold object
        System.out.println("\reating a Gold object with initial amount of 100.");
        Gold playerGold = new Gold(100);
        System.out.println("Player's Gold: " + playerGold); // Should output 100g

        // 2. Test getAmount()
        System.out.println("\n2. Getting current gold amount.");
        System.out.println("Current Gold: " + playerGold.getAmount() + "g"); // Should output 100g

        // 3. Test add()
        System.out.println("\n3. Adding 50 gold.");
        playerGold.add(50);
        System.out.println("Player's Gold after adding: " + playerGold); // Should output 150g

        System.out.println("\n4. Attempting to add negative gold (should throw exception).");
        try {
            playerGold.add(-20);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage()); // Should output "Cannot add negative gold."
        }
        System.out.println("Player's Gold (should remain 150g): " + playerGold);

        // 5. Test subtract()
        System.out.println("\n5. Subtracting 30 gold.");
        boolean subtracted = playerGold.subtract(30);
        System.out.println("Subtraction successful: " + subtracted); // Should be true
        System.out.println("Player's Gold after subtracting: " + playerGold); // Should output 120g

        System.out.println("\n6. Attempting to subtract more gold than available.");
        subtracted = playerGold.subtract(200); // Current is 120g
        System.out.println("Subtraction successful: " + subtracted); // Should be false
        System.out.println("Player's Gold (should remain 120g): " + playerGold);

        System.out.println("\n7. Subtracting remaining gold (120g).");
        subtracted = playerGold.subtract(120);
        System.out.println("Subtraction successful: " + subtracted); // Should be true
        System.out.println("Player's Gold after subtracting all: " + playerGold); // Should output 0g

        System.out.println("\n8. Attempting to subtract negative gold (should throw exception).");
        try {
            playerGold.subtract(-10);
        } catch (IllegalArgumentException e) {
            System.out.println("Caught expected exception: " + e.getMessage()); // Should output "Cannot subtract negative gold."
        }
        System.out.println("Player's Gold (should remain 0g): " + playerGold);

        System.out.println("\n--- Gold Class Testing Complete ---");
    }
}

/**
 * COMMAND UNTUK NGE RUN
 * ~/Documents/Java/TubesOOP %javac -d . src/core/player/Gold.java test/GoldTester.java
 * ~/Documents/Java/TubesOOP %javac -d . src/core/player/Gold.java test/GoldTester.java
 */

