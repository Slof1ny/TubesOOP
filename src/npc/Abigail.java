
package npc;

import java.util.Set;
import core.player.Player;

public class Abigail extends NPC {
    public Abigail() {
        super("Abigail", "Abigail's Tent",
            Set.of("CROP_BLUEBERRY", "CROP_MELON", "CROP_PUMPKIN", "CROP_GRAPE", "CROP_CRANBERRY"),
            Set.of("FOOD_BAGUETTE", "FOOD_PUMPKIN_PIE", "FOOD_WINE"),
            Set.of("CROP_HOT_PEPPER", "CROP_CAULIFLOWER", "CROP_PARSNIP", "CROP_WHEAT")
        );
    }
    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Yo, " + player.getName() + "! Hari yang bagus untuk berpetualang, kan? Aku baru menemukan gua keren kemarin!\"";
    }
}
