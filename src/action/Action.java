package action;

import fishing.FishingManager;
import fishing.FishingLocation;
import core.player.Player;
import core.world.FarmMap;
import time.GameCalendar;
import time.Time;

import java.util.Scanner;
import java.util.concurrent.Future;

public class Action {

    public static Future<?> fish(FarmMap farm, FishingLocation location, Player player, Time time, GameCalendar calendar, Scanner scanner) {
        return FishingManager.fish(farm, location, player, time, calendar, scanner);
    }

    // public static Future<?> cook() {
    // }
}
