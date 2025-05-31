package npc;

import core.player.Player;
import item.Item;
import java.util.Set;

public class Orenji extends NPC {
    public Orenji() {
        super(
            "Orenji",
            "Warung Kopi Orenji (Tersembunyi)",
            Set.of("Premium Coffee Bean", "Catnip Supreme"),
            Set.of("Baguette", "Warm Milk"), 
            Set.of("Cheap Coffee Powder", "Dog Biscuit")
        );
    }

    @Override
    public String getReactionToItem(Item giftedItem) {
        if (giftedItem == null) {
            System.out.println(this.name + ": ...meow? (Dia memiringkan kepalanya, tampak bingung dengan ketiadaan.)");
            return "neutral";
        }

        // Special rule for all fish
        if (giftedItem.getCategory().equals("Fish")) {
            System.out.println(this.name + ": *HISSSS* Meooowww! (Dia terlihat sangat tersinggung dengan " + giftedItem.getName() + " itu!)");
            return "hated";
        }

        // Rely on the parent NPC's logic for loved/liked/hated items based on the Set<Item>
        String reaction = super.getReactionToItem(giftedItem);

        switch (reaction) {
            case "loved":
                System.out.println(this.name + ": Purrrr... Meow. (Matanya berbinar melihat " + giftedItem.getName() + ", sepertinya ini pilihan yang tepat.)");
                break;
            case "liked":
                System.out.println(this.name + ": Meow. (Dia mengangguk perlahan pada " + giftedItem.getName() + ", sebuah pengakuan singkat.)");
                break;
            case "hated":
                System.out.println(this.name + ": Mrrrrow... (Dia menatap " + giftedItem.getName() + " dengan pandangan menghakimi.)");
                break;
            case "neutral":
            default:
                System.out.println(this.name + ": ...meow? (Dia memiringkan kepalanya pada " + giftedItem.getName() + ", tampak tidak terkesan.)");
                break;
        }
        return reaction;
    }

    @Override
    public String getChatDialogue(Player player) {
        if (this.heartPoints < 20) {
            return this.name + ": Meow. (Dia sibuk membersihkan kumisnya, tidak terlalu memperhatikanmu, " + player.getName() + ".)";
        } else if (this.heartPoints < 70) {
            return this.name + ": Purrr... Meow meow, " + player.getName() + "? (Dia bertanya apakah kopimu sudah sesuai standar hari ini.)";
        } else if (this.heartPoints < NPC.MAX_HEART_POINTS) {
            return this.name + ": Meooooow purrrr. (Dia mulai bercerita tentang makna eksistensi kepada " + player.getName() + "... dalam bahasa kucing, tentu saja.)";
        } else {
            return this.name + ": Meow! <3 (Dia menggosokkan kepalanya ke kakimu, " + player.getName() + ". Sepertinya dia benar-benar menyukaimu!)";
        }
    }
}