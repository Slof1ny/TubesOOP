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
    private FishType type;
    protected List<Season> seasons;
    protected List<Time> timeRange;
    protected List<Weather> weathers;
    protected List<FishingLocation> locations;
    public static final int ENERGY_RESTORED = 1;

    public Fish(String name, int buyPrice, int sellPrice, FishType type, List<Season> seasons, List<Time> timeRange, List<Weather> weathers, List<FishingLocation> locations) {
        super(name, buyPrice, sellPrice);
        this.type = type;
        this.seasons = seasons;
        this.timeRange = timeRange;
        this.weathers = weathers;
        this.locations = locations;
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

    public boolean isCatchable(Season season, Time time, Weather weather, FishingLocation location) {
        return seasons.contains(season) && timeRange.contains(time) && weathers.contains(weather) && locations.contains(location);
    }
}