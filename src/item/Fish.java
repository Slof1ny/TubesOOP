package item;

import java.util.List;
import fishing.*;
import time.*;
import core.world.*;

// cd item
// javac -d ../classes Fish.java Item.java EdibleItem.java ../fishing/Fishtype.java ../core/world/Season.java 
// ../time/Time.java ../fishing/FishingLocation.java ../time/GameCalendar.java ../core/world/Weather.java ../system/StatisticsManager.java 
// ../core/player/* ../npc/NPC.java ../core/relationship/RelationshipStatus.java ../item/* ../core/world/* ../core/house/*

public class Fish extends Item implements EdibleItem {
    private final FishType type;
    private final List<Season> seasons;
    private final List<TimeRange> timeRanges;
    private final List<Weather> weathers;
    private final List<FishingLocation> locations;
    private static final int ENERGY_RESTORED = 1;

    public Fish(String name, int buyPrice, int sellPrice, FishType type, List<Season> seasons, List<TimeRange> timeRanges, List<Weather> weathers, List<FishingLocation> locations) {
        super(name, buyPrice, sellPrice);
        this.type       = type;
        this.seasons    = seasons;
        this.timeRanges = timeRanges;
        this.weathers   = weathers;
        this.locations  = locations;
    }

    @Override
    public String getName() {
        return name;
    }

    public FishType getType() {
        return type;
    }

    @Override
    public String getCategory() {
        return "Fish";
    }

    public int getEnergyRestored() {
        return ENERGY_RESTORED;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public List<TimeRange> getTimeRanges() {
        return timeRanges;
    }

    public List<Weather> getWeathers() {
        return weathers;
    }

    public List<FishingLocation> getLocations() {
        return locations;
    }

    public boolean isCatchable(Season season, Time time, Weather weather, FishingLocation location) {
        if (!seasons.contains(season))   return false;
        if (!weathers.contains(weather)) return false;
        if (!locations.contains(location)) return false;
        for (var tr : timeRanges) {
            if (tr.contains(time)) return true;
        }
        return false;
    }
}
