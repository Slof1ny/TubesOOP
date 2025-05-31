package item;

import fishing.FishRegistry;
import fishing.FishingLocation;
import cooking.FuelRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
    private static final Map<String, Item> ALL_ITEMS = new HashMap<>();
    private static Map<String, FishingLocation> gameFishingLocations = null;

    static {
        // Register Misc items
        for (Map.Entry<String, Item> entry : Misc.getItems().entrySet()) {
            ALL_ITEMS.put(entry.getKey(), entry.getValue());
        }

        // Register Fuel items
        for (Item fuel : FuelRegistry.getAllFuelsAsItems()) {
            ALL_ITEMS.put(fuel.getName(), fuel);
        }

        // Register Seeds
        for (Seed seed : SeedRegistry.getAllSeeds()) {
            ALL_ITEMS.put(seed.getName(), seed);
        }

        // Register Harvested Crops
        for (Item crop : CropRegistry.getAllHarvestedCrops()) {
            ALL_ITEMS.put(crop.getName(), crop);
        }

        // Register Food items
        for (Food food : FoodRegistry.getAllFood()) {
            ALL_ITEMS.put(food.getName(), food);
        }

        // Fish items are NOT registered here, because fishing locations are not available at static init.
        // They must be registered at runtime via initializeFishItems().
    }

    public static void initializeFishItems(Map<String, FishingLocation> fishingLocations) {
        // Always update fish items, even if called multiple times, to ensure registry is up to date.
        gameFishingLocations = fishingLocations;
        List<Fish> allFish = FishRegistry.buildAll(gameFishingLocations);
        for (Fish fish : allFish) {
            ALL_ITEMS.put(fish.getName(), fish);
        }
    }

    public static Item getItemByName(String name) {
        return ALL_ITEMS.get(name);
    }

    public static List<Item> getAllItems() {
        return new ArrayList<>(ALL_ITEMS.values());
    }
}