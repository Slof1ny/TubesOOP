package npc;

import java.util.Set;
import core.player.Player;

public class Dasco extends NPC {

    public Dasco() {
        super("Dasco", "Dasco's Gambling Den",
            Set.of("The Legends of Spakbor", "Cooked Pig's Head", "Wine", "Fugu", "Spakbor Salad"), 
            Set.of("Fish Sandwich", "Fish Stew", "Baguette", "Fish n’ Chips"), 
            Set.of("Legend", "Grape", "Cauliflower", "Wheat", "Pufferfish", "Salmon")
        );
    }

    @Override
    public String getChatDialogue(Player player) {
        return getName() + ": \"Selamat datang di kasinoku, " + player.getName() + "! Siap mempertaruhkan keberuntunganmu hari ini? Jangan sampai bangkrut, ya! Haha!\"";
    }
}