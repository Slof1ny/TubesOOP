package cooking;

import item.Item;

/**
 * Implementasi Coal sebagai bahan bakar.
 * 1 Coal bisa memasak 2 item makanan.
 */
public class Coal extends Item implements Fuel { 
    public static final String ITEM_ID = "Coal";
    private static final String NAME = "Coal"; 
    private static final int FOOD_COOK_CAPACITY = 2;

    public Coal() {
        super(NAME, 15, 7); 
    }

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
    public String getCategory() { 
        return "Fuel";
    }

    @Override
    public String toString() {
        return NAME + " (Kapasitas: " + FOOD_COOK_CAPACITY + " makanan per unit)";
    }
}