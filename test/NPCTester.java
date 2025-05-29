package test;

import npc.*;
import core.player.Player;
import core.player.RelationshipStatus;
import action.NPCActions;
import item.*;
import fishing.FishingLocation;
import core.world.FarmMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

// javac -cp src -d out test/NPCTester.java
// java -cp out test.NPCTester

public class NPCTester {

    private static Player player;
    private static NPCActions npcActions;
    private static Map<String, NPC> allNPCs;
    private static Scanner scanner;

    public static void main(String[] args) {
        initializeGame();
        runTester();
    }

    private static void initializeGame() {
        player = new Player("TestPlayer", "MALE");
        npcActions = new NPCActions(player);
        Map<String, FishingLocation> dummyFishingLocations = createDummyFishingLocations();
        ItemRegistry.initializeFishItems(dummyFishingLocations);

        for (Item item : ItemRegistry.getAllItems()) {
            player.getInventory().addItem(item, 5);
        }
        player.getInventory().addItem(ItemRegistry.getItemByName("Proposal Ring"), 1);

        allNPCs = new HashMap<>();
        allNPCs.put("Abigail", new Abigail());
        allNPCs.put("Caroline", new Caroline());
        allNPCs.put("Dasco", new Dasco());
        allNPCs.put("Emily", new Emily());
        allNPCs.put("Mayor Tadi", new MayorTadi());
        allNPCs.put("Orenji si Kucing Barista", new Orenji());
        allNPCs.put("Perry", new Perry());

        scanner = new Scanner(System.in);

        System.out.println("=== NPC Tester Initialized ===");
        System.out.println("Player Name: " + player.getName());
        System.out.println("Player Energy: " + player.getEnergy());
        System.out.println("Player Gold: " + player.getGold().getAmount());
        System.out.println("Player Inventory:");
        player.getInventory().showInventory();
        System.out.println("------------------------------");
    }

    private static Map<String, FishingLocation> createDummyFishingLocations() {
        Map<String, FishingLocation> locations = new HashMap<>();
        Player dummyPlayerForFarmMap = new Player("Dummy", "Female"); 
        
        locations.put("Mountain Lake", new fishing.FreeFishingLocation("Mountain Lake", new ArrayList<>(), new FarmMap(dummyPlayerForFarmMap))); 
        locations.put("Pond", new fishing.SpecificFishingLocation("Pond", new ArrayList<>(), new FarmMap(dummyPlayerForFarmMap)) { 
            @Override public boolean canFishAt(Player p) { return true; }
        });
        locations.put("Forest River", new fishing.FreeFishingLocation("Forest River", new ArrayList<>(), new FarmMap(dummyPlayerForFarmMap))); 
        locations.put("Ocean", new fishing.FreeFishingLocation("Ocean", new ArrayList<>(), new FarmMap(dummyPlayerForFarmMap))); 
        return locations;
    }

    private static void runTester() {
        while (true) {
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Interact with an NPC");
            System.out.println("2. Show Player Status");
            System.out.println("3. Show Player Inventory");
            System.out.println("4. Set Player Energy"); // New option added
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    interactWithNPCMenu();
                    break;
                case "2":
                    showPlayerStatus();
                    break;
                case "3":
                    player.getInventory().showInventory();
                    break;
                case "4":
                    setPlayerEnergy(); // Call the new method
                    break;
                case "0":
                    System.out.println("Exiting NPC Tester. Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void setPlayerEnergy() {
        System.out.print("Enter new energy value: ");
        try {
            int newEnergy = Integer.parseInt(scanner.nextLine());
            player.setEnergy(newEnergy); // Assumes Player class has this method
            System.out.println("Player energy set to " + newEnergy);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void interactWithNPCMenu() {
        System.out.println("\n--- Choose an NPC ---");
        List<String> npcNames = new ArrayList<>(allNPCs.keySet());
        for (int i = 0; i < npcNames.size(); i++) {
            System.out.println((i + 1) + ". " + npcNames.get(i));
        }
        System.out.println("0. Back to Main Menu");
        System.out.print("Enter NPC number: ");

        try {
            int npcChoice = Integer.parseInt(scanner.nextLine());
            if (npcChoice == 0) return;
            if (npcChoice > 0 && npcChoice <= npcNames.size()) {
                String selectedNPCName = npcNames.get(npcChoice - 1);
                NPC selectedNPC = allNPCs.get(selectedNPCName);
                if (selectedNPC != null) {
                    interactWithSpecificNPC(selectedNPC);
                } else {
                    System.out.println("NPC not found.");
                }
            } else {
                System.out.println("Invalid NPC number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        }
    }

    private static void interactWithSpecificNPC(NPC npc) {
        System.out.println("\n--- Interacting with " + npc.getName() + " ---");
        System.out.println("Current Heart Points: " + npc.getHeartPoints() + "/" + NPC.MAX_HEART_POINTS);
        System.out.println("Relationship Status: " + npc.getRelationshipStatus());

        while (true) {
            System.out.println("\nActions for " + npc.getName() + ":");
            System.out.println("1. Chat");
            System.out.println("2. Gift Item");
            System.out.println("3. Propose (Requires Proposal Ring & Max Hearts)");
            System.out.println("4. Marry (Requires Fiance status & 1+ day after proposal)");
            System.out.println("0. Back to NPC Selection");
            System.out.print("Choose an action: ");

            String actionChoice = scanner.nextLine();

            switch (actionChoice) {
                case "1":
                    System.out.println(npcActions.chatWithNPC(npc));
                    System.out.println(npc.getChatDialogue(player));
                    break;
                case "2":
                    giftItemToNPC(npc);
                    break;
                case "3":
                    boolean hasRing = player.getInventory().getItemCount(ItemRegistry.getItemByName("Proposal Ring")) > 0;
                    System.out.println(npcActions.proposeToNPC(npc, hasRing));
                    if (npc.getRelationshipStatus() == RelationshipStatus.FIANCE) {
                        npcActions.setDaysSinceLastProposalForTesting(1);
                        System.out.println("DEBUG: Days since last proposal set to 1 for marriage test.");
                    }
                    break;
                case "4":
                    boolean hasRingForMarriage = player.getInventory().getItemCount(ItemRegistry.getItemByName("Proposal Ring")) > 0;
                    System.out.println(npcActions.marryNPC(npc, hasRingForMarriage));
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid action. Please try again.");
            }
            System.out.println("Player Energy: " + player.getEnergy() + ". " + npc.getName() + " Hearts: " + npc.getHeartPoints());
        }
    }

    private static void giftItemToNPC(NPC npc) {
        System.out.println("\n--- Your Inventory ---");
        player.getInventory().showInventory();
        System.out.print("Enter the name of the item to gift (e.g., 'Potato', 'Catfish', 'Baguette'): ");
        String itemName = scanner.nextLine();

        Item itemToGift = ItemRegistry.getItemByName(itemName);

        if (itemToGift == null) {
            System.out.println("Item not found in game data. Please enter an exact item name.");
            return;
        }

        System.out.println(npcActions.giftToNPC(npc, itemToGift));
    }
    
    private static void showPlayerStatus() {
        System.out.println("\n--- Player Status ---");
        System.out.println("Name: " + player.getName());
        System.out.println("Energy: " + player.getEnergy() + "/" + Player.getMaxEnergy());
        System.out.println("Gold: " + player.getGold().getAmount());
        System.out.println("Equipped: " + (player.getEquipmentManager().getEquippedItem() != null ? player.getEquipmentManager().getEquippedItem().getName() : "None"));
        System.out.println("Partner: " + (player.getPartner() != null ? player.getPartner().getName() + " (" + player.getRelationshipStatus(player.getPartner()) + ")" : "None"));
        System.out.println("--- Relationships ---");
        if (player.getAllRelationships().isEmpty()) {
            System.out.println("No established relationships yet.");
        } else {
            for (Map.Entry<NPC, RelationshipStatus> entry : player.getAllRelationships().entrySet()) {
                System.out.println("- " + entry.getKey().getName() + ": " + entry.getValue() + " (" + entry.getKey().getHeartPoints() + " hearts)");
            }
        }
    }
}