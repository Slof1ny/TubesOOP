package cooking;

import recipe.Recipe;
import cooking.UnlockCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import item.Item;
import item.Food;
import item.Crop;
import item.Fish;
import item.Seed;
import item.Misc;

/**
 * Kelas untuk menyimpan dan mengelola semua resep dalam game.
 */
public class RecipeData {
    private static final List<Recipe> ALL_RECIPES = new ArrayList<>();

    static {
        // Inisialisasi semua resep di sini
        // Format bahan: Map.of("ITEM_ID_BAHAN_1", JUMLAH, "ITEM_ID_BAHAN_2", JUMLAH)
        // Format output: "ITEM_ID_MAKANAN_HASIL", JUMLAH_HASIL

        // recipe_1: Fish n’ Chips
        // NOTE: ANY_FISH is a placeholder, you may want to handle it specially in your game logic
        ALL_RECIPES.add(new Recipe("recipe_1", "Fish n’ Chips",
                Map.of(
                        new Misc("Any Fish", 0, 0), 2, // Placeholder for ANY_FISH
                        new Crop("Wheat", 10, 8, 5), 1,
                        new Crop("Potato", 50, 40, 3), 1
                ),
                new Food("Fish n' Chips", 150, 135, 50), 1, UnlockCondition.STORE_BOUGHT, "Toko Emily"));

        // recipe_2: Baguette
        ALL_RECIPES.add(new Recipe("recipe_2", "Baguette",
                Map.of(
                        new Crop("Wheat", 10, 8, 5), 3
                ),
                new Food("Baguette", 100, 80, 25), 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_3: Sashimi
        ALL_RECIPES.add(new Recipe("recipe_3", "Sashimi",
                Map.of(
                        new Fish("Salmon", 0, 90, null, null, null, null, null), 3
                ),
                new Food("Sashimi", 300, 275, 70), 1, UnlockCondition.FISHING_MILESTONE, "Memancing 10 ikan"));
        
        // recipe_4: Fugu
        ALL_RECIPES.add(new Recipe("recipe_4", "Fugu",
                Map.of(
                        new Fish("Pufferfish", 0, 135, null, null, null, null, null), 1
                ),
                new Food("Fugu", 0, 135, 50), 1, UnlockCondition.OBTAIN_ITEM, "Memancing Pufferfish"));

        // recipe_5: Wine
        ALL_RECIPES.add(new Recipe("recipe_5", "Wine",
                Map.of(
                        new Crop("Grape", 60, 50, 2), 2
                ),
                new Food("Wine", 100, 90, 20), 1, UnlockCondition.DEFAULT, "Resep bawaan"));
        
        // recipe_6: Pumpkin Pie
        ALL_RECIPES.add(new Recipe("recipe_6", "Pumpkin Pie",
                Map.of(
                        new Misc("Egg", 0, 0), 1, // Placeholder for egg
                        new Crop("Wheat", 10, 8, 5), 1,
                        new Crop("Pumpkin", 150, 120, 1), 1
                ),
                new Food("Pumpkin Pie", 120, 100, 35), 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_7: Veggie Soup
        ALL_RECIPES.add(new Recipe("recipe_7", "Veggie Soup",
                Map.of(
                        new Crop("Cauliflower", 80, 60, 1), 1,
                        new Crop("Parsnip", 20, 15, 1), 1,
                        new Crop("Potato", 50, 40, 3), 1,
                        new Crop("Tomato", 50, 35, 3), 1
                ),
                new Food("Veggie Soup", 140, 120, 40), 1, UnlockCondition.HARVEST_MILESTONE, "Memanen pertama kali"));

        // recipe_8: Fish Stew
        ALL_RECIPES.add(new Recipe("recipe_8", "Fish Stew",
                Map.of(
                        new Misc("Any Fish", 0, 0), 2, // Placeholder for ANY_FISH
                        new Crop("Hot Pepper", 40, 30, 1), 1,
                        new Crop("Cauliflower", 80, 60, 1), 2
                ),
                new Food("Fish Stew", 280, 260, 70), 1, UnlockCondition.OBTAIN_ITEM, "Mendapatkan Hot Pepper"));

        // recipe_9: Spakbor Salad
        ALL_RECIPES.add(new Recipe("recipe_9", "Spakbor Salad",
                Map.of(
                        new Crop("Melon", 80, 60, 1), 1,
                        new Crop("Cranberry", 100, 80, 1), 1,
                        new Crop("Blueberry", 80, 60, 1), 1,
                        new Crop("Tomato", 50, 35, 3), 1
                ),
                new Food("Spakbor Salad", 0, 250, 70), 1, UnlockCondition.DEFAULT, "Resep bawaan"));
        
        // recipe_10: Fish Sandwich
        ALL_RECIPES.add(new Recipe("recipe_10", "Fish Sandwich",
                Map.of(
                        new Misc("Any Fish", 0, 0), 1, // Placeholder for ANY_FISH
                        new Crop("Wheat", 10, 8, 5), 2,
                        new Crop("Tomato", 50, 35, 3), 1,
                        new Crop("Hot Pepper", 40, 30, 1), 1
                ),
                new Food("Fish Sandwich", 200, 180, 50), 1, UnlockCondition.STORE_BOUGHT, "Toko Emily"));

        // recipe_11: The Legends of Spakbor
        ALL_RECIPES.add(new Recipe("recipe_11", "The Legends of Spakbor",
                Map.of(
                        new Fish("Legend", 0, 2000, null, null, null, null, null), 1,
                        new Crop("Potato", 50, 40, 3), 2,
                        new Crop("Parsnip", 20, 15, 1), 1,
                        new Crop("Tomato", 50, 35, 3), 1,
                        new Crop("Eggplant", 100, 80, 1), 1
                ),
                new Food("The Legends of Spakbor", 0, 2000, 100), 1, UnlockCondition.OBTAIN_ITEM, "Memancing Legend Fish"));
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
