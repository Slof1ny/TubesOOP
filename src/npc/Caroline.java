package npc;

import java.util.Set;
import core.player.Player;

public class Caroline extends NPC {
    public Caroline() {
        super("Caroline", "Caroline's Carpentry",
            Set.of("Firewood", "Coal"), 
            Set.of("Potato", "Wheat"),
            Set.of("Hot Pepper")         
        );
    }
    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Hai " + player.getName() + ", sedang mencari kayu berkualitas? Atau mungkin butuh sesuatu diperbaiki?\"";
    }
}