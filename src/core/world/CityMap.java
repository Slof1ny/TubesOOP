package core.world;

import core.player.Player;
import java.util.ArrayList;
import java.util.List;

public class CityMap implements GameMap {
    public static final int SIZE = 20;
    protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected List<DeployedObject> objects = new ArrayList<>();
    private String name = "City Map";

    // City building/object symbols (matching constants in Tile.java)
    private static final char STORE_SYMBOL = Tile.STORE_SYMBOL;
    private static final char MAYOR_MANOR_SYMBOL = Tile.MAYOR_MANOR_SYMBOL;
    private static final char CARPENTRY_SYMBOL = Tile.CARPENTRY_SYMBOL;
    private static final char PERRY_CABIN_SYMBOL = Tile.PERRY_CABIN_SYMBOL; // 'P'
    private static final char GAMBLING_DEN_SYMBOL = Tile.GAMBLING_DEN_SYMBOL;
    private static final char ABIGAIL_TENT_SYMBOL = Tile.ABIGAIL_TENT_SYMBOL;
    private static final char EXIT_SYMBOL = Tile.EXIT_SYMBOL;
    private static final char ORENJI_SYMBOL = Tile.ORENJI_SYMBOL;
    private static final char FENCE_SYMBOL = Tile.FENCE_SYMBOL;

    // Terrain symbols (matching constants in Tile.java)
    private static final char JALAN_SYMBOL = Tile.DEFAULT_UNTILLED_CHAR; // '.'
    private static final char RUMPUT_HIJAU_SYMBOL = Tile.RUMPUT_HIJAU_SYMBOL; // '1'
    private static final char BATU_SYMBOL = Tile.BATU_SYMBOL;          // '2'
    private static final char AIR_SYMBOL = Tile.AIR_SYMBOL;           // '3'
    private static final char BUNGA_PINK_SYMBOL = Tile.BUNGA_PINK_SYMBOL;    // '4'
    private static final char TANAH_SYMBOL = Tile.TANAH_SYMBOL;         // '5'
    private static final char JALAN_COKLAT_KIRI = Tile.JALAN_COKLAT_KIRI_SYMBOL; // 'K'
    private static final char JALAN_COKLAT_KANAN = Tile.JALAN_COKLAT_KANAN_SYMBOL; // 'N'
    private static final char JALAN_COKLAT_ATAS = Tile.JALAN_COKLAT_ATAS_SYMBOL; // 'U'
    private static final char JALAN_COKLAT_BAWAH = Tile.JALAN_COKLAT_BAWAH_SYMBOL; // 'D'
    private static final char JALAN_TENGAH = Tile.JALAN_TENGAH_SYMBOL; // 'T'


    public CityMap(Player player) {
        initTiles();          
        placeCityBuildings(); 
        spawnPlayer(player);  
    }

    /**
     * Builds a static City layout, filling tiles with varied terrain.
     */
    private void initTiles() {
        // 1) Fill all with water (’3’)
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                grid[x][y] = new Tile(x, y);
                grid[x][y].deployObject(AIR_SYMBOL);
            }
        }

        // 2) Carve out a big grass island inside the water border
        for (int x = 1; x < SIZE - 1; x++) {
            for (int y = 1; y < SIZE - 1; y++) {
                grid[x][y].deployObject(RUMPUT_HIJAU_SYMBOL);
            }
        }

        // 3) Central dirt plaza (6x6) and flower ring
        int centerX = SIZE / 2, centerY = SIZE / 2; // (10,10) for 20x20
        int plazaHalf = 3; // 6x6 area
        // a) Fill 6x6 with dirt ('5')
        for (int dx = -plazaHalf; dx < plazaHalf; dx++) {
            for (int dy = -plazaHalf; dy < plazaHalf; dy++) {
                int tx = centerX + dx, ty = centerY + dy;
                if (inBounds(tx, ty)) grid[tx][ty].deployObject(BUNGA_PINK_SYMBOL);
            }
        }
        // b) Surround with a ring of flowers (’4’):
        int ringOuter = plazaHalf + 1; 
        for (int dx = -ringOuter; dx <= ringOuter; dx++) {
            for (int dy = -ringOuter; dy < ringOuter; dy++) {
                int tx = centerX + dx, ty = centerY + dy;
                if (inBounds(tx, ty) && (Math.abs(dx) == ringOuter + 3 || Math.abs(dy) == ringOuter + 3)) {
                    grid[tx][ty].deployObject(BUNGA_PINK_SYMBOL);
                }
            }
        }

        // Road to Emily's Store (3,3) - connect to central road (centerX-1, 7)
        placeRoadSegment(3, 6, centerX - 2, 6, RUMPUT_HIJAU_SYMBOL); // Path from building (3,3) to central road (9,7)
        placeRoadSegment(3, 7, centerX - 2, 7, RUMPUT_HIJAU_SYMBOL); // Second lane of road
        
        // Road to Mayor's Manor (15,2) - connect to central road (centerX, 5)
        placeRoadSegment(17, 5, centerX + 1, 5, RUMPUT_HIJAU_SYMBOL); // Path from Mayor (17,2) to central road (10,5)

        // Road to Perry's Cabin (10,15) - connect to central road (centerX-1, 14)
        placeRoadSegment(11, 11, centerX - 1, 19, TANAH_SYMBOL); // Path from building (10,15) to central road (9,14)

        // Road to Dasco's Gambling Den (16,12) - connect to central road (centerX, 11)
        placeRoadSegment(18, 11, centerX + 1, 11, RUMPUT_HIJAU_SYMBOL); // Path from building (16,12) to central road (10,11)

        // Road to Abigail's Tent (5,17) - connect to central road (centerX-1, 17)
        placeRoadSegment(6, 17, centerX - 2, 17, RUMPUT_HIJAU_SYMBOL); // Path from building (5,17) to central road (9,17)

        // Road to Orenji's Cafe (10,8) - connect to central road (centerX-1, 9)
        placeRoadSegment(10, 9, centerX - 2, 9, RUMPUT_HIJAU_SYMBOL); // Path from building (10,8) to central road (9,9)

        // NEW: 2-tile wide central roads + specific placements
        // [9][0] - [9][19] except [9][9] and [9][10] = jalan_coklatKiri.png
        for (int y = 0; y < SIZE; y++) {
            if (y == 9 || y == 10) continue; // Skip central intersection
            if (inBounds(9, y)) grid[9][y].deployObject(JALAN_COKLAT_KIRI);
        }
        // [10][0] - [10][19] except [10][9] and [10][10] = jalan_coklatKanan.png
        for (int y = 0; y < SIZE; y++) {
            if (y == 9 || y == 10) continue; // Skip central intersection
            if (inBounds(10, y)) grid[10][y].deployObject(JALAN_COKLAT_KANAN);
        }

        // [0][9] - [19][9] = jalan_coklatAtas.png
        for (int x = 0; x < SIZE; x++) {
            if (inBounds(x, 9)) grid[x][9].deployObject(JALAN_COKLAT_ATAS);
        }
        // [0][10] - [19][10] = jalan_coklatBawah.png
        for (int x = 0; x < SIZE; x++) {
            if (inBounds(x, 10)) grid[x][10].deployObject(JALAN_COKLAT_BAWAH);
        }

        for (int y = 6; y <= 9; y++) {
            if (inBounds(4, 9)) grid[4][9].deployObject(JALAN_TENGAH);
        }
        for (int y = 6; y <= 9; y++) {
            if (inBounds(4, y)) grid[4][y].deployObject(JALAN_COKLAT_KIRI);
            if (inBounds(5, y)) grid[5][y].deployObject(JALAN_COKLAT_KANAN);
            if (inBounds(4, 9)) grid[4][9].deployObject(JALAN_TENGAH);
            if (inBounds(4, 9)) grid[5][9].deployObject(JALAN_TENGAH);
        }

        // Correct the intersection of the two-tile wide roads (they should be plain road)
        grid[9][9].deployObject(JALAN_TENGAH);
        grid[10][9].deployObject(JALAN_TENGAH);
        grid[9][10].deployObject(JALAN_TENGAH);
        grid[10][10].deployObject(JALAN_TENGAH);
        grid[11][9].deployObject(JALAN_TENGAH);
    }
    
    // Helper to place a straight road segment between two points
    private void placeRoadSegment(int x1, int y1, int x2, int y2, char roadSymbol) {
        // Horizontal segment
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            if (inBounds(x, y1)) grid[x][y1].deployObject(roadSymbol);
        }
        // Vertical segment
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            if (inBounds(x2, y)) {
                grid[x2][y].deployObject(roadSymbol);
            }
        }
    }


    private void placeCityBuildings() { // Keep this separate to place buildings AFTER terrain
        deployObject(new Building(
            "Emily's Store", 3, 3, 4, 3, STORE_SYMBOL,
            "/resources/asset/png/House_2_Emily.png"
        ));

        deployObject(new Building(
            "Mayor's Manor", 15, 2, 5, 4, MAYOR_MANOR_SYMBOL,
            "/resources/asset/png/House_2_Mayor.png" // Assumed correct
        ));

        deployObject(new Building(
            "Caroline's Carpentry", 2, 10, 4, 4, CARPENTRY_SYMBOL,
            "/resources/asset/png/House_2_Caroline.png"
        ));

        deployObject(new Building(
            "Perry's Cabin", 10, 15, 3, 3, PERRY_CABIN_SYMBOL, // Symbol 'P'
            "/resources/asset/png/House_2_Perry.png"
        ));

        deployObject(new Building(
            "Dasco's Gambling Den", 16, 12, 4, 5, GAMBLING_DEN_SYMBOL,
            "/resources/asset/png/House_2_Dasco.png"
        ));

        deployObject(new Building(
            "Abigail's Tent", 5, 16, 2, 2, ABIGAIL_TENT_SYMBOL,
            "/resources/asset/png/House_2_Abigail.png"
        ));

        deployObject(new Building(
            "Orenji si Kucing Barista",
            11, 8, 1, 1,
            ORENJI_SYMBOL,
            "/resources/asset/png/House_2_Orenji.png"
        ));

        // Exit at bottom center (X): This will be a fence image now.
        deployObject(new DeployedObject(SIZE/2, SIZE - 1, 1, 1, EXIT_SYMBOL) {
            @Override
            public void interact(Player p, FarmMap map) {
                System.out.println("You are at the exit to the Farm!");
            }
            @Override public boolean isWalkable() {
                return true; // Exit is walkable
            }
        });

        deployObject(new DeployedObject(SIZE/2 - 1, SIZE - 1, 1, 1, EXIT_SYMBOL) {
            @Override
            public void interact(Player p, FarmMap map) {
                System.out.println("You are at the exit to the Farm!");
            }
            @Override public boolean isWalkable() {
                return true; // Exit is walkable
            }
        });
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && x < SIZE && y >= 0 && y < SIZE;
    }

    private void deployObject(DeployedObject obj) {
        objects.add(obj);
        for (int dx = 0; dx < obj.getWidth(); dx++) { // Use getWidth() / getHeight()
            for (int dy = 0; dy < obj.getHeight(); dy++) {
                int tx = obj.getX() + dx, ty = obj.getY() + dy;
                if (inBounds(tx, ty)) {
                    grid[tx][ty].deployObject(obj.getSymbol());
                }
            }
        }
    }

    private void spawnPlayer(Player player) {
        int spawnX = SIZE / 2;
        int spawnY = SIZE / 2;
        while (!isWalkable(spawnX, spawnY) && spawnY < SIZE - 1) { // Spawn above last row to avoid fence/exit
            spawnY++;
        }
        player.setPosition(spawnX, spawnY);
    }

    @Override
    public Tile getTileAt(int x, int y) {
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
        return terrainChar != AIR_SYMBOL; // Water is not walkable
    }

    @Override
    public boolean movePlayer(Player player, int dx, int dy) {
        int newX = player.getX() + dx, newY = player.getY() + dy;
        if (inBounds(newX, newY) && isWalkable(newX, newY)) {
            player.setPosition(newX, newY);
            return true;
        }
        return false;
    }

    @Override
    public boolean atEdge(Player player) {
        int x = player.getX(), y = player.getY();
        return x == 0 || x == SIZE - 1 || y == 0 || y == SIZE - 1;
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
