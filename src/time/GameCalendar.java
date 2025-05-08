package time;

import core.world.Season;
import core.world.Weather;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameCalendar {
    private int dayInSeason; //1-10 (1 Season = 10 hari)
    private int totalDay;
    private Season currentSeason;
    private Weather currentWeather;
    private int rainyDaysInSeason; //1 season minimal rain 2x
    private int hour;
    private int minute;
    private boolean isNight;
    private final ScheduledExecutorService scheduler;

    public GameCalendar(){
        this.dayInSeason = 1; //diawali day 1
        this.totalDay = 1; //diawali day 1
        this.currentSeason = Season.SPRING;
        this.currentWeather = generateWeather();
        if (this.currentWeather == Weather.RAINY){
            this.rainyDaysInSeason = 1;
        } 
        else {
            this.rainyDaysInSeason = 0;
        }
        this.hour = 6; 
        this.minute = 0;
        this.isNight = false;
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    private void changeSeason(){
        if (currentSeason == Season.SPRING){
            currentSeason = Season.SUMMER;
        }
        else if (currentSeason == Season.SUMMER){
            currentSeason = Season.FALL;
        }
        else if (currentSeason == Season.FALL){
            currentSeason = Season.WINTER;
        }
        else if (currentSeason == Season.WINTER){
            currentSeason = Season.SPRING;
        }

        rainyDaysInSeason = 0; //reset hujan saat ganti musim (krn satu musim min 2x hujan)
    }

    private Weather generateWeather(){
        if (dayInSeason == 9 && rainyDaysInSeason == 0){
            rainyDaysInSeason++;
            return Weather.RAINY;
        }
        if (dayInSeason == 10 && rainyDaysInSeason < 2){
            rainyDaysInSeason++;
            return Weather.RAINY;
        }

        double randomHujan = Math.random();
        if (randomHujan < 0.35){
            rainyDaysInSeason++;
            return Weather.RAINY;
        } 
        else{
            return Weather.SUNNY;
        }

    }

    public void nextDay(){
        totalDay++;
        dayInSeason++;

        if(dayInSeason > 10){
            changeSeason();
            dayInSeason = 1;
        }

        currentWeather = generateWeather();
    }

    public void runTime(){
        Runnable updateTime = () -> {
            System.out.printf("%02d : %02d\n", hour, minute);
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
                
                if(hour == 24){
                    hour = 0;
                    nextDay();
                }

            }
        };
        scheduler.scheduleAtFixedRate(updateTime, 0, 1, TimeUnit.SECONDS);
    };


    public int getDayInSeason(){
        return dayInSeason;
    }
    public int getTotalDay(){
        return totalDay;
    }
    public Season getCurrentSeason(){
        return currentSeason;
    }
    public Weather getCurrentWeater(){
        return currentWeather;
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

    
}
