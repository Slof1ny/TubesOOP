// Path: TubesOOP/src/cooking/RecipeData.java
package cooking;

import recipe.Recipe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
// Item imports like Misc, CropRegistry, FoodRegistry are no longer directly needed here for Map.of keys/values
// as we will be using Strings. ItemRegistry will be used by CookingManager later.

public class RecipeData {
    private static final List<Recipe> ALL_RECIPES = new ArrayList<>();

    static {
        // Ingredients are now Map<String, Integer> (ItemName -> Quantity)
        // Output is String (ItemName)

        // recipe_1: Fish n' Chips [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_1", "Fish n' Chips",
                Map.of(
                        "ANY_FISH", 2,       // Special key for any fish
                        "Wheat", 1,
                        "Potato", 1
                ),
                "Fish n' Chips", 1, UnlockCondition.STORE_BOUGHT, "Toko Emily"));

        // recipe_2: Baguette [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_2", "Baguette",
                Map.of(
                        "Wheat", 3
                ),
                "Baguette", 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_3: Sashimi [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_3", "Sashimi",
                Map.of(
                        "Salmon", 3 // Specific fish name
                ),
                "Sashimi", 1, UnlockCondition.FISHING_MILESTONE, "Memancing 10 ikan"));

        // recipe_4: Fugu [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_4", "Fugu",
                Map.of(
                        "Pufferfish", 1 // Specific fish name
                ),
                "Fugu", 1, UnlockCondition.OBTAIN_ITEM, "Memancing Pufferfish"));

        // recipe_5: Wine [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_5", "Wine",
                Map.of(
                        "Grape", 2
                ),
                "Wine", 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_6: Pumpkin Pie [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_6", "Pumpkin Pie",
                Map.of(
                        "Egg", 1,          // Assuming "Egg" is a defined item name in Misc or ItemRegistry
                        "Wheat", 1,
                        "Pumpkin", 1
                ),
                "Pumpkin Pie", 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_7: Veggie Soup [cite: 221]
        ALL_RECIPES.add(new Recipe("recipe_7", "Veggie Soup",
                Map.of(
                        "Cauliflower", 1,
                        "Parsnip", 1,
                        "Potato", 1,
                        "Tomato", 1
                ),
                "Veggie Soup", 1, UnlockCondition.HARVEST_MILESTONE, "Memanen pertama kali"));

        // recipe_8: Fish Stew [cite: 224]
        ALL_RECIPES.add(new Recipe("recipe_8", "Fish Stew",
                Map.of(
                        "ANY_FISH", 2,
                        "Hot Pepper", 1,
                        "Cauliflower", 2
                ),
                "Fish Stew", 1, UnlockCondition.OBTAIN_ITEM, "Mendapatkan Hot Pepper"));

        // recipe_9: Spakbor Salad [cite: 224]
        ALL_RECIPES.add(new Recipe("recipe_9", "Spakbor Salad",
                Map.of(
                        "Melon", 1,
                        "Cranberry", 1,
                        "Blueberry", 1,
                        "Tomato", 1
                ),
                "Spakbor Salad", 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_10: Fish Sandwich [cite: 224]
        ALL_RECIPES.add(new Recipe("recipe_10", "Fish Sandwich",
                Map.of(
                        "ANY_FISH", 1,
                        "Wheat", 2,
                        "Tomato", 1,
                        "Hot Pepper", 1
                ),
                "Fish Sandwich", 1, UnlockCondition.STORE_BOUGHT, "Toko Emily"));

        // recipe_11: The Legends of Spakbor [cite: 224]
        ALL_RECIPES.add(new Recipe("recipe_11", "The Legends of Spakbor",
                Map.of(
                        "Legend", 1,       // Specific legendary fish name
                        "Potato", 2,
                        "Parsnip", 1,
                        "Tomato", 1,
                        "Eggplant", 1      // Assuming "Eggplant" is a defined item name
                ),
                "The Legends of Spakbor", 1, UnlockCondition.OBTAIN_ITEM, "Memancing Legend Fish"));
    }

    public static List<Recipe> getAllRecipes() {
        return Collections.unmodifiableList(ALL_RECIPES);
    }

    public static Recipe getRecipeById(String recipeId) {
        return ALL_RECIPES.stream()
                .filter(recipe -> recipe.getRecipeId().equals(recipeId))
                .findFirst()
                .orElse(null);
    }
}