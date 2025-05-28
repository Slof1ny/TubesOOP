// TubesOOP/src/gui/PlayerInfoPanel.java
package gui;

import javax.swing.*;
import java.awt.*;
import core.player.Player;
import core.player.Inventory; // To access inventory details

public class PlayerInfoPanel extends JPanel {
    private Player player;
    private JLabel nameLabel;
    private JLabel genderLabel;
    private JLabel energyLabel;
    private JLabel goldLabel;
    private JLabel locationLabel;
    private JTextArea inventoryArea; // For a simple text-based inventory display initially

    public PlayerInfoPanel(Player player) {
        this.player = player;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // Arrange components vertically
        setBackground(new Color(200, 220, 255)); // Light blue background for info panel
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        // --- Player Stats ---
        nameLabel = new JLabel("Name: " + player.getName());
        genderLabel = new JLabel("Gender: " + player.getGender());
        energyLabel = new JLabel("Energy: " + player.getEnergy() + "/100"); // Assuming MAX_ENERGY is 100
        goldLabel = new JLabel("Gold: " + player.getGold().getAmount() + "g");
        locationLabel = new JLabel("Location: " + player.getLocation() + " (" + player.getX() + ", " + player.getY() + ")");

        // Style labels
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

        add(Box.createVerticalStrut(15)); // Spacer

        // --- Inventory Section (Simple Text Display) ---
        add(new JLabel("--- Inventory ---"));
        inventoryArea = new JTextArea(8, 20); // 8 rows, 20 columns
        inventoryArea.setEditable(false); // Make it read-only
        inventoryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(inventoryArea); // Add scrollbar if content overflows
        add(scrollPane);

        refreshPlayerInfo(); // Initial refresh
    }

    /**
     * Updates the displayed player information and inventory.
     * Call this method whenever player stats or inventory change.
     */
    public void refreshPlayerInfo() {
        // All GUI updates must happen on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            nameLabel.setText("Name: " + player.getName());
            genderLabel.setText("Gender: " + player.getGender());
            energyLabel.setText("Energy: " + player.getEnergy() + "/100");
            goldLabel.setText("Gold: " + player.getGold().getAmount() + "g");
            locationLabel.setText("Location: " + player.getLocation() + " (" + player.getX() + ", " + player.getY() + ")");

            // Update inventory display
            StringBuilder inventoryText = new StringBuilder();
            if (player.getInventory() != null) {
                // Accessing internal map for simplicity in display; ideally, Inventory would have a method for this.
                // Assuming getTestInventory() or similar for direct map access if needed for testing,
                // otherwise iterate through a structured representation.
                // For now, let's adapt to showInventory's output format or use player.getInventory().getAllItems() if it exists.
                // Since `Inventory.showInventory()` prints to console, let's create a method to get its contents.
                inventoryText.append("Items:\n");
                for (var entry : player.getInventory().getAllItems().entrySet()) { // Assuming getAllItems() exists and returns Map<Item, Integer>
                    inventoryText.append("- ").append(entry.getKey().getName()).append(" x").append(entry.getValue()).append("\n");
                }
                inventoryText.append("Equipment:\n");
                for (var entry : player.getEquipmentManager().getOwnedEquipment().entrySet()) {
                    String status = entry.getValue().isEquipped() ? " [EQUIPPED]" : " [STORED]";
                    inventoryText.append("- ").append(entry.getKey()).append(status).append("\n");
                }
            } else {
                inventoryText.append("Inventory not initialized.");
            }
            inventoryArea.setText(inventoryText.toString());
        });
    }
}