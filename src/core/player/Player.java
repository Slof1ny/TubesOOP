package core.player;

import core.world.Tile;

public class Player {
    private static final int MAX_ENERGY = 100;
    private int x, y;
    private int energy = MAX_ENERGY;
    private Inventory inventory = new Inventory();

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

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        if (energy < 0) {
            this.energy = 0;
        } else if (energy > MAX_ENERGY) {
            this.energy = MAX_ENERGY;
        } else {
            this.energy = energy;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }
}
