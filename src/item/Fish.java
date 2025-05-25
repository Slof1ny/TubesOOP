package item;

import java.util.List;
import fishing.*;
import time.*;
import core.world.*;

public class Fish extends Item implements EdibleItem {
    private FishType type;
    protected List<Season> seasons;
    protected List<GameCalendar> timeRange;
    protected List<Weather> weathers;
    protected List<FishingLocation> locations;
    public static final int ENERGY_RESTORED = 1;

    public Fish(String name, int buyPrice, int sellPrice, FishType type, List<Season> seasons, List<GameCalendar> timeRange, List<Weather> weathers, List<FishingLocation> locations) {
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

    public boolean isCatchable(Season season, GameCalendar time, Weather weather, FishingLocation location) {
        return seasons.contains(season) && timeRange.contains(time) && weathers.contains(weather) && locations.contains(location);
    }
}