package core.world;

import item.Crop;
import java.util.Objects; // Import for Objects.hash

public class Tile {
    public enum TileType {
        UNTILLED,
        TILLED,
        PLANTED,
        DEPLOYED
    }

    private final int x, y;
    private TileType type;
    private char deployedChar;
    private Crop plantedCrop;

    public Tile(int x, int y) {
        // Consider if FarmMap.SIZE is accessible here or if validation should be elsewhere
        // For now, assuming FarmMap.SIZE might not be static or universally accessible for this constructor.
        // if (x < 0 || y < 0 /* || x >= SomeMap.SIZE || y >= SomeMap.SIZE */ ) {
        //     throw new IllegalArgumentException("Coordinates out of bounds");
        // }
        this.x = x;
        this.y = y;
        this.type = TileType.UNTILLED;
        this.plantedCrop = null;
        this.deployedChar = ' ';
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
        if (this.type == TileType.TILLED) { // Can only plant on tilled soil
            this.plantedCrop = crop;
            this.type = TileType.PLANTED;
        } else {
            System.out.println("Cannot plant here. Soil is not tilled.");
            // Or throw an exception
        }
    }

    public void clearPlantedCrop() {
        this.plantedCrop = null;
        // Optionally revert type to TILLED if that's the desired state after harvest/clearing
        // if (this.type == TileType.PLANTED) this.type = TileType.TILLED;
    }

    public void setType(TileType type) {
        if (this.type == TileType.DEPLOYED && type != TileType.DEPLOYED) {
            // Potentially prevent changing type if an object is deployed,
            // unless explicitly cleared by clearDeployment()
            System.out.println("Cannot change type of a deployed tile directly. Use clearDeployment first.");
            return;
        }
        if (type == TileType.DEPLOYED) {
            throw new IllegalStateException("Use deployObject(char) or clearDeployment() method to manage DEPLOYED state and symbol.");
        }
        this.type = type;
        this.deployedChar = ' '; // Clear deployed char if type is not DEPLOYED
        if (type != TileType.PLANTED) {
            this.plantedCrop = null; // Clear crop if not planted type
        }
    }

    public void deployObject(char c) {
        // Simplified validation, ensure this list is comprehensive for all map objects
        if (c == 'h' || c == 'o' || c == 's' || // FarmMap symbols
            c == 'S' || c == 'M' || c == 'C' || c == 'R' || c == 'G' || c == 'A' || c == 'B' || c == 'T' ||c == 'X' || c == 'O') { // CityMap symbols
            this.type = TileType.DEPLOYED;
            this.deployedChar = c;
            this.plantedCrop = null; // Cannot have a crop if an object is deployed
        } else {
            throw new IllegalArgumentException("Invalid deployed object character: " + c);
        }
    }

    public void clearDeployment() {
        if (this.type == TileType.DEPLOYED) {
            this.type = TileType.UNTILLED; // Default state after clearing an object
            this.deployedChar = ' ';
        }
    }

    public boolean isWalkable() {
        // Player can walk on UNTILLED and TILLED land.
        // PLANTED land might be walkable or not depending on game rules (usually walkable before full growth).
        // DEPLOYED land is generally not walkable, except for special cases (handled in map.isWalkable)
        return type == TileType.UNTILLED || type == TileType.TILLED || type == TileType.PLANTED;
    }

    public char displayChar() {
        switch (type) {
            case UNTILLED:  return '.';
            case TILLED:    return 't';
            case PLANTED:   return plantedCrop != null ? 'l' : 't'; // Show 'l' if crop, 't' if somehow planted but no crop
            case DEPLOYED:  return deployedChar;
        }
        return '?'; // Should not happen
    }

    // ADD THESE METHODS: equals() and hashCode()
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