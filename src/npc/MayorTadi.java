

package npc;

import java.util.Set;
import core.player.Player;
import item.Item;

public class MayorTadi extends NPC {
    public MayorTadi() {
        super("Mayor Tadi", "Mayor's Manor",
            Set.of("FISH_LEGEND"),
            Set.of("FISH_ANGLER", "FISH_CRIMSONFISH", "FISH_GLACIERFISH"),
            null
        );
    }

    @Override
    public String getReactionToItem(Item giftedItem) {
        if (giftedItem == null || giftedItem.getName() == null) return "hated";
        String itemName = giftedItem.getName();
        if (getLovedItemIds().contains(itemName)) return "loved";
        if (getLikedItemIds().contains(itemName)) return "liked";
        return "hated";
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Ah, selamat datang! Bagaimana keadaan Spakbor Hills hari ini? Semoga pajaknya lancar, ya.\"";
    }
    public Set<String> getLovedItemIds() { return lovedItemIds; }
    public Set<String> getLikedItemIds() { return likedItemIds; }
}
