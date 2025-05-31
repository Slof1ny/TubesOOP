package action;

import npc.NPC;
import core.player.Player;
import core.player.RelationshipStatus;
import item.Item;
import time.Time;

/**
 * Kelas untuk Mengelola Aksi Terkait NPC.
 */
public class NPCActions {
    private Player player;
    // private GameManager gameManager; // Untuk mengelola waktu, hari, musim, dll.

    private Time time;

    public NPCActions(Player player, Time time) {
        this.player = player;
        this.time = time;
    }

    /**
     * Aksi Chatting dengan NPC.
     * Efek: -10 energi, +10 menit waktu game (konseptual), +10 heartPoints NPC.
     * @param npc NPC yang diajak bicara.
     * @return Pesan hasil interaksi.
     */
    public String chatWithNPC(NPC npc) {
        if (npc == null) return "NPC tidak valid untuk diajak bicara.";
        if (player.getEnergy() < (Player.MIN_ENERGY + 10)) {
            return "Energi tidak cukup untuk berbicara dengan " + npc.getName() + ".";
        }
        player.setEnergy(player.getEnergy() - 10);
        // gameManager.advanceTime(10); // Logika waktu akan dihandle oleh GameManager
        npc.addHeartPoints(10);
        // System.out.println(npc.getChatDialogue(player)); // Tampilkan dialog khas NPC
        return "Kamu berbincang dengan " + npc.getName() + ".\n" +
               "Energi pemain: " + player.getEnergy() + ". Heart points " + npc.getName() + ": " + npc.getHeartPoints() + ". (Waktu +10 menit)";
    }

    /**
     * Aksi Gifting item ke NPC.
     * Efek: -5 energi, +10 menit waktu game (konseptual), item hilang, heartPoints berubah.
     * @param npc NPC penerima hadiah.
     * @param itemToGift Item yang diberikan.
     * @return Pesan hasil interaksi.
     */
    public String giftToNPC(NPC npc, Item itemToGift) {
        if (npc == null) return "NPC tidak valid untuk diberi hadiah.";
        if (itemToGift == null) {
            return "Item yang ingin diberikan tidak valid.";
        }
        if (player.getEnergy() < (Player.MIN_ENERGY + 5)) {
            return "Energi tidak cukup untuk memberi hadiah kepada " + npc.getName() + ".";
        }
        // Cek jumlah item berdasarkan nama
        if (player.getInventory().getItemCount(itemToGift.getName()) < 1) {
            return "Kamu tidak memiliki " + itemToGift.getName() + " di inventory.";
        }
        player.setEnergy(player.getEnergy() - 5);
        // gameManager.advanceTime(10); // Logika waktu
        player.getInventory().removeByName(itemToGift.getName(), 1);

        String reaction = npc.getReactionToItem(itemToGift);
        int heartChange = 0;
        String reactionMessage = "";

        switch (reaction) {
            case "loved":
                heartChange = 25;
                reactionMessage = npc.getName() + " sangat menyukai " + itemToGift.getName() + "!";
                break;
            case "liked":
                heartChange = 20;
                reactionMessage = npc.getName() + " menyukai " + itemToGift.getName() + ".";
                break;
            case "hated":
                heartChange = -25;
                reactionMessage = npc.getName() + " terlihat tidak senang dengan " + itemToGift.getName() + ".";
                break;
            default: // neutral
                reactionMessage = npc.getName() + " menerima " + itemToGift.getName() + " dengan biasa saja.";
                break;
        }
        npc.addHeartPoints(heartChange);
        if (!reactionMessage.isEmpty()) {
            System.out.println(reactionMessage);
        }


        return "Hadiah diberikan kepada " + npc.getName() + ".\n" +
               "Energi pemain: " + player.getEnergy() + ". Heart points " + npc.getName() + ": " + npc.getHeartPoints() + ". (Waktu +10 menit)";
    }

    /**
     * Aksi Proposing ke NPC.
     * Syarat: NPC heartPoints maks (150), player punya Proposal Ring.
     * Efek diterima: -10 energi, +1 jam waktu game (konseptual), NPC jadi Fiance.
     * Efek ditolak: -20 energi, +1 jam waktu game (konseptual).
     * @param npc NPC yang akan dilamar.
     * @param hasProposalRing Apakah player memiliki Proposal Ring (diasumsikan item "PROPOSAL_RING").
     * @return Pesan hasil interaksi.
     */
    public String proposeToNPC(NPC npc, boolean hasProposalRing) {
        if (npc == null) return "NPC tidak valid untuk dilamar.";
        if (!hasProposalRing) {
            return "Kamu membutuhkan Proposal Ring untuk melamar!";
        }
        if (player.getEnergy() < (Player.MIN_ENERGY + 10)) { // Ensure player can afford the energy cost
            return "Energi tidak cukup untuk melamar.";
        }
        // Check if player is already committed, using the relationship status with their current partner
        if (player.getPartner() != null && player.getRelationshipStatus(player.getPartner()) != RelationshipStatus.SINGLE) {
             return "Kamu sudah memiliki komitmen dengan " + player.getPartner().getName() + ". Kamu tidak bisa melamar NPC lain.";
        }
        
        time.advanceGameMinutes(60);

        if (npc.getHeartPoints() >= NPC.MAX_HEART_POINTS && npc.getRelationshipStatus() == RelationshipStatus.SINGLE) { // NPC must also be single
            player.setEnergy(player.getEnergy() - 10);
            npc.setRelationshipStatus(RelationshipStatus.FIANCE); // Set NPC's status
            player.setRelationshipStatus(npc, RelationshipStatus.FIANCE); // Sets player's status and partner
            player.setDaysSinceProposalWithPartner(0); // MODIFIED: Set on player object
            return npc.getName() + " menerima lamaranmu! Kalian sekarang bertunangan.\n" +
                   "Energi pemain: " + player.getEnergy() + ". (Waktu +1 jam)";
        } else {
            player.setEnergy(player.getEnergy() - 20);
             String reason = npc.getHeartPoints() < NPC.MAX_HEART_POINTS ? "hatimu belum cukup dekat ("+ npc.getHeartPoints() + "/" + NPC.MAX_HEART_POINTS + " hati)." : "NPC tersebut tidak lajang.";
            return npc.getName() + " menolak lamaranmu. Mungkin " + reason + "\n" +
                   "Energi pemain: " + player.getEnergy() + ". (Waktu +1 jam)";
        }
    }

    public String marryNPC(NPC npc, boolean hasProposalRing) {
        if (npc == null) return "NPC tidak valid untuk dinikahi.";
        
        // Check if the NPC is the player's current partner and their status with the player is FIANCE
        if (player.getPartner() != npc || player.getRelationshipStatus(npc) != RelationshipStatus.FIANCE) {
            return "Kamu hanya bisa menikahi " + npc.getName() + " jika dia adalah tunanganmu.";
        }
        
        // MODIFIED: Check player's daysSinceProposalWithPartner
        if (player.getDaysSinceProposalWithPartner() < 1) {
            return "Kamu baru saja bertunangan dengan " + npc.getName() + ". Pernikahan bisa dilakukan paling cepat besok.";
        }
        if (!hasProposalRing) {
            return "Kamu membutuhkan Proposal Ring untuk menikah!";
        }
        if (player.getEnergy() < (Player.MIN_ENERGY + 80)) { // Ensure player can afford energy
            return "Energi tidak cukup untuk upacara pernikahan.";
        }

        player.setEnergy(player.getEnergy() - 80);
        time.skipTo(22, 0);
        // Player location might change based on game design, e.g., to HouseMap
        // For now, assuming the caller or GameManager handles location change post-marriage event.
        // player.setLocation("House"); // Example if they auto-move to house

        // This will set NPC's status to MARRIED, player's status with NPC to MARRIED,
        // confirm NPC as partner, and reset daysSinceProposalWithPartner in Player.
        player.setRelationshipStatus(npc, RelationshipStatus.MARRIED); 
        npc.setRelationshipStatus(RelationshipStatus.MARRIED); // Also ensure NPC's own status is updated

        return "Selamat! Kamu dan " + npc.getName() + " sekarang resmi menikah!\n" +
               "Hari ini dihabiskan untuk merayakannya. (Waktu melompat ke 22.00)\n" +
               "Energi pemain: " + player.getEnergy() + ".";
    }
    
    public String divorceNPC() {
        if (player.getPartner() == null || player.getRelationshipStatus(player.getPartner()) != RelationshipStatus.MARRIED) {
            return "Kamu tidak memiliki pasangan untuk diceraikan.";
        }
        NPC formerPartner = player.getPartner();
        formerPartner.addHeartPoints(-NPC.MAX_HEART_POINTS); // Drastically reduce hearts
        System.out.println(formerPartner.getName() + "'s heart points drastically reduced due to divorce.");
        
        // Player.setRelationshipStatus handles resetting player's partner and daysSinceProposal counter
        player.setRelationshipStatus(formerPartner, RelationshipStatus.SINGLE); 
        formerPartner.setRelationshipStatus(RelationshipStatus.SINGLE); // NPC also becomes single

        time.advanceGameMinutes(30); // Time passes for the divorce proceedings
        return "Kamu telah resmi bercerai dengan " + formerPartner.getName() + ".\n" +
               "Hubunganmu dengannya kini kembali ke awal.";
    }

    public void incrementDayForMarriageCheck() {
        // MODIFY: Check and increment player's daysSinceProposalWithPartner
        // This should only increment if the player is engaged (FIANCE) to their current partner.
        if (player.getPartner() != null && player.getRelationshipStatus(player.getPartner()) == RelationshipStatus.FIANCE) {
            int currentDays = player.getDaysSinceProposalWithPartner();
            if (currentDays != -1) { // Only if a proposal is effectively active (0 or more)
                player.setDaysSinceProposalWithPartner(currentDays + 1);
                System.out.println("DEBUG: Days since proposal with " + player.getPartner().getName() + " incremented to: " + player.getDaysSinceProposalWithPartner());
            }
        }
    }
}