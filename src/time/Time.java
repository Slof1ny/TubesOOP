package time;
    
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import system.StatisticsManager;
import core.player.Player;
import core.world.ShippingBin;
import core.world.Weather;
import system.GameManager;

public class Time {
    private int hour;
    private int minute;
    private boolean isNight;
    private final ScheduledExecutorService scheduler;

    private final GameCalendar calendar;
    private Player player;
    private GameManager gameManager;
    private boolean alreadyForcedSleepToday = false;

    public Time(GameCalendar calendar){
        this.hour = 6;
        this.minute = 0;
        this.isNight = false;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.calendar = calendar;
    }

    public Time(GameCalendar calendar, StatisticsManager data){
        this.hour = data.savedHour;
        this.minute = data.savedMinute;
        this.isNight = (hour >= 18 || hour < 6);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.calendar = calendar;
    }

    public Time(GameCalendar calendar, Player player){ // Tambahkan Player ke constructor
        this.hour = 6;
        this.minute = 0;
        this.isNight = false;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.calendar = calendar;
        this.player = player; // Inisialisasi Player
    }
    
    public Time(GameCalendar calendar, Player player, GameManager gameManager){ // Tambahkan Player ke constructor
        this.hour = 6;
        this.minute = 0;
        this.isNight = false;
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.calendar = calendar;
        this.player = player; // Inisialisasi Player
        this.gameManager = gameManager;
    }

    public Time(GameCalendar calendar, StatisticsManager data, Player player, GameManager gameManager){ // Tambahkan Player ke constructor
        this.hour = data.savedHour;
        this.minute = data.savedMinute;
        this.isNight = (hour >= 18 || hour < 6);
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.calendar = calendar;
        this.player = player; // Inisialisasi Player
        this.gameManager = gameManager;
    }

    private void tickOneInterval() {
        minute += 5;
        if (minute >= 60) {
            minute = 0;
            hour++;
            if (hour == 18) {
                isNight = true;
                System.out.println("===NIGHT MODE===");
            } else if (hour == 6) {
                isNight = false;
                System.out.println("===LIGHT MODE===");
            }
            if (hour == 24) {
                hour = 0;
                calendar.nextDay();
            }
        }
        System.out.printf("%02d : %02d\n", hour, minute);
    }

    public void runTime() {
        scheduler.scheduleAtFixedRate(this::tickOneInterval, 0, 1, TimeUnit.SECONDS);
    }

    public void pause() {
        scheduler.shutdownNow();
    }

    public void resume() {
        if (scheduler.isShutdown()) {
            runTime();
        }
    }

    public void advanceGameMinutes(int minutes) {
        int steps = minutes / 5;
        for (int i = 0; i < steps; i++) tickOneInterval();
    }

    public void sleep(){
        this.hour = 6;
        this.minute = 0;
        this.isNight = false;
        calendar.nextDay();
        if (this.gameManager != null) { // Update UI immediately after sleep
            this.gameManager.onGameTimeTick();
        }
    }

    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }

    public boolean isNight(){
        return isNight;
    }
    
    /**
     * Skip waktu ke jam dan menit tertentu pada hari yang sama.
     * Jika waktu mundur (lebih awal dari waktu sekarang), maka tetap di hari yang sama.
     * @param hour jam tujuan (0-23)
     * @param minute menit tujuan (0-59)
     */
    public void skipTo(int hour, int minute) {
        if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
            throw new IllegalArgumentException("Jam atau menit tidak valid");
        }
        this.hour = hour;
        this.minute = minute;
        this.isNight = (hour >= 18 || hour < 6);
        System.out.printf("Waktu di-skip ke %02d:%02d\n", hour, minute);
    }

    public void runTime2(){
        Runnable updateTime = () -> {
            minute += 5;

            if (minute >= 60){
                minute = 0;
                hour++;
                if(hour == 18){
                    isNight = true;
                    System.out.println("===NIGHT MODE===");
                } else if (hour == 6){
                    isNight = false;
                    System.out.println("===LIGHT MODE===");
                }
                
                if(hour == 24){ // Hari baru dimulai
                    hour = 0;

                    Weather weatherOfJustEndedDay = calendar.getCurrentWeather();
                    calendar.nextDay();
                    int dayThatJustEnded = calendar.getTotalDay();

                    if (this.gameManager != null) {
                        this.gameManager.processNewDayUpdates(
                            calendar.getTotalDay(), // Current (new) day number
                            calendar.getCurrentSeason(), // Current (new) season
                            weatherOfJustEndedDay == core.world.Weather.RAINY, // Was the day that just ended rainy?
                            dayThatJustEnded // Pass the day number that was rainy for accurate crop watering
                        );
                    }

                    // Proses penjualan Shipping Bin di akhir hari
                    if (player != null && player.getShippingBin() != null) {
                        player.getShippingBin().processSales(player, calendar.getCurrentSeason()); // MODIFIED: Pass current season
                    }

                    if (this.gameManager != null) {
                        this.gameManager.checkMilestonesAndShowStatistics();
                    }
                }
            }
            if (this.hour == 2 && this.minute == 0 && !alreadyForcedSleepToday) {
                // Check if player is active (not already in a sleep transition from -20 energy)
                // This simple check assumes if time hits 02:00, and player hasn't manually slept
                // or been forced to sleep by MIN_ENERGY yet for *this night cycle*, they pass out.
                if (player != null /* && !player.isSleeping() // if you had such a flag */ ) {
                    System.out.println("Console: Time is 02:00 AM. Forcing player to sleep.");
                    if (gameManager != null) {
                        // Show message before sleep call, as sleep call changes state immediately
                         SwingUtilities.invokeLater(() -> {
                            if (gameManager.getGameViewInstance() != null && gameManager.getGameViewInstance().isVisible()) { // You'd need getGameViewInstance() in GameManager
                                JOptionPane.showMessageDialog(gameManager.getGameViewInstance(),
                                    "It's 02:00 AM! You collapse from exhaustion and pass out...",
                                    "Too Late!",
                                    JOptionPane.WARNING_MESSAGE);
                            }
                        });
                        gameManager.forcePlayerSleep(); // This calls sleep2()
                        alreadyForcedSleepToday = true; // Prevent re-triggering until flag is reset at 06:00 or new day
                    }
                }
            }

            System.out.printf("Console Time: %02d : %02d\n", hour, minute);
            if (this.gameManager != null) {
                this.gameManager.onGameTimeTick();
            }

        };
        scheduler.scheduleAtFixedRate(updateTime, 0, 1, TimeUnit.SECONDS);
    };

    public void sleep2() {
        System.out.println("Player is going to sleep. Current energy: " + (player != null ? player.getEnergy() : "N/A"));

        // 1. Restore Player Energy (based on NEW rules)
        if (player != null) {
            int currentEnergy = player.getEnergy();
            int maxEnergy = Player.MAX_ENERGY; // Assuming Player.MAX_ENERGY is public static final int
            double lowEnergyThreshold = 0.10 * maxEnergy; // e.g., 10 if MAX_ENERGY is 100

            if (currentEnergy <= 0) {
                player.setEnergy(10); // Rule 3: If current energy <= 0, restore to 10 energy
                System.out.println("Energy was <= 0. Restored to 10. New energy: " + player.getEnergy());
            } else if (currentEnergy < lowEnergyThreshold) {
                player.setEnergy(maxEnergy / 2); // Rule 2: If current energy < 10% MAX_ENERGY (and > 0), restore to half
                System.out.println("Energy was low (but > 0). Restored to half: " + player.getEnergy());
            } else { // currentEnergy >= lowEnergyThreshold (i.e. >= 10% MAX_ENERGY)
                player.setEnergy(maxEnergy); // Rule 1: Restore to MAX_ENERGY
                System.out.println("Energy was sufficient. Restored to full: " + player.getEnergy());
            }
        } else {
            System.err.println("Time.sleep2(): Player object is null, cannot restore energy.");
        }

        // 2. Set time to morning (06:00)
        this.hour = 6;
        this.minute = 0;
        this.isNight = false;
        this.alreadyForcedSleepToday = false;

        // 3. Advance to the next day & process daily updates
        Weather weatherOfJustEndedDay = calendar.getCurrentWeather();
        int dayThatJustEnded = calendar.getTotalDay(); 
        calendar.nextDay();

        if (player != null && player.getStats() != null) {
            player.getStats().incrementDaysPlayedInSeason(calendar.getCurrentSeason());
        }

        if (this.gameManager != null) {
            this.gameManager.processNewDayUpdates(
                calendar.getTotalDay(),
                calendar.getCurrentSeason(),
                weatherOfJustEndedDay == core.world.Weather.RAINY,
                dayThatJustEnded
            );
            this.gameManager.onGameTimeTick(); // For immediate UI update
        }

        // 4. Process Shipping Bin sales
        if (player != null && player.getShippingBin() != null) {
            player.getShippingBin().processSales(player, calendar.getCurrentSeason()); // MODIFIED: Pass current season
        }

        if (this.gameManager != null) {
            this.gameManager.checkMilestonesAndShowStatistics();
        }

        System.out.println("Console: Slept. New Day - Day " + calendar.getTotalDay() +
                           ", Season: " + calendar.getCurrentSeason() +
                           ", Weather: " + calendar.getCurrentWeather() +
                           ", Player Energy: " + (player != null ? player.getEnergy() : "N/A"));
    }

    public void displayTime(){ // ADD THIS METHOD
    System.out.printf("Current Game Time: %02d:%02d%n", hour, minute);
    System.out.println("Is Night: " + isNight());
    }

}