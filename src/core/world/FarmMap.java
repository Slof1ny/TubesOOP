package core.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import core.house.*;
import core.player.Player;

public class FarmMap {
    class Pond extends DeployedObject {
        public Pond(int x, int y, int w, int h, char symbol) { 
            super(x, y, w, h, symbol); 
        }

        @Override 
        public void interact(Player p, FarmMap map) {
            if (adjacent(p, this)) {
                // map.getFishingManager().openPond(p);
            } else {
                System.out.println("You need to be next to the pond to fish.");
            }
        }
    }

    class ShippingBin extends DeployedObject {
        public ShippingBin(int x, int y, int w, int h, char symbol) { 
            super(x, y, w, h, symbol); 
        }

        @Override 
        public void interact(Player p, FarmMap map) {
            if (adjacent(p, this)) {
                // map.getShippingManager().openBin(p);
            } else {
                System.out.println("You need to be next to the shipping bin to use it.");
            }
        }
    }

    public static final int SIZE = 32;
    protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected List<DeployedObject> objects = new ArrayList<>();
    protected Player player;
    private final Random rng = new Random();

    public FarmMap(Player player) {
        this.player = player;
        initTiles();
        placeHouseAndPond();
        placeShippingBin();
        spawnPlayer();
    }

    public void initTiles() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Tile(x, y);
            }
        }
    }

    public Tile getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            return null;
        }
        return grid[x][y];
    }

    private void placeHouseAndPond() {
        int hx, hy;
        while (true) {
            hx = rng.nextInt(SIZE - 6 + 1);
            hy = rng.nextInt(SIZE - 6 + 1);
            if (areaFree(hx, hy, 6, 6)) break;
        }
        House house = new House(hx, hy, 6, 6, 'h');
        deployObject(house);

        int px, py;
        while (true) {
            px = rng.nextInt(SIZE - 4 + 1);
            py = rng.nextInt(SIZE - 3 + 1);
            if (areaFree(px, py, 4, 3)) break;
        }
        Pond pond = new Pond(px, py, 4, 3, 'o');
        deployObject(pond);
    }

    private void placeShippingBin() {
        House house = objects.stream()
                             .filter(o -> o instanceof House)
                             .map(o -> (House)o)
                             .findFirst()
                             .orElseThrow();
        int bx = house.getX() + house.getWidth() + 1;
        int by = house.getY();

        bx = Math.min(bx, SIZE - 3);
        ShippingBin bin = new ShippingBin(bx, by, 3, 2, 's');
        deployObject(bin);
    }

    private void deployObject(DeployedObject obj) {
        objects.add(obj);
        for (int dx = 0; dx < obj.width; dx++) {
            for (int dy = 0; dy < obj.height; dy++) {
                grid[obj.getX() + dx][obj.getY() + dy].deployObject(obj.getSymbol());
            }
        }
    }

    private boolean areaFree(int x, int y, int w, int h) {
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                if (!grid[x + dx][y + dy].isWalkable()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void spawnPlayer() {
    House house = objects.stream()
                         .filter(o -> o instanceof House)
                         .map(o -> (House)o)
                         .findFirst()
                         .orElseThrow();
    int px = house.getX() + house.getWidth() / 2;
    int py = house.getY() + house.getHeight() + 1;
    
    if (py >= SIZE) py = SIZE - 1;
    if (px >= SIZE) px = SIZE - 1;
    
    while (!isWalkable(px, py) && py > 0) {
        py--;
    }
    if (!isWalkable(px, py)) {
        boolean found = false;
        for (int dy = -3; dy <= 3 && !found; dy++) {
            for (int dx = -3; dx <= 3 && !found; dx++) {
                int newX = house.getX() + house.getWidth() / 2 + dx;
                int newY = house.getY() + house.getHeight() + 1 + dy;
                if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && isWalkable(newX, newY)) {
                    px = newX;
                    py = newY;
                    found = true;
                }
            }
        }
    }
    
    player.setPosition(px, py);
}

    public boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return false;
        if (!grid[x][y].isWalkable()) return false;
        for (DeployedObject o : objects) {
            if (o.occupies(x, y)) return false;
        }
        return true;
    }

    /** True if the player is on any edge tile */
    public boolean atEdge() {
        int x = player.getX(), y = player.getY();
        return x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1;
    }

    // /** True if the player stands adjacent (N/E/S/W) to object o */
    private boolean adjacent(Player p, DeployedObject o) {
        int px = p.getX(), py = p.getY();
        return px >= o.getX() - 1 && px <= o.getX() + o.getWidth()
            && py >= o.getY() - 1 && py <= o.getY() + o.getHeight()
            && !o.occupies(px, py);
    }


    public boolean movePlayer(int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        
        if (isWalkable(newX, newY)) {
            player.setPosition(newX, newY);
            return true;
        }
        return false;
    }

    public boolean movePlayerUp() {
        return movePlayer(0, -1);
    }

    public boolean movePlayerDown() {
        return movePlayer(0, 1);
    }

    public boolean movePlayerLeft() {
        return movePlayer(-1, 0);
    }

    public boolean movePlayerRight() {
        return movePlayer(1, 0);
    }

    public void displayFarmMap() {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (player != null && player.getX() == x && player.getY() == y) {
                    System.out.print("P ");
                } else {
                    System.out.print(grid[x][y].displayChar() + " ");
                }
            }
            System.out.println();
        }
    }
}
