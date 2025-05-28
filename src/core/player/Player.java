package core.player;

import core.world.Tile;
import item.Equipment;
import item.EquipmentManager;
import npc.NPC;
import java.util.HashMap;
import core.world.ShippingBin;
import core.world.FarmMap;

public class Player {
    private static final int MAX_ENERGY = 100;
    private String name;
    private String gender;
    private Gold gold = new Gold(0);
    private int x, y;
    private int energy = MAX_ENERGY;
    private Inventory inventory;
    private EquipmentManager equipmentManager;
    private PlayerStats playerStats;
    private NPC partner = null;
    private HashMap<NPC, RelationshipStatus> relationships = new HashMap<>();
    private ShippingBin shippingBin;

    public Player(String name, String gender) {
        this.name = name;
        this.gender = gender;
        this.x = 0;
        this.y = 0;
        this.playerStats = new PlayerStats();
        this.equipmentManager = new EquipmentManager();
        this.inventory = new Inventory(this.playerStats, this.equipmentManager);
        this.shippingBin = null;
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

    public String getLocation() {
        return " ";
    }

    public Tile getCurrentTile() {
        return null;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        if (energy < 0) {
            this.energy = 0;
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

    public void setPartner(NPC partner) {
        this.partner = partner;
    }

    public RelationshipStatus getRelationshipStatus(NPC npc) {
        return relationships.getOrDefault(npc, RelationshipStatus.SINGLE);
    }

    public void setRelationshipStatus(NPC npc, RelationshipStatus status) {
        if (status == RelationshipStatus.FIANCE || status == RelationshipStatus.MARRIED) {
            relationships.put(npc, status);
        } else {
            relationships.remove(npc);
        }
    }

    public HashMap<NPC, RelationshipStatus> getAllRelationships() {
        return relationships;
    }
    
    public boolean isSingle() {
     return getRelationshipStatus(partner) == RelationshipStatus.SINGLE;
    }

    public void setShippingBin(ShippingBin shippingBin) {
        this.shippingBin = shippingBin;
    }

    public ShippingBin getShippingBin() {
        return shippingBin;
    }
}
