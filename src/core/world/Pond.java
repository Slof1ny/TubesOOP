// TubesOOP/src/core/world/Pond.java
package core.world;

import core.player.Player; // Required for interact method

public class Pond extends DeployedObject {
    public Pond(int x, int y, int w, int h, char symbol) {
        super(x, y, w, h, symbol);
    }

    // Add a no-argument constructor for consistency, similar to ShippingBin
    public Pond() {
        super(0, 0, 0, 0, ' '); // Placeholder values
    }

    @Override
    public void interact(Player p, FarmMap map) {
        // As discussed, the actual interaction logic (e.g., opening a fishing menu)
        // will be handled by the FarmMapController when the 'E' key is pressed.
        // This method here can be a placeholder or used for more complex internal
        // object-specific interactions not directly tied to a player action.
        // For now, it just prints a message (which will be superseded by GUI dialogs).
        System.out.println("You are interacting with the Pond.");
        // Example: map.getFishingManager().openPond(p); // If you had a FishingManager instance here
    }
}