package core.world;

import item.Crop;
import java.util.Objects;

/**
 * Generic Tile class (used by FarmMap, HouseMap, etc.).
 * It handles Farm symbols (h, o, s, and now B/T for generic deployed), and base ground
 * (UNTILLED, TILLED, PLANTED). City-only symbols (1..5, S, M, C, etc.) are handled in CityTile.
 */
public class Tile {
    public enum TileType {
        UNTILLED,
        TILLED,
        PLANTED,
        DEPLOYED
    }

    private final int x, y;
    private TileType type;
    private char deployedChar; // Used for DEPLOYED objects and base terrain
    public static final char DEFAULT_UNTILLED_CHAR = '.'; // Default symbol for plain ground or road

    private Crop plantedCrop;

    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
        this.type = TileType.UNTILLED;
        this.plantedCrop = null;
        this.deployedChar = DEFAULT_UNTILLED_CHAR;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public TileType getType() {
        return type;
    }

    public Crop getPlantedCrop() {
        return plantedCrop;
    }

    public void plantCrop(Crop crop) {
        if (this.type == TileType.TILLED) {
            this.plantedCrop = crop;
            this.type = TileType.PLANTED;
        } else {
            System.out.println("Cannot plant here. Soil is not tilled.");
        }
    }

    public void clearPlantedCrop() {
        this.plantedCrop = null;
        if (this.type == TileType.PLANTED) {
            this.type = TileType.TILLED;
        }
    }

    public void setType(TileType type) {
        if (this.type == TileType.DEPLOYED && type != TileType.DEPLOYED) {
            System.out.println("Cannot change type of a deployed tile directly. Use clearDeployment first.");
            return;
        }
        if (type == TileType.DEPLOYED) {
            throw new IllegalStateException("Use deployObject(char) to manage DEPLOYED state.");
        }
        this.type = type;
        this.deployedChar = DEFAULT_UNTILLED_CHAR; // Reset to default terrain char
        if (type != TileType.PLANTED) {
            this.plantedCrop = null;
        }
    }

    /**
     * Deploys an object or sets a terrain type on this tile using a character symbol.
     * This version handles Farm symbols (h, o, s), generic deployed symbols (B, T),
     * and the default '.' (UNTILLED). City-only symbols are handled in CityTile.
     *
     * @param c The character symbol representing the object or terrain type.
     */
    public void deployObject(char c) {
        switch (c) {
            // ---- FarmMap and generic deployed symbols ----
            case 'h': // House on farm
            case 'o': // Pond on farm
            case 's': // Shipping Bin on farm
            case 'B': // Generic Furniture/Building marker (e.g. HouseMap uses B)
            case 'T': // Another generic marker, if used (e.g. for Table, etc.)
            case 'S': // Generic symbol for a deployed object (e.g. Storage)
            case 'X': // Generic symbol for a deployed object (e.g. Exit)
                this.type = TileType.DEPLOYED;
                this.deployedChar = c;
                this.plantedCrop = null;
                break;

            // Base ground (UNTILLED) if '.' is used
            case DEFAULT_UNTILLED_CHAR:
                this.type = TileType.UNTILLED;
                this.deployedChar = c;
                this.plantedCrop = null;
                break;

            default:
                throw new IllegalArgumentException("Tile.deployObject: invalid Farm or generic symbol: " + c);
        }
    }

    public void clearDeployment() {
        if (this.type == TileType.DEPLOYED || this.deployedChar != DEFAULT_UNTILLED_CHAR) {
            this.type = TileType.UNTILLED;
            this.deployedChar = DEFAULT_UNTILLED_CHAR;
            this.plantedCrop = null;
        }
    }

    /**
     * Base tile walkability:
     *  - If type == DEPLOYED, return false (unless overridden by a DeployedObject).
     *  - If deployedChar == '3' (water on farm), not walkable.
     *  - Otherwise, walkable.
     */
    public boolean isWalkable() {
        if (type == TileType.DEPLOYED) return false;
        if (deployedChar == '3') return false; // Water on farm not walkable
        return true;
    }

    /**
     * Which single character to draw for this tile:
     *  - UNTILLED → return deployedChar
     *  - TILLED → 't'
     *  - PLANTED → 'l'
     *  - DEPLOYED → deployedChar
     */
    public char displayChar() {
        switch (type) {
            case UNTILLED:
                return deployedChar;
            case TILLED:
                return 't';
            case PLANTED:
                return 'l';
            case DEPLOYED:
                return deployedChar;
        }
        return '?';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return x == tile.x &&
               y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
