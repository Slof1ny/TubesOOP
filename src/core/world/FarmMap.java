package core.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import core.house.*;
import core.player.Player;
import item.Crop;


public class FarmMap implements GameMap {
    public static final int SIZE = 32;
    protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected List<DeployedObject> objects = new ArrayList<>();
    private final Random rng = new Random();
    private String name = "Farm Map";

    public FarmMap(Player player) {
        initTiles();
        placeHouseAndPond();
        placeShippingBin();
        spawnPlayer(player); // Pass player to spawn method
    }

    public void initTiles() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Tile(x, y);
            }
        }
    }

    @Override
    public Tile getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            return null;
        }
        return grid[x][y];
    }

    private void placeHouseAndPond() {
        int hx, hy;
        while (true) {
            hx = rng.nextInt(SIZE - 6);
            hy = rng.nextInt(SIZE - 6);
            if (areaFree(hx, hy, 6, 6)) break;
        }
        House house = new House(hx, hy, 6, 6, 'h');
        deployObject(house);

        int px, py;
        while (true) {
            px = rng.nextInt(SIZE / 2) + SIZE / 2 - 4;
            py = rng.nextInt(SIZE / 2) + SIZE / 2 - 3;

            px = Math.max(0, Math.min(px, SIZE - 4));
            py = Math.max(0, Math.min(py, SIZE - 3));

            if (areaFree(px, py, 4, 3)) break;
            if (System.currentTimeMillis() % 100 == 0) {
                 px = 25;
                 py = 25;
                 if (areaFree(px,py,4,3)) break;
            }
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
                if (x + dx < 0 || x + dx >= SIZE || y + dy < 0 || y + dy >= SIZE) {
                    return false;
                }
                if (!grid[x + dx][y + dy].isWalkable()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void spawnPlayer(Player player) { // Now accepts player as parameter
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
        // Player location set by GameManager when setting current map
    }

    @Override
    public boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return false;
        if (!grid[x][y].isWalkable()) return false;
        for (DeployedObject o : objects) {
            if (o.occupies(x, y)) return false;
        }
        return true;
    }

    @Override
    public boolean atEdge(Player player) {
        int x = player.getX(), y = player.getY();
        return x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1;
    }

    @Override
    public boolean movePlayer(Player player, int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && isWalkable(newX, newY)) {
            player.setPosition(newX, newY);
            return true;
        }
        return false;
    }

    @Override
    public void displayMap(Player player) {
        System.out.println("--- " + getName() + " ---");
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

    @Override
    public List<DeployedObject> getDeployedObjects() {
        return objects;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getSize() {
        return SIZE;
    }

    public void updateDailyCropGrowth(int currentDay, Season currentSeason, boolean wasYesterdayRainy) {
        System.out.println("FarmMap: Updating daily crop growth for day " + currentDay + ". Yesterday rainy: " + wasYesterdayRainy);
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Tile tile = grid[x][y];
                if (tile != null && tile.getType() == Tile.TileType.PLANTED && tile.getPlantedCrop() != null) {
                    Crop crop = tile.getPlantedCrop();
                    // Pass necessary info to crop's newDay method
                    crop.newDay(currentDay, currentSeason, wasYesterdayRainy);
                }
            }
        }
    }

    // --- Map transition helpers (see GameMap interface) ---
    // Map transition helpers (tidak pakai @Override karena belum ada di interface GameMap)
    public int getExitToCityX() { return SIZE / 2; }
    public int getExitToCityY() { return SIZE - 1; }
    public int getEntryFromCityX() { return SIZE / 2; }
    public int getEntryFromCityY() { return 0; }

    // Tetap pertahankan helper untuk house exit agar tidak break logic lain
    public int getHouseExitSpawnX() {
        for (DeployedObject obj : objects) {
            if (obj instanceof core.house.House) {
                return obj.getX() + obj.getWidth() / 2;
            }
        }
        return SIZE / 2;
    }
    public int getHouseExitSpawnY() {
        for (DeployedObject obj : objects) {
            if (obj instanceof core.house.House) {
                return obj.getY() + obj.getHeight();
            }
        }
        return SIZE - 2;
    }
}