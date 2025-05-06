package fishing;

import java.util.List;
import item.Fish;
import core.world.*;
import core.player.Player;

public class SpecificFishingLocation extends FishingLocation {
    protected FarmMap farmMap;
    protected List<Tile> fishingPositions; // nanti cari posisi pond dlu

    public SpecificFishingLocation(String name, List<Fish> possibleFish, FarmMap farmMap, List<Tile> fishingPositions) {
        super(name, possibleFish);
        this.farmMap = farmMap;
        this.fishingPositions = fishingPositions; // loop matriks buat cari posisi pond baru +1
    }

    @Override
    public boolean canFishAt(Player player) {
        if (player.getLocation().equals("Farm Map")) {
            for (Tile tile : fishingPositions) {
                if (tile.equals(player.getCurrentTile())) {
                    return true;
                }
            }
        }
        return false;
    }
} 
