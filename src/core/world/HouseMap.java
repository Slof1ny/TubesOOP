package core.world;

import java.awt.Point;
import core.player.Player;
import java.util.ArrayList;
import java.util.List;

// Simple DeployedObject for bed and TV interaction spots


public class HouseMap implements GameMap {
    public static final int SIZE = 24; // As per specification bonus
    private Tile[][] grid = new Tile[SIZE][SIZE];
    private List<DeployedObject> objects = new ArrayList<>();
    private Player player; // Keep a reference if needed for spawn logic relative to player

    private static final char BED_SYMBOL = 'B';
    private static final char TV_SYMBOL = 'T';
    private static final char EXIT_SYMBOL = 'X'; // To leave the house
    private static final char STOVE_SYMBOL = 'S';

    // Define specific coordinates for interaction spots
    public static final Point BED_LOCATION = new Point(5, 5); // Example location
    public static final Point TV_LOCATION = new Point(5, 10);  // Example location
    public static final Point STOVE_LOCATION = new Point(10, 5);
    public static final Point EXIT_LOCATION = new Point(SIZE / 2, SIZE - 1); // Example exit (e.g., "front door" at the bottom edge)
    public static final Point ENTRY_LOCATION = new Point(SIZE / 2, SIZE - 2); // Where player appears when entering


    public HouseMap(Player player) {
        this.player = player;
        initTiles();
        placeFurnitureAndSpots();
        // spawnPlayer(player); // Player position will be set on transition
    }

    private void initTiles() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                // For simplicity, make all house floor tiles walkable by default
                grid[x][y] = new Tile(x, y);
                // You can add wall logic here if desired by setting some tiles as non-walkable
                // e.g., if (x == 0 || x == SIZE -1 || y == 0 || y == SIZE -1) grid[x][y].setType(Tile.TileType.DEPLOYED); // Basic walls
            }
        }
    }

    private void placeFurnitureAndSpots() {
        // Bed Interaction Spot
        InteractionSpot bed = new InteractionSpot(BED_LOCATION.x, BED_LOCATION.y, BED_SYMBOL, "BED");
        deployObject(bed, grid[BED_LOCATION.x][BED_LOCATION.y]);

        // TV Interaction Spot
        InteractionSpot tv = new InteractionSpot(TV_LOCATION.x, TV_LOCATION.y, TV_SYMBOL, "TV");
        deployObject(tv, grid[TV_LOCATION.x][TV_LOCATION.y]);

        // Stove Interaction Spot
        InteractionSpot stove = new InteractionSpot(STOVE_LOCATION.x, STOVE_LOCATION.y, STOVE_SYMBOL, "STOVE"); // "STOVE" type
        deployObject(stove, grid[STOVE_LOCATION.x][STOVE_LOCATION.y]);
        
        // Exit Spot
        InteractionSpot exit = new InteractionSpot(EXIT_LOCATION.x, EXIT_LOCATION.y, EXIT_SYMBOL, "EXIT_TO_FARM");
        deployObject(exit, grid[EXIT_LOCATION.x][EXIT_LOCATION.y]);

        // You could add more complex furniture as DeployedObjects that might be non-walkable
        // For example, a 2x1 table:
        // DeployedObject table = new DeployedObject(10, 10, 2, 1, '#') {
        //     @Override public boolean isWalkable() { return false; }
        //     @Override public void interact(Player p, GameMap map) {}
        // };
        // deployObject(table, null); // Pass null if deploying multi-tile object not on a single tile's char
    }

    private void deployObject(DeployedObject obj, Tile targetTileForSymbol) {
        objects.add(obj);
        for (int dx = 0; dx < obj.getWidth(); dx++) {
            for (int dy = 0; dy < obj.getHeight(); dy++) {
                int currentX = obj.getX() + dx;
                int currentY = obj.getY() + dy;
                if (currentX < SIZE && currentY < SIZE && currentX >= 0 && currentY >= 0) {
                    // If it's a simple 1x1 spot, set its symbol on its tile
                    if (targetTileForSymbol != null && obj.getWidth() == 1 && obj.getHeight() == 1 && dx==0 && dy==0) {
                        grid[currentX][currentY].deployObject(obj.getSymbol());
                    } else if (targetTileForSymbol == null) { // For multi-tile objects or ones without a specific tile char
                         grid[currentX][currentY].setType(Tile.TileType.DEPLOYED); // Mark as deployed
                         if (!obj.isWalkable()) {
                            // if tile has its own isWalkable, set it here.
                            // This simplistic Tile model uses TileType for walkability.
                         }
                    }
                }
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

    @Override
    public boolean isWalkable(int x, int y) {
        if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) return false;
        Tile tile = grid[x][y];
        if (tile.getType() == Tile.TileType.DEPLOYED) {
            // Check if there's a specific DeployedObject here that might be walkable (like our InteractionSpot)
            for (DeployedObject obj : objects) {
                if (obj.occupies(x, y)) {
                    return obj.isWalkable(); // Defer to the object
                }
            }
            return false; // Generic deployed tile is not walkable
        }
        return tile.isWalkable(); // For UNTILLED, TILLED, PLANTED (though not relevant in house)
    }

    @Override
    public boolean movePlayer(Player player, int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;

        if (isWalkable(newX, newY)) { // isWalkable now checks within bounds
            player.setPosition(newX, newY);
            return true;
        }
        return false;
    }

    @Override
    public boolean atEdge(Player player) {
        // This might not be used for transitions if we use an EXIT_SYMBOL spot
        int x = player.getX();
        int y = player.getY();
        return x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1;
    }

    @Override
    public void displayMap(Player player) { // For console debugging
        System.out.println("--- " + getName() + " ---");
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (player != null && player.getX() == x && player.getY() == y && player.getLocation().equals(getName())) {
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
        return "House Interior";
    }

    @Override
    public int getSize() {
        return SIZE;
    }
}