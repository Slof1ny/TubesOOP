package item;

import core.world.Season;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeedRegistry {
    private static final Map<String, Seed> SEEDS_BY_NAME = new HashMap<>();

    static {
        // Spring Seeds
        SEEDS_BY_NAME.put("Parsnip Seeds", new Seed("Parsnip Seeds", 20, Arrays.asList(Season.SPRING), 1, 1));
        SEEDS_BY_NAME.put("Cauliflower Seeds", new Seed("Cauliflower Seeds", 80, Arrays.asList(Season.SPRING), 5, 1));
        SEEDS_BY_NAME.put("Potato Seeds", new Seed("Potato Seeds", 50, Arrays.asList(Season.SPRING), 3, 1));
        SEEDS_BY_NAME.put("Wheat Seeds", new Seed("Wheat Seeds", 60, Arrays.asList(Season.SPRING, Season.FALL), 1, 3));

        // Summer Seeds
        SEEDS_BY_NAME.put("Blueberry Seeds", new Seed("Blueberry Seeds", 80, Arrays.asList(Season.SUMMER), 7, 3));
        SEEDS_BY_NAME.put("Tomato Seeds", new Seed("Tomato Seeds", 50, Arrays.asList(Season.SUMMER), 3, 1));
        SEEDS_BY_NAME.put("Hot Pepper Seeds", new Seed("Hot Pepper Seeds", 40, Arrays.asList(Season.SUMMER), 1, 1));
        SEEDS_BY_NAME.put("Melon Seeds", new Seed("Melon Seeds", 80, Arrays.asList(Season.SUMMER), 4, 1));

        // Fall Seeds
        SEEDS_BY_NAME.put("Cranberry Seeds", new Seed("Cranberry Seeds", 100, Arrays.asList(Season.FALL), 2, 10));
        SEEDS_BY_NAME.put("Pumpkin Seeds", new Seed("Pumpkin Seeds", 150, Arrays.asList(Season.FALL), 7, 1));
        // Wheat Seeds are already added for Spring and Fall
        SEEDS_BY_NAME.put("Grape Seeds", new Seed("Grape Seeds", 60, Arrays.asList(Season.FALL), 3, 20));
    }

    public static Seed getSeedByName(String name) {
        return SEEDS_BY_NAME.get(name);
    }

    public static List<Seed> getAllSeeds() {
        return List.copyOf(SEEDS_BY_NAME.values());
    }
}