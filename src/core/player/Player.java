package core.player;

import core.world.Tile;

public class Player {
    private int x, y;

    public Player() {
        this.x = 0;
        this.y = 0;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getLocation() {
        return " ";
    }

    public Tile getCurrentTile() {
        return null;
    }
}
