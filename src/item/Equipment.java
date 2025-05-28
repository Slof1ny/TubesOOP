package item;

public class Equipment extends Item {
    private boolean isEquipped;
    
    public Equipment(String name, int buyPrice, int sellPrice){
        super(name, buyPrice, sellPrice);
        this.isEquipped = false;
    }

    @Override
    public String getCategory(){
        return "Equipment";
    }
    
    public boolean isEquipped() {
        return isEquipped;
    }
    
    public void setEquipped(boolean equipped) {
        this.isEquipped = equipped;
    }
}