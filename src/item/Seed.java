package item;

public class Seed extends Item{
    private String season; //menunggu implementasi class ENUM Season
    private int daysToHarvest;

    public Seed(String name, int buyPrice, String season, int daysToHarvest){
        super(name, buyPrice, buyPrice / 2);
        this.season = season;
        this.daysToHarvest = daysToHarvest;
    }

    @Override
    public String getCategory(){
        return "Seed";
    }

    public String getSeason(){
        return season;
    }

    public int getDaysToHarvest(){
        return daysToHarvest;
    }
    
    
}
