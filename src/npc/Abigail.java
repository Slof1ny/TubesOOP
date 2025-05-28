package npc;

import java.util.Set;
import core.player.Player;

public class Abigail extends NPC {
    public Abigail() {
        super("Abigail", "Abigail's Tent",
            Set.of("Blueberry", "Melon", "Pumpkin", "Grape", "Cranberry"),
            Set.of("Baguette", "Pumpkin Pie", "Wine"),
            Set.of("Hot Pepper", "Cauliflower", "Parsnip", "Wheat")
        );
    }
    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Yo, " + player.getName() + "! Hari yang bagus untuk berpetualang, kan? Aku baru menemukan gua keren kemarin!\"";
    }
}