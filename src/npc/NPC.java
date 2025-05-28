package npc;

import core.player.Player;
import core.player.RelationshipStatus;
import item.Item;
import item.Misc;       
import item.Seed; 
import item.Crop;
import item.FoodRegistry; 
import fishing.FishRegistry;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class NPC {
    protected String name;
    protected int heartPoints;
    protected Set<Item> lovedItems; 
    protected Set<Item> likedItems; 
    protected Set<Item> hatedItems;
    protected RelationshipStatus relationshipStatus;
    protected String homeLocation;

    public static final int MAX_HEART_POINTS = 150;
    public static final int MIN_HEART_POINTS = 0;

    public NPC(String name, String homeLocation, Set<String> lovedItemNames, Set<String> likedItemNames, Set<String> hatedItemNames) {
        this.name = name;
        this.homeLocation = homeLocation;
        this.heartPoints = MIN_HEART_POINTS;

        // Convert string names to Item objects using the helper method
        this.lovedItems = lovedItemNames != null ? lovedItemNames.stream()
                                                     .map(this::getItemByName)
                                                     .filter(Objects::nonNull)
                                                     .collect(Collectors.toSet()) : new HashSet<>();
        this.likedItems = likedItemNames != null ? likedItemNames.stream()
                                                     .map(this::getItemByName)
                                                     .filter(Objects::nonNull)
                                                     .collect(Collectors.toSet()) : new HashSet<>();
        this.hatedItems = hatedItemNames != null ? hatedItemNames.stream()
                                                     .map(this::getItemByName)
                                                     .filter(Objects::nonNull)
                                                     .collect(Collectors.toSet()) : new HashSet<>();

        this.relationshipStatus = RelationshipStatus.SINGLE;
    }

    /**
     * Retrieves an Item by name from various item registries.
     * This method checks different item types and their registries.
     * It handles prefixes (CROP_, SEED_, ITEM_) or direct name lookup.
     */
    private Item getItemByName(String itemName) {
        Item item = null;

        // Try prefixed names first
        if (itemName.startsWith("CROP_")) {
            String cropName = itemName.substring(5);
            item = Crop.getHarvestedCropByName(cropName);
        } else if (itemName.startsWith("SEED_")) {
            String seedName = itemName.substring(5) + " Seeds"; 
            item = Seed.getSeedByName(seedName);
        } else if (itemName.startsWith("ITEM_")) {
            String actualItemName = itemName.substring(5);
            item = Misc.get(actualItemName);
            if (item == null) {
                item = FoodRegistry.getFoodByName(actualItemName); 
            }
        } else {
            // If no prefix, try to find item by exact name across all known registries
            item = FoodRegistry.getFoodByName(itemName); // (Try food first)
            if (item == null) {
                item = Crop.getHarvestedCropByName(itemName); // (Then harvested crops)
            }
            if (item == null) {
                item = Seed.getSeedByName(itemName); // (Then seeds)
            }
            if (item == null) {
                item = Misc.get(itemName); // (Then misc items)
            }
            if (item == null) {
                item = FishRegistry.getFishByName(itemName); // (Finally, fish)
            }
        }

        if (item == null) {
            System.err.println("Warning: Item '" + itemName + "' not found in any registry.");
        }
        return item;
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
        if (giftedItem == null) {
            return "neutral";
        }
        // Direct comparison with Item objects in the sets
        if (lovedItems.contains(giftedItem)) return "loved";
        if (likedItems.contains(giftedItem)) return "liked";
        if (hatedItems.contains(giftedItem)) return "hated";
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