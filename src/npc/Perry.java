package npc;

import java.util.Set;
import item.Item;
import core.player.Player;

public class Perry extends NPC {
    public Perry() {
        super("Perry", "Perry's Cabin",
            Set.of("Cranberry", "Blueberry"),
            Set.of("Wine"),                     
            null
        );
    }

    @Override
    public String getReactionToItem(Item giftedItem) {
        if (giftedItem == null) return "neutral";
        // Membenci semua item Fish
        String category = giftedItem.getCategory();
        if (category != null && category.equals("Fish")) { 
            return "hated";
        }
        return super.getReactionToItem(giftedItem);
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Oh... halo, " + player.getName() + ". Maaf, aku sedang... berpikir tentang plot novel baruku.\"";
    }
}