package fishing;

import item.Fish;
import core.world.Season;
import core.world.Weather;
import time.TimeRange;

import java.util.*;

public class FishRegistry {
    private static record FishDefinition(String name, FishType type, List<Season> seasons, List<TimeRange> timeRanges, List<Weather> weathers, List<String> locations) {}

    private static final List<FishDefinition> DEFINITIONS = List.of(
      // ── COMMON ────────────────────────────────────────────────────
      new FishDefinition("Bullhead", FishType.COMMON,
        Arrays.asList(Season.values()),
        createAllDayTimeRanges(),
        Arrays.asList(Weather.values()),
        List.of("Mountain Lake")
      ),
      new FishDefinition("Carp", FishType.COMMON,
        Arrays.asList(Season.values()),
        createAllDayTimeRanges(),
        Arrays.asList(Weather.values()),
        List.of("Mountain Lake","Pond")
      ),
      new FishDefinition("Chub", FishType.COMMON,
        Arrays.asList(Season.values()),
        createAllDayTimeRanges(),
        Arrays.asList(Weather.values()),
        List.of("Forest River","Mountain Lake")
      ),

      // ── REGULAR ───────────────────────────────────────────────────
      new FishDefinition("Largemouth Bass", FishType.REGULAR,
        Arrays.asList(Season.values()),
        List.of(new TimeRange(6,18)),
        Arrays.asList(Weather.values()),
        List.of("Mountain Lake")
      ),
      new FishDefinition("Rainbow Trout", FishType.REGULAR,
        List.of(Season.SUMMER),
        List.of(new TimeRange(6,18)),
        List.of(Weather.SUNNY),
        List.of("Forest River","Mountain Lake")
      ),
      new FishDefinition("Sturgeon", FishType.REGULAR,
        List.of(Season.SUMMER, Season.WINTER),
        List.of(new TimeRange(6,18)),
        Arrays.asList(Weather.values()),
        List.of("Mountain Lake")
      ),
      new FishDefinition("Midnight Carp", FishType.REGULAR,
        List.of(Season.FALL, Season.WINTER),
        List.of(new TimeRange(20,2)),
        Arrays.asList(Weather.values()),
        List.of("Mountain Lake","Pond")
      ),
      new FishDefinition("Flounder", FishType.REGULAR,
        List.of(Season.SPRING, Season.SUMMER),
        List.of(new TimeRange(6,22)),
        Arrays.asList(Weather.values()),
        List.of("Ocean")
      ),
      new FishDefinition("Halibut", FishType.REGULAR,
        Arrays.asList(Season.values()),
        List.of(new TimeRange(6,11), new TimeRange(19,2)),
        Arrays.asList(Weather.values()),
        List.of("Ocean")
      ),
      new FishDefinition("Octopus", FishType.REGULAR,
        List.of(Season.SUMMER),
        List.of(new TimeRange(6,22)),
        Arrays.asList(Weather.values()),
        List.of("Ocean")
      ),
      new FishDefinition("Pufferfish", FishType.REGULAR,
        List.of(Season.SUMMER),
        List.of(new TimeRange(0,16)),
        List.of(Weather.SUNNY),
        List.of("Ocean")
      ),
      new FishDefinition("Sardine", FishType.REGULAR,
        Arrays.asList(Season.values()),
        List.of(new TimeRange(6,18)),
        Arrays.asList(Weather.values()),
        List.of("Ocean")
      ),
      new FishDefinition("Super Cucumber", FishType.REGULAR,
        List.of(Season.SUMMER,Season.FALL,Season.WINTER),
        List.of(new TimeRange(18,2)),
        Arrays.asList(Weather.values()),
        List.of("Ocean")
      ),
      new FishDefinition("Catfish", FishType.REGULAR,
        List.of(Season.SPRING,Season.SUMMER,Season.FALL),
        List.of(new TimeRange(6,22)),
        List.of(Weather.RAINY),
        List.of("Forest River","Pond")
      ),
      new FishDefinition("Salmon", FishType.REGULAR,
        List.of(Season.FALL),
        List.of(new TimeRange(6,18)),
        Arrays.asList(Weather.values()),
        List.of("Forest River")
      ),

      // ── LEGENDARY ────────────────────────────────────────────────
      new FishDefinition("Angler", FishType.LEGENDARY,
        List.of(Season.FALL),
        List.of(new TimeRange(8,20)),
        Arrays.asList(Weather.values()),
        List.of("Pond")
      ),
      new FishDefinition("Crimsonfish", FishType.LEGENDARY,
        List.of(Season.SUMMER),
        List.of(new TimeRange(8,20)),
        Arrays.asList(Weather.values()),
        List.of("Ocean")
      ),
      new FishDefinition("Glacierfish", FishType.LEGENDARY,
        List.of(Season.WINTER),
        List.of(new TimeRange(8,20)),
        Arrays.asList(Weather.values()),
        List.of("Forest River")
      ),
      new FishDefinition("Legend", FishType.LEGENDARY,
        List.of(Season.SPRING),
        List.of(new TimeRange(8,20)),
        List.of(Weather.RAINY),
        List.of("Mountain Lake")
      )
    );

    private static final Map<String, Fish> FISH_BY_NAME = new HashMap<>();

    private static List<TimeRange> createAllDayTimeRanges() {
        return List.of(new TimeRange(0, 24));
    }

    public static List<Fish> buildAll(Map<String,FishingLocation> locationsByName) {
        List<Fish> allCreatedFish = new ArrayList<>(DEFINITIONS.size());
        FISH_BY_NAME.clear();

        for (var d : DEFINITIONS) {
            var associatedLocations = new ArrayList<FishingLocation>();
            for (var name : d.locations()) {
                var fl = locationsByName.get(name);
                if (fl != null) {
                    associatedLocations.add(fl);
                }
            }

            int totalHours = 0;
            for (TimeRange tr : d.timeRanges()) {
                totalHours += tr.totalHours();
            }
            int price = calculateSellPrice(d.seasons().size(), totalHours,
                                           d.weathers().size(), d.locations().size(),
                                           d.type());

            Fish newFish = new Fish(d.name(), 0, price, d.type(), d.seasons(), d.timeRanges(), d.weathers(), associatedLocations);
            allCreatedFish.add(newFish);
            FISH_BY_NAME.put(newFish.getName(), newFish); 

            for (FishingLocation loc : associatedLocations) {
                loc.addFish(newFish);
            }
        }
        return allCreatedFish;
    }

    private static int calculateSellPrice(int seasons, int hours, int weathers, int locations, FishType type) {
        int C = switch(type) {
            case COMMON    -> 10;
            case REGULAR   -> 5;
            case LEGENDARY -> 25;
        };
        return (4*seasons) * hours * (2*weathers) * (4*locations) / C;
    }

    public static Fish getFishByName(String name) {
        return FISH_BY_NAME.get(name);
    }

    public static List<Fish> getAllFish() {
        return List.copyOf(FISH_BY_NAME.values());
    }
}