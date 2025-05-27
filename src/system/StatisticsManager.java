package system;

import core.world.Season;
import core.world.Weather;
public class StatisticsManager {
    public String playerName; 
    public int savedHour;
    public int savedMinute;
    public int savedDayInSeason;
    public int savedTotalDay;
    public Season savedSeason;
    public Weather savedWeather;

    public StatisticsManager(String playerName, int hour, int minute, int day, int total, Season season, Weather weather) {
        this.playerName = playerName;
        this.savedHour = hour;
        this.savedMinute = minute;
        this.savedDayInSeason = day;
        this.savedTotalDay = total;
        this.savedSeason = season;
        this.savedWeather = weather;
    }

    public void printSavedData() {
        System.out.println("--- Saved Statistics ---");
        System.out.println("Player Name: " + playerName);
        System.out.println("Hour: " + savedHour);
        System.out.println("Minute: " + savedMinute);
        System.out.println("Day In Season: " + savedDayInSeason);
        System.out.println("Total Day: " + savedTotalDay);
        System.out.println("Season: " + savedSeason);
        System.out.println("Weather: " + savedWeather);
        System.out.println("------------------------");
    }
}
