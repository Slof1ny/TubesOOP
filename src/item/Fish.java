package item;

public class Fish extends Item implements EdibleItem{
    public enum FishType { COMMON, REGULAR, LEGENDARY}

    private FishType type;

    public Fish(String name, int sellPrice, FishType type){
        super(name, 0, sellPrice); // tidak bisa membeli ikan
    }

    @Override
    public String getCategory(){
        return "Fish";
    }

    @Override
    public int getEnergyRestored(){
        return 1;
    }

    public FishType getFishType(){
        return type;
    }
    
}
