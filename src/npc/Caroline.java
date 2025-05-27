package npc;

import java.util.Set;
import core.player.Player;

public class Caroline extends NPC {
    public Caroline() {
        super("Caroline", "Caroline's Carpentry",
            Set.of("ITEM_FIREWOOD", "ITEM_COAL"), // lovedItems
            Set.of("CROP_POTATO", "CROP_WHEAT"),   // likedItems
            Set.of("CROP_HOT_PEPPER")              // hatedItems
        );
    }
    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Hai " + player.getName() + ", sedang mencari kayu berkualitas? Atau mungkin butuh sesuatu diperbaiki?\"";
    }
}
