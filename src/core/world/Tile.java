package core.world;

import item.Crop;
import java.util.Objects;

/**
 * Universal Tile class for all maps (Farm, House, City).
 * Handles all terrain types (UNTILLED variants, TILLED, PLANTED)
 * and all deployed object symbols (buildings, interactables).
 */
public class Tile {
    public enum TileType {
        UNTILLED,   // Default walkable ground (e.g., grass, plain road, dirt)
        TILLED,     // Tilled soil for planting
        PLANTED,    // Soil with a planted crop
        DEPLOYED    // Occupied by a non-walkable object (building, rock, etc.)
    }

    private final int x, y;
    private TileType type;
    private char deployedChar; // Stores the specific symbol for display, or terrain type
    private Crop plantedCrop;

    // Default symbol for plain ground/road
    public static final char DEFAULT_UNTILLED_CHAR = '.';

    // Universal symbols for all map types (to be used in deployObject)
    // FarmMap symbols
    public static final char FARM_HOUSE_SYMBOL = 'h';
    public static final char POND_SYMBOL = 'o';
    public static final char SHIPPING_BIN_SYMBOL = 's';
    public static final char GENERIC_FARM_FURNITURE_B = 'B'; // Example: used in HouseMap for bed/table
    public static final char GENERIC_FARM_FURNITURE_T = 'T'; // Example: for chair/TV

    // CityMap building/object symbols
    public static final char STORE_SYMBOL = 'S';
    public static final char MAYOR_MANOR_SYMBOL = 'M';
    public static final char CARPENTRY_SYMBOL = 'C';
    public static final char PERRY_CABIN_SYMBOL = 'P'; // Changed from 'R' to 'P' for uniqueness (was conflicting with Jalan_coklatKanan_SYMBOL)
    public static final char GAMBLING_DEN_SYMBOL = 'G';
    public static final char ABIGAIL_TENT_SYMBOL = 'A';
    public static final char EXIT_SYMBOL = 'X'; // Exit point (Door.png)
    public static final char ORENJI_SYMBOL = 'O'; // Orenji's Cafe
    public static final char FENCE_SYMBOL = 'F'; // Fence

    // Terrain symbols (for varied backgrounds)
    public static final char RUMPUT_HIJAU_SYMBOL  = '1';   // JalanHijau-16px.png
    public static final char BATU_SYMBOL          = '2';   // Stone_Round.png
    public static final char AIR_SYMBOL           = '3';   // Kolam_air.png
    public static final char BUNGA_PINK_SYMBOL    = '4';   // BungaPink.png
    public static final char TANAH_SYMBOL         = '5';   // Tanah-16px.png

    // Specific road variants for 2-tile wide roads (must be unique from other symbols)
    public static final char JALAN_COKLAT_KIRI_SYMBOL = 'K'; // Custom symbol for Jalan_coklatKiri.png
    public static final char JALAN_COKLAT_KANAN_SYMBOL = 'R'; // Custom symbol for Jalan_coklatKanan.png
    public static final char JALAN_COKLAT_ATAS_SYMBOL = 'U'; // Custom symbol for Jalan_coklatAtas.png
    public static final char JALAN_COKLAT_BAWAH_SYMBOL = 'D'; // Custom symbol for Jalan_coklatBawah.png
    public static final char JALAN_TENGAH_SYMBOL = 'T'; // Custom symbol for Jalan_coklatTengah.png

    // Farm-specific terrain/state symbols (often used by `displayChar()` directly)
    public static final char TILLED_CHAR = 't';
    public static final char PLANTED_CHAR = 'l';


    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = TileType.UNTILLED;
        this.plantedCrop = null;
        this.deployedChar = DEFAULT_UNTILLED_CHAR; // Default to '.' road/ground
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public TileType getType() { return type; }
    public Crop getPlantedCrop() { return plantedCrop; }

    public void plantCrop(Crop crop) {
        if (this.type == TileType.TILLED) {
            this.plantedCrop = crop;
            this.type = TileType.PLANTED;
            this.deployedChar = PLANTED_CHAR; // Set char for planted state
        } else {
            System.out.println("Cannot plant here. Soil is not tilled.");
        }
    }

    public void clearPlantedCrop() {
        this.plantedCrop = null;
        if (this.type == TileType.PLANTED) { // After harvesting, revert to tilled
            this.type = TileType.TILLED;
            this.deployedChar = TILLED_CHAR; // Set char for tilled state
        }
    }

    public void setType(TileType type) {
        // Prevent direct type change for DEPLOYED if object is still on it
        if (this.type == TileType.DEPLOYED && type != TileType.DEPLOYED) {
            System.out.println("Warning: Cannot change type of a deployed tile directly. Use clearDeployment first.");
            return;
        }
        this.type = type;
        // If type changes to non-deployed, ensure deployedChar reverts to default terrain char
        if (type != TileType.DEPLOYED && type != TileType.TILLED && type != TileType.PLANTED) { // Only revert if not tilled or planted either
            this.deployedChar = DEFAULT_UNTILLED_CHAR;
        }
        if (type != TileType.PLANTED) {
            this.plantedCrop = null;
        }
    }

    /**
     * Deploys an object or sets a terrain type on this tile using a character symbol.
     * This method is universal and handles symbols for all map types and terrain.
     * @param c The character symbol representing the object or terrain type.
     */
    public void deployObject(char c) {
        switch (c) {
            // --- DEPLOYED symbols (objects that sit on tiles) ---
            case FARM_HOUSE_SYMBOL:
            case POND_SYMBOL:
            case SHIPPING_BIN_SYMBOL:
            case GENERIC_FARM_FURNITURE_B:
            case GENERIC_FARM_FURNITURE_T:
            case STORE_SYMBOL:
            case MAYOR_MANOR_SYMBOL:
            case CARPENTRY_SYMBOL:
            case PERRY_CABIN_SYMBOL: // Changed from 'R' to 'P' for uniqueness
            case GAMBLING_DEN_SYMBOL:
            case ABIGAIL_TENT_SYMBOL:
            case EXIT_SYMBOL:
            case ORENJI_SYMBOL:
            case FENCE_SYMBOL:
                this.type = TileType.DEPLOYED;
                this.deployedChar = c;
                this.plantedCrop = null;
                break;

            // --- Terrain symbols (these are UNTILLED ground types for core logic) ---
            case DEFAULT_UNTILLED_CHAR: // '.' for default road/untilled (Jalan-16px.png)
            case RUMPUT_HIJAU_SYMBOL:  // '1'
            case BATU_SYMBOL:          // '2'
            case AIR_SYMBOL:           // '3'
            case BUNGA_PINK_SYMBOL:    // '4'
            case TANAH_SYMBOL:         // '5'
            case JALAN_COKLAT_KIRI_SYMBOL: // 'K'
            case JALAN_COKLAT_KANAN_SYMBOL: // 'N'
            case JALAN_COKLAT_ATAS_SYMBOL: // 'U'
            case JALAN_COKLAT_BAWAH_SYMBOL: // 'D'
                this.type = TileType.UNTILLED;
                this.deployedChar = c;
                this.plantedCrop = null;
                break;

            // --- Tilled and Planted symbols (specific ground states) ---
            case TILLED_CHAR: // 't'
                this.type = TileType.TILLED;
                this.deployedChar = c;
                this.plantedCrop = null;
                break;
            case PLANTED_CHAR: // 'l'
                this.type = TileType.PLANTED;
                this.deployedChar = c;
                // plantedCrop is set separately by plantCrop()
                break;

            default:
                throw new IllegalArgumentException("Invalid deployed object or terrain character: '" + c +
                                                   "' for Tile at (" + x + "," + y + ")");
        }
    }

    public void clearDeployment() {
        if (this.type == TileType.DEPLOYED) {
            this.type = TileType.UNTILLED;
            this.deployedChar = DEFAULT_UNTILLED_CHAR; // Revert to default '.' terrain symbol
            this.plantedCrop = null;
        }
    }

    /**
     * Determines if this tile is walkable based on its current type and symbol.
     * Note: For DEPLOYED objects, their specific isWalkable() method (in DeployedObject subclass)
     * will ultimately determine walkability in the Map.isWalkable() method.
     */
    public boolean isWalkable() {
        if (this.type == TileType.DEPLOYED) {
             return false;
        }
        // Specific non-walkable terrain types
        if (this.deployedChar == AIR_SYMBOL) return false; // Water ('3') is not walkable

        // All other UNTILLED (including varied terrain), TILLED, and PLANTED tiles are walkable by default.
        return true;
    }

    /**
     * Returns the character representing this tile for display purposes.
     * This character will be used by Map Panels to select the correct image.
     */
    public char displayChar() {
        return this.deployedChar;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}