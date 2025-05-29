package item;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Misc {
    private static final Map<String, Item> ITEMS = new ConcurrentHashMap<>();

    static {
        ITEMS.put("Proposal Ring", new Item("Proposal Ring", 1000, 500) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Premium Coffee Bean", new Item("Premium Coffee Bean", 50, 25) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Catnip Supreme", new Item("Catnip Supreme", 70, 35) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Warm Milk", new Item("Warm Milk", 20, 10) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Cheap Coffee Powder", new Item("Cheap Coffee Powder", 5, 2) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Dog Biscuit", new Item("Dog Biscuit", 8, 4) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Egg", new Item("Egg", 10, 5) {
            @Override public String getCategory() { return "Misc"; }
        });
        ITEMS.put("Eggplant", new Item("Eggplant", 0, 60) {
            @Override public String getCategory() { return "Harvested Crop"; } 
        });
    }

    private Misc() {}

    public static Map<String, Item> getItems() {
        return ITEMS;
    }

    public static Item get(String name) {
        return ITEMS.get(name);
    }

    public static void register(Item i) {
        ITEMS.put(i.getName(), i);
    }
}