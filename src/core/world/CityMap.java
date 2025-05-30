package core.world;

import core.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CityMap implements GameMap {
    public static final int SIZE = 20;
    protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected List<DeployedObject> objects = new ArrayList<>();
    private final Random rng = new Random();
    private String name = "City Map";

    private static final char STORE_SYMBOL = 'S';
    private static final char MAYOR_MANOR_SYMBOL = 'M';
    private static final char CARPENTRY_SYMBOL = 'C';
    private static final char PERRY_CABIN_SYMBOL = 'R';
    private static final char GAMBLING_DEN_SYMBOL = 'G';
    private static final char ABIGAIL_TENT_SYMBOL = 'A';
    private static final char EXIT_SYMBOL = 'X';
    private static final char ORENJI_SYMBOL = 'O';
    private static final char FENCE_SYMBOL = 'F';

    public CityMap(Player player) {
        initTiles();
        placeCityBuildings();
        spawnPlayer(player);
    }

    private void initTiles() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Tile(x, y);
            }
        }
    }

    private void placeCityBuildings() {
        // Updated to consistent /assets/png/ paths
        deployObject(new Building("Emily's Store", 3, 3, 4, 3, STORE_SYMBOL, "/resources/asset/png/House_2_Emily.png"));
        deployObject(new Building("Mayor's Manor", 15, 2, 5, 4, MAYOR_MANOR_SYMBOL, "/resources/asset/png/House_2_Mayor.png"));
        deployObject(new Building("Caroline's Carpentry", 2, 10, 4, 4, CARPENTRY_SYMBOL, "/resources/asset/png/House_2_Caroline.png"));
        deployObject(new Building("Perry's Cabin", 10, 15, 3, 3, PERRY_CABIN_SYMBOL, "/resources/asset/png/House_2_Perry.png"));
        deployObject(new Building("Dasco's Gambling Den", 16, 12, 4, 5, GAMBLING_DEN_SYMBOL, "/resources/asset/png/House_2_Dasco.png"));
        deployObject(new Building("Abigail's Tent", 5, 17, 2, 2, ABIGAIL_TENT_SYMBOL, "/resources/asset/png/House_2_Abigail.png"));
        deployObject(new Building("Orenji si Kucing Barista", 10, 8, 1, 1, ORENJI_SYMBOL, "/resources/asset/png/House_2_Orenji.png"));

        deployObject(new DeployedObject(SIZE / 2, SIZE - 1, 1, 1, EXIT_SYMBOL) {
            @Override
            public void interact(Player p, FarmMap map) {
                System.out.println("You are at the exit to the Farm!");
            }
            @Override public boolean isWalkable() { return true; }
        });

        // Ensure Fence.png is also moved to /assets/png/ or keep its unique path consistently
        // For consistency, let's assume Fence.png is now also in /assets/png/
        for (int i = 0; i < SIZE; i++) {
            if (grid[i][SIZE - 1].displayChar() == '.') {
                deployObject(new Building("Fence", i, SIZE - 1, 1, 1, FENCE_SYMBOL, "/resources/asset/png/Fence.png"));
            }
        }
    }

    public class Building extends DeployedObject {
        private String buildingName;
        private String imagePath;

        public Building(String buildingName, int x, int inty, int w, int h, char symbol, String imagePath) {
            super(x, inty, w, h, symbol);
            this.buildingName = buildingName;
            this.imagePath = imagePath;
        }

        @Override
        public void interact(Player p, FarmMap map) {
            System.out.println("You are interacting with " + buildingName + ".");
        }

        public String getBuildingName() {
            return buildingName;
        }

        public String getImagePath() {
            return imagePath;
        }
    }

    private void deployObject(DeployedObject obj) {
        objects.add(obj);
        for (int dx = 0; dx < obj.width; dx++) {
            for (int dy = 0; dy < obj.height; dy++) {
                grid[obj.getX() + dx][obj.getY() + dy].deployObject(obj.getSymbol());
            }
        }
    }

    private void spawnPlayer(Player player) {
        int spawnX = SIZE / 2;
        int spawnY = SIZE / 2;
        while (!isWalkable(spawnX, spawnY) && spawnY < SIZE) {
            spawnY++;
        }
        player.setPosition(spawnX, spawnY);
    }

    @Override
    public Tile getTileAt(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
            return null;
        }
        return grid[x][y];
    }

    @Override
    public boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return false;
        for (DeployedObject obj : objects) {
            if (obj.occupies(x, y)) {
                return obj.isWalkable();
            }
        }
        return grid[x][y].isWalkable();
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
    public boolean atEdge(Player player) {
        int x = player.getX();
        int y = player.getY();
        return x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1;
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
}