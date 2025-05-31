package core.world;

import core.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A CityMap with a fixed "island in a lake" layout, now using CityTile instead of generic Tile.
 */
public class CityMap implements GameMap {
    public static final int SIZE = 20;
    // ------------------------------------------------------
    // STEP 1: Change the grid to hold CityTile instead of Tile:
    // ------------------------------------------------------
    //protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected CityTile[][] grid = new CityTile[SIZE][SIZE];

    protected List<DeployedObject> objects = new ArrayList<>();
    private final Random rng = new Random();
    private String name = "City Map";

    // (All building/terrain symbols as before...)
    private static final char STORE_SYMBOL = 'S';
    private static final char MAYOR_MANOR_SYMBOL = 'M';
    private static final char CARPENTRY_SYMBOL = 'C';
    private static final char PERRY_CABIN_SYMBOL = 'R';
    private static final char GAMBLING_DEN_SYMBOL = 'G';
    private static final char ABIGAIL_TENT_SYMBOL = 'A';
    private static final char EXIT_SYMBOL = 'X';
    private static final char ORENJI_SYMBOL = 'O';
    private static final char FENCE_SYMBOL = 'F';

    // Terrain symbols
    private static final char JALAN_SYMBOL = '.';         // Default road
    private static final char RUMPUT_HIJAU_SYMBOL = '1';  // Grass
    private static final char BATU_SYMBOL = '2';          // Stone
    private static final char AIR_SYMBOL = '3';           // Water
    private static final char BUNGA_PINK_SYMBOL = '4';    // Flower (pink)
    private static final char TANAH_SYMBOL = '5';         // Dirt

    public CityMap(Player player) {
        initTiles();          
        placeCityBuildings(); 
        spawnPlayer(player);  
    }

    /**
     * Build a static City layout, BUT now every tile is a CityTile.
     */
    private void initTiles() {
        // 1) Fill all with water (’3’)
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                //---------------------------------------------------
                // STEP 2: Instantiate CityTile instead of Tile
                //---------------------------------------------------
                // grid[x][y] = new Tile(x, y);
                grid[x][y] = new CityTile(x, y);
                grid[x][y].deployObject(AIR_SYMBOL);
            }
        }

        // 2) Carve out a big grass island inside the water border
        for (int x = 1; x < SIZE - 1; x++) {
            for (int y = 1; y < SIZE - 1; y++) {
                grid[x][y].deployObject(RUMPUT_HIJAU_SYMBOL);
            }
        }

        // 3) Central dirt plaza...
        int plazaHalf = 3;
        int centerX = SIZE / 2;
        int centerY = SIZE / 2;

        for (int dx = -plazaHalf; dx < plazaHalf; dx++) {
            for (int dy = -plazaHalf; dy < plazaHalf; dy++) {
                int tx = centerX + dx;
                int ty = centerY + dy;
                if (inBounds(tx, ty)) {
                    grid[tx][ty].deployObject(TANAH_SYMBOL);
                }
            }
        }

        // 3b) Surround with a ring of flowers (’4’):
        int ringOuter = plazaHalf + 1;
        for (int dx = -ringOuter; dx <= ringOuter; dx++) {
            for (int dy = -ringOuter; dy <= ringOuter; dy++) {
                int tx = centerX + dx;
                int ty = centerY + dy;
                if ((Math.abs(dx) == ringOuter || Math.abs(dy) == ringOuter) && inBounds(tx, ty)) {
                    grid[tx][ty].deployObject(BUNGA_PINK_SYMBOL);
                }
            }
        }

        // 4) Four straight road spokes (’.’) from center to each edge:
        for (int y = 0; y < SIZE; y++) {
            if (inBounds(centerX, y)) {
                grid[centerX][y].deployObject(JALAN_SYMBOL);
            }
        }
        for (int x = 0; x < SIZE; x++) {
            if (inBounds(x, centerY)) {
                grid[x][centerY].deployObject(JALAN_SYMBOL);
            }
        }

        // 5) Decorative stone patches (’2’) and flower clumps (’4’):
        int[][] stoneCenters = {
            { centerX - 5, centerY - 3 },
            { centerX + 4, centerY + 2 },
            { centerX - 2, centerY + 5 }
        };
        for (int[] sc : stoneCenters) {
            placeCircle(sc[0], sc[1], 2, BATU_SYMBOL);
        }

        int[][] flowerCenters = {
            { centerX - 4, centerY + 4 },
            { centerX + 3, centerY - 5 },
            { centerX + 5, centerY + 1 }
        };
        for (int[] fc : flowerCenters) {
            placeCircle(fc[0], fc[1], 2, BUNGA_PINK_SYMBOL);
        }

        // (Optional random sprinkle omitted)
    }

    private void placeCircle(int cx, int cy, int radius, char c) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int tx = cx + dx;
                int ty = cy + dy;
                if (inBounds(tx, ty) && dx*dx + dy*dy <= radius*radius) {
                    grid[tx][ty].deployObject(c);
                }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    private void placeCityBuildings() {
        deployObject(new Building(
            "Emily's Store",
            3, 3, 4, 3,
            STORE_SYMBOL,
            "/resources/asset/png/House_2_Emily.png"
        ));

        deployObject(new Building(
            "Mayor's Manor",
            15, 2, 5, 4,
            MAYOR_MANOR_SYMBOL,
            "/resources/asset/png/House_2_Mayor.png"
        ));

        deployObject(new Building(
            "Caroline's Carpentry",
            2, 10, 4, 4,
            CARPENTRY_SYMBOL,
            "/resources/asset/png/House_2_Caroline.png"
        ));

        deployObject(new Building(
            "Perry's Cabin",
            10, 15, 3, 3,
            PERRY_CABIN_SYMBOL,
            "/resources/asset/png/House_2_Perry.png"
        ));

        deployObject(new Building(
            "Dasco's Gambling Den",
            16, 12, 4, 5,
            GAMBLING_DEN_SYMBOL,
            "/resources/asset/png/House_2_Dasco.png"
        ));

        deployObject(new Building(
            "Abigail's Tent",
            5, 17, 2, 2,
            ABIGAIL_TENT_SYMBOL,
            "/resources/asset/png/House_2_Abigail.png"
        ));

        deployObject(new Building(
            "Orenji si Kucing Barista",
            10, 8, 1, 1,
            ORENJI_SYMBOL,
            "/resources/asset/png/House_2_Orenji.png"
        ));

        // Exit at bottom center:
        deployObject(new DeployedObject(SIZE/2, SIZE - 1, 1, 1, EXIT_SYMBOL) {
            @Override
            public void interact(Player p, FarmMap map) {
                System.out.println("You are at the exit to the Farm!");
            }
            @Override public boolean isWalkable() {
                return true;
            }
        });

        // Place fences along bottom edge
        for (int x = 0; x < SIZE; x++) {
            if (x == SIZE/2) continue;
            char below = grid[x][SIZE - 1].displayChar();
            if (below == JALAN_SYMBOL || below == RUMPUT_HIJAU_SYMBOL || below == TANAH_SYMBOL) {
                deployObject(new Building(
                    "Fence",
                    x, SIZE - 1,
                    1, 1,
                    FENCE_SYMBOL,
                    "/resources/asset/png/Fence.png"
                ) {
                    @Override public boolean isWalkable() {
                        return false;
                    }
                });
            }
        }
    }

    private void deployObject(DeployedObject obj) {
        objects.add(obj);
        for (int dx = 0; dx < obj.getWidth(); dx++) {
            for (int dy = 0; dy < obj.getHeight(); dy++) {
                int targetX = obj.getX() + dx;
                int targetY = obj.getY() + dy;
                if (inBounds(targetX, targetY)) {
                    grid[targetX][targetY].deployObject(obj.getSymbol());
                }
            }
        }
    }

    private void spawnPlayer(Player player) {
        int spawnX = SIZE / 2;
        int spawnY = SIZE / 2;
        while (!isWalkable(spawnX, spawnY) && spawnY < SIZE - 1) {
            spawnY++;
        }
        player.setPosition(spawnX, spawnY);
    }

    @Override
    public CityTile getTileAt(int x, int y) {
        if (!inBounds(x, y)) return null;
        return grid[x][y];
    }

    @Override
    public boolean isWalkable(int x, int y) {
        if (!inBounds(x, y)) return false;
        for (DeployedObject obj : objects) {
            if (obj.occupies(x, y)) {
                return obj.isWalkable();
            }
        }
        // If no deployed object, check the tile’s terrain:
        char terrainChar = grid[x][y].displayChar();
        return terrainChar != AIR_SYMBOL;
    }

    @Override
    public boolean movePlayer(Player player, int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        if (inBounds(newX, newY) && isWalkable(newX, newY)) {
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

    public class Building extends DeployedObject {
        private String buildingName;
        private String imagePath;

        public Building(String buildingName, int x, int y, int w, int h, char symbol, String imagePath) {
            super(x, y, w, h, symbol);
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
}
