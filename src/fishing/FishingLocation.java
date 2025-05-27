package fishing;

import java.util.List;
import java.util.ArrayList;
import item.Fish;
import time.*;
import core.world.*;
import core.player.Player;

public abstract class FishingLocation {
    protected String name;
    protected List<Fish> possibleFish;

    public FishingLocation(String name, List<Fish> possibleFish) {
        this.name = name;
        this.possibleFish = possibleFish;
    }

    public List<Fish> getPossibleFish(Season seasons, Time time, Weather weather, FishingLocation location) {
        List<Fish> catchableFish = new ArrayList<>();
        for (Fish fish : possibleFish) {
            if (fish.isCatchable(seasons, time, weather, location)) {
                catchableFish.add(fish);
            }
        }
        return catchableFish;
    }

    public void setPossibleFish(List<Fish> fish) {
        this.possibleFish = fish;
    }

    public void addFish(Fish f) {
        this.possibleFish.add(f);
    }
        
    public abstract boolean canFishAt(Player player);
}