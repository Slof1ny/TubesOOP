package npc;

import core.player.Player;
import core.player.RelationshipStatus;
import item.Item;
import java.util.HashSet;
import java.util.Set;

public abstract class NPC {
    protected String name;
    protected int heartPoints;
    protected Set<String> lovedItemIds;
    protected Set<String> likedItemIds;
    protected Set<String> hatedItemIds;
    protected RelationshipStatus relationshipStatus;
    protected String homeLocation;

    public static final int MAX_HEART_POINTS = 150;
    public static final int MIN_HEART_POINTS = 0;

    public NPC(String name, String homeLocation, Set<String> lovedItemIds, Set<String> likedItemIds, Set<String> hatedItemIds) {
        this.name = name;
        this.homeLocation = homeLocation;
        this.heartPoints = MIN_HEART_POINTS;
        this.lovedItemIds = lovedItemIds != null ? new HashSet<>(lovedItemIds) : new HashSet<>();
        this.likedItemIds = likedItemIds != null ? new HashSet<>(likedItemIds) : new HashSet<>();
        this.hatedItemIds = hatedItemIds != null ? new HashSet<>(hatedItemIds) : new HashSet<>();
        this.relationshipStatus = RelationshipStatus.SINGLE;
    }

    public String getName() {
        return name;
    }

    public int getHeartPoints() {
        return heartPoints;
    }

    public RelationshipStatus getRelationshipStatus() {
        return relationshipStatus;
    }

    public String getHomeLocation() {
        return homeLocation;
    }

    public void setRelationshipStatus(RelationshipStatus status) {
        this.relationshipStatus = status;
        System.out.println(this.name + " sekarang berstatus: " + status);
    }

    public void addHeartPoints(int amount) {
        this.heartPoints += amount;
        if (this.heartPoints > MAX_HEART_POINTS) {
            this.heartPoints = MAX_HEART_POINTS;
        } else if (this.heartPoints < MIN_HEART_POINTS) {
            this.heartPoints = MIN_HEART_POINTS;
        }
    }

    public String getReactionToItem(Item giftedItem) {
        if (giftedItem == null || giftedItem.getName() == null) {
            return "neutral";
        }
        String itemName = giftedItem.getName();
        if (lovedItemIds.contains(itemName)) return "loved";
        if (likedItemIds.contains(itemName)) return "liked";
        if (hatedItemIds.contains(itemName)) return "hated";
        return "neutral";
    }

    public String getChatDialogue(Player player) {
        return name + ": \"Halo, pemain. Ada yang bisa kubantu?\"";
    }

    @Override
    public String toString() {
        return "NPC{" +
               "name='" + name + '\'' +
               ", heartPoints=" + heartPoints +
               ", relationshipStatus=" + relationshipStatus +
               ", location='" + homeLocation + '\'' +
               '}';
    }
}
