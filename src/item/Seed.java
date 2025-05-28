package item;

import core.world.Season;
import java.util.List;
import java.util.Arrays;

public class Seed extends Item{
    private List<Season> seasons;
    private int daysToGrow;
    private int quantityPerHarvest;

    public Seed(String name, int buyPrice, List<Season> seasons, int daysToGrow, int quantityPerHarvest){
        super(name, buyPrice, buyPrice / 2);
        this.seasons = seasons;
        this.daysToGrow = daysToGrow;
        this.quantityPerHarvest = quantityPerHarvest;
    }

    @Override
    public String getCategory(){
        return "Seed";
    }

    public List<Season> getSeasons(){
        return seasons;
    }

    public int getDaysToGrow(){
        return daysToGrow;
    }

    public int getQuantityPerHarvest() {
        return quantityPerHarvest;
    }

    public static Seed getSeedByName(String name) {
        switch (name) {
            case "Parsnip Seeds":
                return new Seed("Parsnip Seeds", 20, Arrays.asList(Season.SPRING), 1, 1);
            case "Cauliflower Seeds":
                return new Seed("Cauliflower Seeds", 80, Arrays.asList(Season.SPRING), 5, 1); 
            case "Potato Seeds":
                return new Seed("Potato Seeds", 50, Arrays.asList(Season.SPRING), 3, 1); 
            case "Wheat Seeds":
                return new Seed("Wheat Seeds", 60, Arrays.asList(Season.SPRING, Season.FALL), 1, 3);
            case "Blueberry Seeds":
                return new Seed("Blueberry Seeds", 80, Arrays.asList(Season.SUMMER), 7, 3); 
            case "Tomato Seeds":
                return new Seed("Tomato Seeds", 50, Arrays.asList(Season.SUMMER), 3, 1); 
            case "Hot Pepper Seeds":
                return new Seed("Hot Pepper Seeds", 40, Arrays.asList(Season.SUMMER), 1, 1); 
            case "Melon Seeds":
                return new Seed("Melon Seeds", 80, Arrays.asList(Season.SUMMER), 4, 1); 
            case "Cranberry Seeds":
                return new Seed("Cranberry Seeds", 100, Arrays.asList(Season.FALL), 2, 10);
            case "Pumpkin Seeds":
                return new Seed("Pumpkin Seeds", 150, Arrays.asList(Season.FALL), 7, 1);
            case "Grape Seeds":
                return new Seed("Grape Seeds", 60, Arrays.asList(Season.FALL), 3, 20);
            default:
                return null;
        }
    }
}