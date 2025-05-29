package cooking;

import core.player.Player;
import recipe.Recipe;
import item.Item;
import core.player.Inventory;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


/**
 * Mengelola semua logika yang berkaitan dengan aksi memasak.
 */
public class CookingManager implements CookingTask.CookingCompleteListener {
    private final Player player; // Pemain yang melakukan aksi
    private final Set<String> unlockedRecipeIds; // ID resep yang sudah dibuka pemain
    private final ExecutorService cookingExecutor; // Untuk menjalankan tugas memasak secara asynchronous
    // private GameManager gameManager; // Untuk interaksi dengan sistem waktu game

    // Daftar semua item yang mungkin ada di game, untuk mengambil objek Item dari ID
    // Dalam game nyata, ini akan dikelola oleh ItemManager atau serupa.
    // Untuk contoh ini, kita akan menggunakan ItemData placeholder.
    // private final Map<String, Item> allGameItems; 

    public CookingManager(Player player /*, GameManager gameManager, Map<String, Item> allGameItems */) {
        this.player = player;
        this.unlockedRecipeIds = new HashSet<>(); // Awalnya kosong, bisa diisi dari save game
        this.cookingExecutor = Executors.newCachedThreadPool(); // Atau fixed thread pool
        // this.gameManager = gameManager;
        // this.allGameItems = allGameItems;
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

    /**
     * Memulai proses memasak.
     * @param recipeId ID resep yang akan dimasak.
     * @param selectedFuel Objek Fuel yang dipilih pemain (Firewood atau Coal).
     * @return Pesan status memulai memasak atau pesan error.
     */
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
        Map<Item, Integer> requiredIngredients = recipe.getRequiredIngredients();
        // Check if player has all required ingredients
        for (Map.Entry<Item, Integer> entry : requiredIngredients.entrySet()) {
            Item ingredient = entry.getKey();
            int quantityNeeded = entry.getValue();
            if (inventory.getItemCount(ingredient) < quantityNeeded) {
                return "Bahan tidak cukup untuk '" + recipe.getRecipeName() + "'. Butuh " + quantityNeeded + "x " + ingredient.getName() + ".";
            }
        }
        // Check fuel
        int totalFoodOutput = recipe.getOutputQuantity();
        int fuelUnitsNeeded = (int) Math.ceil((double) totalFoodOutput / selectedFuel.getFoodCookCapacity());
        if (fuelUnitsNeeded <= 0) fuelUnitsNeeded = 1;
        // Find the actual fuel item object in inventory by name (object-based)
        Item fuelItem = null;
        for (Item item : inventory.getAllItems().keySet()) {
            if (item.getName().equalsIgnoreCase(selectedFuel.getName())) {
                fuelItem = item;
                break;
            }
        }
        int fuelInInventoryCount = (fuelItem != null) ? inventory.getItemCount(fuelItem) : 0;
        if (fuelInInventoryCount < fuelUnitsNeeded) {
            return "Bahan bakar tidak cukup. Butuh " + fuelUnitsNeeded + "x " + selectedFuel.getName() + ", hanya punya " + fuelInInventoryCount + ".";
        }
        // Deduct energy
        player.setEnergy(player.getEnergy() - Recipe.ENERGY_COST_TO_START_COOKING);
        // Remove ingredients
        for (Map.Entry<Item, Integer> entry : requiredIngredients.entrySet()) {
            inventory.removeItem(entry.getKey(), entry.getValue());
        }
        // Remove fuel
        if (fuelItem != null) {
            inventory.removeItem(fuelItem, fuelUnitsNeeded);
        }
        // Add output
        Item output = recipe.getOutputItem();
        inventory.addItem(output, recipe.getOutputQuantity());
        return "Memasak '" + recipe.getRecipeName() + "' selesai! " + recipe.getOutputQuantity() + "x " + output.getName() + " ditambahkan ke inventory.";
    }
    

    // Implementasi CookingCompleteListener
    @Override
    public void onCookingComplete(Player player, Item itemProduced, int quantity) {
        // Bisa tambahkan notifikasi ke UI pemain atau log game
        System.out.println("[CookingManager] Notifikasi: " + player.getName() + " telah selesai memasak " + quantity + "x " + itemProduced.getName() + "!");
    }
    
    @Override
    public void onCookingFailed(Player player, String recipeName, String reason) {
        System.err.println("[CookingManager] Notifikasi: " + player.getName() + " gagal memasak " + recipeName + ". Alasan: " + reason);
        // Mungkin perlu logika untuk mengembalikan bahan jika gagal karena error sistem,
        // tapi jika karena interupsi, bahan biasanya sudah terpakai.
    }


    /**
     * Membersihkan thread pool saat game ditutup.
     */
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
