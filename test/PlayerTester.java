package test;

import core.player.Player;
import core.player.Gold;
import core.player.RelationshipStatus;
import npc.*;

public class PlayerTester {

    public static void main(String[] args) {
        System.out.println("--- Starting Player Tester ---");

        // Test Scenario 1: Player Initialization
        System.out.println("\n--- Test Scenario 1: Player Initialization ---");
        Player player1 = new Player("Alice", "Female");
        System.out.println("Player Name: " + player1.getName());
        System.out.println("Player Gender: " + player1.getGender());
        System.out.println("Player Energy: " + player1.getEnergy());
        System.out.println("Player Gold: " + player1.getGold().getAmount() + "g");
        System.out.println("Player X, Y Position: (" + player1.getX() + ", " + player1.getY() + ")");
        player1.getInventory().showInventory();
        System.out.println("Is Player Single? " + player1.isSingle());

        // Test Scenario 2: Energy Management
        System.out.println("\n--- Test Scenario 2: Energy Management ---");
        player1.setEnergy(50);
        System.out.println("Energy set to 50: " + player1.getEnergy());
        player1.setEnergy(120);
        System.out.println("Energy set to 120 (capped): " + player1.getEnergy());
        player1.setEnergy(-10);
        System.out.println("Energy set to -10 (capped): " + player1.getEnergy());
        player1.setEnergy(100);
        System.out.println("Energy restored to 100: " + player1.getEnergy());

        // Test Scenario 3: Gold Management
        System.out.println("\n--- Test Scenario 3: Gold Management ---");
        Gold playerGold = player1.getGold();
        System.out.println("Initial Gold: " + playerGold.getAmount() + "g");
        playerGold.add(500);
        System.out.println("Added 500g. Current Gold: " + playerGold.getAmount() + "g");
        boolean subtracted = playerGold.subtract(200);
        System.out.println("Subtracted 200g. Success: " + subtracted + ". Current Gold: " + playerGold.getAmount() + "g");
        subtracted = playerGold.subtract(1000);
        System.out.println("Attempt to subtract 1000g. Success: " + subtracted + ". Current Gold: " + playerGold.getAmount() + "g");

        // Test Scenario 4: Position Management
        System.out.println("\n--- Test Scenario 4: Position Management ---");
        player1.setPosition(10, 5);
        System.out.println("Player position set to (10, 5): (" + player1.getX() + ", " + player1.getY() + ")");

        // Test Scenario 5: Inventory Access
        System.out.println("\n--- Test Scenario 5: Inventory Access ---");
        player1.getInventory().showInventory();

        // Test Scenario 6: Relationship Status Management
        System.out.println("\n--- Test Scenario 6: Relationship Status Management ---");
        Caroline caroline = new Caroline(); 
        Perry perry = new Perry();

        System.out.println("Initial relationship with Caroline: " + player1.getRelationshipStatus(caroline));
        System.out.println("Initial relationship with Perry: " + player1.getRelationshipStatus(perry));
        System.out.println("Is Player Single? " + player1.isSingle());

        // Set Caroline as partner
        player1.setRelationshipStatus(caroline, RelationshipStatus.FIANCE); // Sets partner implicitly
        System.out.println("Relationship with Caroline set to FIANCE: " + player1.getRelationshipStatus(caroline));
        System.out.println("Player's partner: " + (player1.getPartner() != null ? player1.getPartner().getName() : "None"));
        System.out.println("Is Player Single? " + player1.isSingle());

        player1.setRelationshipStatus(caroline, RelationshipStatus.MARRIED);
        System.out.println("Relationship with Caroline set to MARRIED: " + player1.getRelationshipStatus(caroline));
        System.out.println("Is Player Single? " + player1.isSingle());

        player1.setRelationshipStatus(perry, RelationshipStatus.SINGLE);
        System.out.println("Relationship with Perry (set to SINGLE): " + player1.getRelationshipStatus(perry));

        System.out.println("All Player Relationships: " + player1.getAllRelationships());

        System.out.println("\n--- Player Tester Finished ---");
    }
}