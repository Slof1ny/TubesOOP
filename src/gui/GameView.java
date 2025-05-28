// TubesOOP/src/gui/GameView.java
package gui;

import javax.swing.*;
import java.awt.*;
import core.player.Player;
import core.world.FarmMap;
import core.world.ShippingBin;
import time.GameCalendar;
import time.Time;
import system.Store;
import system.PriceList;
import npc.Emily;
import core.player.Inventory; // Make sure this import is here

public class GameView extends JFrame {

    // --- Model Components ---
    private Player player;
    private FarmMap farmMap;
    private GameCalendar gameCalendar;
    private Time gameTime;
    private Store gameStore;
    private ShippingBin shippingBin;

    // --- View Components (Panels) ---
    private JPanel mainPanel; // Uses CardLayout to switch between different screens
    public MainMenu mainMenuPanel;
    public FarmMapPanel farmMapPanel;
    public PlayerInfoPanel playerInfoPanel; // Declare PlayerInfoPanel

    public GameView() {
        // --- 1. Basic JFrame Setup ---
        setTitle("Spakbor Hills");
        setSize(1000, 700); // Increased size to accommodate info panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- 2. Initialize Game Model Components ---
        player = new Player("Dr. Asep Spakbor", "Male");
        shippingBin = new ShippingBin();
        player.setShippingBin(shippingBin);
        farmMap = new FarmMap(player);
        gameCalendar = new GameCalendar();
        gameTime = new Time(gameCalendar, player);

        try {
            PriceList.loadPrices("resources/price_list.csv");
            System.out.println("PriceList loaded successfully.");
        } catch (java.io.IOException e) {
            System.err.println("Failed to load price_list.csv: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load game data (price_list.csv). Game may not function correctly.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        gameStore = new Store("Emily's Store", new Emily());

        // Ensure starting equipment is added to inventory, if not handled by Player constructor
        // Your Player constructor already calls giveStartingEquipment().
        // For testing purposes, let's ensure some items are present for inventory display.
        player.getInventory().addItem(item.Seed.getSeedByName("Wheat Seeds"), 5);
        player.getInventory().addItem(new item.Food("Fish n' Chips", 150, 135, 50), 2);
        player.getInventory().addItem(new item.Misc("Coal", 30, 20), 10);

        // Equip starting tools (done in Player constructor, but explicitly equipping here too for clarity)
        player.equipItem("Hoe");
        player.equipItem("Watering Can");
        player.equipItem("Pickaxe");
        player.equipItem("Fishing Rod");
        System.out.println("Starting equipment equipped.");


        // --- 3. Initialize GUI View Components (Panels) ---
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout());

        mainMenuPanel = new MainMenu();
        mainMenuPanel.setGameView(this);

        playerInfoPanel = new PlayerInfoPanel(player); // PlayerInfoPanel must be initialized before FarmMapPanel now

        // Instantiate FarmMapPanel with the new required arguments
        farmMapPanel = new FarmMapPanel(farmMap, player, gameTime, gameCalendar, playerInfoPanel);

        // The KeyListener is added within FarmMapPanel's constructor now, so you can remove this line:
        // farmMapPanel.addKeyListener(new FarmMapController(player, farmMap, farmMapPanel, gameTime, gameCalendar, playerInfoPanel));


        // Create a panel for the game screen which includes map and info panel
        JPanel gameScreenPanel = new JPanel(new BorderLayout());
        gameScreenPanel.add(farmMapPanel, BorderLayout.CENTER);
        gameScreenPanel.add(playerInfoPanel, BorderLayout.EAST);
        gameScreenPanel.setName("GameScreen");

        // Add panels to the mainPanel
        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(gameScreenPanel, "GameScreen");


        // Add the mainPanel to the JFrame
        add(mainPanel);

        // --- 4. Initial Screen Display ---
        showScreen("MainMenu");

        // --- 5. Start Game Time ---
        // This will now update the model, and we need to link it to refresh the GUI.
        // We'll need a way for Time to notify panels when a minute or day passes.
        // For now, it runs, but GUI won't auto-update from it without more plumbing.
        gameTime.runTime2(); // This is already running in a background thread
    }

    public void showScreen(String screenName) {
        CardLayout cl = (CardLayout)(mainPanel.getLayout());
        cl.show(mainPanel, screenName);

        // Request focus for the newly shown panel if it needs keyboard input
        if (screenName.equals("GameScreen")) { // Now we show 'GameScreen' which contains FarmMapPanel
            farmMapPanel.requestFocusInWindow();
        }
        revalidate();
        repaint();
    }

    // --- Main Method to Run the Application ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView game = new GameView();
            game.setVisible(true);
        });
    }
}