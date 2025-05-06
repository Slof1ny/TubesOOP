package core.world;

import item.Crop;

public class Tile {
    enum TileType {
        UNTILLED,  
        TILLED,   
        PLANTED,
        DEPLOYED   
    }
    
    private final int x, y;
    private TileType type;
    private Crop plantedCrop;
    private FarmMap map;

    public Tile(int x, int y) throws IllegalArgumentException { 
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException("Coordinates must be non-negative.");
        }
        else if (map.grid[x][y].type == TileType.DEPLOYED) {
            throw new IllegalArgumentException("Tile already exists at this location.");
        }
        
        this.x = x; 
        this.y = y;
        this.type = TileType.UNTILLED;
        this.plantedCrop = null;
    }

    public boolean isWalkable() {
        return type != TileType.DEPLOYED;
    }
}
