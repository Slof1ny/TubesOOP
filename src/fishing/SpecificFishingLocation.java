package fishing;

import java.util.List;
import java.util.ArrayList;

import item.Fish;
import core.world.*;
import core.world.Tile.*;
import core.player.*;

public class SpecificFishingLocation extends FishingLocation {
    private static final int[][] ADJACENT = {
        {0, 1}, {0, -1}, {1, 0}, {-1, 0}
    };

    protected FarmMap farmMap;
    protected List<Tile> fishingPositions;

    public SpecificFishingLocation(String name, List<Fish> possibleFish, FarmMap farmMap) {
        super(name, possibleFish);
        this.farmMap = farmMap;
        this.fishingPositions = new ArrayList<>();
        getFishingPositions();
    }

    private void getFishingPositions() {
        for (int x = 0; x < FarmMap.SIZE; x++) {
            for (int y = 0; y < FarmMap.SIZE; y++) {
                Tile t = farmMap.getTileAt(x, y);
                if (t.getType() == TileType.DEPLOYED && t.displayChar() == 'o') {
                    for (int[] d : ADJACENT) {
                        int nx = x + d[0], ny = y + d[1];
                        if (nx >= 0 && ny >= 0 && nx < FarmMap.SIZE && ny < FarmMap.SIZE) {
                            Tile adj = farmMap.getTileAt(nx, ny);
                            if (adj.isWalkable()) {
                                fishingPositions.add(adj);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean canFishAt(Player player) {
        if (!player.getLocation().equals("Farm Map")) {
            return false;
        }

        Tile currentTile = this.farmMap.getTileAt(player.getX(), player.getY());
        if(currentTile == null){
            return false;
        }
        return fishingPositions.contains(currentTile);
    }
} 
