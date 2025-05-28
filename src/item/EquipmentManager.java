package item;

import java.util.HashMap;
import java.util.Map;

public class EquipmentManager {
    private Map<String, Equipment> ownedEquipment;
    private Equipment equippedItem;
    
    public EquipmentManager() {
        this.ownedEquipment = new HashMap<>();
        this.equippedItem = null;
    }
    
    public void addEquipment(Equipment equipment) {
        ownedEquipment.put(equipment.getName(), equipment);
    }
    
    public boolean removeEquipment(String equipmentName) {
        Equipment equipment = ownedEquipment.get(equipmentName);
        if (equipment != null) {
            if (equipment.isEquipped()) {
                unequipItem(equipmentName);
            }
            ownedEquipment.remove(equipmentName);
            return true;
        }
        return false;
    }
    
    public boolean hasEquipment(String equipmentName) {
        return ownedEquipment.containsKey(equipmentName);
    }
    
    public boolean equipItem(String equipmentName) {
        Equipment equipment = ownedEquipment.get(equipmentName);
        if (equipment != null) {
            if (equipment.isEquipped()) {
                System.out.println(equipmentName + " is already equipped.");
                return false;
            }

            if (equippedItem != null) {
                equippedItem.setEquipped(false);
            }

            // Equip new item
            equipment.setEquipped(true);
            equippedItem = equipment;
            return true;
        }
        return false;
    }
    
    public boolean unequipItem(String equipmentName) {
        if (equippedItem != null && equippedItem.getName().equals(equipmentName)) {
            equippedItem.setEquipped(false);
            equippedItem = null;
            return true;
        }
        return false;
    }
    
    public boolean isEquipped(String equipmentName) {
        return equippedItem != null && equippedItem.getName().equals(equipmentName);
    }
    
    public Equipment getEquippedItem() {
        return equippedItem;
    }
    
    public Map<String, Equipment> getOwnedEquipment() {
        return new HashMap<>(ownedEquipment);
    }

    public void showEquipmentStatus() {
        System.out.println("Equipment:");
        for (Equipment eq : ownedEquipment.values()) {
            String status = eq.isEquipped() ? " [EQUIPPED]" : " [STORED]";
            System.out.println("- " + eq.getName() + status);
        }
    }
}