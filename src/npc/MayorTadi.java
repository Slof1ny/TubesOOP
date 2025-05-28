package npc;

import java.util.Set;
import core.player.Player;
import item.Item;

public class MayorTadi extends NPC {
    public MayorTadi() {
        super("Mayor Tadi", "Mayor's Manor",
            Set.of("Legend"),
            Set.of("Angler", "Crimsonfish", "Glacierfish"),
            null
        );
    }

    @Override
    public String getReactionToItem(Item giftedItem) {
        return super.getReactionToItem(giftedItem);
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Ah, selamat datang! Bagaimana keadaan Spakbor Hills hari ini? Semoga pajaknya lancar, ya.\"";
    }
}