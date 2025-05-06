package item;

public class Food extends Item implements EdibleItem {
    private int energyRestored;

    public Food(String name, int buyPrice, int sellPrice, int energyRestored){
        super(name, buyPrice, sellPrice);
        this.energyRestored = energyRestored;
    }

    @Override
    public String getCategory(){
        return "Food";
    }

    @Override
    public int getEnergyRestored(){
        return energyRestored;
    }
    
}
