package item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoodRegistry {
    private static final Map<String, Food> FOOD_BY_NAME = new HashMap<>();

    static {
        FOOD_BY_NAME.put("Fish n' Chips", new Food("Fish n' Chips", 150, 135, 50));
        FOOD_BY_NAME.put("Baguette", new Food("Baguette", 100, 80, 25));
        FOOD_BY_NAME.put("Sashimi", new Food("Sashimi", 300, 275, 70));
        FOOD_BY_NAME.put("Fugu", new Food("Fugu", 0, 135, 50));
        FOOD_BY_NAME.put("Wine", new Food("Wine", 100, 90, 20));
        FOOD_BY_NAME.put("Pumpkin Pie", new Food("Pumpkin Pie", 120, 100, 35));
        FOOD_BY_NAME.put("Veggie Soup", new Food("Veggie Soup", 140, 120, 40));
        FOOD_BY_NAME.put("Fish Stew", new Food("Fish Stew", 280, 260, 70));
        FOOD_BY_NAME.put("Spakbor Salad", new Food("Spakbor Salad", 0, 250, 70));
        FOOD_BY_NAME.put("Fish Sandwich", new Food("Fish Sandwich", 200, 180, 50));
        FOOD_BY_NAME.put("The Legends of Spakbor", new Food("The Legends of Spakbor", 0, 2000, 100));
        FOOD_BY_NAME.put("Cooked Pig's Head", new Food("Cooked Pig's Head", 1000, 0, 100));
    }

    public static Food getFoodByName(String name) {
        return FOOD_BY_NAME.get(name);
    }

    public static List<Food> getAllFood() {
        return List.copyOf(FOOD_BY_NAME.values());
    }
}