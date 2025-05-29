package cooking;

import recipe.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import item.Misc;
import item.CropRegistry;
import item.FoodRegistry;

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
                        Misc.get("ANY_FISH"), 2,
                        CropRegistry.getHarvestedCropByName("Wheat"), 1,
                        CropRegistry.getHarvestedCropByName("Potato"), 1
                ),
                FoodRegistry.getFoodByName("Fish n’ Chips"), 1, UnlockCondition.STORE_BOUGHT, "Toko Emily"));

        // recipe_2: Baguette
        ALL_RECIPES.add(new Recipe("recipe_2", "Baguette",
                Map.of(
                        CropRegistry.getHarvestedCropByName("Wheat"), 3
                ),
                FoodRegistry.getFoodByName("Baguette"), 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_3: Sashimi
        ALL_RECIPES.add(new Recipe("recipe_3", "Sashimi",
                Map.of(
                        Misc.get("Salmon"), 3
                ),
                FoodRegistry.getFoodByName("Sashimi"), 1, UnlockCondition.FISHING_MILESTONE, "Memancing 10 ikan"));
        
        // recipe_4: Fugu
        ALL_RECIPES.add(new Recipe("recipe_4", "Fugu",
                Map.of(
                        Misc.get("Pufferfish"), 1
                ),
                FoodRegistry.getFoodByName("Fugu"), 1, UnlockCondition.OBTAIN_ITEM, "Memancing Pufferfish"));

        // recipe_5: Wine
        ALL_RECIPES.add(new Recipe("recipe_5", "Wine",
                Map.of(
                        CropRegistry.getHarvestedCropByName("Grape"), 2
                ),
                FoodRegistry.getFoodByName("Wine"), 1, UnlockCondition.DEFAULT, "Resep bawaan"));
        
        // recipe_6: Pumpkin Pie
        ALL_RECIPES.add(new Recipe("recipe_6", "Pumpkin Pie",
                Map.of(
                        Misc.get("Egg"), 1,
                        CropRegistry.getHarvestedCropByName("Wheat"), 1,
                        CropRegistry.getHarvestedCropByName("Pumpkin"), 1
                ),
                FoodRegistry.getFoodByName("Pumpkin Pie"), 1, UnlockCondition.DEFAULT, "Resep bawaan"));

        // recipe_7: Veggie Soup
        ALL_RECIPES.add(new Recipe("recipe_7", "Veggie Soup",
                Map.of(
                        CropRegistry.getHarvestedCropByName("Cauliflower"), 1,
                        CropRegistry.getHarvestedCropByName("Parsnip"), 1,
                        CropRegistry.getHarvestedCropByName("Potato"), 1,
                        CropRegistry.getHarvestedCropByName("Tomato"), 1
                ),
                FoodRegistry.getFoodByName("Veggie Soup"), 1, UnlockCondition.HARVEST_MILESTONE, "Memanen pertama kali"));

        // recipe_8: Fish Stew
        ALL_RECIPES.add(new Recipe("recipe_8", "Fish Stew",
                Map.of(
                        Misc.get("ANY_FISH"), 2,
                        CropRegistry.getHarvestedCropByName("Hot Pepper"), 1,
                        CropRegistry.getHarvestedCropByName("Cauliflower"), 2
                ),
                FoodRegistry.getFoodByName("Fish Stew"), 1, UnlockCondition.OBTAIN_ITEM, "Mendapatkan Hot Pepper"));

        // recipe_9: Spakbor Salad
        ALL_RECIPES.add(new Recipe("recipe_9", "Spakbor Salad",
                Map.of(
                        CropRegistry.getHarvestedCropByName("Melon"), 1,
                        CropRegistry.getHarvestedCropByName("Cranberry"), 1,
                        CropRegistry.getHarvestedCropByName("Blueberry"), 1,
                        CropRegistry.getHarvestedCropByName("Tomato"), 1
                ),
                FoodRegistry.getFoodByName("Spakbor Salad"), 1, UnlockCondition.DEFAULT, "Resep bawaan"));
        
        // recipe_10: Fish Sandwich
        ALL_RECIPES.add(new Recipe("recipe_10", "Fish Sandwich",
                Map.of(
                        Misc.get("ANY_FISH"), 1,
                        CropRegistry.getHarvestedCropByName("Wheat"), 2,
                        CropRegistry.getHarvestedCropByName("Tomato"), 1,
                        CropRegistry.getHarvestedCropByName("Hot Pepper"), 1
                ),
                FoodRegistry.getFoodByName("Fish Sandwich"), 1, UnlockCondition.STORE_BOUGHT, "Toko Emily"));

        // recipe_11: The Legends of Spakbor
        ALL_RECIPES.add(new Recipe("recipe_11", "The Legends of Spakbor",
                Map.of(
                        Misc.get("Legend"), 1,
                        CropRegistry.getHarvestedCropByName("Potato"), 2,
                        CropRegistry.getHarvestedCropByName("Parsnip"), 1,
                        CropRegistry.getHarvestedCropByName("Tomato"), 1,
                        CropRegistry.getHarvestedCropByName("Eggplant"), 1
                ),
                FoodRegistry.getFoodByName("The Legends of Spakbor"), 1, UnlockCondition.OBTAIN_ITEM, "Memancing Legend Fish"));
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
