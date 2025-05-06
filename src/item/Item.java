package item;

public abstract class Item {
    protected String name;
    protected int buyPrice;
    protected int sellPrice;

    public Item(String name, int buyPrice, int sellPrice){
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public String getName(){
        return name;
    }
    
    public int getBuyPrice() {
        return buyPrice;
    }

    public int getSellPrice(){
        return sellPrice;
    }

    public abstract String getCategory();
    
}
