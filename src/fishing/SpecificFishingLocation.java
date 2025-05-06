package fishing;

import java.util.List;
import item.Fish;
import core.world.*;
import core.player.Player;

public class SpecificFishingLocation extends FishingLocation {
    private FarmMap farmMap;
    protected List<Tile> fishingPositions;

    public SpecificFishingLocation(String name, List<Fish> possibleFish, FarmMap farmMap, List<Tile> fishingPositions) {
        super(name, possibleFish);
        this.farmMap = farmMap;
        this.fishingPositions = fishingPositions;
    }

    @Override
    public boolean canFishAt(Player player) {
        if (player.getLocation().equals(this.name)) {
            for (Tile tile : fishingPositions) {
                if (tile.equals(player.getCurrentTile())) {
                    return true;
                }
            }
        }
        return false;
    }
} 
