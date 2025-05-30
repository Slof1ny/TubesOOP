package core.world;
import core.player.Player;

public class InteractionSpot extends DeployedObject {
    private String interactionType; // e.g., "BED", "TV"
    public InteractionSpot(int x, int y, char symbol, String type) {
        super(x, y, 1, 1, symbol); // Assume 1x1 interaction spots
        this.interactionType = type;
    }
    public String getInteractionType() { return interactionType; }
    @Override public boolean isWalkable() { return true; } // Player stands on it to interact
    public void interact(Player p, GameMap mapContext) { /* Logic handled by controller */ }
    public void interact(Player p, FarmMap map){};
    
}
