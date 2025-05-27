
package test;

import time.GameCalendar;
import time.Time;
import core.world.Season;
import core.world.Weather;
import system.StatisticsManager; 

import java.util.concurrent.TimeUnit; // For Thread.sleep


public class TimeTester {

    public static void main(String[] args) {
        System.out.println("--- Starting Time and GameCalendar Tester ---");

        // --- Test GameCalendar ---
        System.out.println("\n--- Test Scenario 1: GameCalendar Initialization & Day Progression ---");
        GameCalendar calendar1 = new GameCalendar();
        printCalendarStatus(calendar1, "Initial Calendar 1");

        // Advance a few days
        for (int i = 0; i < 3; i++) {
            calendar1.nextDay();
            printCalendarStatus(calendar1, "Calendar 1 after " + (i + 1) + " day(s)");
        }

        System.out.println("\n--- Test Scenario 2: GameCalendar Season Change ---");
        // Fast-forward to near end of season to test season change
        calendar1 = new GameCalendar(); // Reset calendar
        System.out.println("Resetting Calendar 1 for season change test.");
        for (int i = 0; i < 9; i++) { // Advance to Day 10 of Spring
            calendar1.nextDay();
        }
        printCalendarStatus(calendar1, "Calendar 1 at end of Spring (Day 10)");
        calendar1.nextDay(); // This should trigger season change to Summer
        printCalendarStatus(calendar1, "Calendar 1 after season change (Day 1 of Summer)");

        System.out.println("\n--- Test Scenario 3: GameCalendar with StatisticsManager (Placeholder) ---");
        // Instantiate StatisticsManager with all required parameters
        StatisticsManager savedData = new StatisticsManager(
            "TestPlayer", // playerName
            10,           // savedHour
            30,           // savedMinute
            5,            // savedDayInSeason
            15,           // savedTotalDay
            Season.FALL,  // savedSeason
            Weather.RAINY // savedWeather
        );
        GameCalendar calendar2 = new GameCalendar(savedData);
        printCalendarStatus(calendar2, "Calendar 2 initialized from StatisticsManager");


        // --- Test Time ---
        System.out.println("\n--- Test Scenario 4: Time Initialization & Real-time Progression ---");
        GameCalendar calendarForTime = new GameCalendar(); // Use a fresh calendar for Time
        Time gameTime = new Time(calendarForTime);
        printTimeStatus(gameTime, calendarForTime, "Initial Game Time");

        System.out.println("Starting real-time progression for 5 seconds (expect 25 in-game minutes)...");
        gameTime.runTime(); // This starts the scheduled executor
        try {
            TimeUnit.SECONDS.sleep(5); // Let it run for 5 real seconds
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Time progression interrupted.");
        }
        // Note: The output from runTime() will be interleaved with this tester's output.
        printTimeStatus(gameTime, calendarForTime, "Game Time after 5 real seconds");

        System.out.println("\n--- Test Scenario 5: Time Sleep Action ---");
        System.out.println("Current Time before sleep: " + gameTime.getHour() + ":" + gameTime.getMinute());
        System.out.println("Current Day before sleep: " + calendarForTime.getTotalDay());
        gameTime.sleep(); // This should reset time to 6:00 and advance day
        printTimeStatus(gameTime, calendarForTime, "Game Time after sleep action");
        System.out.println("Current Day after sleep: " + calendarForTime.getTotalDay());

        // Test Time initialized with StatisticsManager
        System.out.println("\n--- Test Scenario 6: Time with StatisticsManager (Placeholder) ---");
        StatisticsManager savedTimeData = new StatisticsManager(
            "SavedPlayer", // playerName
            20,            // savedHour (8 PM)
            30,            // savedMinute
            1,             // savedDayInSeason
            50,            // savedTotalDay
            Season.WINTER, // savedSeason
            Weather.SUNNY  // savedWeather
        );

        GameCalendar calendarForSavedTime = new GameCalendar(savedTimeData);
        Time savedGameTime = new Time(calendarForSavedTime, savedTimeData);
        printTimeStatus(savedGameTime, calendarForSavedTime, "Game Time initialized from StatisticsManager");
        System.out.println("Is it night? " + savedGameTime.isNight()); // Should be true (20:30)

        // It's important to shut down the scheduler if you're not using it anymore
        // In a full game, you'd manage this gracefully on game exit.
        // For this tester, we can't easily stop the scheduler started by runTime()
        // without modifying the Time class to expose a shutdown method.
        // So, the program will likely continue printing time updates until manually stopped.
        System.out.println("\n--- Time and GameCalendar Tester Finished ---");
        System.out.println("Note: The time progression thread might continue printing until the JVM exits.");
    }

    /**
     * Helper method to print GameCalendar status.
     */
    private static void printCalendarStatus(GameCalendar calendar, String label) {
        System.out.println(label + ":");
        System.out.println("  Day in Season: " + calendar.getDayInSeason());
        System.out.println("  Total Day: " + calendar.getTotalDay());
        System.out.println("  Current Season: " + calendar.getCurrentSeason());
        System.out.println("  Current Weather: " + calendar.getCurrentWeater());
    }

    /**
     * Helper method to print Time and associated GameCalendar status.
     */
    private static void printTimeStatus(Time time, GameCalendar calendar, String label) {
        System.out.println(label + ":");
        System.out.printf("  Current Time: %02d:%02d%n", time.getHour(), time.getMinute());
        System.out.println("  Is Night: " + time.isNight());
        System.out.println("  Associated Calendar Day: " + calendar.getTotalDay());
        System.out.println("  Associated Calendar Season: " + calendar.getCurrentSeason());
        System.out.println("  Associated Calendar Weather: " + calendar.getCurrentWeater());
    }
}
