package system;

import core.player.Player;
import core.world.FarmMap;
import core.world.CityMap;
import core.world.GameMap;
import time.GameCalendar;
import time.Time;
import npc.*;
import gui.TopInfoBarPanel;
import fishing.FishingLocation; // Import
import fishing.SpecificFishingLocation; // Import
import fishing.FreeFishingLocation; // Import
import fishing.FishRegistry; // Import
import java.util.Map; // Import
import java.util.HashMap; // Import
import java.util.ArrayList; //Import
import java.util.List;
import item.*;

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

    public GameManager() {
        // Player initialization might be deferred or updated by PlayerCreationPanel
        player = new Player("Dr. Asep Spakbor", "Male"); // Default, will be changed
        player.getGold().add(500); //
        player.getInventory().addItem(item.Seed.getSeedByName("Wheat Seeds"), 5); //
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


    public boolean transitionMap(String destinationMapName) { //
        if (destinationMapName.equals(farmMap.getName())) { //
            currentMap = farmMap; //
            // Player position for farm map is usually set by its spawnPlayer logic or specific transitions
            // For now, let's use a default if coming from city. City->Farm exit logic in CityMapController
            // sets player to farmMap.getName(), then GameScreen is shown which uses currentMap.
            // GameManager's transitionMap for Farm could set a standard entry point.
            // Example: player.setPosition(farmMap.getSize() / 2, farmMap.getSize() -1 ); // Entry from South
            player.setLocation(farmMap.getName()); //
            System.out.println("Transitioned to Farm Map."); //
        } else if (destinationMapName.equals(cityMap.getName())) { //
            currentMap = cityMap; //
            // Example: Player position when entering city from farm.
            // Farm->City exit logic in FarmMapController sets player to cityMap.getName()
            player.setPosition(cityMap.getSize() / 2, 0); // Entry from North (top)
            player.setLocation(cityMap.getName()); //
            System.out.println("Transitioned to City Map."); //
        } else {
            System.out.println("Unknown map: " + destinationMapName); //
            return false;
        }
        
        if (topInfoBarPanel != null) { //
            topInfoBarPanel.refreshInfo(); //
        }
        return true;
    }

    public void onGameTimeTick(){
        if(topInfoBarPanel != null && topInfoBarPanel.isVisible()){
            topInfoBarPanel.refreshInfo();
        }
    }
}