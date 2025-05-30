package core.world;

import core.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random; // Kept for consistency, though not heavily used here

public class CityMap implements GameMap {
    public static final int SIZE = 20; // Size of the city map (e.g., 20x20 tiles)
    protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected List<DeployedObject> objects = new ArrayList<>();
    private final Random rng = new Random(); // Not heavily used for fixed city layout, but kept for consistency
    private String name = "City Map";

    // Define symbols for city buildings (could be extended later)
    private static final char STORE_SYMBOL = 'S'; // Emily's Store
    private static final char MAYOR_MANOR_SYMBOL = 'M'; // Mayor Tadi's Manor
    private static final char CARPENTRY_SYMBOL = 'C'; // Caroline's Carpentry
    private static final char PERRY_CABIN_SYMBOL = 'R'; // Perry's Cabin (using R to avoid 'P' player clash)
    private static final char GAMBLING_DEN_SYMBOL = 'G'; // Dasco's Gambling Den
    private static final char ABIGAIL_TENT_SYMBOL = 'A'; // Abigail's Tent
    private static final char EXIT_SYMBOL = 'X'; // Exit to Farm

    public CityMap(Player player) { // Player passed for initial spawn logic
        initTiles();
        placeCityBuildings();
        spawnPlayer(player); // Initial spawn point for player in the city
    }

    private void initTiles() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Tile(x, y);
            }
        }
    }

    private void placeCityBuildings() {
        // Place Emily's Store (Building type)
        deployObject(new Building("Emily's Store", 3, 3, 4, 3, STORE_SYMBOL));

        // Place Mayor's Manor
        deployObject(new Building("Mayor's Manor", 15, 2, 5, 4, MAYOR_MANOR_SYMBOL));

        // Place Caroline's Carpentry
        deployObject(new Building("Caroline's Carpentry", 2, 10, 4, 4, CARPENTRY_SYMBOL));

        // Place Perry's Cabin
        deployObject(new Building("Perry's Cabin", 10, 15, 3, 3, PERRY_CABIN_SYMBOL));

        // Place Dasco's Gambling Den
        deployObject(new Building("Dasco's Gambling Den", 16, 12, 4, 5, GAMBLING_DEN_SYMBOL));

        // Place Abigail's Tent
        deployObject(new Building("Abigail's Tent", 7, 18, 2, 2, ABIGAIL_TENT_SYMBOL));

        // Place an exit point to the farm (e.g., at the bottom center edge)
        deployObject(new DeployedObject(SIZE / 2, SIZE - 1, 1, 1, EXIT_SYMBOL) {
            @Override
            public void interact(Player p, FarmMap map) {
                System.out.println("You are at the exit to the Farm!");
                // Actual transition logic will be in the controller
            }
            @Override public boolean isWalkable() { return true; } // Exit tile should be walkable to stand on
        });
    }

    // A generic building class for city structures
    public class Building extends DeployedObject { // Make public so CityMapController can access
        private String buildingName;

        public Building(String buildingName, int x, int y, int w, int h, char symbol) {
            super(x, y, w, h, symbol);
            this.buildingName = buildingName;
        }

        @Override
        public void interact(Player p, FarmMap map) { // Note: map parameter might be irrelevant for city buildings
            System.out.println("You are interacting with " + buildingName + ".");
            // Specific interaction (e.g., opening store for Emily's Store) will be handled in controller
        }

        public String getBuildingName() {
            return buildingName;
        }
    }


    private void deployObject(DeployedObject obj) {
        objects.add(obj);
        for (int dx = 0; dx < obj.width; dx++) {
            for (int dy = 0; dy < obj.height; dy++) {
                int tx = obj.getX() + dx, ty = obj.getY() + dy;
                grid[tx][ty].deployObject(obj.getSymbol());
                // If the object is not walkable, ensure the tile is marked as DEPLOYED (not walkable by Tile.isWalkable)
                // This is for extra safety, but pathfinding and movement should always use map.isWalkable
            }
        }
    }

    private void spawnPlayer(Player player) {
        // Simple spawn in the middle or near an entrance point for now
        int spawnX = SIZE / 2;
        int spawnY = SIZE / 2;
        // Ensure spawn is on a walkable tile
        while (!isWalkable(spawnX, spawnY) && spawnY < SIZE) {
            spawnY++;
        }
        player.setPosition(spawnX, spawnY);
        // Player location set by GameManager when setting current map
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
            if (obj.occupies(x, y) && !obj.isWalkable()) return false;
        }
        // If any object occupies and is walkable (like EXIT), allow it
        for (DeployedObject obj : objects) {
            if (obj.occupies(x, y) && obj.isWalkable()) return true;
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