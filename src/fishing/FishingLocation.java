package fishing;

import java.util.List;
import java.util.ArrayList;
import item.Fish;
import core.world.*;
import core.player.Player;

public abstract class FishingLocation {
    protected String name;
    protected List<Fish> possibleFish;

    public FishingLocation(String name, List<Fish> possibleFish) {
        this.name = name;
        this.possibleFish = possibleFish;
    }

    public List<Fish> getPossibleFish(Season seasons, Time time, Weather weather) {
        List<Fish> catchableFish = new ArrayList<>();
        for (Fish fish : possibleFish) {
            if (fish.isCatchable(seasons, time, weather, this.name)) {
                catchableFish.add(fish);
            }
        }
        return catchableFish;
    }
        
    public abstract boolean canFishAt(Player player);
}