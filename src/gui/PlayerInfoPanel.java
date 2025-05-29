package gui;

import javax.swing.*;
import java.awt.*;
import core.player.Player;
import core.player.Inventory;
import system.GameManager;

public class PlayerInfoPanel extends JPanel {
    private GameManager gameManager;
    private JLabel nameLabel;
    private JLabel genderLabel;
    private JLabel energyLabel;
    private JLabel goldLabel;
    private JLabel locationLabel;
    private JTextArea inventoryArea;

    public PlayerInfoPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(200, 220, 255));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Set a preferred size for the panel to ensure it takes up space in BorderLayout.EAST
        setPreferredSize(new Dimension(250, 0)); // Set a fixed width, height will be determined by layout

        nameLabel = new JLabel("Name: ");
        genderLabel = new JLabel("Gender: ");
        energyLabel = new JLabel("Energy: ");
        goldLabel = new JLabel("Gold: ");
        locationLabel = new JLabel("Location: ");

        Font infoFont = new Font("Arial", Font.PLAIN, 14);
        nameLabel.setFont(infoFont);
        genderLabel.setFont(infoFont);
        energyLabel.setFont(infoFont);
        goldLabel.setFont(infoFont);
        locationLabel.setFont(infoFont);

        add(new JLabel("--- Player Stats ---"));
        add(nameLabel);
        add(genderLabel);
        add(energyLabel);
        add(goldLabel);
        add(locationLabel);

        add(Box.createVerticalStrut(15));

        add(new JLabel("--- Inventory ---"));
        inventoryArea = new JTextArea(8, 20);
        inventoryArea.setEditable(false);
        inventoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(inventoryArea);
        add(scrollPane);

        refreshPlayerInfo();
    }

    public void refreshPlayerInfo() {
        SwingUtilities.invokeLater(() -> {
            Player player = gameManager.getPlayer();
            nameLabel.setText("Name: " + player.getName());
            genderLabel.setText("Gender: " + player.getGender());
            energyLabel.setText("Energy: " + player.getEnergy() + "/100");
            goldLabel.setText("Gold: " + player.getGold().getAmount() + "g");
            // Ensure player location is refreshed here
            locationLabel.setText("Location: " + player.getLocation() + " (" + player.getX() + ", " + player.getY() + ")");

            StringBuilder inventoryText = new StringBuilder();
            if (player.getInventory() != null) {
                inventoryText.append("Items:\n");
                if (player.getInventory().getAllItems().isEmpty()) {
                    inventoryText.append("  (No regular items)\n");
                } else {
                    for (var entry : player.getInventory().getAllItems().entrySet()) {
                        inventoryText.append("- ").append(entry.getKey().getName()).append(" x").append(entry.getValue()).append("\n");
                    }
                }
                inventoryText.append("Equipment:\n");
                if (player.getEquipmentManager().getOwnedEquipment().isEmpty()) {
                    inventoryText.append("  (No equipment)\n");
                } else {
                    for (var entry : player.getEquipmentManager().getOwnedEquipment().entrySet()) {
                        String status = entry.getValue().isEquipped() ? " [EQUIPPED]" : " [STORED]";
                        inventoryText.append("- ").append(entry.getKey()).append(status).append("\n");
                    }
                }
            } else {
                inventoryText.append("Inventory not initialized.");
            }
            inventoryArea.setText(inventoryText.toString());
        });
    }
}