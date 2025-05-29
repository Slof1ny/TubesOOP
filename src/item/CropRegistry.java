package item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CropRegistry {
    protected static final Map<String, Item> HARVESTED_CROPS_BY_NAME = new HashMap<>();

    static {
        HARVESTED_CROPS_BY_NAME.put("Parsnip", new Item("Parsnip", 50, 35) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Cauliflower", new Item("Cauliflower", 200, 150) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Potato", new Item("Potato", 0, 80) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Wheat", new Item("Wheat", 50, 30) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Blueberry", new Item("Blueberry", 150, 40) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Tomato", new Item("Tomato", 90, 60) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Hot Pepper", new Item("Hot Pepper", 0, 40) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Melon", new Item("Melon", 0, 250) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Cranberry", new Item("Cranberry", 0, 25) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Pumpkin", new Item("Pumpkin", 300, 250) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
        HARVESTED_CROPS_BY_NAME.put("Grape", new Item("Grape", 100, 10) {
            @Override public String getCategory() { return "Harvested Crop"; }
        });
    }

    public static Item getHarvestedCropByName(String name) {
        return HARVESTED_CROPS_BY_NAME.get(name);
    }

    public static List<Item> getAllHarvestedCrops() {
        return List.copyOf(HARVESTED_CROPS_BY_NAME.values());
    }
}