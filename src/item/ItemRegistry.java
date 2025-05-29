package item;

import fishing.FishRegistry;
import fishing.FishingLocation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();
    private static Map<String, FishingLocation> gameFishingLocations;

    static {
        for (Map.Entry<String, Item> entry : Misc.getItems().entrySet()) {
            ALL_ITEMS.put(entry.getKey(), entry.getValue());
        }

        for (Seed seed : SeedRegistry.getAllSeeds()) {
            ALL_ITEMS.put(seed.getName(), seed);
        }

        for (Item crop : CropRegistry.getAllHarvestedCrops()) {
            ALL_ITEMS.put(crop.getName(), crop);
        }

        for (Food food : FoodRegistry.getAllFood()) {
            ALL_ITEMS.put(food.getName(), food);
        }

    }

    /**
     * Initializes Fish items, requiring the actual FishingLocation instances from the game world.
     * This method should be called once the game's fishing locations are set up.
     * It ensures Fish objects created by FishRegistry are added to the central ItemRegistry.
     * @param fishingLocations A map of fishing location names to their instances.
     */
    public static void initializeFishItems(Map<String, FishingLocation> fishingLocations) {
        if (gameFishingLocations == null) { 
            gameFishingLocations = fishingLocations;
            List<Fish> allFish = FishRegistry.buildAll(gameFishingLocations);
            for (Fish fish : allFish) {
                ALL_ITEMS.put(fish.getName(), fish);
            }
        }
    }

    public static Item getItemByName(String name) {
        return ALL_ITEMS.get(name);
    }

    public static List<Item> getAllItems() {
        return new ArrayList<>(ALL_ITEMS.values());
    }
}