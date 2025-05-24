package core.world;

import core.player.*;
class Farm {
    private String name;
    private Player player;
    private FarmMap farmMap;
    private int day;
    private Season season;
    private Weather weather;

    public Farm(String name, Player player) {
        this.name = name;
        this.player = player;
        this.farmMap = new FarmMap(player);
        this.day = 1;
        this.season = Season.SPRING;
        this.weather = Weather.SUNNY;;
    }

    public String getName() {
        return name;
    }

    public Player getPlayer() {
        return player;
    }

    public FarmMap getFarmMap() {
        return farmMap;
    }

    public int getDay() {
        return day;
    }

    public Season getSeason() {
        return season;
    }

    public Weather getWeather() {
        return weather;
    }

}
