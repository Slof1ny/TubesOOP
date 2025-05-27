package time;
    
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import system.StatisticsManager;

public class Time {
    private int hour;
    private int minute;
    private boolean isNight;
    private final ScheduledExecutorService scheduler;

    private final GameCalendar calendar;

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
}

