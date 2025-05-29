package cooking;

/**
 * Implementasi Coal sebagai bahan bakar.
 * 1 Coal bisa memasak 2 item makanan.
 */
public class Coal implements Fuel {
    public static final String ITEM_ID = "ITEM_COAL"; // Sesuaikan dengan ID item di sistem Anda
    private static final String NAME = "Coal";
    private static final int FOOD_COOK_CAPACITY = 2;

    @Override
    public String getFuelItemId() {
        return ITEM_ID;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public int getFoodCookCapacity() {
        return FOOD_COOK_CAPACITY;
    }
     @Override
    public String toString() {
        return NAME + " (Kapasitas: " + FOOD_COOK_CAPACITY + " makanan per unit)";
    }
}
