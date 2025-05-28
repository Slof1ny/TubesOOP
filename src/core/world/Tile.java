package core.world;

import item.Crop;

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
        if (x < 0 || y < 0 || x >= FarmMap.SIZE || y >= FarmMap.SIZE) {
            throw new IllegalArgumentException("Coordinates out of bounds");
        }
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
        this.plantedCrop = crop;
        this.type = TileType.PLANTED;
    }

    public void clearPlantedCrop() {
        this.plantedCrop = null;
    }

    public void setType(TileType type) {
        if (type == TileType.DEPLOYED) {
            throw new IllegalStateException("Use deployObject method to mark a tile as DEPLOYED");
        }
        this.type = type;
        this.deployedChar = ' ';
        if (type != TileType.PLANTED) {
            plantedCrop = null;
        }
    }
    
    public void deployObject(char c) {
        if (c == 'h' || c == 'o' || c == 's') {
            this.type = TileType.DEPLOYED;
            this.deployedChar = c;
        }
        else {
            throw new IllegalArgumentException("Invalid deployed object character: " + c);
        }
        
    }

    public void clearDeployment() {
        this.type = TileType.UNTILLED;
        this.deployedChar = ' ';
    }

    public boolean isWalkable() {
        return type != TileType.DEPLOYED;
    }

    public char displayChar() {
        switch (type) {
            case UNTILLED:  return '.';
            case TILLED:    return 't';
            case PLANTED:   return 'l';
            case DEPLOYED:  return deployedChar;
        }
        return ' ';
    }
}
