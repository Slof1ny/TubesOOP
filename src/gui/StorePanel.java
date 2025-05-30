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
import system.Store;
import system.GameManager; // Ensure GameManager is imported

public class StorePanel extends JPanel {

    private GameView gameView;
    private Player player; // Player can be fetched from GameManager
    private Store store;
    // private PlayerInfoPanel playerInfoPanel; // REMOVE - No longer passed directly
    private GameManager gameManager;

    private JPanel itemsPanel;
    private JScrollPane scrollPane;

    // MODIFIED CONSTRUCTOR
    public StorePanel(GameView gameView, Store store, GameManager gameManager) {
        this.gameView = gameView;
        this.store = store;
        this.gameManager = gameManager;
        this.player = gameManager.getPlayer(); // Get player from GameManager

        setLayout(new BorderLayout());
        setBackground(new Color(230, 240, 255));

        JLabel titleLabel = new JLabel("Welcome to " + store.getName() + "!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        itemsPanel.setBackground(new Color(255, 255, 240));

        scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // refreshStoreDisplay(); // Called by onShow

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    if (gameManager.getCurrentMap().getName().equals(gameManager.getCityMap().getName())) {
                        gameView.showScreen("CityScreen");
                    } else {
                        gameView.returnToPreviousScreen();
                    }
                }
            }
        });
    }

    public void refreshStoreDisplay() {
        itemsPanel.removeAll();

        Map<String, Item> itemsForSale = store.getItemsForSale();

        if (itemsForSale.isEmpty()) {
            itemsPanel.add(new JLabel("No items currently available for sale."));
        } else {
            for (Map.Entry<String, Item> entry : itemsForSale.entrySet()) {
                final Item item = entry.getValue();
                final String itemName = item.getName();

                if (item.getBuyPrice() > 0) {
                    JPanel itemEntryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
                    itemEntryPanel.setBackground(new Color(255, 255, 240));
                    itemEntryPanel.setBorder(BorderFactory.createEtchedBorder());

                    JLabel itemLabel = new JLabel(String.format("%-25s (%s) - %dg", item.getName(), item.getCategory(), item.getBuyPrice()));
                    itemLabel.setFont(new Font("Monospaced", Font.PLAIN, 14));

                    JTextField quantityField = new JTextField("1", 3);
                    JButton buyButton = new JButton("Buy");

                    buyButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                int quantity = Integer.parseInt(quantityField.getText());
                                if (quantity <= 0) {
                                    JOptionPane.showMessageDialog(StorePanel.this, "Please enter a positive quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                                    StorePanel.this.requestFocusInWindow();
                                    return;
                                }

                                // Attempt the purchase
                                boolean purchaseSuccessful = store.handlePurchase(player, itemName, quantity); //

                                if (purchaseSuccessful) {
                                    int totalCost = item.getBuyPrice() * quantity;
                                    gameManager.getPlayer().getStats().addGoldSpent(totalCost, gameManager.getGameCalendar().getCurrentSeason());
                                    
                                    if (gameManager.getTopInfoBarPanel() != null) {
                                        gameManager.getTopInfoBarPanel().refreshInfo();
                                    }
                                    // No direct playerInfoPanel refresh here, TopInfoBarPanel should cover main stats

                                    gameManager.checkMilestonesAndShowStatistics();

                                    JOptionPane.showMessageDialog(StorePanel.this, "Successfully bought " + quantity + " " + itemName + ".", "Purchase Complete", JOptionPane.INFORMATION_MESSAGE);
                                    // Refresh the store display to reflect any changes in purchasable quantity (though not implemented here)
                                    // or if an item becomes unavailable after purchase (also not a current feature).
                                    // For now, primarily for UI consistency.
                                    refreshStoreDisplay(); 
                                } else {
                                    JOptionPane.showMessageDialog(StorePanel.this, "Could not complete the purchase for " + itemName + ". See console for details (e.g., insufficient funds).", "Purchase Failed", JOptionPane.WARNING_MESSAGE);
                                }

                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(StorePanel.this, "Please enter a valid number for quantity.", "Input Error", JOptionPane.ERROR_MESSAGE);
                            } finally {
                                StorePanel.this.requestFocusInWindow();
                            }
                        }
                    });

                    itemEntryPanel.add(itemLabel);
                    itemEntryPanel.add(new JLabel("Quantity:"));
                    itemEntryPanel.add(quantityField);
                    itemEntryPanel.add(buyButton);
                    itemsPanel.add(itemEntryPanel);
                }
            }
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    public void onShow() {
        refreshStoreDisplay();
        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }
}