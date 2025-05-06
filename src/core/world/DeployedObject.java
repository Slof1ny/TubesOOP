package core.world;

import core.player.*;

public abstract class DeployedObject {
    protected int x, y;
    protected int width, height;

    public DeployedObject(int x, int y, int w, int h, char symbol) {
        this.x = x; 
        this.y = y;
        this.width = w; 
        this.height = h;
    }

    public boolean occupies(int tx, int ty) {
        return tx >= x && tx < x+width && ty >= y && ty < y+height;
    }

    public boolean isWalkable() {
        return false;
    }

    public abstract void interact(Player p, FarmMap map);
}