// Path: TubesOOP/src/gui/GameView.java
package gui;

import javax.swing.*;
import java.awt.*;
import system.GameManager;
import npc.NPC;

public class GameView extends JFrame {

    private String previousScreenName = "MainMenu";
    private String currentScreenName = "MainMenu";
    private GameManager gameManager;

    private JPanel centerCardPanel;
    public MainMenu mainMenuPanel;
    public FarmMapPanel farmMapPanel;
    public TopInfoBarPanel topInfoBarPanel;
    public StorePanel storePanel;
    public CityMapPanel cityMapPanel;
    public PlayerCreationPanel playerCreationPanel;
    public ShippingBinPanel shippingBinPanel;
    public NPCInteractionPanel npcInteractionPanel;
    public InventoryScreenPanel inventoryScreenPanel;
    public HouseMapPanel houseMapPanel;
    public HelpScreenPanel helpScreenPanel;
    public CookingPanel cookingPanel;


    public GameView() {
        setTitle("Spakbor Hills");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        gameManager = new GameManager();
        gameManager.setGameView(this);

        topInfoBarPanel = new TopInfoBarPanel(gameManager);
        gameManager.setTopInfoBarPanel(topInfoBarPanel);

        mainMenuPanel = new MainMenu();
        mainMenuPanel.setGameView(this);

        playerCreationPanel = new PlayerCreationPanel(this, gameManager);
        farmMapPanel = new FarmMapPanel(gameManager.getFarmMap(), gameManager.getPlayer(), gameManager.getGameTime(), gameManager.getGameCalendar(), null, this);
        cityMapPanel = new CityMapPanel(gameManager, this);
        storePanel = new StorePanel(this, gameManager.getPlayer(), gameManager.getGameStore(), null);
        shippingBinPanel = new ShippingBinPanel(this, gameManager);
        npcInteractionPanel = new NPCInteractionPanel(this, gameManager);
        inventoryScreenPanel = new InventoryScreenPanel(this, gameManager);
        houseMapPanel = new HouseMapPanel(gameManager, this);
        helpScreenPanel = new HelpScreenPanel(this, gameManager);
        cookingPanel = new CookingPanel(this, gameManager);

        JPanel gameScreenOnlyMapPanel = new JPanel(new BorderLayout());
        gameScreenOnlyMapPanel.add(farmMapPanel, BorderLayout.CENTER);

        JPanel cityScreenOnlyMapPanel = new JPanel(new BorderLayout());
        cityScreenOnlyMapPanel.add(cityMapPanel, BorderLayout.CENTER);
        
        JPanel houseScreenOnlyMapPanel = new JPanel(new BorderLayout());
        houseScreenOnlyMapPanel.add(houseMapPanel, BorderLayout.CENTER);


        centerCardPanel = new JPanel(new CardLayout());
        centerCardPanel.add(mainMenuPanel, "MainMenu");
        centerCardPanel.add(playerCreationPanel, "PlayerCreationScreen");
        centerCardPanel.add(gameScreenOnlyMapPanel, "GameScreen");
        centerCardPanel.add(storePanel, "StoreScreen");
        centerCardPanel.add(cityScreenOnlyMapPanel, "CityScreen");
        centerCardPanel.add(shippingBinPanel, "ShippingBinScreen");
        centerCardPanel.add(npcInteractionPanel, "NPCInteractionScreen");
        centerCardPanel.add(inventoryScreenPanel, "InventoryScreen");
        centerCardPanel.add(houseScreenOnlyMapPanel, "HouseScreen");
        centerCardPanel.add(helpScreenPanel, "HelpScreen");
        centerCardPanel.add(cookingPanel, "CookingScreen");


        setLayout(new BorderLayout());
        add(centerCardPanel, BorderLayout.CENTER);
        add(topInfoBarPanel, BorderLayout.NORTH);
        showScreen("MainMenu");
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    public void showNPCInteractionScreen(NPC npc) {
        if (npc != null && npcInteractionPanel != null) {
            npcInteractionPanel.setupForNPC(npc);
            showScreen("NPCInteractionScreen");
        } else {
            System.err.println("Error: NPC or NPCInteractionPanel is null. Cannot show interaction screen.");
            showScreen("CityScreen");
        }
    }

    public void showScreen(String screenName) {
        if (screenName == null) return;

        if (this.currentScreenName != null && !this.currentScreenName.equals(screenName)) {
            this.previousScreenName = this.currentScreenName;
        }
        this.currentScreenName = screenName;

        System.out.println("GameView: Showing screen - " + screenName + ". Previous screen was: " + previousScreenName);

        CardLayout cl = (CardLayout) (centerCardPanel.getLayout());
        cl.show(centerCardPanel, screenName);

        boolean showTopBar = !screenName.equals("MainMenu") && !screenName.equals("PlayerCreationScreen");
        topInfoBarPanel.setVisible(showTopBar);
        if (showTopBar && topInfoBarPanel != null) {
            topInfoBarPanel.refreshInfo();
        }

        // Request focus for the specific panel that will receive key events.
        // Also, call refresh/onShow methods as appropriate.
        // Using SwingUtilities.invokeLater for focus requests is generally safer.
        switch (screenName) {
            case "MainMenu":
                SwingUtilities.invokeLater(mainMenuPanel::requestFocusInWindow);
                break;
            case "PlayerCreationScreen":
                SwingUtilities.invokeLater(playerCreationPanel::requestFocusInWindow);
                break;
            case "GameScreen":
                farmMapPanel.refreshMap();
                SwingUtilities.invokeLater(farmMapPanel::requestFocusInWindow);
                break;
            case "CityScreen":
                cityMapPanel.refreshMap();
                SwingUtilities.invokeLater(cityMapPanel::requestFocusInWindow);
                break;
            case "StoreScreen":
                storePanel.refreshStoreDisplay();
                SwingUtilities.invokeLater(storePanel::requestFocusInWindow);
                break;
            case "ShippingBinScreen":
                shippingBinPanel.onShow(); // onShow should handle its own refresh and focus
                break;
            case "NPCInteractionScreen":
                // npcInteractionPanel.setupForNPC() is called before showScreen.
                // If it has an onShow(), call it, otherwise ensure it requests focus.
                SwingUtilities.invokeLater(npcInteractionPanel::requestFocusInWindow);
                break;
            case "InventoryScreen":
                inventoryScreenPanel.refreshPanelData(); // refreshPanelData should handle focus
                break;
            case "HouseScreen":
                houseMapPanel.refreshMap();
                SwingUtilities.invokeLater(houseMapPanel::requestFocusInWindow);
                break;
            case "HelpScreen":
                helpScreenPanel.onShow(); // onShow should handle focus
                break;
            case "CookingScreen":
                cookingPanel.onShow(); // onShow should handle focus
                break;
            default:
                // Fallback for any other screen, try to focus the center panel's visible component
                // This part might be redundant if all cases are handled above.
                for (Component comp : centerCardPanel.getComponents()) {
                    if (comp.isVisible()) {
                        SwingUtilities.invokeLater(comp::requestFocusInWindow);
                        break;
                    }
                }
                break;
        }
    
        revalidate();
        repaint();
    }

    public void returnToPreviousScreen() {
        if (currentScreenName.equals("HelpScreen") && previousScreenName.equals("HelpScreen")) {
             if (gameManager.getPlayer().getLocation().equals(gameManager.getFarmMap().getName())) {
                showScreen("GameScreen");
            } else if (gameManager.getPlayer().getLocation().equals(gameManager.getCityMap().getName())) {
                showScreen("CityScreen");
            } else if (gameManager.getPlayer().getLocation().equals(gameManager.getHouseMap().getName())) {
                showScreen("HouseScreen");
            } else {
                showScreen("MainMenu");
            }
            return;
        }
        showScreen(previousScreenName);
    }

    public String getCurrentScreenName() {
        return this.currentScreenName;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameView game = new GameView();
            game.setVisible(true);
        });
    }
}