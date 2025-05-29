package item;

import core.world.Season;
import java.util.List;

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
}