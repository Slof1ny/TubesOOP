// Path: TubesOOP/src/recipe/Recipe.java
package recipe;

import cooking.UnlockCondition;
import item.Item; // Will be used by methods resolving names, or in CookingManager

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Recipe {

    private final String recipeId;
    private final String recipeName;
    private final Map<String, Integer> requiredIngredientNames; // Changed from Map<Item, Integer>
    private final String outputItemName; // Changed from Item
    private final int outputQuantity;
    private final UnlockCondition unlockCondition;
    private final String unlockDetail;

    public static final int COOKING_DURATION_MINUTES = 60; //
    public static final int ENERGY_COST_TO_START_COOKING = 10; //

    public Recipe(String recipeId, String recipeName, Map<String, Integer> requiredIngredientNames,
                  String outputItemName, int outputQuantity, UnlockCondition unlockCondition, String unlockDetail) { // Constructor updated

        if (recipeId == null || recipeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe ID tidak boleh null atau kosong.");
        }
        if (recipeName == null || recipeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama resep tidak boleh null atau kosong.");
        }
        if (requiredIngredientNames == null || requiredIngredientNames.isEmpty()) {
            throw new IllegalArgumentException("Resep harus memiliki setidaknya satu bahan.");
        }
        if (outputItemName == null || outputItemName.trim().isEmpty()) { // Check for empty string too
            throw new IllegalArgumentException("Output Item name tidak boleh null atau kosong.");
        }
        if (outputQuantity <= 0) {
            throw new IllegalArgumentException("Kuantitas output harus lebih besar dari 0.");
        }
        if (unlockCondition == null) {
            throw new IllegalArgumentException("Kondisi unlock tidak boleh null.");
        }

        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.requiredIngredientNames = Collections.unmodifiableMap(new HashMap<>(requiredIngredientNames)); // Store the string map
        this.outputItemName = outputItemName;
        this.outputQuantity = outputQuantity;
        this.unlockCondition = unlockCondition;
        this.unlockDetail = (unlockDetail == null) ? "" : unlockDetail;
    }

    // --- Getters ---

    public String getRecipeId() {
        return recipeId;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public Map<String, Integer> getRequiredIngredientNames() { // Getter updated
        return requiredIngredientNames;
    }

    public String getOutputItemName() { // Getter updated
        return outputItemName;
    }

    public int getOutputQuantity() {
        return outputQuantity;
    }

    public UnlockCondition getUnlockCondition() {
        return unlockCondition;
    }

    public String getUnlockDetail() {
        return unlockDetail;
    }

    // --- Metode Utilitas dan Override ---

    @Override
    public String toString() {
        // This will now show item names, which is fine for a general toString.
        // Resolution to Item objects and their details will happen in CookingManager or UI.
        return "Recipe Information:\n" +
               "  ID        : " + recipeId + "\n" +
               "  Name      : " + recipeName + "\n" +
               "  Ingredients: " + requiredIngredientsMapToString() + "\n" + // Call the updated helper
               "  Output    : " + outputItemName + " (x" + outputQuantity + ")\n" +
               "  Unlock    : " + unlockCondition + (unlockDetail.isEmpty() ? "" : " (" + unlockDetail + ")") + "\n" +
               "  Cook Time : " + COOKING_DURATION_MINUTES + " minutes (game)\n" +
               "  Energy Cost: " + ENERGY_COST_TO_START_COOKING;
    }

    // Helper method to print the string map of ingredients
    private String requiredIngredientsMapToString() {
        if (requiredIngredientNames.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> entry : requiredIngredientNames.entrySet()) {
            sb.append("\n    - ").append(entry.getKey()).append(": ").append(entry.getValue());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return recipeId.equals(recipe.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId);
    }
}