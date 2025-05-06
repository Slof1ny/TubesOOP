package item;

public class Crop extends Item implements EdibleItem {
    private int quantityPerHarvest;

    public Crop(String name, int buyPrice, int sellPrice, int quantityPerHarvest){
        super(name, buyPrice, sellPrice);
        this.quantityPerHarvest = quantityPerHarvest;
    }

    public int getQuantityPerHarvest(){
        return quantityPerHarvest;
    }

    @Override
    public String getCategory(){
        return "Crop";
    }

    @Override
    public int getEnergyRestored(){
        return 3;
    }
    
}
