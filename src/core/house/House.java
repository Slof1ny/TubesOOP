package core.house;

import java.util.ArrayList;
import java.util.List;

import core.world.*;
import core.player.Player;

public class House extends DeployedObject {
    private List<Furniture> furnitures;

    public House(int x, int y, int w, int h, char symbol) {
        super(x, y, 6, 6, 'h');
        this.furnitures = new ArrayList<>();
    }

    public void addFurniture(Furniture f) {
        furnitures.add(f);
    }

    public List<Furniture> getFurnitures() {
        return furnitures;
    }

    public void interact(Player p, FarmMap map){
    }
}
