package item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central registry for “misc” items: fuel, crafting materials, etc.
 */
public final class Misc {
    private static final Map<String, Item> ITEMS = new ConcurrentHashMap<>();

    static {
        ITEMS.put("Firewood", new Item("Firewood", 10, 5) {
            @Override public String getCategory() { return "Material"; }
        });
        ITEMS.put("Coal",    new Item("Coal",    15, 7) {
            @Override public String getCategory() { return "Material"; }
        });
    }

    private Misc() {}

    public static Map<String, Item> getItems() {
        return ITEMS;
    }
    
    /** Lookup by exact display name. */
    public static Item get(String name) {
        return ITEMS.get(name);
    }

    /** Register any other ad-hoc Item at startup if you wish. */
    public static void register(Item i) {
        ITEMS.put(i.getName(), i);
    }
}
