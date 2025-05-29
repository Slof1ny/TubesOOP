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
import core.player.Inventory;
import system.GameManager;

public class GameView extends JFrame {

    private GameManager gameManager;

    private JPanel centerCardPanel; // Panel that uses CardLayout
    public MainMenu mainMenuPanel;
    public FarmMapPanel farmMapPanel;
    public PlayerInfoPanel playerInfoPanel; // The single, persistent info panel
    public StorePanel storePanel;
    public CityMapPanel cityMapPanel;
    public PlayerCreationPanel playerCreationPanel;
    public ShippingBinPanel shippingBinPanel;

    public GameView() {
        setTitle("Spakbor Hills");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gameManager = new GameManager();

        // 1. Create the single PlayerInfoPanel
        playerInfoPanel = new PlayerInfoPanel(gameManager);
        gameManager.setPlayerInfoPanel(playerInfoPanel); // GameManager can still have a reference to refresh it

        // 2. Create other panels (they no longer take playerInfoPanel directly if it's managed by GameView layout)
        mainMenuPanel = new MainMenu();
        mainMenuPanel.setGameView(this); // mainMenuPanel needs a reference to GameView to switch screens

        playerCreationPanel = new PlayerCreationPanel(this, gameManager);
        // Pass all necessary dependencies to FarmMapPanel and CityMapPanel
        // Ensure FarmMapPanel's controller gets the GameView reference to access GameManager
        farmMapPanel = new FarmMapPanel(gameManager.getFarmMap(), gameManager.getPlayer(), gameManager.getGameTime(), gameManager.getGameCalendar(), playerInfoPanel, this);
        cityMapPanel = new CityMapPanel(gameManager, this); // CityMapPanel gets GameManager and GameView
        storePanel = new StorePanel(this, gameManager.getPlayer(), gameManager.getGameStore(), playerInfoPanel);
        shippingBinPanel = new ShippingBinPanel(this, gameManager);

        // 3. Create screen-specific content panels (WITHOUT PlayerInfoPanel)
        JPanel gameScreenOnlyMapPanel = new JPanel(new BorderLayout());
        gameScreenOnlyMapPanel.add(farmMapPanel, BorderLayout.CENTER);
        // gameScreenOnlyMapPanel.setName("GameScreenContent"); // Optional: for clarity

        JPanel cityScreenOnlyMapPanel = new JPanel(new BorderLayout());
        cityScreenOnlyMapPanel.add(cityMapPanel, BorderLayout.CENTER);
        // cityScreenOnlyMapPanel.setName("CityScreenContent"); // Optional: for clarity

        // 4. Setup the panel that uses CardLayout
        centerCardPanel = new JPanel(new CardLayout());
        centerCardPanel.add(mainMenuPanel, "MainMenu");
        centerCardPanel.add(playerCreationPanel, "PlayerCreationScreen");
        centerCardPanel.add(gameScreenOnlyMapPanel, "GameScreen");
        centerCardPanel.add(storePanel, "StoreScreen");
        centerCardPanel.add(cityScreenOnlyMapPanel, "CityScreen");
        centerCardPanel.add(shippingBinPanel, "ShippingBinScreen");


        // 5. Set GameView's main layout and add components
        setLayout(new BorderLayout()); // Main layout for GameView JFrame
        add(centerCardPanel, BorderLayout.CENTER);
        add(playerInfoPanel, BorderLayout.EAST); // PlayerInfoPanel is always on the EAST

        showScreen("MainMenu");
    }

    // Getter for GameManager so controllers can access it via GameView reference
    public GameManager getGameManager() {
        return this.gameManager;
    }

    public void showScreen(String screenName) {
        CardLayout cl = (CardLayout)(centerCardPanel.getLayout()); // Get layout from centerCardPanel
        cl.show(centerCardPanel, screenName); // Show screen in centerCardPanel
        if (screenName.equals("MainMenu")) {
            playerInfoPanel.setVisible(false); // Hide PlayerInfoPanel on MainMenu
            mainMenuPanel.requestFocusInWindow();
        } else {
            playerInfoPanel.setVisible(true); // Show PlayerInfoPanel on other screens
            if (playerInfoPanel != null) {
                playerInfoPanel.refreshPlayerInfo();
            }
        // Request focus and refresh map for the active panel
        if (screenName.equals("PlayerCreationScreen")) {
            playerCreationPanel.requestFocusInWindow();
            // Player info panel might show default/old data here, but will refresh after creation
        } else if (screenName.equals("GameScreen")) {
            farmMapPanel.requestFocusInWindow();
            farmMapPanel.refreshMap();
        } else if (screenName.equals("CityScreen")) {
            cityMapPanel.requestFocusInWindow();
            cityMapPanel.refreshMap();
        } else if (screenName.equals("StoreScreen")) {
            storePanel.requestFocusInWindow();
            storePanel.refreshStoreDisplay();
        } else if (screenName.equals("ShippingBinScreen")){
            shippingBinPanel.onShow();
        }
    }
        // MainMenu doesn't usually need a specific refresh call here for its components

        revalidate(); // Revalidate the whole GameView
        repaint();    // Repaint the whole GameView
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView game = new GameView();
            game.setVisible(true);
        });
    }
}