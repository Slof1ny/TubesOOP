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

public class StorePanel extends JPanel {

    private GameView gameView;
    private Player player;
    private Store store;
    private PlayerInfoPanel playerInfoPanel;

    private JPanel itemsPanel;
    private JScrollPane scrollPane;

    public StorePanel(GameView gameView, Player player, Store store, PlayerInfoPanel playerInfoPanel) {
        this.gameView = gameView;
        this.player = player;
        this.store = store;
        this.playerInfoPanel = playerInfoPanel;

        setLayout(new BorderLayout());
        setBackground(new Color(230, 240, 255));

        JLabel titleLabel = new JLabel("Welcome to " + store.getName() + "!", SwingConstants.CENTER); //
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); //
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); //
        add(titleLabel, BorderLayout.NORTH); //

        itemsPanel = new JPanel(); //
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS)); //
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //
        itemsPanel.setBackground(new Color(255, 255, 240)); //

        scrollPane = new JScrollPane(itemsPanel); //
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); //
        add(scrollPane, BorderLayout.CENTER); //

        refreshStoreDisplay(); //

        setFocusable(true); //
        addKeyListener(new KeyAdapter() { //
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { //
                    gameView.showScreen("CityScreen"); //
                }
            }
        });
    }

    public void refreshStoreDisplay() {
        itemsPanel.removeAll(); //

        Map<String, Item> itemsForSale = store.getItemsForSale(); //

        if (itemsForSale.isEmpty()) { //
            itemsPanel.add(new JLabel("No items currently available for sale.")); //
        } else {
            for (Map.Entry<String, Item> entry : itemsForSale.entrySet()) { //
                Item item = entry.getValue(); //
                if (item.getBuyPrice() > 0) { //
                    JPanel itemEntryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5)); //
                    itemEntryPanel.setBackground(new Color(255, 255, 240)); //
                    itemEntryPanel.setBorder(BorderFactory.createEtchedBorder()); //

                    JLabel itemLabel = new JLabel(String.format("%-25s (%s) - %dg", item.getName(), item.getCategory(), item.getBuyPrice())); //
                    itemLabel.setFont(new Font("Monospaced", Font.PLAIN, 14)); //

                    JTextField quantityField = new JTextField("1", 3); //
                    JButton buyButton = new JButton("Buy"); //

                    buyButton.addActionListener(new ActionListener() { //
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            try {
                                int quantity = Integer.parseInt(quantityField.getText()); //
                                store.handlePurchase(player, item.getName(), quantity); //
                                playerInfoPanel.refreshPlayerInfo(); //
                                JOptionPane.showMessageDialog(StorePanel.this, "Transaction complete. Check your inventory!", "Purchase Status", JOptionPane.INFORMATION_MESSAGE); //
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(StorePanel.this, "Please enter a valid number for quantity.", "Input Error", JOptionPane.ERROR_MESSAGE); //
                            } finally {
                                // ADD THIS LINE to request focus back to the StorePanel
                                StorePanel.this.requestFocusInWindow();
                            }
                        }
                    });

                    itemEntryPanel.add(itemLabel); //
                    itemEntryPanel.add(new JLabel("Quantity:")); //
                    itemEntryPanel.add(quantityField); //
                    itemEntryPanel.add(buyButton); //
                    itemsPanel.add(itemEntryPanel); //
                }
            }
        }
        itemsPanel.revalidate(); //
        itemsPanel.repaint(); //
    }
}