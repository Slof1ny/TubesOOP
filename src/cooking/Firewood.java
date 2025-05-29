package cooking;

import item.Item;

/**
 * Implementasi Firewood sebagai bahan bakar.
 * 1 Firewood bisa memasak 1 item makanan.
 */
public class Firewood extends Item implements Fuel {
    public static final String ITEM_ID = "Firewood"; 
    private static final String NAME = "Firewood"; 
    private static final int FOOD_COOK_CAPACITY = 1;

    public Firewood() { 
        super(NAME, 10, 5); 
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