package npc;

import java.util.Set;
import item.Item;
import core.player.Player;

public class Emily extends NPC {

    public Emily() {
        super("Emily", "Store",
            null,
            Set.of("Catfish", "Salmon", "Sardine"), 
            Set.of("Coal", "Firewood") 
        );
    }

    @Override
    public String getReactionToItem(Item giftedItem) {
        if (giftedItem == null) return "neutral";

        // Emily loves all items with category "Seed"
        String category = giftedItem.getCategory();
        if (category != null && category.equals("Seed")) {
            return "loved";
        }

        // For other items, defer to the parent NPC's logic for liked/hated/neutral
        return super.getReactionToItem(giftedItem);
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Selamat datang di toko, " + player.getName() + "! Cari bibit, makanan, atau sekadar ngobrol? Aku siap melayani!\"";
    }
}