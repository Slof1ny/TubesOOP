package cooking;

import core.player.Player;
import recipe.Recipe;
import item.Item;
import item.ItemRegistry;
import item.Fish; // Keep for "ANY_FISH" check if needed by ItemRegistry logic
import core.player.Inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
// Import ScheduledExecutorService and related classes
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit; // For TimeUnit
import java.util.stream.Collectors;


public class CookingManager implements CookingTask.CookingCompleteListener {
    private final Player player;
    private final Set<String> unlockedRecipeIds;
    // Change ExecutorService to ScheduledExecutorService
    private final ScheduledExecutorService cookingScheduler;
    private static final int COOKING_DELAY_SECONDS = 12; // 1 game hour = 60 game mins; 5 game mins = 1 real sec => 60/5 = 12 real secs

    public CookingManager(Player player) {
        this.player = player;
        this.unlockedRecipeIds = new HashSet<>();
        // Initialize as a single-thread scheduled executor
        this.cookingScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName("CookingSchedulerThread"); // Optional: Name the thread
            return thread;
        });
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
            // System.out.println("Resep '" + RecipeData.getRecipeById(recipeId).getRecipeName() + "' telah dibuka untuk " + player.getName());
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
        if (player.getEnergy() < Recipe.ENERGY_COST_TO_START_COOKING) {
            return "Energi tidak cukup untuk memulai memasak. Butuh: " + Recipe.ENERGY_COST_TO_START_COOKING + ", dimiliki: " + player.getEnergy() + ".";
        }

        Inventory inventory = player.getInventory();
        Map<String, Integer> requiredIngredientNames = recipe.getRequiredIngredientNames();
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
                    for(int i=0; i < invEntry.getValue(); i++){
                        playerFishAvailable.add(invEntry.getKey());
                    }
                }
            }
            if (playerFishAvailable.size() < anyFishNeeded) {
                return "Bahan tidak cukup untuk '" + recipe.getRecipeName() + "'. Butuh " + anyFishNeeded + "x ANY_FISH, hanya punya " + playerFishAvailable.size() + " ikan.";
            }
            for (int i = 0; i < anyFishNeeded; i++) {
                Item fishToUse = playerFishAvailable.get(i);
                itemsToConsume.put(fishToUse, itemsToConsume.getOrDefault(fishToUse, 0) + 1);
            }
        }

        Item outputItemForRecipe = ItemRegistry.getItemByName(recipe.getOutputItemName());
        if (outputItemForRecipe == null) {
             return "Error: Produk resep '" + recipe.getOutputItemName() + "' tidak dikenal.";
        }
        int totalFoodOutput = recipe.getOutputQuantity();
        int fuelUnitsNeeded = (int) Math.ceil((double) totalFoodOutput / selectedFuel.getFoodCookCapacity());
        if (fuelUnitsNeeded <= 0) fuelUnitsNeeded = 1;

        Item fuelItem = ItemRegistry.getItemByName(selectedFuel.getName());
        if (fuelItem == null) {
            return "Error: Bahan bakar '" + selectedFuel.getName() + "' tidak dikenal sebagai item.";
        }
        int fuelInInventoryCount = inventory.getItemCount(fuelItem);
        if (fuelInInventoryCount < fuelUnitsNeeded) {
            return "Bahan bakar tidak cukup. Butuh " + fuelUnitsNeeded + "x " + selectedFuel.getName() + ", hanya punya " + fuelInInventoryCount + ".";
        }

        // All checks passed, consume resources and schedule task
        player.setEnergy(player.getEnergy() - Recipe.ENERGY_COST_TO_START_COOKING);

        for (Map.Entry<Item, Integer> entry : itemsToConsume.entrySet()) {
            inventory.removeItem(entry.getKey(), entry.getValue());
        }
        inventory.removeItem(fuelItem, fuelUnitsNeeded);

        // Create and schedule the CookingTask
        CookingTask cookingTask = new CookingTask(
                player,
                outputItemForRecipe, // Pass the resolved Item object
                recipe.getOutputQuantity(),
                inventory, // Pass the inventory reference
                0, // cookingDurationMillis for Thread.sleep in task, not strictly needed if using scheduler delay
                this // Pass CookingManager as the listener
        );
        
        // Schedule the task to run after 1 game hour (12 real seconds)
        cookingScheduler.schedule(cookingTask, COOKING_DELAY_SECONDS, TimeUnit.SECONDS);

        return "Memasak '" + recipe.getRecipeName() + "' dimulai! Akan selesai dalam 1 jam game. ("+ outputItemForRecipe.getName() +" x"+ recipe.getOutputQuantity()+")";
    }
    

    // Implementasi CookingCompleteListener
    @Override
    public void onCookingComplete(Player player, Item itemProduced, int quantity) {
        // This method is called by CookingTask when it finishes
        // You can add UI notifications here if needed, e.g., via a callback to GameView/GameManager
        // For now, it just logs to console. The item is already added to inventory by CookingTask.
        System.out.println("[CookingManager NOTIFICATION] " + player.getName() + " telah selesai memasak " + quantity + "x " + itemProduced.getName() + "! Item ditambahkan ke inventory.");
        // If you have a GameView reference or an event bus, you could trigger a small GUI notification.
        // Example: gameManager.notifyPlayer("Cooking complete: " + itemProduced.getName());
    }
    
    @Override
    public void onCookingFailed(Player player, String recipeName, String reason) {
        System.err.println("[CookingManager NOTIFICATION] " + player.getName() + " gagal memasak " + recipeName + ". Alasan: " + reason);
        // Potentially refund ingredients here if appropriate, or notify player.
    }


    public void shutdown() {
        cookingScheduler.shutdown();
        try {
            if (!cookingScheduler.awaitTermination(1, TimeUnit.SECONDS)) { // Shorter wait time
                cookingScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cookingScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("CookingManager (Scheduler) telah dimatikan.");
    }
}