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

import java.util.HashMap; // Import
import java.util.ArrayList; //Import
import java.util.List;
import item.*;
import action.NPCActions;
import core.world.HouseMap;

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

    public GameManager() {
        // Player initialization might be deferred or updated by PlayerCreationPanel
        player = new Player("Dr. Asep Spakbor", "Male"); // Default, will be changed
        player.getGold().add(500); //
        player.getInventory().addItem(item.SeedRegistry.getSeedByName("Wheat Seeds"), 5); //
        player.getInventory().addItem(new item.Food("Fish n' Chips", 150, 135, 50), 2); //
        player.getInventory().addItem(new item.Item("Coal", 30, 20) {
                @Override public String getCategory() { return "Misc"; }
        }, 10);
        
        // Equip tools - ensure equipment added by Player constructor is accessible by name
        player.equipItem("Hoe"); //
        player.equipItem("Watering Can"); //
        player.equipItem("Pickaxe"); //
        player.equipItem("Fishing Rod"); //

        this.allNpcs = new ArrayList<>();
        this.allNpcs.add(new Abigail());
        this.allNpcs.add(new Caroline());
        this.allNpcs.add(new Dasco());
        this.allNpcs.add(new Emily());
        this.allNpcs.add(new MayorTadi());
        this.allNpcs.add(new Orenji());
        this.allNpcs.add(new Perry());


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

        initializeFishingLocations(); // New method to set up fishing

        gameTime.runTime2(); //
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

    // public PlayerInfoPanel getPlayerInfoPanel() { //
    //     return playerInfoPanel;
    // }

    // public void setPlayerInfoPanel(PlayerInfoPanel playerInfoPanel) { //
    //     this.playerInfoPanel = playerInfoPanel;
    // }

    public TopInfoBarPanel getTopInfoBarPanel() { // << ADD THIS
        return topInfoBarPanel;
    }

    public void setTopInfoBarPanel(TopInfoBarPanel topInfoBarPanel) { // << ADD THIS
        this.topInfoBarPanel = topInfoBarPanel;
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


    public boolean transitionMap(String destinationMapName) {
        System.out.println("GameManager: Attempting transition to " + destinationMapName);
        if (destinationMapName.equals(farmMap.getName())) {
            currentMap = farmMap;
            player.setLocation(farmMap.getName());
            player.setPosition(farmMap.getHouseExitSpawnX(), farmMap.getHouseExitSpawnY()); // Example
        } else if (destinationMapName.equals(cityMap.getName())) {
            currentMap = cityMap;
            player.setLocation(cityMap.getName());
            player.setPosition(cityMap.getSize() / 2, 0); // Example
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
        if (player.getEnergy() <= Player.MIN_ENERGY) {
            System.out.println("GameManager: Player energy at or below minimum. Forcing sleep.");
            // Display a message to the player via GUI
            // This needs to be done on the EDT
            SwingUtilities.invokeLater(() -> {
                if (gameViewInstance != null && gameViewInstance.isVisible()) { // Check if gameViewInstance is set and visible
                    JOptionPane.showMessageDialog(gameViewInstance,
                        "You've exhausted all your energy and passed out!",
                        "Exhausted",
                        JOptionPane.WARNING_MESSAGE);
                } else { // Fallback if GUI context isn't readily available
                    System.out.println("Player has passed out from exhaustion!");
                }
            });


            gameTime.sleep2();

            if (this.gameViewInstance != null) {
            System.out.println("GameManager: Player fainted. Transitioning to HouseScreen.");
            // Transition the game state to HouseMap first
            this.transitionMap(this.getHouseMap().getName()); // This sets currentMap and player location/position

            // Then show the HouseScreen in the GUI
            SwingUtilities.invokeLater(() -> this.gameViewInstance.showScreen("HouseScreen"));
        }
        }
    }
}