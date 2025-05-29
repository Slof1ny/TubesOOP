package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import core.player.Player;
import item.Item;
import item.Equipment; // To exclude equipment from being shippable if desired
import system.GameManager;

public class ShippingBinPanel extends JPanel {
    private GameView gameView;
    private GameManager gameManager;
    private Player player;

    private JPanel itemsToShipPanel;
    private JScrollPane scrollPane;
    private JLabel currentBinContentsLabel; // To show what's already in the bin

    public ShippingBinPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;
        this.player = gameManager.getPlayer();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(210, 180, 140)); // Tan background

        JLabel titleLabel = new JLabel("Shipping Bin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        itemsToShipPanel = new JPanel();
        itemsToShipPanel.setLayout(new BoxLayout(itemsToShipPanel, BoxLayout.Y_AXIS));
        itemsToShipPanel.setBackground(new Color(245, 222, 179)); // Wheat color

        scrollPane = new JScrollPane(itemsToShipPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for current bin contents and close instruction
        JPanel bottomPanel = new JPanel(new BorderLayout());
        currentBinContentsLabel = new JLabel("Bin: 0g (Press ESC to Close)");
        currentBinContentsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        currentBinContentsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        bottomPanel.add(currentBinContentsLabel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);


        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    gameView.showScreen("GameScreen"); // Go back to Farm Map
                }
            }
        });
    }

    public void refreshShippingBinDisplay() {
        itemsToShipPanel.removeAll();
        if (player == null || player.getInventory() == null) {
            itemsToShipPanel.add(new JLabel("Error: Player or inventory not available."));
            return;
        }

        Map<Item, Integer> playerItems = player.getInventory().getAllItems();

        if (playerItems.isEmpty()) {
            itemsToShipPanel.add(new JLabel("Your inventory is empty."));
        } else {
            for (Map.Entry<Item, Integer> entry : playerItems.entrySet()) {
                Item item = entry.getKey();
                int currentQuantityInInventory = entry.getValue();

                // Optionally, exclude certain item categories like "Equipment"
                if (item instanceof Equipment || item.getSellPrice() <= 0) {
                    continue; // Skip non-sellable items or equipment
                }

                JPanel itemEntry = new JPanel(new FlowLayout(FlowLayout.LEFT));
                itemEntry.setBackground(itemsToShipPanel.getBackground());

                JLabel itemLabel = new JLabel(String.format("%s (x%d, Sell: %dg ea.)",
                        item.getName(), currentQuantityInInventory, item.getSellPrice()));
                itemLabel.setToolTipText(item.getCategory());

                JTextField quantityToShipField = new JTextField("0", 3);
                JButton shipButton = new JButton("Ship");

                shipButton.addActionListener(e -> {
                    try {
                        int quantityToShip = Integer.parseInt(quantityToShipField.getText());
                        if (quantityToShip <= 0) {
                            JOptionPane.showMessageDialog(this, "Please enter a positive quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (quantityToShip > currentQuantityInInventory) {
                            JOptionPane.showMessageDialog(this, "Not enough " + item.getName() + " in inventory.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Add to actual shipping bin
                        boolean success = player.getShippingBin().addItem(player, item, quantityToShip); //
                        if (success) {
                            // Refresh player info (gold doesn't change yet, but inventory does)
                            if(gameManager.getTopInfoBarPanel() != null) {
                                gameManager.getTopInfoBarPanel().refreshInfo();
                            }
                            // Refresh this panel to show updated inventory quantities
                            refreshShippingBinDisplay();
                            updateBinContentsLabel(); // Update the label showing bin total
                            JOptionPane.showMessageDialog(this, quantityToShip + " " + item.getName() + " added to bin.", "Item Shipped", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                             JOptionPane.showMessageDialog(this, "Could not add " + item.getName() + " to bin.", "Shipping Error", JOptionPane.ERROR_MESSAGE);
                        }
                        // Request focus back
                        this.requestFocusInWindow();

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Invalid quantity entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
                        this.requestFocusInWindow();
                    }
                });

                itemEntry.add(itemLabel);
                itemEntry.add(new JLabel("Qty to ship:"));
                itemEntry.add(quantityToShipField);
                itemEntry.add(shipButton);
                itemsToShipPanel.add(itemEntry);
            }
        }
        updateBinContentsLabel();
        itemsToShipPanel.revalidate();
        itemsToShipPanel.repaint();
    }
    
    private void updateBinContentsLabel() {
        if (player != null && player.getShippingBin() != null) {
            // This requires ShippingBin to have a method to calculate current value or show contents
            // For simplicity, let's just show number of unique items for now, or total value if easily accessible.
            // Assuming ShippingBin.getProspectiveSaleValue() exists:
            // int currentValue = player.getShippingBin().getProspectiveSaleValue();
            // currentBinContentsLabel.setText(String.format("Bin Value: %dg (Press ESC to Close)", currentValue));
            // If not, just a generic message or count of items
             currentBinContentsLabel.setText(String.format("Bin: %d unique item types (Press ESC to Close)", player.getShippingBin().getUniqueSlotsUsed())); //
        }
    }

    // Call this when the panel is shown
    public void onShow() {
        refreshShippingBinDisplay();
        this.requestFocusInWindow();
    }
}