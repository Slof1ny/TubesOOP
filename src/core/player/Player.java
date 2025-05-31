package core.player;

import core.world.Tile;
import item.Equipment;
import item.EquipmentManager;
import npc.NPC;
import java.util.HashMap;
import core.world.ShippingBin;
import core.world.FarmMap; // Keep this for now, might be removed if FarmMap reference is not direct

public class Player {
    public static final int MAX_ENERGY = 100;
    public static final int MIN_ENERGY = -20;
    private String name;
    private String gender;
    private String farmName;
    private Gold gold = new Gold(0);
    private int x, y;
    private int energy = MAX_ENERGY;
    private Inventory inventory;
    private EquipmentManager equipmentManager;
    private PlayerStats playerStats;
    private NPC partner = null;
    private HashMap<NPC, RelationshipStatus> relationships = new HashMap<>();
    private ShippingBin shippingBin;
    private String currentLocation;
    private int daysSinceProposalWithPartner = -1;

    public Player(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.farmName = "My Farm";
        this.x = 0;
        this.y = 0;
        this.playerStats = new PlayerStats();
        this.equipmentManager = new EquipmentManager();
        this.inventory = new Inventory(this.playerStats, this.equipmentManager);
        this.shippingBin = null;
        this.currentLocation = "";
        giveStartingEquipment();
    }

    private void giveStartingEquipment() {
        equipmentManager.addEquipment(new Equipment("Hoe", 0, 0));
        equipmentManager.addEquipment(new Equipment("Watering Can", 0, 0));
        equipmentManager.addEquipment(new Equipment("Pickaxe", 0, 0));
        equipmentManager.addEquipment(new Equipment("Fishing Rod", 0, 0));
    }
    
    public String getName(){
        return name;
    }

    public String getGender(){
        return gender;
    }

    public Gold getGold(){
        return gold;
    }
    
    public static int getMaxEnergy() {
        return MAX_ENERGY;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getFarmName() {
        return farmName;
    }

    public void setFarmName(String farmName) {
        this.farmName = (farmName == null || farmName.trim().isEmpty()) ? "My Farm" : farmName.trim();
    }

    // This method now returns the actual map name (e.g., "Farm Map", "City Map")
    
    public String getLocation() {
        return currentLocation;
    }

    // ADD THIS METHOD
    public void setLocation(String location) {
        this.currentLocation = location;
    }

    public Tile getCurrentTile() {
        // This method will need to be refactored to work with the current active map
        // For now, it might return null or a dummy tile if not on FarmMap
        return null;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        if (energy < MIN_ENERGY) {
            this.energy = MIN_ENERGY;
        } else if (energy > MAX_ENERGY) {
            this.energy = MAX_ENERGY;
        } else {
            this.energy = energy;
        }
    }

    public Inventory getInventory() {
        return inventory;
    }

    public PlayerStats getStats() {
        return playerStats;
    }

    public EquipmentManager getEquipmentManager() {
        return equipmentManager;
    }
    
    public boolean hasEquipment(String equipmentName) {
        return equipmentManager.hasEquipment(equipmentName);
    }
    
    public boolean isEquipped(String equipmentName) {
        return equipmentManager.isEquipped(equipmentName);
    }
    
    public boolean equipItem(String equipmentName) {
        return equipmentManager.equipItem(equipmentName);
    }
    
    public boolean unequipItem(String equipmentName) {
        return equipmentManager.unequipItem(equipmentName);
    }

    public NPC getPartner() {
        return partner;
    }

    public int getDaysSinceProposalWithPartner() {
        return daysSinceProposalWithPartner;
    }

    public void setDaysSinceProposalWithPartner(int days) {
        this.daysSinceProposalWithPartner = days;
    }

    // MODIFIED setPartner
    public void setPartner(NPC partner) {
        this.partner = partner;
        if (this.partner == null) { // If unsetting partner
            this.daysSinceProposalWithPartner = -1; // Reset proposal counter
        }
        // If setting a new partner, the proposal counter should be managed by the proposal logic itself
        // or when relationship status changes.
    }

    public RelationshipStatus getRelationshipStatus(NPC npc) {
        return relationships.getOrDefault(npc, RelationshipStatus.SINGLE);
    }

    public void setRelationshipStatus(NPC npc, RelationshipStatus status) {
        if (npc == null) return;

        RelationshipStatus oldStatusWithNpc = relationships.getOrDefault(npc, RelationshipStatus.SINGLE);

        if (status == RelationshipStatus.FIANCE) {
            if (partner != null && partner != npc && getRelationshipStatus(partner) != RelationshipStatus.SINGLE) { // Check current partner's status with player
                System.out.println("Kamu sudah memiliki komitmen dengan " + partner.getName() + ". Tidak bisa bertunangan dengan " + npc.getName());
                return;
            }
            partner = npc; // Set this NPC as the current partner
            relationships.put(npc, RelationshipStatus.FIANCE);
            // daysSinceProposalWithPartner will be set to 0 by NPCActions.proposeToNPC when a proposal is made
            System.out.println(name + " is now FIANCE with " + npc.getName());
        } else if (status == RelationshipStatus.MARRIED) {
            if (partner != npc || oldStatusWithNpc != RelationshipStatus.FIANCE) {
                System.out.println("Tidak bisa menikah dengan " + npc.getName() + " kecuali dia adalah tunanganmu saat ini.");
                return;
            }
            relationships.put(npc, RelationshipStatus.MARRIED);
            // partner remains npc
            daysSinceProposalWithPartner = -1; // Reset counter upon marriage
            System.out.println(name + " is now MARRIED to " + npc.getName());
        } else if (status == RelationshipStatus.SINGLE) {
            relationships.put(npc, RelationshipStatus.SINGLE); // Set this NPC to single in player's map
            if (partner == npc) { // If this NPC *was* the current partner
                NPC tempPartner = partner; // for logging
                partner = null;
                daysSinceProposalWithPartner = -1;
                System.out.println(name + " is now SINGLE from " + (tempPartner != null ? tempPartner.getName() : "Unknown") + ". Partner and proposal counter reset.");
            } else {
                System.out.println(name + "'s relationship with " + npc.getName() + " is now SINGLE.");
            }
        }
    }

    public HashMap<NPC, RelationshipStatus> getAllRelationships() {
        return relationships;
    }
    
    public boolean isSingle() {
        return partner == null || getRelationshipStatus(partner) == RelationshipStatus.SINGLE;
    }

    public void setShippingBin(ShippingBin shippingBin) {
        this.shippingBin = shippingBin;
    }

    public ShippingBin getShippingBin() {
        return shippingBin;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setGender(String gender){
        this.gender = gender;
    }
}