package recipe;

import cooking.UnlockCondition;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import item.Item;

/**
 * Merepresentasikan satu resep masakan dalam game "Spakbor Hills".
 * Kelas ini menyimpan semua informasi yang relevan tentang sebuah resep,
 * termasuk bahan yang dibutuhkan, item yang dihasilkan, dan cara untuk membukanya.
 */
public class Recipe {

    private final String recipeId;          // ID unik untuk resep, mis: "recipe_1", "recipe_fish_n_chips"
    private final String recipeName;        // Nama resep yang akan ditampilkan ke pemain, mis: "Fish n’ Chips"
    
    // Bahan yang dibutuhkan: Kunci adalah Item, Nilai adalah kuantitas (Integer)
    private final Map<Item, Integer> requiredIngredients; 
    
    private final Item outputItem;          // Item makanan yang dihasilkan oleh resep ini
    private final int outputQuantity;       // Jumlah item makanan yang dihasilkan setiap kali resep ini dimasak
    
    private final UnlockCondition unlockCondition; // Kondisi bagaimana resep ini bisa di-unlock oleh pemain
    private final String unlockDetail;      // Deskripsi tambahan untuk kondisi unlock (mis: nama toko, nama item, dll.)

    // Konstanta berdasarkan spesifikasi tugas
    public static final int COOKING_DURATION_MINUTES = 60; // Durasi memasak adalah 1 jam (60 menit)
    public static final int ENERGY_COST_TO_START_COOKING = 10; // Biaya energi untuk memulai percobaan memasak

    /**
     * Konstruktor untuk membuat objek Recipe baru.
     * @param recipeId ID unik untuk resep.
     * @param recipeName Nama resep yang ditampilkan.
     * @param requiredIngredients Peta bahan yang dibutuhkan (itemId -> kuantitas).
     * @param outputItemId ID item dari makanan yang dihasilkan.
     * @param outputQuantity Jumlah makanan yang dihasilkan.
     * @param unlockCondition Kondisi untuk membuka resep ini.
     * @param unlockDetail Detail tambahan mengenai kondisi unlock.
     */
    public Recipe(String recipeId, String recipeName, Map<Item, Integer> requiredIngredients,
                  Item outputItem, int outputQuantity, UnlockCondition unlockCondition, String unlockDetail) {
        
        // Validasi input dasar
        if (recipeId == null || recipeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipe ID tidak boleh null atau kosong.");
        }
        if (recipeName == null || recipeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nama resep tidak boleh null atau kosong.");
        }
        if (requiredIngredients == null || requiredIngredients.isEmpty()) {
            throw new IllegalArgumentException("Resep harus memiliki setidaknya satu bahan.");
        }
        if (outputItem == null) {
            throw new IllegalArgumentException("Output Item tidak boleh null.");
        }
        if (outputQuantity <= 0) {
            throw new IllegalArgumentException("Kuantitas output harus lebih besar dari 0.");
        }
        if (unlockCondition == null) {
            throw new IllegalArgumentException("Kondisi unlock tidak boleh null.");
        }

        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.requiredIngredients = Collections.unmodifiableMap(new HashMap<>(requiredIngredients));
        this.outputItem = outputItem;
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

    /**
     * Mengembalikan peta bahan yang dibutuhkan yang tidak dapat diubah.
     * @return Map<String, Integer> dari itemId ke kuantitas.
     */
    public Map<Item, Integer> getRequiredIngredients() {
        return requiredIngredients; // Sudah unmodifiable dari constructor
    }

    public Item getOutputItem() {
        return outputItem;
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
        return "Recipe Information:\n" +
               "  ID        : " + recipeId + "\n" +
               "  Name      : " + recipeName + "\n" +
               "  Ingredients: " + requiredIngredientsToString() + "\n" +
               "  Output    : " + outputItem.getName() + " (x" + outputQuantity + ")\n" +
               "  Unlock    : " + unlockCondition + (unlockDetail.isEmpty() ? "" : " (" + unlockDetail + ")") + "\n" +
               "  Cook Time : " + COOKING_DURATION_MINUTES + " minutes (game)\n" +
               "  Energy Cost: " + ENERGY_COST_TO_START_COOKING;
    }
    
    private String requiredIngredientsToString() {
        if (requiredIngredients.isEmpty()) {
            return "None";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Item, Integer> entry : requiredIngredients.entrySet()) {
            sb.append("\n    - ").append(entry.getKey().getName()).append(": ").append(entry.getValue());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return recipeId.equals(recipe.recipeId); // ID Resep dianggap unik
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId); // ID Resep digunakan untuk hashCode
    }
}