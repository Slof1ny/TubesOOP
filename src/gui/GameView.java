package gui;

import javax.swing.*;
import java.awt.*;
import system.GameManager;
import npc.NPC;

public class GameView extends JFrame {

    private GameManager gameManager;

    private JPanel centerCardPanel; // Panel that uses CardLayout
    public MainMenu mainMenuPanel;
    public FarmMapPanel farmMapPanel;
    public TopInfoBarPanel topInfoBarPanel; // The single, persistent info panel
    public StorePanel storePanel;
    public CityMapPanel cityMapPanel;
    public PlayerCreationPanel playerCreationPanel;
    public ShippingBinPanel shippingBinPanel;
    public NPCInteractionPanel npcInteractionPanel;
    public InventoryScreenPanel inventoryScreenPanel;
    public HouseMapPanel houseMapPanel;

    public GameView() {
        setTitle("Spakbor Hills");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gameManager = new GameManager();
        gameManager.setGameView(this);

        // 1. Create the single PlayerInfoPanel
        topInfoBarPanel = new TopInfoBarPanel(gameManager); // << INITIALIZE THIS
        gameManager.setTopInfoBarPanel(topInfoBarPanel);

        // 2. Create other panels (they no longer take playerInfoPanel directly if it's managed by GameView layout)
        mainMenuPanel = new MainMenu();
        mainMenuPanel.setGameView(this); // mainMenuPanel needs a reference to GameView to switch screens

        playerCreationPanel = new PlayerCreationPanel(this, gameManager);
        // Pass all necessary dependencies to FarmMapPanel and CityMapPanel
        // Ensure FarmMapPanel's controller gets the GameView reference to access GameManager
        farmMapPanel = new FarmMapPanel(gameManager.getFarmMap(), gameManager.getPlayer(), gameManager.getGameTime(), gameManager.getGameCalendar(), null, this);
        cityMapPanel = new CityMapPanel(gameManager, this); // CityMapPanel gets GameManager and GameView
        storePanel = new StorePanel(this, gameManager.getPlayer(), gameManager.getGameStore(), null);
        shippingBinPanel = new ShippingBinPanel(this, gameManager);
        npcInteractionPanel = new NPCInteractionPanel(this, gameManager);
        inventoryScreenPanel = new InventoryScreenPanel(this, gameManager);
        houseMapPanel = new HouseMapPanel(gameManager, this);

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
        centerCardPanel.add(npcInteractionPanel, "NPCInteractionScreen");
        centerCardPanel.add(inventoryScreenPanel, "InventoryScreen"); 
        centerCardPanel.add(houseMapPanel, "HouseScreen"); 


        // 5. Set GameView's main layout and add components
        setLayout(new BorderLayout()); // Main layout for GameView JFrame
        add(centerCardPanel, BorderLayout.CENTER);
        add(topInfoBarPanel, BorderLayout.NORTH);
        showScreen("MainMenu");
    }

    // Getter for GameManager so controllers can access it via GameView reference
    public GameManager getGameManager() {
        return this.gameManager;
    }

    public void showNPCInteractionScreen(NPC npc) {
        if (npc != null && npcInteractionPanel != null) {
            npcInteractionPanel.setupForNPC(npc);
            showScreen("NPCInteractionScreen"); // Use your existing showScreen logic
        } else {
            System.err.println("Error: NPC or NPCInteractionPanel is null. Cannot show interaction screen.");
            // Optionally, show an error message to the user or default to city screen
            showScreen("CityScreen");
        }
    }

    public void showScreen(String screenName) {
        CardLayout cl = (CardLayout)(centerCardPanel.getLayout()); // Get layout from centerCardPanel
        cl.show(centerCardPanel, screenName); // Show screen in centerCardPanel

        boolean showTopBar = !screenName.equals("MainMenu") && !screenName.equals("PlayerCreationScreen");
        topInfoBarPanel.setVisible(showTopBar);
        if (showTopBar && topInfoBarPanel != null) {
            topInfoBarPanel.refreshInfo(); // Refresh whenever a relevant screen is shown
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
        } else if (screenName.equals("NPCInteractionScreen") && npcInteractionPanel.isShowing()) {
            npcInteractionPanel.requestFocusInWindow();
        } else if (screenName.equals("InventoryScreen") && inventoryScreenPanel.isShowing()) {
            inventoryScreenPanel.refreshPanelData();
            inventoryScreenPanel.requestFocusInWindow();
        }  else if (screenName.equals("HouseScreen") && houseMapPanel.isShowing()) { // << ADD CASE FOR HOUSE SCREEN
            houseMapPanel.refreshMap();
            houseMapPanel.requestFocusInWindow();
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