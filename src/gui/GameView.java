// TubesOOP/src/gui/GameView.java
package gui;

import javax.swing.*;
import java.awt.*;
import core.player.Player;
import core.world.FarmMap;
import core.world.ShippingBin; // Import ShippingBin
import time.GameCalendar;
import time.Time;
import system.Store;
import system.PriceList; // Import PriceList for loading prices
import npc.Emily; // Emily is the store owner

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
    public MainMenu mainMenuPanel; // Make public to allow GameView to set its reference
    public FarmMapPanel farmMapPanel; // Make public to allow GameView to request focus
    // Add other panels as you create them (e.g., PlayerInfoPanel, InventoryPanel)

    public GameView() {
        // --- 1. Basic JFrame Setup ---
        setTitle("Spakbor Hills");
        setSize(800, 600); // Set initial window size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close operation
        setLocationRelativeTo(null); // Center the window on the screen

        // --- 2. Initialize Game Model Components ---
        // These are your existing core game classes.
        // Order matters for dependencies.
        player = new Player("Dr. Asep Spakbor", "Male"); // Create the player
        shippingBin = new ShippingBin();
        player.setShippingBin(shippingBin); // Link player to shipping bin
        farmMap = new FarmMap(player); // FarmMap needs player to spawn
        gameCalendar = new GameCalendar(); // GameCalendar
        gameTime = new Time(gameCalendar, player); // Time needs GameCalendar and Player

        // Load prices from CSV (adjust path if necessary)
        try {
            PriceList.loadPrices("resources/price_list.csv");
            System.out.println("PriceList loaded successfully.");
        } catch (java.io.IOException e) {
            System.err.println("Failed to load price_list.csv: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to load game data (price_list.csv). Game may not function correctly.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        gameStore = new Store("Emily's Store", new Emily()); // Initialize the store with Emily as owner

        // Give player starting equipment (already handled by Inventory constructor, but ensure they are equipped for actions)
        player.equipItem("Hoe");
        player.equipItem("Watering Can");
        player.equipItem("Pickaxe");
        player.equipItem("Fishing Rod");
        System.out.println("Starting equipment equipped.");


        // --- 3. Initialize GUI View Components (Panels) ---
        mainPanel = new JPanel();
        mainPanel.setLayout(new CardLayout()); // Use CardLayout to switch between different game screens

        // Create instances of your custom panels
        mainMenuPanel = new MainMenu();
        mainMenuPanel.setGameView(this); // Pass reference of GameView to MainMenu

        farmMapPanel = new FarmMapPanel(farmMap, player);
        // If FarmMapController needs GameTime/Calendar, pass them in the constructor
        // farmMapPanel.addKeyListener(new FarmMapController(player, farmMap, farmMapPanel, gameTime, gameCalendar));


        // Add panels to the mainPanel with unique string identifiers
        mainPanel.add(mainMenuPanel, "MainMenu");
        mainPanel.add(farmMapPanel, "FarmMap");
        // Add other panels here as they are created

        // Add the mainPanel to the JFrame
        add(mainPanel);

        // --- 4. Initial Screen Display ---
        showScreen("MainMenu"); // Start the game by showing the main menu

        // --- 5. Start Game Time (Optional, depending on game flow) ---
        // You might want to start gameTime.runTime2() only after "New Game" is selected
        // For demonstration, let's start it immediately after game view is set up.
        // This runs in a separate thread.
        gameTime.runTime2();
    }

    /**
     * Method to switch between different panels/screens in the main window.
     * @param screenName The unique string identifier for the screen to show.
     */
    public void showScreen(String screenName) {
        CardLayout cl = (CardLayout)(mainPanel.getLayout());
        cl.show(mainPanel, screenName);
        // Request focus for the newly shown panel if it needs keyboard input
        if (screenName.equals("FarmMap")) {
            farmMapPanel.requestFocusInWindow();
        }
        // You might need to call revalidate() and repaint() if components aren't updating visually
        revalidate();
        repaint();
    }

    // --- Main Method to Run the Application ---
    public static void main(String[] args) {
        // Ensure that GUI creation and updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            GameView game = new GameView();
            game.setVisible(true); // Make the JFrame visible
        });
    }
}