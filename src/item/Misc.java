package item;

public class Misc extends Item {
    public Misc(String name, int buyPrice, int sellPrice){
        super(name, buyPrice, sellPrice);
        if(sellPrice >= buyPrice){
            throw new IllegalArgumentException("Sell price must be less than buy price for Misc items.");
        } 
    }
    
    @Override
    public String getCategory(){
        return "Misc";
    }
}
