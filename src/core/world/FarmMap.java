package core.world;

import java.util.ArrayList;
import java.util.List;
import core.player.*;

public class FarmMap { // masih placeholder ya klo mau ganti langsung ubah" aja
    public static final int SIZE = 32;
    protected Tile[][] grid = new Tile[SIZE][SIZE];
    protected List<DeployedObject> objects = new ArrayList<>();
    protected Player player;

    public FarmMap(Player player) {
        this.player = player;
        initTiles(); // nanti implement satu"
        placeHouseAndPond();
        placeShippingBin();
        spawnPlayer();
    }

    private void initTiles() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Tile(i, j);
            }
        }
    }

    private void placeHouseAndPond() {
    //     // 1) pick random (x,y) so 6×6 fits inside 32×32 with a buffer
    //     // 2) same for 4×3 pond
    //     // 3) add new House(...) and Pond(...)
    //     // you’ll want to check they don’t overlap
    }

    private void placeShippingBin() {
    //     // House h = findHouse();
    //     // e.g. at (h.x + h.width + 1, h.y)
    //     // objects.add(new ShippingBin(h.x + h.width + 1, h.y));
    }

    private void spawnPlayer() {
    //     // put p somewhere near the house entrance, e.g.
    //     // player.setPosition(house.x + house.width/2, house.y + house.height + 1);
    }

    public boolean isWalkable(int x, int y) {
    //     if (x < 0 || y < 0 || x >= SIZE || y >= SIZE) {
    //         return false;
    //     }

    //     for(var obj : objects) {
    //         if (obj.occupies(x,y)) {
    //             return false;
    //         }
    //     }
            
    //     return grid[x][y].isWalkable();
    // }

    // public void movePlayer(Direction d) {
    //     int nx = player.getX() + d.dx, ny = player.getY() + d.dy;
    //     if (isWalkable(nx, ny)) {
    //         player.setPosition(nx, ny);
    //     }
    }

    public boolean atEdge() {
    //     int x = player.getX(), y = player.getY();
    //     return x == 0 || y == 0 || x == SIZE - 1 || y == SIZE - 1;
    // }

    // /** if standing adjacent to an object, activate its interact() */
    // public void tryInteract() {
    //     for(var obj : objects) {
    //         if (adjacent(player, obj)) {
    //             obj.interact(player, this);
    //             return;
    //         }
    //     }
    //     // else if standing on tile: maybe till/plant/harvest
    //     Tile t = grid[player.getX()][player.getY()];
    //     player.performTileAction(t);
    }

    private boolean adjacent(Player p, DeployedObject o) {
    //     int px = p.getX(), py = p.getY();
    //     // check any tile next to the bounding box of o
    //     return (px >= o.x-1 && px <= o.x + o.width && py >= o.y-1 && py <= o.y + o.height) && !o.occupies(px, py);
    }
}

