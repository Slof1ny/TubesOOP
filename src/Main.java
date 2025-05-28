// Main.java
import core.player.Player;
import core.world.FarmMap;
import core.world.ShippingBin;
import system.PriceList;
import time.GameCalendar;
import time.Time;
import npc.*; // Import all NPC classes
import core.world.*;
import fishing.*;
import item.*;



import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException; // Add this import
import java.util.concurrent.Future; 
import java.util.HashMap;
import java.util.Map;
// import java.util.Scanner; // No longer needed
import java.util.concurrent.TimeUnit;

public class Main {
    // Change from Scanner to BufferedReader
    private static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Player currentPlayer;
    private static FarmMap currentFarmMap;
    private static GameCalendar gameCalendar;
    private static Time gameTime;
    private static boolean running = true;
    private static Map<String, FishingLocation> gameFishingLocations;

    public static void main(String[] args) {
        System.out.println("Welcome to Spakbor Hills!");
        loadGameData();

        try { // Add try-catch for IOException on reader.close()
            while (running) {
                displayMainMenu();
                int choice = getUserChoice();
                if (!running) { // Check if getUserChoice set running to false (e.g., on EOF)
                    break;
                }
                handleMainMenuChoice(choice);
            }
        } finally {
            try {
                reader.close(); // Close the reader when the application exits
            } catch (IOException e) {
                System.err.println("Error closing reader: " + e.getMessage());
            }
        }


        System.out.println("Thank you for playing Spakbor Hills!");
        if (gameTime != null) {
            gameTime.pause(); // Ensure the time scheduler is shut down
        }
    }

    private static void loadGameData() {
        try {
            PriceList.loadPrices("resources/price_list.csv");
            System.out.println("Game data loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading price list: " + e.getMessage());
            System.err.println("Please ensure 'resources/price_list.csv' is in the correct directory.");
            // In a full game, you might want to gracefully exit or prompt the user.
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. New Game");
        System.out.println("2. Load Game (Bonus - Placeholder)");
        System.out.println("3. Credits");
        System.out.println("4. Exit");
    }

    /**
     * Prompts the user for input and handles basic input validation using BufferedReader.
     * @return The user's integer choice. Returns -1 if an error occurs or EOF is reached.
     */
    private static int getUserChoice() {
        while (true) {
            System.out.print("Enter your choice: ");
            String inputLine = null;
            try {
                inputLine = reader.readLine(); // Use readLine()
            } catch (IOException e) {
                System.err.println("Error reading input: " + e.getMessage());
                running = false; // Set running to false to exit main loop
                return -1; // Return sentinel value or rethrow
            }

            if (inputLine == null) { // This signifies EOF (End of File) or stream closed
                System.out.println("End of input detected. Exiting.");
                running = false; // Set running to false to exit main loop
                return -1; // Return sentinel value
            }

            try {
                int choice = Integer.parseInt(inputLine.trim()); // Trim whitespace before parsing
                return choice;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }


    private static void handleMainMenuChoice(int choice) {
        if (!running) { // If choice was -1 due to EOF/error, just return
            return;
        }
        switch (choice) {
            case 1:
                startNewGame();
                break;
            case 2:
                System.out.println("Load Game functionality is not yet implemented.");
                break;
            case 3:
                showCredits();
                break;
            case 4:
                running = false;
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    private static void startNewGame() {
        System.out.println("\n--- Starting New Game ---");
        String playerName = null;
        try {
            System.out.print("Enter your player name: ");
            playerName = reader.readLine();
            System.out.print("Enter your player gender (Male/Female): ");
            String playerGender = reader.readLine();

            currentPlayer = new Player(playerName, playerGender);
            gameCalendar = new GameCalendar();
            gameTime = new Time(gameCalendar, currentPlayer);
            currentFarmMap = new FarmMap(currentPlayer);
            currentPlayer.setShippingBin(new ShippingBin());

            gameFishingLocations = new HashMap<>();

            FishingLocation farmPondLocation = new fishing.SpecificFishingLocation("Pond", new java.util.ArrayList<>(), currentFarmMap) {
                @Override
                public boolean canFishAt(Player player) {
                    // Check if player is adjacent to any 'o' (pond) tile on the farm map
                    int px = player.getX();
                    int py = player.getY();

                    for (int dy = -1; dy <= 1; dy++) {
                        for (int dx = -1; dx <= 1; dx++) {
                            if (dx == 0 && dy == 0) continue; // Skip player's own position

                            int checkX = px + dx;
                            int checkY = py + dy;

                            if (checkX >= 0 && checkX < FarmMap.SIZE && checkY >= 0 && checkY < FarmMap.SIZE) {
                                core.world.Tile tile = currentFarmMap.getTileAt(checkX, checkY);
                                if (tile != null && tile.displayChar() == 'o') {
                                    return true; // Found pond tile adjacent to player
                                }
                            }
                        }
                    }
                    return false; // No pond tile found adjacent to player
                }
            };
            gameFishingLocations.put("Pond", farmPondLocation);
            // Other free fishing locations, if applicable, based on your game design
            gameFishingLocations.put("Mountain Lake", new fishing.FreeFishingLocation("Mountain Lake", new java.util.ArrayList<>(), currentFarmMap));
            gameFishingLocations.put("Forest River", new fishing.FreeFishingLocation("Forest River", new java.util.ArrayList<>(), currentFarmMap));
            gameFishingLocations.put("Ocean", new fishing.FreeFishingLocation("Ocean", new java.util.ArrayList<>(), currentFarmMap));

            // Build all fish and distribute them to the locations
            fishing.FishRegistry.buildAll(gameFishingLocations);

            
            // Initialize NPCs (example, you'd manage these in a comprehensive way)
            new Emily();
            new MayorTadi();
            new Abigail();
            new Caroline();
            new Dasco();
            new Perry();
            new Orenji();

            System.out.println("Game started! Welcome, " + currentPlayer.getName() + " to Spakbor Hills!");

            // Start the game's internal time progression
            gameTime.runTime2();

            gameLoop();
        } catch (IOException e) {
            System.err.println("Error during new game setup: " + e.getMessage());
            running = false; // Abort game if input fails
        }
    }

    private static void gameLoop() {
        boolean inGame = true;
        while (inGame && running) { // Also check global 'running' flag
            System.out.println("\n--- Game State ---");
            System.out.println("Current Location: " + currentPlayer.getLocation() + " (" + currentPlayer.getX() + ", " + currentPlayer.getY() + ")");
            System.out.println("Energy: " + currentPlayer.getEnergy() + " | Gold: " + currentPlayer.getGold().getAmount() + "g");
            System.out.printf("Date: Day %d (%s, %s, %s) | Time: %02d:%02d%n",
                    gameCalendar.getTotalDay(),
                    gameCalendar.getCurrentSeason(),
                    gameCalendar.getCurrentWeather(),
                    (gameTime.isNight() ? "Night" : "Day"), // Add this part for night/day status
                    gameTime.getHour(), gameTime.getMinute());
            currentFarmMap.displayFarmMap();

            System.out.println("\n--- Available Actions ---");
            System.out.println("1. Move (W/A/S/D)");
            System.out.println("2. Show Inventory");
            System.out.println("3. Sleep");
            System.out.println("4. Display Shipping Bin Contents");
            System.out.println("5. Interact (with nearby objects)"); // New action
            System.out.println("6. Back to Main Menu"); // Old 5, now 6


            System.out.print("Enter action choice: ");
            String actionChoice = null;
            try {
                actionChoice = reader.readLine(); // Use readLine()
            } catch (IOException e) {
                System.err.println("Error reading action choice: " + e.getMessage());
                running = false;
                inGame = false;
                break;
            }

            if (actionChoice == null) { // EOF in game loop
                System.out.println("End of input detected. Exiting game loop.");
                running = false;
                inGame = false;
                break;
            }

            switch (actionChoice.trim().toLowerCase()) { // Trim and convert to lowercase
                case "1":
                case "move":
                    handleMovement();
                    break;
                case "2":
                case "inventory":
                    currentPlayer.getInventory().showInventory();
                    break;
                case "3":
                case "sleep":
                    System.out.println("You decided to sleep...");
                    gameTime.sleep2();
                    currentPlayer.setEnergy(100);
                    System.out.println("You woke up refreshed!");
                    break;
                case "4":
                case "shippingbin":
                    if (currentPlayer.getShippingBin() != null) {
                        currentPlayer.getShippingBin().displayContents();
                    } else {
                        System.out.println("Shipping Bin not set up for player.");
                    }
                    break;
                case "5": // New interact action
                case "interact":
                    handleInteraction();
                    break;
                case "6": // Shifted
                case "mainmenu":
                    inGame = false;
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    System.out.println("Invalid action. Try again.");
                    break;
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Game loop interrupted.");
            }

            if (currentPlayer.getGold().getAmount() >= 17209 || !currentPlayer.isSingle()) {
                System.out.println("\n--- GAME MILESTONE REACHED! ---");
                System.out.println("Total Gold Earned: " + currentPlayer.getStats().getTotalGoldEarned() + "g");
                System.out.println("Player Married: " + (!currentPlayer.isSingle() ? "Yes" : "No"));
                running = false;
                inGame = false;
            }
        }
    }

    private static void handleMovement() {
        System.out.println("--- Movement Mode ---");
        System.out.println("Use WASD to move (W=up, A=left, S=down, D=right), Q to quit movement mode:");
        
        while (true) {
            System.out.print("Move (WASD/Q): ");
            String direction = null;
            try {
                direction = reader.readLine(); // Use readLine()
            } catch (IOException e) {
                System.err.println("Error reading movement input: " + e.getMessage());
                running = false; // Set running to false to exit main loop
                break; // Exit movement loop
            }

            if (direction == null) { // EOF
                System.out.println("End of input detected. Aborting movement.");
                running = false; // Set running to false to exit main loop
                break; // Exit movement loop
            }

            String trimmedDirection = direction.trim().toLowerCase();

            if (trimmedDirection.equals("q")) {
                System.out.println("Exiting movement mode.");
                break; // Exit the loop and return to main game loop
            }

            boolean moved = false;
            switch (trimmedDirection) {
                case "w":
                    moved = currentFarmMap.movePlayerUp();
                    break;
                case "a":
                    moved = currentFarmMap.movePlayerLeft();
                    break;
                case "s":
                    moved = currentFarmMap.movePlayerDown();
                    break;
                case "d":
                    moved = currentFarmMap.movePlayerRight();
                    break;
                default:
                    System.out.println("Invalid direction. Use W/A/S/D or Q to quit.");
                    // Don't set moved to true here, as no valid move was made.
                    continue; // Continue to the next iteration of the movement loop
            }
            if (moved) {
                System.out.println("Moved to (" + currentPlayer.getX() + ", " + currentPlayer.getY() + ")");
                // Optionally redisplay map after each move if you want immediate visual feedback
                currentFarmMap.displayFarmMap();
            } else {
                System.out.println("Cannot move there - blocked or out of bounds!");
            }
        }
    }

    private static void showCredits() {
        System.out.println("\n--- Credits ---");
        System.out.println("Spakbor Hills Anjay");
        System.out.println("Developed by: Kelompok K02 paling gachor");
        System.out.println("ubur ubur ikan lele, cli for the win leeee");
        System.out.println("-----------------");
    }


    private static void handleInteraction() {
    boolean interacted = false;
    core.world.DeployedObject interactableObject = null;

        // Find if player is adjacent to any interactable object on the map
        for (core.world.DeployedObject obj : currentFarmMap.getDeployedObjects()) {
            if (isPlayerAdjacentTo(obj)) {
                interactableObject = obj;
                break; // Interact with the first adjacent object found
            }
        }

        if (interactableObject != null) {
            // Handle interaction based on object type
            if (interactableObject.getSymbol() == 's') { // Shipping Bin
                System.out.println("You are next to the shipping bin!");
                if (currentPlayer.getShippingBin() != null) {
                    currentPlayer.getShippingBin().displayContents(); // Display current contents

                    System.out.print("Do you want to add an item to the shipping bin? (y/n): ");
                    try {
                        String addChoice = reader.readLine().trim().toLowerCase();
                        if (addChoice.equals("y")) {
                            currentPlayer.getInventory().showInventory(); // Show player's inventory
                            System.out.print("Enter the name of the item to add: ");
                            String itemName = reader.readLine().trim();

                            System.out.print("Enter the quantity: ");
                            String quantityStr = reader.readLine().trim();
                            int quantity;

                            try {
                                quantity = Integer.parseInt(quantityStr);
                                if (quantity <= 0) {
                                    System.out.println("Quantity must be positive.");
                                } else {
                                    // Get the actual Item object from the player's inventory
                                    item.Item itemToAdd = currentPlayer.getInventory().getItemByName(itemName); 

                                    if (itemToAdd == null) {
                                        System.out.println("You don't have '" + itemName + "' in your inventory.");
                                    } else {
                                        // Attempt to add the item to the shipping bin
                                        boolean success = currentPlayer.getShippingBin().addItem(currentPlayer, itemToAdd, quantity);
                                        if (success) {
                                            // Success message is already handled by ShippingBin.addItem
                                        } else {
                                            // Error message (e.g., not enough items) is already handled by ShippingBin.addItem
                                        }
                                    }
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid quantity. Please enter a number.");
                            }
                        } else {
                            System.out.println("Cancelled adding items to shipping bin.");
                        }
                    } catch (IOException e) {
                        System.err.println("Error reading input for shipping bin: " + e.getMessage());
                    }
                } else {
                    System.out.println("Shipping Bin not set up for player.");
                }
                interacted = true;
            } else if (interactableObject.getSymbol() == 'o') { // Pond
                System.out.println("You are next to the pond! Do you want to fish? (y/n)");
                try {
                    String choice = reader.readLine().trim().toLowerCase();
                    if (choice.equals("y")) {
                        FishingLocation pondLocation = gameFishingLocations.get("Pond");
                        if (pondLocation != null && pondLocation.canFishAt(currentPlayer)) {
                        System.out.println("Starting fishing minigame...");
                        System.out.println("You cast your line into the pond...");
                        Future<?> fishingTask = FishingManager.fish(currentFarmMap, pondLocation, currentPlayer, gameTime, gameCalendar, reader);
                        try {
                            fishingTask.get(); // Wait for the fishing minigame to complete
                        } catch (InterruptedException e) {
                            System.err.println("Fishing minigame interrupted: " + e.getMessage());
                            Thread.currentThread().interrupt(); // Re-interrupt the thread
                        } catch (ExecutionException e) {
                            System.err.println("Fishing minigame failed: " + e.getCause().getMessage());
                        }
                    } else {
                        System.out.println("Cannot fish here under current conditions (e.g., not at the right spot, or conditions not met).");
                    }
                    } else {
                        System.out.println("Fishing cancelled.");
                    }
                } catch (IOException e) {
                    System.err.println("Error reading fishing choice: " + e.getMessage());
                }
                interacted = true;
            } else if (interactableObject.getSymbol() == 'h') { // House
                System.out.println("You are next to your house! (Interaction with house to be implemented)");
                interacted = true;
            } else {
                System.out.println("This object is not interactable in this context.");
            }
        }

        if (!interacted) {
            System.out.println("No interactable objects nearby.");
        }
    }

// Helper method to check if the player is adjacent to a deployed object
    private static boolean isPlayerAdjacentTo(core.world.DeployedObject obj) {
        int px = currentPlayer.getX();
        int py = currentPlayer.getY();
        int objX = obj.getX();
        int objY = obj.getY();
        int objWidth = obj.getWidth();
        int objHeight = obj.getHeight();

        // Check if player is within 1 tile distance of the object's bounding box
        boolean isAdjacent = (px >= objX - 1 && px <= objX + objWidth &&
                            py >= objY - 1 && py <= objY + objHeight);

        // Ensure player is not *inside* the object
        boolean isInside = (px >= objX && px < objX + objWidth &&
                            py >= objY && py < objY + objHeight);

        return isAdjacent && !isInside;
    }
}