package action;

import fishing.FishingManager;
import fishing.FishingLocation;
import core.player.Player;
import core.world.FarmMap;
import time.GameCalendar;
import time.Time;

import java.io.BufferedReader; // Change import from Scanner to BufferedReader
import java.io.IOException;
import java.util.concurrent.Future;

public class Action {
    public static Future<?> fish(FarmMap farm, FishingLocation location, Player player, Time time, GameCalendar calendar, BufferedReader reader) {
        return FishingManager.fish(farm, location, player, time, calendar, reader);
    }

    // public static Future<?> cook() {
    // }
}
