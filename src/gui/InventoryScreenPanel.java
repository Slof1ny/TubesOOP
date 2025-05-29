package gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import system.GameManager;
import item.Item;
import item.Equipment;
import item.Seed; // For categorizing
import item.Food;   // For categorizing and using
import item.EdibleItem; // For using
import core.player.Player;

public class InventoryScreenPanel extends JPanel {
    private GameView gameView;
    private GameManager gameManager;
    private Player player;

    private JList<String> generalItemsList;
    private DefaultListModel<String> generalItemsListModel;
    private JList<String> equipmentList;
    private DefaultListModel<String> equipmentListModel;

    private JButton useItemButton;
    private JButton equipButton;
    private JButton unequipButton;
    private JButton backButton;

    // To store the actual Item objects corresponding to JList entries
    private java.util.List<Item> actualGeneralItems;
    private java.util.List<Equipment> actualEquipmentItems;


    public InventoryScreenPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;
        this.player = gameManager.getPlayer();
        this.actualGeneralItems = new ArrayList<>();
        this.actualEquipmentItems = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(210, 220, 230)); // Light bluish-gray

        initComponents();
        addEventListeners();
        setFocusable(true);
    }

    private void initComponents() {
        JLabel titleLabel = new JLabel("Inventory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // Two columns: Items | Equipment

        // General Items Panel
        JPanel itemsPanel = new JPanel(new BorderLayout(5,5));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Items (Seeds, Food, Misc)"));
        generalItemsListModel = new DefaultListModel<>();
        generalItemsList = new JList<>(generalItemsListModel);
        generalItemsList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        generalItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsPanel.add(new JScrollPane(generalItemsList), BorderLayout.CENTER);
        useItemButton = new JButton("Use Selected Item");
        useItemButton.setFont(new Font("Arial", Font.PLAIN, 12));
        itemsPanel.add(useItemButton, BorderLayout.SOUTH);
        contentPanel.add(itemsPanel);

        // Equipment Panel
        JPanel equipmentDisplayPanel = new JPanel(new BorderLayout(5,5));
        equipmentDisplayPanel.setBorder(BorderFactory.createTitledBorder("Equipment"));
        equipmentListModel = new DefaultListModel<>();
        equipmentList = new JList<>(equipmentListModel);
        equipmentList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        equipmentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        equipmentDisplayPanel.add(new JScrollPane(equipmentList), BorderLayout.CENTER);

        JPanel equipButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        equipButton = new JButton("Equip Selected");
        equipButton.setFont(new Font("Arial", Font.PLAIN, 12));
        unequipButton = new JButton("Unequip Current");
        unequipButton.setFont(new Font("Arial", Font.PLAIN, 12));
        equipButtonsPanel.add(equipButton);
        equipButtonsPanel.add(unequipButton);
        equipmentDisplayPanel.add(equipButtonsPanel, BorderLayout.SOUTH);
        contentPanel.add(equipmentDisplayPanel);

        add(contentPanel, BorderLayout.CENTER);

        // Bottom Back Button
        backButton = new JButton("Back to Game");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshPanelData() {
        if (player == null) return;

        // Populate General Items
        generalItemsListModel.clear();
        actualGeneralItems.clear();
        Map<Item, Integer> ownedItems = player.getInventory().getAllItems(); // This gets non-equipment
        if (ownedItems.isEmpty()) {
            generalItemsListModel.addElement("No general items.");
        } else {
            for (Map.Entry<Item, Integer> entry : ownedItems.entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();
                generalItemsListModel.addElement(item.getName() + " x" + quantity + " (" + item.getCategory() + ")");
                actualGeneralItems.add(item);
            }
        }

        // Populate Equipment
        equipmentListModel.clear();
        actualEquipmentItems.clear();
        Map<String, Equipment> ownedEquipment = player.getEquipmentManager().getOwnedEquipment();
        if (ownedEquipment.isEmpty()) {
            equipmentListModel.addElement("No equipment owned.");
        } else {
            for (Equipment eq : ownedEquipment.values()) {
                equipmentListModel.addElement(eq.getName() + (eq.isEquipped() ? " [EQUIPPED]" : " [STORED]"));
                actualEquipmentItems.add(eq);
            }
        }

        generalItemsList.clearSelection();
        equipmentList.clearSelection();
        updateButtonStates();

        if (gameManager.getTopInfoBarPanel() != null) {
            gameManager.getTopInfoBarPanel().refreshInfo();
        }
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private String getBaseNameFromDisplay(String displayName, boolean isEquipment) {
        if (displayName == null) return null;
        if (isEquipment) {
            if (displayName.endsWith(" [EQUIPPED]")) {
                return displayName.substring(0, displayName.length() - " [EQUIPPED]".length());
            }
            if (displayName.endsWith(" [STORED]")) {
                return displayName.substring(0, displayName.length() - " [STORED]".length());
            }
        } else {
            // For general items, format is "Item Name xQuantity (Category)"
            // We need to extract "Item Name"
            int xIndex = displayName.lastIndexOf(" x");
            if (xIndex != -1) {
                return displayName.substring(0, xIndex);
            }
        }
        return displayName; // Fallback
    }

    private void updateButtonStates() {
        // Use Item Button
        int generalItemIndex = generalItemsList.getSelectedIndex();
        if (generalItemIndex != -1 && generalItemIndex < actualGeneralItems.size()) {
            Item selectedGeneralItem = actualGeneralItems.get(generalItemIndex);
            useItemButton.setEnabled(selectedGeneralItem instanceof EdibleItem); // Enable if item is edible
        } else {
            useItemButton.setEnabled(false);
        }

        // Equip/Unequip Buttons
        int equipIndex = equipmentList.getSelectedIndex();
        Equipment currentlyEquipped = player.getEquipmentManager().getEquippedItem();

        if (equipIndex != -1 && equipIndex < actualEquipmentItems.size()) {
            Equipment selectedEquipment = actualEquipmentItems.get(equipIndex);
            equipButton.setEnabled(!selectedEquipment.isEquipped());
        } else {
            equipButton.setEnabled(false);
        }
        unequipButton.setEnabled(currentlyEquipped != null);
    }

    private void addEventListeners() {
        generalItemsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });
        equipmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) updateButtonStates();
        });

        useItemButton.addActionListener(e -> {
            int selectedIndex = generalItemsList.getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < actualGeneralItems.size()) {
                Item itemToUse = actualGeneralItems.get(selectedIndex);
                if (itemToUse instanceof EdibleItem) {
                    EdibleItem edible = (EdibleItem) itemToUse;
                    int energyBefore = player.getEnergy();
                    player.setEnergy(player.getEnergy() + edible.getEnergyRestored());
                    player.getInventory().removeItem(itemToUse, 1); // Assumes removeItem works correctly
                    
                    JOptionPane.showMessageDialog(this,
                        "Used " + itemToUse.getName() + ".\nEnergy restored: " + edible.getEnergyRestored() +
                        "\nEnergy: " + energyBefore + " -> " + player.getEnergy(),
                        "Item Used", JOptionPane.INFORMATION_MESSAGE);
                    refreshPanelData(); // Refresh inventory list and potentially player stats display
                } else {
                     JOptionPane.showMessageDialog(this, itemToUse.getName() + " is not usable.", "Cannot Use", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        equipButton.addActionListener(e -> {
            int selectedIndex = equipmentList.getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < actualEquipmentItems.size()) {
                Equipment equipmentToEquip = actualEquipmentItems.get(selectedIndex);
                player.getEquipmentManager().equipItem(equipmentToEquip.getName());
                refreshPanelData();
            }
        });

        unequipButton.addActionListener(e -> {
            Equipment currentlyEquipped = player.getEquipmentManager().getEquippedItem();
            if (currentlyEquipped != null) {
                player.getEquipmentManager().unequipItem(currentlyEquipped.getName());
                refreshPanelData();
            }
        });

        backButton.addActionListener(e -> performLeaveAction());

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE || evt.getKeyCode() == KeyEvent.VK_I) {
                    performLeaveAction(); // ESC or 'I' again closes inventory
                }
            }
        });
    }

    private void performLeaveAction() {
        // Go back to the map screen the player was on
        String previousScreen = "GameScreen"; // Default
        if (gameManager.getCurrentMap().getName().equals(gameManager.getCityMap().getName())) {
            previousScreen = "CityScreen";
        }
        gameView.showScreen(previousScreen);
    }
}