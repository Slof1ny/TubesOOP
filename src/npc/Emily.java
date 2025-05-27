
package npc;

import java.util.Set;
import item.Item;
import core.player.Player;

public class Emily extends NPC {

    public Emily() {
        super("Emily", "Store",
            null,
            Set.of("FISH_CATFISH", "FISH_SALMON", "FISH_SARDINE"),
            Set.of("ITEM_COAL", "ITEM_WOOD")
        );
    }


    @Override
    public String getReactionToItem(Item giftedItem) {
        if (giftedItem == null) return "neutral";
        String category = giftedItem.getCategory();
        if (category != null && category.equals("Seed")) {
            return "loved";
        }
        return super.getReactionToItem(giftedItem);
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Selamat datang di toko, " + player.getName() + "! Cari bibit, makanan, atau sekadar ngobrol? Aku siap melayani!\"";
    }
}
