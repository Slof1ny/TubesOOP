package core.world;

import core.player.*;

public abstract class DeployedObject {
    protected int x, y;
    protected int width, height;
    protected char symbol;

    public DeployedObject(int x, int y, int w, int h, char symbol) {
        this.x = x; 
        this.y = y;
        this.width = w; 
        this.height = h;
        this.symbol = symbol;
    }

    public boolean occupies(int tx, int ty) {
        return tx >= x && tx < x+width && ty >= y && ty < y+height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public char getSymbol() {
        return symbol;
    }
    
    public boolean isWalkable() {
        return false;
    }

    public abstract void interact(Player p, FarmMap map);
}