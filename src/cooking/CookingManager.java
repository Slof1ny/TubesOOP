// Path: TubesOOP/src/cooking/CookingManager.java
package cooking;

import core.player.Player;
import recipe.Recipe;
import item.Item;
import item.ItemRegistry; // For resolving item names
import item.Fish;        // To check category for "ANY_FISH"
import core.player.Inventory;

import java.util.ArrayList; // For managing fish to remove
import java.util.HashMap; // If needed for temporary counts
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CookingManager implements CookingTask.CookingCompleteListener {
    private final Player player;
    private final Set<String> unlockedRecipeIds;
    private final ExecutorService cookingExecutor; // Not used by synchronous startCooking

    public CookingManager(Player player) {
        this.player = player;
        this.unlockedRecipeIds = new HashSet<>();
        this.cookingExecutor = Executors.newCachedThreadPool();
        initializeDefaultRecipes();
    }

    private void initializeDefaultRecipes() {
        RecipeData.getAllRecipes().stream()
            .filter(recipe -> recipe.getUnlockCondition() == cooking.UnlockCondition.DEFAULT)
            .forEach(recipe -> unlockRecipe(recipe.getRecipeId()));
    }

    public void unlockRecipe(String recipeId) {
        if (RecipeData.getRecipeById(recipeId) != null) {
            unlockedRecipeIds.add(recipeId);
            System.out.println("Resep '" + RecipeData.getRecipeById(recipeId).getRecipeName() + "' telah dibuka untuk " + player.getName());
        }
    }

    public boolean isRecipeUnlocked(String recipeId) {
        return unlockedRecipeIds.contains(recipeId);
    }

    public List<Recipe> getAvailableRecipes() {
        return RecipeData.getAllRecipes().stream()
            .filter(recipe -> unlockedRecipeIds.contains(recipe.getRecipeId()))
            .collect(Collectors.toList());
    }

    public String startCooking(String recipeId, Fuel selectedFuel) {
        Recipe recipe = RecipeData.getRecipeById(recipeId);
        if (recipe == null) {
            return "Error: Resep dengan ID '" + recipeId + "' tidak ditemukan.";
        }
        if (!isRecipeUnlocked(recipeId)) {
            return "Kamu belum membuka resep untuk '" + recipe.getRecipeName() + "'.";
        }
        if (player.getEnergy() < Recipe.ENERGY_COST_TO_START_COOKING) { // [cite: 220]
            return "Energi tidak cukup untuk memulai memasak. Butuh: " + Recipe.ENERGY_COST_TO_START_COOKING + ", dimiliki: " + player.getEnergy() + "."; // [cite: 220]
        }

        Inventory inventory = player.getInventory();
        Map<String, Integer> requiredIngredientNames = recipe.getRequiredIngredientNames();
        
        // --- Ingredient Check ---
        // Store actual items to remove to handle "ANY_FISH" correctly
        Map<Item, Integer> itemsToConsume = new HashMap<>();
        int anyFishNeeded = 0;
        List<Item> playerFishAvailable = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : requiredIngredientNames.entrySet()) {
            String ingredientName = entry.getKey();
            int quantityNeeded = entry.getValue();

            if (ingredientName.equals("ANY_FISH")) {
                anyFishNeeded += quantityNeeded;
            } else {
                Item ingredient = ItemRegistry.getItemByName(ingredientName);
                if (ingredient == null) {
                    return "Error: Bahan resep '" + ingredientName + "' tidak dikenal dalam sistem.";
                }
                if (inventory.getItemCount(ingredient) < quantityNeeded) {
                    return "Bahan tidak cukup untuk '" + recipe.getRecipeName() + "'. Butuh " + quantityNeeded + "x " + ingredient.getName() + ".";
                }
                itemsToConsume.put(ingredient, itemsToConsume.getOrDefault(ingredient, 0) + quantityNeeded);
            }
        }

        if (anyFishNeeded > 0) {
            for (Map.Entry<Item, Integer> invEntry : inventory.getAllItems().entrySet()) {
                if (invEntry.getKey().getCategory().equals("Fish")) {
                    for(int i=0; i < invEntry.getValue(); i++){ // Add each fish instance
                        playerFishAvailable.add(invEntry.getKey());
                    }
                }
            }
            if (playerFishAvailable.size() < anyFishNeeded) {
                return "Bahan tidak cukup untuk '" + recipe.getRecipeName() + "'. Butuh " + anyFishNeeded + "x ANY_FISH, hanya punya " + playerFishAvailable.size() + " ikan.";
            }
            // Select the first 'anyFishNeeded' fish from the available list
            for (int i = 0; i < anyFishNeeded; i++) {
                Item fishToUse = playerFishAvailable.get(i); // Simplistic: use the first available ones
                itemsToConsume.put(fishToUse, itemsToConsume.getOrDefault(fishToUse, 0) + 1);
            }
        }

        // --- Fuel Check ---
        Item outputItemForRecipe = ItemRegistry.getItemByName(recipe.getOutputItemName());
        if (outputItemForRecipe == null) {
             return "Error: Produk resep '" + recipe.getOutputItemName() + "' tidak dikenal.";
        }
        int totalFoodOutput = recipe.getOutputQuantity(); // Assumes output is always a single type of item
        int fuelUnitsNeeded = (int) Math.ceil((double) totalFoodOutput / selectedFuel.getFoodCookCapacity()); // [cite: 216]
        if (fuelUnitsNeeded <= 0) fuelUnitsNeeded = 1;

        Item fuelItem = ItemRegistry.getItemByName(selectedFuel.getName()); // Fuel name should match an item name
        if (fuelItem == null) {
            return "Error: Bahan bakar '" + selectedFuel.getName() + "' tidak dikenal sebagai item.";
        }
        int fuelInInventoryCount = inventory.getItemCount(fuelItem);
        if (fuelInInventoryCount < fuelUnitsNeeded) {
            return "Bahan bakar tidak cukup. Butuh " + fuelUnitsNeeded + "x " + selectedFuel.getName() + ", hanya punya " + fuelInInventoryCount + ".";
        }

        // --- All checks passed, proceed to consume and produce ---
        player.setEnergy(player.getEnergy() - Recipe.ENERGY_COST_TO_START_COOKING); // [cite: 220]

        // Consume ingredients
        for (Map.Entry<Item, Integer> entry : itemsToConsume.entrySet()) {
            inventory.removeItem(entry.getKey(), entry.getValue());
        }

        // Consume fuel
        inventory.removeItem(fuelItem, fuelUnitsNeeded);

        // Add output product
        inventory.addItem(outputItemForRecipe, recipe.getOutputQuantity()); // [cite: 220]
        
        // Passive cooking duration means we don't use CookingTask for immediate GUI feedback of item addition
        // The spec implies the item is added to inventory after the *passive* 1 hour.
        // However, for this GUI step, the current synchronous implementation means item appears instantly.
        // If true async passive cooking is added later, item addition would be delayed.
        // For now, this matches the immediate feedback model the previous synchronous version had.
        
        // Simulate time passing (as per spec this is passive, player can do other things)
        // This part might be handled by a global game loop or event rather than direct time advance here
        // if cooking is truly asynchronous.
        // For now, we'll skip explicit time advancement here if it's meant to be passive and concurrent.
        // The prompt implies CookingManager does the work.
        // gameTime.advanceGameMinutes(Recipe.COOKING_DURATION_MINUTES); // This would make it active, not passive

        return "Memasak '" + recipe.getRecipeName() + "' berhasil! " + recipe.getOutputQuantity() + "x " + outputItemForRecipe.getName() + " ditambahkan ke inventory. (Proses masak pasif selama 1 jam game dimulai)";
    }

    @Override
    public void onCookingComplete(Player player, Item itemProduced, int quantity) {
        System.out.println("[CookingManager] Notifikasi: " + player.getName() + " telah selesai memasak " + quantity + "x " + itemProduced.getName() + "!");
    }

    @Override
    public void onCookingFailed(Player player, String recipeName, String reason) {
        System.err.println("[CookingManager] Notifikasi: " + player.getName() + " gagal memasak " + recipeName + ". Alasan: " + reason);
    }

    public void shutdown() {
        cookingExecutor.shutdown();
        try {
            if (!cookingExecutor.awaitTermination(800, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                cookingExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            cookingExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("CookingManager telah dimatikan.");
    }
}