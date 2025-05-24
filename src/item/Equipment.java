package item;

public class Equipment extends Item {
    public Equipment(String name, int buyPrice, int sellPrice){
        super(name, buyPrice, sellPrice);
    }

    @Override
    public String getCategory(){
        return "Equipment";
    }
    
}
