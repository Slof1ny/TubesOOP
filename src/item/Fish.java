package item;

import java.util.List;
import fishing.FishType;
import core.world.*;

public class Fish extends Item implements EdibleItem {
    private FishType type;
    protected List<Season> seasons;
    protected List<Time> timeRange;
    protected List<Weather> weathers;
    protected List<Location> locations;
    public static final int ENERGY_RESTORED = 1;

    public Fish(String name, int price, FishType type, List<Season> seasons, List<Time> timeRange, List<Weather> weathers, List<Location> locations) {
        super(name, price);
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

    @Override
    public int getPrice() {
        return price;
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

    public boolean isCatchable(Season season, Time time, Weather weather, Location location) {
        return seasons.contains(season) && timeRange.contains(time) && weathers.contains(weather) && locations.contains(location);
    }
}