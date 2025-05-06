package fishing;

import java.util.List;
import item.Fish;
import core.world.*;
import core.player.Player;

public class FreeFishingLocation extends FishingLocation {
    private FarmMap farmMap;

    public FreeFishingLocation(String name, List<Fish> possibleFish, FarmMap farmMap) {
        super(name, possibleFish);
        this.farmMap = farmMap;
    }

    @Override
    public boolean canFishAt(Player player) {
        if (player.getLocation().equals(this.name)) {
            return true;
        }
        return false;
    }
}
