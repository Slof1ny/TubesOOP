package cooking;

import item.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FuelRegistry {
    private static final Map<String, Fuel> FUELS_BY_NAME = new HashMap<>();

    static {
        FUELS_BY_NAME.put("Firewood", new Firewood());
        FUELS_BY_NAME.put("Coal", new Coal());
    }

    public static Fuel getFuelByName(String name) {
        return FUELS_BY_NAME.get(name);
    }

    public static List<Item> getAllFuelsAsItems() {
        return FUELS_BY_NAME.values().stream()
                            .map(fuel -> {
                                if (fuel instanceof Item) {
                                    return (Item) fuel;
                                }
                                return null;
                            })
                            .filter(java.util.Objects::nonNull)
                            .collect(Collectors.toList());
    }
}