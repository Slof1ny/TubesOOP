package test;

import core.world.FarmMap;
import core.player.Player;
import core.world.Season;
import core.world.Weather;
import fishing.*;
import item.Fish;
import time.GameCalendar;
import time.Time;
import system.StatisticsManager;

import java.util.*;
import java.util.concurrent.Future;

// javac -cp src -d out test/FishingTester.java
// java -cp out test.FishingTester

public class FishingTester {
    public static void main(String[] args) {
        // Create a test player with inventory
        TestPlayer player = new TestPlayer("Fisher", "MALE");
        
        // Create time and calendar systems with test calendar
        TestGameCalendar calendar = new TestGameCalendar();
        Time time = new Time(calendar);
        
        // Create farm map
        FarmMap farm = new FarmMap(player);
        
        // Set the farm map reference to prevent position resets
        player.setFarmMapReference(farm);
        
        // Create fishing locations
        Map<String, FishingLocation> fishingLocations = createFishingLocations(farm);
        
        // Create fish registry
        List<Fish> allFish = FishRegistry.buildAll(fishingLocations); // Pass the map here
        
        // Add fish to locations - FIXED: Logic handled within FishRegistry.buildAll or refined here
        // setupFishInLocations(fishingLocations, allFish, time, calendar); // This method is no longer needed in its previous form

        System.out.println("=== Fishing Test Demo ===");
        System.out.println("Current Season: " + calendar.getCurrentSeason());
        System.out.println("Current Weather: " + calendar.getCurrentWeather());
        System.out.println("Current Time: " + String.format("%02d:%02d", time.getHour(), time.getMinute()));
        System.out.println("Player Energy: " + player.getEnergy());
        System.out.println();
        
        // Display the farm map
        System.out.println("=== Farm Map ===");
        System.out.println("Legend: P = Player, h = House, o = Pond, s = Shipping Bin, . = Untilled");
        farm.displayFarmMap();
        System.out.println();
        
        // Interactive fishing test
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("\n=== Fishing Test Menu ===");
            System.out.println("1. Move player (WASD)");
            System.out.println("2. Try fishing at current location");
            System.out.println("3. Visit and fish at Mountain Lake");
            System.out.println("4. Visit and fish at Forest River");
            System.out.println("5. Visit and fish at Ocean");
            System.out.println("6. Change time");
            System.out.println("7. Change weather");
            System.out.println("8. Change season");
            System.out.println("9. Show current status");
            System.out.println("10. Show inventory");
            System.out.println("0. Quit");
            System.out.print("Choose option: ");
            
            String input = scanner.nextLine().trim();
            
            switch (input) {
                case "1":
                    handleMovement(farm, player, scanner);
                    break;
                case "2":
                    testFishingAtCurrentLocation(farm, fishingLocations, player, time, calendar, scanner);
                    break;
                case "3":
                    // Simulate visiting Mountain Lake
                    player.setLocation("Mountain Lake");
                    testFishing(farm, fishingLocations.get("Mountain Lake"), player, time, calendar, scanner);
                    break;
                case "4":
                    // Simulate visiting Forest River
                    player.setLocation("Forest River");
                    testFishing(farm, fishingLocations.get("Forest River"), player, time, calendar, scanner);
                    break;
                case "5":
                    // Simulate visiting Ocean
                    player.setLocation("Ocean");
                    testFishing(farm, fishingLocations.get("Ocean"), player, time, calendar, scanner);
                    break;
                case "6":
                    changeTime(time, scanner);
                    break;
                case "7":
                    changeWeather(calendar, scanner);
                    break;
                case "8":
                    changeSeason(calendar, scanner);
                    break;
                case "9":
                    showStatus(player, time, calendar);
                    break;
                case "10":
                    showInventory(player);
                    break;
                case "0":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
    
    private static void handleMovement(FarmMap farm, TestPlayer player, Scanner scanner) {
        System.out.println("Current position: (" + player.getX() + ", " + player.getY() + ")");
        System.out.print("Move (WASD): ");
        String move = scanner.nextLine().trim().toLowerCase();
        
        boolean moved = false;
        switch (move) {
            case "w":
                moved = farm.movePlayerUp();
                break;
            case "a":
                moved = farm.movePlayerLeft();
                break;
            case "s":
                moved = farm.movePlayerDown();
                break;
            case "d":
                moved = farm.movePlayerRight();
                break;
            default:
                System.out.println("Invalid move!");
                return;
        }
        
        if (moved) {
            System.out.println("Moved to (" + player.getX() + ", " + player.getY() + ")");
            farm.displayFarmMap();
        } else {
            System.out.println("Can't move there!");
        }
    }
    
    private static void testFishingAtCurrentLocation(FarmMap farm, Map<String, FishingLocation> locations, TestPlayer player, Time time, TestGameCalendar calendar, Scanner scanner) {
        System.out.println("\n=== Checking Fishing at Current Location ===");
        System.out.println("Player position: (" + player.getX() + ", " + player.getY() + ")");
        
        // Check if player is at pond on farm map
        if (player.getLocation().equals("Farm Map")) {
            FishingLocation pondLocation = locations.get("Pond");
            if (pondLocation.canFishAt(player)) {
                System.out.println("You are adjacent to the pond and can fish here!");
                testFishing(farm, pondLocation, player, time, calendar, scanner);
            } else {
                System.out.println("You need to be adjacent to the pond to fish. Move next to a pond tile (marked with 'o').");
                
                // Show nearby tiles for debugging
                System.out.println("Nearby tiles:");
                int px = player.getX();
                int py = player.getY();
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dx = -1; dx <= 1; dx++) {
                        int checkX = px + dx;
                        int checkY = py + dy;
                        if (checkX >= 0 && checkX < FarmMap.SIZE && checkY >= 0 && checkY < FarmMap.SIZE) {
                            char tileChar = farm.getTileAt(checkX, checkY).displayChar();
                            if (dx == 0 && dy == 0) {
                                System.out.print("[P] ");
                            } else {
                                System.out.print(" " + tileChar + " ");
                            }
                        } else {
                            System.out.print(" X ");
                        }
                    }
                    System.out.println();
                }
            }
        } else {
            // Player is at some other location
            FishingLocation currentLocation = locations.get(player.getLocation());
            if (currentLocation != null && currentLocation.canFishAt(player)) {
                System.out.println("You can fish at " + player.getLocation() + "!");
                testFishing(farm, currentLocation, player, time, calendar, scanner);
            } else {
                System.out.println("You cannot fish at your current location: " + player.getLocation());
            }
        }
    }
    
    private static void testFishing(FarmMap farm, FishingLocation location, TestPlayer player, Time time, TestGameCalendar calendar, Scanner scanner) {
        System.out.println("\n=== Attempting to Fish at " + location.getClass().getSimpleName() + " ===");
        
        // First check if the player can fish at this location
        if (!location.canFishAt(player)) {
            System.out.println("You cannot fish at this location from your current position.");
            return;
        }
        
        // Show available fish at this location
        List<Fish> possibleFish = location.getPossibleFish(calendar.getCurrentSeason(), time, calendar.getCurrentWeather(), location);
        System.out.println("Fish available at this time/weather/season: " + possibleFish.size());
        for (Fish fish : possibleFish) {
            System.out.println("  - " + fish.getName() + " (" + fish.getType() + ")");
        }
        
        if (possibleFish.isEmpty()) {
            System.out.println("No fish available right now!");
            return;
        }
        
        System.out.print("Proceed with fishing? (y/n): ");
        String proceed = scanner.nextLine().trim().toLowerCase();
        
        if (proceed.equals("y")) {
            Future<?> fishingResult = FishingManager.fish(farm, location, player, time, calendar, scanner);
            try {
                fishingResult.get(); // Wait for fishing to complete
            } catch (Exception e) {
                System.out.println("Fishing interrupted: " + e.getMessage());
            }
        }
    }
    
    private static void changeTime(Time time, Scanner scanner) {
        System.out.println("Current time: " + String.format("%02d:%02d", time.getHour(), time.getMinute()));
        System.out.print("Enter new hour (0-23): ");
        try {
            int hour = Integer.parseInt(scanner.nextLine().trim());
            if (hour >= 0 && hour <= 23) {
                System.out.print("Enter new minute (0-59): ");
                int minute = Integer.parseInt(scanner.nextLine().trim());
                if (minute >= 0 && minute <= 59) {
                    time.skipTo(hour, minute);
                    System.out.println("Time changed to: " + String.format("%02d:%02d", time.getHour(), time.getMinute()));
                } else {
                    System.out.println("Invalid minute!");
                }
            } else {
                System.out.println("Invalid hour!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid number!");
        }
    }
    
    private static void changeWeather(TestGameCalendar calendar, Scanner scanner) {
        System.out.println("Current weather: " + calendar.getCurrentWeather());
        System.out.println("1. SUNNY");
        System.out.println("2. RAINY");
        System.out.print("Choose weather (1-2): ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                calendar.setWeather(Weather.SUNNY);
                System.out.println("Weather changed to SUNNY");
                break;
            case "2":
                calendar.setWeather(Weather.RAINY);
                System.out.println("Weather changed to RAINY");
                break;
            default:
                System.out.println("Invalid choice");
        }
    }
    
    private static void changeSeason(TestGameCalendar calendar, Scanner scanner) {
        System.out.println("Current season: " + calendar.getCurrentSeason());
        System.out.println("1. SPRING");
        System.out.println("2. SUMMER");
        System.out.println("3. FALL");
        System.out.println("4. WINTER");
        System.out.print("Choose season (1-4): ");
        
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1":
                calendar.setSeason(Season.SPRING);
                System.out.println("Season changed to SPRING");
                break;
            case "2":
                calendar.setSeason(Season.SUMMER);
                System.out.println("Season changed to SUMMER");
                break;
            case "3":
                calendar.setSeason(Season.FALL);
                System.out.println("Season changed to FALL");
                break;
            case "4":
                calendar.setSeason(Season.WINTER);
                System.out.println("Season changed to WINTER");
                break;
            default:
                System.out.println("Invalid choice");
        }
    }
    
    private static void showStatus(TestPlayer player, Time time, TestGameCalendar calendar) {
        System.out.println("\n=== Current Status ===");
        System.out.println("Player: " + player.getName());
        System.out.println("Location: " + player.getLocation());
        System.out.println("Position: (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("Energy: " + player.getEnergy());
        System.out.println("Time: " + String.format("%02d:%02d", time.getHour(), time.getMinute()));
        System.out.println("Day: " + calendar.getDayInSeason() + " of " + calendar.getCurrentSeason());
        System.out.println("Weather: " + calendar.getCurrentWeather());
        System.out.println("Night Mode: " + (time.isNight() ? "Yes" : "No"));
    }
    
    private static void showInventory(TestPlayer player) {
        System.out.println("\n=== Inventory ===");
        var items = player.getTestInventory().getAllItems();

        if (items.isEmpty()) {
            System.out.println("Inventory is empty");
        } else {
            for (var entry : items.entrySet()) {
                System.out.println(entry.getKey().getName() + " x" + entry.getValue());
            }
        }
    }
    
    private static Map<String, FishingLocation> createFishingLocations(FarmMap farm) {
        Map<String, FishingLocation> locations = new HashMap<>();
        
        // Create a pond location that checks adjacency properly
        FishingLocation pondLocation = new SpecificFishingLocation("Pond", new ArrayList<>(), farm) {
            @Override
            public boolean canFishAt(Player player) {
                if (!player.getLocation().equals("Farm Map")) {
                    return false;
                }
                
                // Check if player is adjacent to any pond tile
                int px = player.getX();
                int py = player.getY();
                
                // Check all tiles around the player for pond tiles
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        if (dx == 0 && dy == 0) continue; // Skip player's own position
                        
                        int checkX = px + dx;
                        int checkY = py + dy;
                        
                        if (checkX >= 0 && checkX < FarmMap.SIZE && checkY >= 0 && checkY < FarmMap.SIZE) {
                            core.world.Tile tile = farm.getTileAt(checkX, checkY);
                            if (tile != null && tile.displayChar() == 'o') {
                                return true; // Found pond tile adjacent to player
                            }
                        }
                    }
                }
                
                return false; // No pond tile found adjacent to player
            }
        };
        
        locations.put("Pond", pondLocation);
        locations.put("Mountain Lake", new FreeFishingLocation("Mountain Lake", new ArrayList<>(), farm));
        locations.put("Forest River", new FreeFishingLocation("Forest River", new ArrayList<>(), farm));
        locations.put("Ocean", new FreeFishingLocation("Ocean", new ArrayList<>(), farm));

        // Debug: Print initial fish counts for each location
        System.out.println("=== Initial Fish Distribution Debug (from FishingTester) ===");
        for (Map.Entry<String, FishingLocation> entry : locations.entrySet()) {
            String locationName = entry.getKey();
            FishingLocation location = entry.getValue();
            // Using a dummy time and calendar to just get all possible fish
            System.out.println(locationName + " (before buildAll processing): " + location.getPossibleFish(Season.SPRING, new Time(new GameCalendar()), Weather.SUNNY, location).size() + " total fish types");
        }

        return locations;
    }
    
    // Extended GameCalendar for testing purposes
    private static class TestGameCalendar extends GameCalendar {
        public TestGameCalendar() {
            super();
        }
        
        public TestGameCalendar(StatisticsManager data) {
            super(data);
        }
        
        // Add methods to directly set weather and season for testing
        public void setWeather(Weather weather) {
            // Use reflection to access private field, or modify the original class
            try {
                java.lang.reflect.Field weatherField = GameCalendar.class.getDeclaredField("currentWeather");
                weatherField.setAccessible(true);
                weatherField.set(this, weather);
            } catch (Exception e) {
                System.err.println("Could not set weather: " + e.getMessage());
            }
        }
        
        public void setSeason(Season season) {
            // Use reflection to access private field, or modify the original class
            try {
                java.lang.reflect.Field seasonField = GameCalendar.class.getDeclaredField("currentSeason");
                seasonField.setAccessible(true);
                seasonField.set(this, season);
            } catch (Exception e) {
                System.err.println("Could not set season: " + e.getMessage());
            }
        }
    }
    
    // Test player implementation
    private static class TestPlayer extends Player {
        private int x = 5, y = 5; // Start in middle of map
        private String currentLocation = "Farm Map";
        private TestInventory inventory = new TestInventory();
        private FarmMap farmMapReference; // Keep reference to avoid recreating
        private core.player.Inventory gameInventory; // Single inventory instance
        
        public TestPlayer(String name, String gender) {
            super(name, gender);
            // Create the game inventory once and keep it
            this.gameInventory = new core.player.Inventory(this.getStats(), this.getEquipmentManager()) {
                @Override
                public void addItem(item.Item item, int quantity) {
                    inventory.addItem(item, quantity);
                }
            };
        }
        
        public void setFarmMapReference(FarmMap farm) {
            this.farmMapReference = farm;
        }
        
        @Override
        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
        
        @Override
        public int getX() {
            return x;
        }
        
        @Override
        public int getY() {
            return y;
        }
        
        @Override
        public String getLocation() {
            return currentLocation;
        }
        
        public void setLocation(String location) {
            this.currentLocation = location;
        }
        
        @Override
        public core.world.Tile getCurrentTile() {
            if (currentLocation.equals("Farm Map") && farmMapReference != null) {
                return farmMapReference.getTileAt(x, y);
            }
            return null; // For other locations
        }
        
        @Override
        public core.player.Inventory getInventory() {
            // Always return the same inventory instance to avoid re-initialization
            return gameInventory;
        }
        
        public TestInventory getTestInventory() {
            return inventory;
        }
    }
    
    // Simple test inventory
    private static class TestInventory {
        private Map<item.Item, Integer> items = new HashMap<>();
        
        public void addItem(item.Item item, int quantity) {
            items.put(item, items.getOrDefault(item, 0) + quantity);
            System.out.println("Added " + quantity + " " + item.getName() + " to inventory");
        }
        
        public Map<item.Item, Integer> getAllItems() {
            return new HashMap<>(items);
        }
    }
}