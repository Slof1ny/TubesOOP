package npc;

import java.util.Set;
import item.Item;
import core.player.Player;

public class Perry extends NPC {
    public Perry() {
        super("Perry", "Perry's Cabin",
            Set.of("CROP_CRANBERRY", "CROP_BLUEBERRY"), // lovedItems
            Set.of("FOOD_WINE"),                         // likedItems
            null // Hated items (semua ikan) dihandle khusus
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
        return super.getReactionToItem(giftedItem); // Cek loved/liked/neutral standar
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Oh... halo, " + player.getName() + ". Maaf, aku sedang... berpikir tentang plot novel baruku.\"";
    }
}
