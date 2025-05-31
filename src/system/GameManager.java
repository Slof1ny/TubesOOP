package system;

import core.house.House;
import core.player.Player;
import core.world.FarmMap;
import core.world.CityMap;
import core.world.GameMap;
import time.GameCalendar;
import time.Time;
import npc.*;
import core.world.Season;
import gui.TopInfoBarPanel;
import fishing.FishingLocation; // Import
import fishing.SpecificFishingLocation; // Import
import fishing.FreeFishingLocation; // Import
import fishing.FishRegistry; // Import
import java.util.Map; // Import
import gui.GameView;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingUtilities;
import core.player.RelationshipStatus; 

import java.util.HashMap; // Import
import java.util.ArrayList; //Import
import java.util.List;
import item.*;
import action.NPCActions;
import core.world.HouseMap;
import  cooking.CookingManager;

public class GameManager {
    private Player player;
    private GameMap currentMap;
    private FarmMap farmMap;
    private CityMap cityMap;
    private GameCalendar gameCalendar;
    private Time gameTime;
    private Store gameStore;
    private TopInfoBarPanel topInfoBarPanel;
    private Map<String, FishingLocation> fishingLocations; // Added to store fishing locations
    private List<NPC> allNpcs;
    private HouseMap houseMap;
    private GameView gameViewInstance;
    private CookingManager cookingManager;
    private boolean statisticsScreenShown = false;

    public GameManager() {
        // Player initialization might be deferred or updated by PlayerCreationPanel
        player = new Player("Dr. Asep Spakbor", "Male"); // Default, will be changed
        player.getGold().add(3500); //
        player.getInventory().addItem(item.SeedRegistry.getSeedByName("Wheat Seeds"), 5); //
        player.getInventory().addItem(new item.Food("Fish n' Chips", 150, 135, 50), 2); //
        Item coalFromRegistry = ItemRegistry.getItemByName("Coal");
        if (coalFromRegistry != null) {
            player.getInventory().addItem(coalFromRegistry, 10);
        } else {
            System.err.println("WARN: GameManager constructor - Coal not found in ItemRegistry. Initial Coal not added.");
        }
        
        // Equip tools - ensure equipment added by Player constructor is accessible by name
        player.equipItem("Hoe"); //
        player.equipItem("Watering Can"); //
        player.equipItem("Pickaxe"); //
        player.equipItem("Fishing Rod"); //

        this.cookingManager = new CookingManager(player);

        farmMap = new FarmMap(player); //
        cityMap = new CityMap(player); //
        houseMap = new HouseMap(player);

        currentMap = farmMap; // Default to farm map
        player.setLocation(farmMap.getName()); // Set initial player location
        // Player position on farmMap is set within farmMap's spawnPlayer

        gameCalendar = new GameCalendar(); //
        gameTime = new Time(gameCalendar, player, this); //

        gameStore = new Store("Emily's Store", new Emily()); //
        player.setShippingBin(new core.world.ShippingBin()); //

        try {
            PriceList.loadPrices("resources/price_list.csv"); //
            System.out.println("PriceList loaded successfully."); //
        } catch (java.io.IOException e) {
            System.err.println("Failed to load price_list.csv: " + e.getMessage()); //
        }

        initializeFishingLocations();
        ItemRegistry.initializeFishItems(fishingLocations); 

        this.allNpcs = new ArrayList<>();
        this.allNpcs.add(new Abigail());
        this.allNpcs.add(new Caroline());
        this.allNpcs.add(new Dasco());
        this.allNpcs.add(new Emily());
        this.allNpcs.add(new MayorTadi());
        this.allNpcs.add(new Orenji());
        this.allNpcs.add(new Perry());

        //gameTime.runTime2(); //
    }

    private void initializeFishingLocations() {
        fishingLocations = new HashMap<>();
        // Pond on the FarmMap
        // Note: The SpecificFishingLocation ties to the farmMap instance.
        // The list of fish for the pond will be populated by FishRegistry.buildAll
        SpecificFishingLocation farmPondLocation = new SpecificFishingLocation("Pond", new ArrayList<>(), farmMap);
        fishingLocations.put("Pond", farmPondLocation);

        // Other locations (not tied to a specific map object, identified by player.getLocation())
        fishingLocations.put("Mountain Lake", new FreeFishingLocation("Mountain Lake", new ArrayList<>(), farmMap));
        fishingLocations.put("Forest River", new FreeFishingLocation("Forest River", new ArrayList<>(), farmMap));
        fishingLocations.put("Ocean", new FreeFishingLocation("Ocean", new ArrayList<>(), farmMap));
        
        FishRegistry.buildAll(fishingLocations); // This populates the possibleFish lists
    }

    public Player getPlayer() { //
        return player;
    }

    public List<NPC> getAllNpcs() { // If this doesn't exist, add it
        return new ArrayList<>(allNpcs); // Return a copy
    }

    public TopInfoBarPanel getTopInfoBarPanel() { // << ADD THIS
        return topInfoBarPanel;
    }

    public void setTopInfoBarPanel(TopInfoBarPanel topInfoBarPanel) { // << ADD THIS
        this.topInfoBarPanel = topInfoBarPanel;
    }

    public CookingManager getCookingManager() { // Added getter
        return cookingManager;
    }

    public GameMap getCurrentMap() { //
        return currentMap;
    }

    public FarmMap getFarmMap() { //
        return farmMap;
    }

    public CityMap getCityMap() { //
        return cityMap;
    }
    
    public HouseMap getHouseMap(){
        return houseMap;
    }

    public GameCalendar getGameCalendar() { //
        return gameCalendar;
    }

    public Time getGameTime() { //
        return gameTime;
    }

    public Store getGameStore() { //
        return gameStore;
    }

    public Map<String, FishingLocation> getFishingLocations() { // Getter for fishing locations
        return fishingLocations;
    }

    public NPC getNpcByName(String name){
        if (allNpcs == null) return null; // Or initialize if needed
        for (NPC npc : allNpcs) {
            if (npc.getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }
        return null;
    }

    public void setGameView(GameView gv) { // <<<<<< 2. ADD THIS SETTER METHOD
        this.gameViewInstance = gv;
    }

    public GameView getGameViewInstance() { 
        return this.gameViewInstance;
    }


    public boolean transitionMap(String destinationMapName) {
        System.out.println("GameManager: Attempting transition to " + destinationMapName);
        if (destinationMapName.equals(farmMap.getName())) {
            currentMap = farmMap;
            // Simpan lokasi sebelumnya sebelum update
            String prevLoc = player.getLocation();
            player.setLocation(farmMap.getName());
            // Jika sebelumnya dari city, masuk ke farm di entry dari city
            if (prevLoc != null && prevLoc.equals(cityMap.getName())) {
                player.setPosition(farmMap.getEntryFromCityX(), farmMap.getEntryFromCityY());
            } else {
                player.setPosition(farmMap.getHouseExitSpawnX(), farmMap.getHouseExitSpawnY());
            }
        } else if (destinationMapName.equals(cityMap.getName())) {
            currentMap = cityMap;
            String prevLoc = player.getLocation();
            player.setLocation(cityMap.getName());
            // Jika sebelumnya dari farm, masuk ke city di entry dari farm
            if (prevLoc != null && prevLoc.equals(farmMap.getName())) {
                // Entry from farm to city is always bottom center (default for your CityMap)
                player.setPosition(cityMap.getSize() / 2, 0);
            } else {
                player.setPosition(cityMap.getSize() / 2, 0); // Default spawn
            }
        } else if (houseMap != null && destinationMapName.equals(houseMap.getName())) { // Check houseMap is not null
            currentMap = houseMap;
            player.setLocation(houseMap.getName());
            // Set player to the defined entry point in HouseMap
            player.setPosition(HouseMap.ENTRY_LOCATION.x, HouseMap.ENTRY_LOCATION.y);
        } else {
            System.err.println("Unknown map for transition: " + destinationMapName);
            return false;
        }
        
        if (getTopInfoBarPanel() != null && getTopInfoBarPanel().isVisible()) {
            getTopInfoBarPanel().refreshInfo();
        }
        System.out.println("Transitioned to " + destinationMapName + ". Player at (" + player.getX() + "," + player.getY() + ")");
        return true;
    }


    public void onGameTimeTick(){
        if(topInfoBarPanel != null && topInfoBarPanel.isVisible()){
            topInfoBarPanel.refreshInfo();
        }
    }

    public void processNewDayUpdates(int currentDayNumber, Season currentSeason, boolean wasYesterdayRainy, int yesterdayDayNumber) {
        System.out.println("GameManager: Processing new day updates for Day " + currentDayNumber + ". Yesterday was rainy: " + wasYesterdayRainy);
        if (farmMap != null) {
            // Pass currentDayNumber for crop growth logic, and whether yesterday was rainy
            // The Crop.newDay() will use wasYesterdayRainy to set lastWateredDay to yesterdayDayNumber if true
            farmMap.updateDailyCropGrowth(currentDayNumber, currentSeason, wasYesterdayRainy);
        }

        // Call NPCActions to increment day for marriage check if applicable
        if (this.player != null && this.gameTime != null) {
            NPCActions tempNpcActions = new NPCActions(this.player, this.gameTime);
            tempNpcActions.incrementDayForMarriageCheck();

        }

        System.out.println("GameManager: Finished processing new day updates.");
    }

    public void forcePlayerSleep() {
        // This method is called for the 2 AM pass out OR -20 energy pass out.
        // The JOptionPane for 2 AM is already shown by Time.java before this is called.
        // The JOptionPane for -20 energy is shown here.

        boolean isFaintFromEnergy = player.getEnergy() <= Player.MIN_ENERGY;

        if (!isFaintFromEnergy) {
            // Jam 2 pagi: autopilot ke bed lewat GUI
            System.out.println("GameManager: Player being forced to sleep due to 02:00 AM time. Will autopilot to bed.");
            SwingUtilities.invokeLater(() -> {
                if (gameViewInstance != null && gameViewInstance.isVisible()) {
                    if (!gameViewInstance.isAutopilotActive()) {
                        JOptionPane.showMessageDialog(gameViewInstance,
                            "It's 2:00 AM! You are exhausted and will be taken home to rest.",
                            "Too Late",
                            JOptionPane.WARNING_MESSAGE);
                        gameViewInstance.startAutopilotForceSleep();
                    }
                } else {
                    // Fallback: no GUI, do instant sleep
                    System.out.println("Player has passed out from exhaustion (02:00 AM, no GUI)!");
                    gameTime.sleep2();
                    if (gameViewInstance != null) {
                        SwingUtilities.invokeLater(() -> gameViewInstance.showScreen("GameScreen"));
                    }
                }
            });
        } else {
            // Energi habis: langsung teleport sleep (instan, tidak autopilot)
            System.out.println("GameManager: Player energy at or below minimum. Forcing instant sleep due to exhaustion.");
            gameTime.sleep2(); // This handles game state: time, day, energy, daily updates.

            // After gameTime.sleep2(), player should be in the house.
            // Update map and player position for the UI.
            if (this.gameViewInstance != null) {
                System.out.println("GameManager: Player slept. Transitioning to HouseScreen.");
                // Ensure currentMap reflects HouseMap in GameManager's state
                this.transitionMap(this.getHouseMap().getName()); 
                // Explicitly set player's position to the house entry point.
                // Player.setLocation should have been updated by transitionMap.
                player.setPosition(HouseMap.ENTRY_LOCATION.x, HouseMap.ENTRY_LOCATION.y);
                // Update the UI to show the HouseScreen
                SwingUtilities.invokeLater(() -> {
                    this.gameViewInstance.showScreen("HouseScreen");
                    if (topInfoBarPanel != null) { // Refresh info bar after sleep and screen change
                        topInfoBarPanel.refreshInfo();
                    }
                });
            } else {
                System.err.println("GameManager.forcePlayerSleep: gameViewInstance is null, cannot switch to HouseScreen and update UI correctly.");
            }
        }
    }

    public void checkMilestonesAndShowStatistics() {
    if (player.getStats().haveMilestonesBeenDisplayed()) { // Use PlayerStats flag
        return;
    }

    boolean goldMilestone = player.getStats().getTotalGoldEarned() >= 17209;
    boolean marriedMilestone = player.getPartner() != null &&
                             player.getRelationshipStatus(player.getPartner()) == RelationshipStatus.MARRIED;

    if (goldMilestone || marriedMilestone) {
        player.getStats().setMilestonesDisplayed(true); // Set the flag
        if (gameViewInstance != null) {
            System.out.println("GameManager: Milestone reached. Showing statistics screen.");
            SwingUtilities.invokeLater(() -> {
                gameViewInstance.showStatisticsScreen();
            });
        } else {
            System.err.println("GameManager: Milestones reached, but GameView instance is null. Cannot show statistics screen.");
        }
    }
}

}