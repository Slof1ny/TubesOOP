package gui;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import java.util.Vector;

import system.GameManager;
import item.Equipment;
import core.player.Player;

public class EquipmentPanel extends JPanel {
    private GameView gameView;
    private GameManager gameManager;
    private Player player;

    private JList<String> equipmentJList;
    private DefaultListModel<String> listModel;
    private JButton equipButton;
    private JButton unequipButton;
    private JButton backButton;
    private JLabel titleLabel;

    public EquipmentPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;
        this.player = gameManager.getPlayer();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(200, 210, 220)); // Light gray-blue

        initComponents();
        addEventListeners();

        setFocusable(true);
    }

    private void initComponents() {
        titleLabel = new JLabel("Manage Equipment", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        equipmentJList = new JList<>(listModel);
        equipmentJList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        equipmentJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(equipmentJList);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        equipButton = new JButton("Equip Selected");
        unequipButton = new JButton("Unequip Current");
        backButton = new JButton("Back to Game");

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        equipButton.setFont(buttonFont);
        unequipButton.setFont(buttonFont);
        backButton.setFont(buttonFont);

        buttonPanel.add(equipButton);
        buttonPanel.add(unequipButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshEquipmentList() {
        if (player == null) return;

        listModel.clear();
        Map<String, Equipment> ownedEquipment = player.getEquipmentManager().getOwnedEquipment();
        Equipment currentlyEquipped = player.getEquipmentManager().getEquippedItem();

        if (ownedEquipment.isEmpty()) {
            listModel.addElement("No equipment owned.");
            equipButton.setEnabled(false);
        } else {
            Vector<String> displayItems = new Vector<>();
            for (Map.Entry<String, Equipment> entry : ownedEquipment.entrySet()) {
                Equipment eq = entry.getValue();
                String displayText = eq.getName() + (eq.isEquipped() ? " [EQUIPPED]" : " [STORED]");
                displayItems.add(displayText);
            }
            // Sort so equipped item is often more visible or consistently placed if desired
            // For now, just adding them.
            for(String s : displayItems){
                listModel.addElement(s);
            }
        }

        // Enable/disable buttons based on selection and equipped status
        equipmentJList.clearSelection(); // Clear selection on refresh
        updateButtonStates();

        // Update PlayerInfoPanel as well
        if (gameManager.getPlayerInfoPanel() != null) {
            gameManager.getPlayerInfoPanel().refreshPlayerInfo();
        }
         // Request focus when panel is shown/refreshed
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }
    
    private String getBaseNameFromDisplay(String displayName) {
        if (displayName == null) return null;
        if (displayName.endsWith(" [EQUIPPED]")) {
            return displayName.substring(0, displayName.length() - " [EQUIPPED]".length());
        }
        if (displayName.endsWith(" [STORED]")) {
            return displayName.substring(0, displayName.length() - " [STORED]".length());
        }
        return displayName; // Should not happen if list is populated correctly
    }


    private void updateButtonStates() {
        String selectedValue = equipmentJList.getSelectedValue();
        Equipment currentlyEquipped = player.getEquipmentManager().getEquippedItem();

        if (selectedValue != null && !selectedValue.equals("No equipment owned.")) {
            String baseSelectedItemName = getBaseNameFromDisplay(selectedValue);
            Equipment selectedEquipment = player.getEquipmentManager().getOwnedEquipment().get(baseSelectedItemName);
            equipButton.setEnabled(selectedEquipment != null && !selectedEquipment.isEquipped());
        } else {
            equipButton.setEnabled(false);
        }
        unequipButton.setEnabled(currentlyEquipped != null);
    }


    private void addEventListeners() {
        equipmentJList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateButtonStates();
                }
            }
        });

        equipButton.addActionListener(e -> {
            String selectedValue = equipmentJList.getSelectedValue();
            if (selectedValue != null && !selectedValue.equals("No equipment owned.")) {
                String equipmentName = getBaseNameFromDisplay(selectedValue);
                if (equipmentName != null) {
                    boolean success = player.getEquipmentManager().equipItem(equipmentName);
                    System.out.println("Equip " + equipmentName + " success: " + success);
                    refreshEquipmentList(); // Refresh list and PlayerInfoPanel
                }
            }
        });

        unequipButton.addActionListener(e -> {
            Equipment currentlyEquipped = player.getEquipmentManager().getEquippedItem();
            if (currentlyEquipped != null) {
                boolean success = player.getEquipmentManager().unequipItem(currentlyEquipped.getName());
                System.out.println("Unequip " + currentlyEquipped.getName() + " success: " + success);
                refreshEquipmentList(); // Refresh list and PlayerInfoPanel
            }
        });

        backButton.addActionListener(e -> {
            // Determine which screen to go back to. Default to GameScreen (FarmMap)
            // or make it smarter if you track previous screen.
            if (gameManager.getCurrentMap().getName().equals(gameManager.getFarmMap().getName())) {
                gameView.showScreen("GameScreen");
            } else if (gameManager.getCurrentMap().getName().equals(gameManager.getCityMap().getName())) {
                gameView.showScreen("CityScreen");
            } else {
                gameView.showScreen("GameScreen"); // Fallback
            }
        });

        // Allow ESC key to go back
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    backButton.doClick(); // Simulate back button click
                }
            }
        });
    }
}