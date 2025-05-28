package npc;

import core.player.Player;
import item.Item;
// import item.ItemCategory;
import java.util.Set;

public class Orenji extends NPC {
    public Orenji() {
        super(
            "Orenji si Kucing Barista",
            "Warung Kopi Orenji (Tersembunyi)",
            Set.of("ITEM_PREMIUM_COFFEE_BEAN", "ITEM_CATNIP_SUPREME"),
            Set.of("FOOD_CROISSANT", "ITEM_WARM_MILK"),
            Set.of("ITEM_CHEAP_COFFEE_POWDER", "ITEM_DOG_BISCUIT")
        );
    }

//     @Override
//     public String getReactionToItem(Item giftedItem) {
//         if (giftedItem == null) {
//             System.out.println(this.name + ": ...meow? (Dia memiringkan kepalanya, tampak bingung dengan ketiadaan.)");
//             return "neutral";
//         }

//         ItemCategory category = giftedItem.getCategory();
//         if (category == ItemCategory.FISH || category == ItemCategory.COMMON_FISH ||
//             category == ItemCategory.REGULAR_FISH || category == ItemCategory.LEGENDARY_FISH) {
//             System.out.println(this.name + ": *HISSSS* Meooowww! (Dia terlihat sangat tersinggung dengan " + giftedItem.getName() + " itu!)");
//             return "hated";
//         }

//         String itemId = giftedItem.getItemId();
//         if (this.lovedItemIds.contains(itemId)) {
//             System.out.println(this.name + ": Purrrr... Meow. (Matanya berbinar melihat " + giftedItem.getName() + ", sepertinya ini pilihan yang tepat.)");
//             return "loved";
//         }
//         if (this.likedItemIds.contains(itemId)) {
//             System.out.println(this.name + ": Meow. (Dia mengangguk perlahan pada " + giftedItem.getName() + ", sebuah pengakuan singkat.)");
//             return "liked";
//         }
//         if (this.hatedItemIds.contains(itemId)) {
//             System.out.println(this.name + ": Mrrrrow... (Dia menatap " + giftedItem.getName() + " dengan pandangan menghakimi.)");
//             return "hated";
//         }
//         System.out.println(this.name + ": ...meow? (Dia memiringkan kepalanya pada " + giftedItem.getName() + ", tampak tidak terkesan.)");
//         return "neutral";
//     }

//     @Override
//     public String getChatDialogue(Player player) {
//         // Dialog Orenji berdasarkan heart points
//         if (this.heartPoints < 20) {
//             return this.name + ": Meow. (Dia sibuk membersihkan kumisnya, tidak terlalu memperhatikanmu, " + player.getName() + ".)";
//         } else if (this.heartPoints < 70) {
//             return this.name + ": Purrr... Meow meow, " + player.getName() + "? (Dia bertanya apakah kopimu sudah sesuai standar hari ini.)";
//         } else if (this.heartPoints < NPC.MAX_HEART_POINTS) {
//             return this.name + ": Meooooow purrrr. (Dia mulai bercerita tentang makna eksistensi kepada " + player.getName() + "... dalam bahasa kucing, tentu saja.)";
//         } else {
//             return this.name + ": Meow! <3 (Dia menggosokkan kepalanya ke kakimu, " + player.getName() + ". Sepertinya dia benar-benar menyukaimu!)";
//         }
//     }
}
