package cooking;

/**
 * Interface untuk bahan bakar yang digunakan dalam memasak.
 */
public interface Fuel {
    /**
     * Mendapatkan ID item dari bahan bakar ini.
     * @return String itemId bahan bakar.
     */
    String getFuelItemId();

    /**
     * Mendapatkan nama bahan bakar.
     * @return String nama bahan bakar.
     */
    String getName();

    /**
     * Mendapatkan kapasitas masak dari 1 unit bahan bakar ini.
     * (Berapa banyak item makanan individual yang bisa dimasak).
     * @return int kapasitas masak.
     */
    int getFoodCookCapacity();
}
