package npc;

import java.util.Set;
import core.player.Player;

public class Dasco extends NPC {

    public Dasco() {
        super("Dasco", "Dasco's Gambling Den",
            Set.of("FOOD_THE_LEGENDS_OF_SPAKBOR", "FOOD_COOKED_PIGS_HEAD", "FOOD_WINE", "FOOD_FUGU", "FOOD_SPAKBOR_SALAD"),
            Set.of("FOOD_FISH_SANDWICH", "FOOD_FISH_STEW", "FOOD_BAGUETTE", "FOOD_FISH_N_CHIPS"),
            Set.of("FISH_LEGEND", "CROP_GRAPE", "CROP_CAULIFLOWER", "CROP_WHEAT", "FISH_PUFFERFISH", "FISH_SALMON")
        );
    }


    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Selamat datang di kasinoku, " + player.getName() + "! Siap mempertaruhkan keberuntunganmu hari ini? Jangan sampai bangkrut, ya! Haha!\"";
    }
}
