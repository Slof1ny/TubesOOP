package fishing;

import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import core.player.Player;
import core.world.FarmMap;
import item.Fish;
import time.GameCalendar;
import time.Time;

public class FishingManager {
    protected FishingLocation fishingLocation;

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static class FishingRNG {
        private Random rng;
    
        public FishingRNG(long seed) {
            this.rng = new Random(seed);
        }
        
        public FishingRNG() {
            this(System.currentTimeMillis());
        }
    
        public int getRandomNumber(int min, int max) {
            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
    
            return min + rng.nextInt(max - min + 1);
        }
    }

    public static Future<?> fish(FarmMap farm, FishingLocation location, Player player, Time time, GameCalendar calendar, Scanner sc) {
        if (!location.canFishAt(player)) {
            System.out.println("You can't fish here.");
            return CompletableFuture.completedFuture(null);
        }
        if (player.getEnergy() < 5) {
            System.out.println("Not enough energy to fish.");
            return CompletableFuture.completedFuture(null);
        }

        player.setEnergy(player.getEnergy() - 5);
        time.advanceGameMinutes(15);

        return executor.submit(() -> {
            List<Fish> catchables = location.getPossibleFish(calendar.getCurrentSeason(), time, calendar.getCurrentWeather(), location);
            if (catchables.isEmpty()) {
                System.out.println("No fish are catchable now.");
                return;
            }

            FishingRNG rng = new FishingRNG();
            Fish chosen = catchables.get(rng.getRandomNumber(0, catchables.size() - 1));

            int bound = switch (chosen.getType()) {
                case COMMON    -> 10;
                case REGULAR   -> 100;
                case LEGENDARY -> 500;
            };
            int tries = chosen.getType() == FishType.LEGENDARY ? 7 : 10;
            int secret = rng.getRandomNumber(1, bound);

            System.out.printf("Guess 1-%d in %d tries\n", bound, tries);
            boolean success = false;
            for (int i = 0; i < tries && !success; i++) {
                System.out.print("Your guess: ");
                int g = sc.nextInt();
                if (g == secret) {
                    success = true;
                } else {
                    System.out.println(g < secret ? "Too low!" : "Too high!");
                }
            }

            if (success) {
                player.getInventory().addItem(chosen, 1);
                System.out.println("You caught: " + chosen.getName());
            } else {
                System.out.println("The fish got away...");
            }
        });
    }
}
