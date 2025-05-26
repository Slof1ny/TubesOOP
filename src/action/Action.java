package action;

// import fishing.FishingLocation;
// import fishing.FishType;
// import fishing.FishingManager.FishingRNG;
// import item.Fish;
// import core.player.Player;
// import core.world.FarmMap;
// import time.GameCalendar;i
// import time.TimeManager;

// import java.util.List;
// import java.util.Scanner;

public class Action {
    // public static void fish(FarmMap farm, FishingLocation location) {
    //     Player p = farm.getPlayer();
    //     GameCalendar calendar = farm.getGameCalendar();
    //     TimeManager tm = farm.getTimeManager();

    //     // 1. Location check
    //     if (!location.canFishAt(p)) {
    //         System.out.println("You can't fish here.");
    //         return;
    //     }

    //     // 2. Energy check and time adjustment
    //     if (p.getEnergy() < 5) {
    //         System.out.println("Not enough energy to fish.");
    //         return;
    //     }

    //     p.setEnergy(p.getEnergy() - 5);
    //     tm.pause();
    //     calendar.advanceMinutes(15);

    //     // 3. Choose fish
    //     List<Fish> catchables = location.getPossibleFish(calendar.getSeason(), calendar, calendar.getWeather(), location);
    //     if (catchables.isEmpty()) {
    //         System.out.println("No fish are catchable now.");
    //         tm.resume();
    //         return;
    //     }

    //     FishingRNG rng = new FishingRNG();
    //     Fish chosenFish = catchables.get(rng.getRandomNumber(0, catchables.size() - 1));
    //     FishType type = chosenFish.getType();

    //     int bound = switch (type) {
    //         case COMMON -> 10;
    //         case REGULAR -> 100;
    //         case LEGENDARY -> 500;
    //     };
    //     int attempts = type == FishType.LEGENDARY ? 7 : 10;
    //     int secret = rng.getRandomNumber(1, bound);

    //     Scanner scanner = new Scanner(System.in);
    //     System.out.printf("Guess the number (1-%d). You have %d tries.%n", bound, attempts);
    //     boolean success = false;
    //     for (int i = 0; i < attempts; i++) {
    //         System.out.print("Your guess: ");
    //         int guess = scanner.nextInt();
    //         if (guess == secret) {
    //             success = true;
    //             break;
    //         }
    //         System.out.println(guess < secret ? "Too low!" : "Too high!");
    //     }

    //     if (success) {
    //         p.getInventory().add(chosenFish);
    //         System.out.println("You caught: " + chosenFish.getName());
    //     } else {
    //         System.out.println("The fish escaped...");
    //     }

    //     tm.resume();
    // }
}
