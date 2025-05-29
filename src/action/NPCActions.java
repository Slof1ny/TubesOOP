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
    private int daysSinceLastProposal; // Untuk melacak syarat menikah
    private Time time;

    public NPCActions(Player player, Time time) {
        this.player = player;
        this.time = time;
        this.daysSinceLastProposal = -1; // Belum ada proposal yang aktif
    }

    /**
     * Aksi Chatting dengan NPC.
     * Efek: -10 energi, +10 menit waktu game (konseptual), +10 heartPoints NPC.
     * @param npc NPC yang diajak bicara.
     * @return Pesan hasil interaksi.
     */
    public String chatWithNPC(NPC npc) {
        if (npc == null) return "NPC tidak valid untuk diajak bicara.";
        if (player.getEnergy() < 10) {
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
        if (player.getEnergy() < 5) {
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
        if (!hasProposalRing) { // Atau cek player.getInventory().hasItem("ITEM_PROPOSAL_RING")
            return "Kamu membutuhkan Proposal Ring untuk melamar!";
        }
        if (npc.getRelationshipStatus() != RelationshipStatus.SINGLE) {
            return npc.getName() + " tidak bisa dilamar saat ini (status: " + npc.getRelationshipStatus() + ").";
        }
        // Partner logic not implemented in Player, so this check is skipped.

        // gameManager.advanceTime(60); // Logika waktu +1 jam

        if (npc.getHeartPoints() >= NPC.MAX_HEART_POINTS) {
            if (player.getEnergy() < 10) return "Energi tidak cukup untuk melamar (jika diterima).";
            player.setEnergy(player.getEnergy() - 10);
            npc.setRelationshipStatus(RelationshipStatus.FIANCE);
            // player.setPartner(npc); // Player sekarang punya tunangan (implementasi partner belum ada)
            daysSinceLastProposal = 0; // Reset hari sejak proposal berhasil
            return npc.getName() + " menerima lamaranmu! Kalian sekarang bertunangan.\n" +
                   "Energi pemain: " + player.getEnergy() + ". (Waktu +1 jam)";
        } else {
            if (player.getEnergy() < 20) return "Energi tidak cukup untuk melamar (jika ditolak).";
            player.setEnergy(player.getEnergy() - 20);
            return npc.getName() + " menolak lamaranmu. Mungkin kamu perlu lebih dekat dengannya ("+ npc.getHeartPoints() + "/" + NPC.MAX_HEART_POINTS + " hati).\n" +
                   "Energi pemain: " + player.getEnergy() + ". (Waktu +1 jam)";
        }
    }

    /**
     * Aksi Marry dengan NPC.
     * Syarat: NPC status Fiance, minimal 1 hari setelah proposal, player punya Proposal Ring.
     * Efek: -80 energi, waktu skip ke 22.00 (konseptual), NPC jadi Spouse.
     * @param npc NPC yang akan dinikahi.
     * @param hasProposalRing Apakah player memiliki Proposal Ring.
     * @return Pesan hasil interaksi.
     */
    public String marryNPC(NPC npc, boolean hasProposalRing) {
        if (npc == null) return "NPC tidak valid untuk dinikahi.";
        // Asumsi: daysSinceLastProposal di-increment setiap hari oleh GameManager
        int daysPassedSinceProposal = (daysSinceLastProposal == -1) ? -1 : this.daysSinceLastProposal;

        if (!hasProposalRing) { // Proposal Ring tidak hilang, jadi tetap dicek
            return "Kamu membutuhkan Proposal Ring untuk menikah!";
        }
        if (npc.getRelationshipStatus() != RelationshipStatus.FIANCE) {
            return "Kamu hanya bisa menikahi " + npc.getName() + " jika dia adalah tunanganmu.";
        }
        // Partner logic not implemented in Player, so this check is skipped.
        if (daysPassedSinceProposal < 1) { // Harus minimal 1 hari setelah proposal
            return "Kamu baru saja bertunangan dengan " + npc.getName() + ". Pernikahan bisa dilakukan paling cepat besok.";
        }
        if (player.getEnergy() < 80) {
            return "Energi tidak cukup untuk upacara pernikahan.";
        }

        player.setEnergy(player.getEnergy() - 80);
        // gameManager.skipTimeTo(22, 0); // Logika waktu skip ke 22.00
        // gameManager.setCurrentLocation(player, "House"); // Pemain dikembalikan ke rumah
        npc.setRelationshipStatus(RelationshipStatus.MARRIED);
        // player.setPartner(npc) sudah di-set saat propose dan statusnya Fiance

        daysSinceLastProposal = -1; // Reset setelah menikah

        return "Selamat! Kamu dan " + npc.getName() + " sekarang resmi menikah!\n" +
               "Hari ini dihabiskan untuk merayakannya. (Waktu melompat ke 22.00)\n" +
               "Energi pemain: " + player.getEnergy() + ".";
    }

    /**
     * Metode ini seharusnya dipanggil oleh GameManager setiap kali hari berganti
     * untuk mengupdate status penantian pernikahan.
     */
    public void incrementDayForMarriageCheck() {
        if (daysSinceLastProposal != -1) { // Hanya jika ada proposal yang aktif (status FIANCE)
            daysSinceLastProposal++;
        }
    }

    /**
     * Untuk keperluan testing atau debugging, memungkinkan set manual.
     * @param days Jumlah hari sejak proposal terakhir.
     */
    public void setDaysSinceLastProposalForTesting(int days) {
        this.daysSinceLastProposal = days;
    }
}