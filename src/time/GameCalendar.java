package time;

import core.world.Season;
import core.world.Weather;
import system.StatisticsManager;


public class GameCalendar {
    private int dayInSeason; //1-10 (1 Season = 10 hari)
    private int totalDay;
    private Season currentSeason;
    private Weather currentWeather;
    private int rainyDaysInSeason; //1 season minimal rain 2x


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
    }

    public GameCalendar(StatisticsManager data){
        this.dayInSeason = data.savedDayInSeason; //diawali day 1
        this.totalDay = data.savedTotalDay; //diawali day 1
        this.currentSeason = data.savedSeason;
        this.currentWeather = data.savedWeather;
        this.rainyDaysInSeason = 0;
    }

    private void changeSeason(){
        if (currentSeason == Season.SPRING) {
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

    public int getDayInSeason(){
        return dayInSeason;
    }
    public int getTotalDay(){
        return totalDay;
    }
    public Season getCurrentSeason(){
        return currentSeason;
    }
    public Weather getCurrentWeather(){
        return currentWeather;
    }

    public void displayCalendar(){ // ADD THIS METHOD
    System.out.println("Current Day in Season: " + dayInSeason);
    System.out.println("Total Game Day: " + totalDay);
    System.out.println("Current Season: " + currentSeason);
    System.out.println("Current Weather: " + currentWeather);
    }
}