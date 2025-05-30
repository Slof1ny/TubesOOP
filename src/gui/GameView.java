// Path: TubesOOP/src/gui/GameView.java
package gui;

import javax.swing.*;
import java.awt.*;
import system.GameManager;
import npc.NPC;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Collections;
import java.util.List;

import core.world.GameMap;
import core.player.Player;

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
    public StatisticsPanel statisticsPanel;


    public GameView() {
        setTitle("Spakbor Hills");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Autopilot energy threshold (can be moved to config)
        this.ENERGY_AUTOPILOT_THRESHOLD = -20;

        gameManager = new GameManager();
        gameManager.setGameView(this);

        topInfoBarPanel = new TopInfoBarPanel(gameManager);
        gameManager.setTopInfoBarPanel(topInfoBarPanel);

        mainMenuPanel = new MainMenu();
        mainMenuPanel.setGameView(this);

        playerCreationPanel = new PlayerCreationPanel(this, gameManager);
        farmMapPanel = new FarmMapPanel(gameManager.getFarmMap(), gameManager.getPlayer(), gameManager.getGameTime(), gameManager.getGameCalendar(), null, this);
        cityMapPanel = new CityMapPanel(gameManager, this);
        storePanel = new StorePanel(this, gameManager.getGameStore(), gameManager);
        shippingBinPanel = new ShippingBinPanel(this, gameManager);
        npcInteractionPanel = new NPCInteractionPanel(this, gameManager);
        inventoryScreenPanel = new InventoryScreenPanel(this, gameManager);
        houseMapPanel = new HouseMapPanel(gameManager, this);
        helpScreenPanel = new HelpScreenPanel(this, gameManager);
        cookingPanel = new CookingPanel(this, gameManager);
        statisticsPanel = new StatisticsPanel(this, gameManager);

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
        centerCardPanel.add(statisticsPanel, "StatisticsScreen");


        setLayout(new BorderLayout());
        add(centerCardPanel, BorderLayout.CENTER);
        add(topInfoBarPanel, BorderLayout.NORTH);
        showScreen("MainMenu");
    }

    public GameManager getGameManager() {
        return this.gameManager;
    }

    // --- AUTOPILOT TO BED FEATURE ---
    private final int ENERGY_AUTOPILOT_THRESHOLD;
    private boolean autopilotActive = false;
    private boolean forceSleepMode = false;

    /**
     * Call this after any action or screen change to check if autopilot is needed.
     */
    public void checkAndAutopilotToBed() {
        if (autopilotActive) return;
        if (gameManager.getPlayer().getEnergy() <= ENERGY_AUTOPILOT_THRESHOLD) {
            autopilotActive = true;
            forceSleepMode = false;
            JOptionPane.showMessageDialog(this, "Energi habis! Anda akan otomatis pulang dan tidur.");
            autopilotToBed();
        }
    }

    /**
     * Called by GameManager.forcePlayerSleep() to start autopilot in force sleep mode.
     */
    public void startAutopilotForceSleep() {
        if (autopilotActive) return;
        autopilotActive = true;
        forceSleepMode = true;
        autopilotToBed();
    }

    /**
     * For GameManager to check if autopilot is running (to avoid double sleep).
     */
    public boolean isAutopilotActive() {
        return autopilotActive;
    }

    /**
     * Autopilot multi-map: animasi jalan ke exit map, transisi, lanjut ke bed.
     */
    public void autopilotToBed() {
        proceedAutopilotToBed();
    }

    // Recursive step for autopilot: jalan ke exit, transisi, lanjut, sampai bed
    private void proceedAutopilotToBed() {
        String playerLoc = gameManager.getPlayer().getLocation();
        Point playerPos = new Point(gameManager.getPlayer().getX(), gameManager.getPlayer().getY());
        Point houseEntry = core.world.HouseMap.ENTRY_LOCATION;
        Point bedPos = core.world.HouseMap.BED_LOCATION;

        if (playerLoc.equals(gameManager.getHouseMap().getName())) {
            // Sudah di house, path ke bed
            List<Point> pathToBed = findPathAStar(gameManager.getHouseMap(), playerPos, bedPos);
            animateMovement(pathToBed, gameManager.getHouseMap(), this::autopilotSleep);
            return;
        }

        if (playerLoc.equals(gameManager.getFarmMap().getName())) {
            // Di farm, jalan ke pintu rumah (exit ke house)
            Point exitToHouse = findNearestHouseExit(gameManager.getFarmMap());
            List<Point> pathToExit = findPathAStar(gameManager.getFarmMap(), playerPos, exitToHouse);
            animateMovement(pathToExit, gameManager.getFarmMap(), () -> {
                // Transisi ke house
                gameManager.transitionMap(gameManager.getHouseMap().getName());
                showScreen("HouseScreen");
                // Set posisi ke entry house
                gameManager.getPlayer().setPosition(houseEntry.x, houseEntry.y);
                // Lanjutkan ke bed
                proceedAutopilotToBed();
            });
            return;
        }

        if (playerLoc.equals(gameManager.getCityMap().getName())) {
            // Di city, animasi jalan ke city exit (bawah tengah), lalu teleport ke farm entry (atas tengah)
            int cityExitX = gameManager.getCityMap().getSize() / 2;
            int cityExitY = gameManager.getCityMap().getSize() - 1;
            Point exitToFarm = new Point(cityExitX, cityExitY);
            List<Point> pathToExit = findPathAStar(gameManager.getCityMap(), playerPos, exitToFarm);
            Runnable afterCityToFarm = () -> {
                // Transisi ke farm
                gameManager.transitionMap(gameManager.getFarmMap().getName());
                showScreen("GameScreen");
                // Set posisi ke entry farm dari city (atas tengah)
                int farmEntryX = gameManager.getFarmMap().getEntryFromCityX();
                int farmEntryY = gameManager.getFarmMap().getEntryFromCityY();
                gameManager.getPlayer().setPosition(farmEntryX, farmEntryY);
                refreshActiveMapPanel();
                // Setelah sampai di farm, animasikan jalan ke pintu house (exit ke house)
                Point farmPos = new Point(farmEntryX, farmEntryY);
                Point exitToHouse = findNearestHouseExit(gameManager.getFarmMap());
                List<Point> pathToExitFarm = findPathAStar(gameManager.getFarmMap(), farmPos, exitToHouse);
                animateMovement(pathToExitFarm, gameManager.getFarmMap(), () -> {
                    // Setelah animasi selesai, transisi ke house
                    gameManager.transitionMap(gameManager.getHouseMap().getName());
                    showScreen("HouseScreen");
                    gameManager.getPlayer().setPosition(houseEntry.x, houseEntry.y);
                    proceedAutopilotToBed();
                });
            };
            if (pathToExit == null || pathToExit.isEmpty()) {
                // Path tidak ditemukan, animasikan langkah lurus ke exit (paksa animasi, bukan teleport)
                java.util.List<Point> fallbackPath = new java.util.ArrayList<>();
                Point current = new Point(playerPos.x, playerPos.y);
                while (!current.equals(exitToFarm)) {
                    if (current.x < exitToFarm.x) current.x++;
                    else if (current.x > exitToFarm.x) current.x--;
                    else if (current.y < exitToFarm.y) current.y++;
                    else if (current.y > exitToFarm.y) current.y--;
                    fallbackPath.add(new Point(current.x, current.y));
                }
                animateMovement(fallbackPath, gameManager.getCityMap(), afterCityToFarm);
                return;
            }
            animateMovement(pathToExit, gameManager.getCityMap(), afterCityToFarm);
            return;
        }

        // Default: jika map tidak dikenali, pastikan player animasi jalan di farm sebelum ke house
        System.out.println("Autopilot fallback: playerLoc=" + playerLoc);
        if (!playerLoc.equals(gameManager.getFarmMap().getName())) {
            // Pindah ke farm, posisikan di entry dari city
            gameManager.transitionMap(gameManager.getFarmMap().getName());
            showScreen("GameScreen");
            int farmEntryX = gameManager.getFarmMap().getEntryFromCityX();
            int farmEntryY = gameManager.getFarmMap().getEntryFromCityY();
            gameManager.getPlayer().setPosition(farmEntryX, farmEntryY);
            refreshActiveMapPanel();
            // Setelah sampai di farm, animasikan jalan ke pintu house (exit ke house)
            Point farmPos = new Point(farmEntryX, farmEntryY);
            Point exitToHouse = findNearestHouseExit(gameManager.getFarmMap());
            List<Point> pathToExit = findPathAStar(gameManager.getFarmMap(), farmPos, exitToHouse);
            animateMovement(pathToExit, gameManager.getFarmMap(), () -> {
                // Setelah animasi selesai, transisi ke house
                gameManager.transitionMap(gameManager.getHouseMap().getName());
                showScreen("HouseScreen");
                gameManager.getPlayer().setPosition(houseEntry.x, houseEntry.y);
                proceedAutopilotToBed();
            });
            return;
        }
        // Jika sudah di farm (fallback), animasikan jalan ke pintu house
        Point farmPos = new Point(gameManager.getPlayer().getX(), gameManager.getPlayer().getY());
        Point exitToHouse = findNearestHouseExit(gameManager.getFarmMap());
        List<Point> pathToExit = findPathAStar(gameManager.getFarmMap(), farmPos, exitToHouse);
        animateMovement(pathToExit, gameManager.getFarmMap(), () -> {
            gameManager.transitionMap(gameManager.getHouseMap().getName());
            showScreen("HouseScreen");
            gameManager.getPlayer().setPosition(houseEntry.x, houseEntry.y);
            proceedAutopilotToBed();
        });
    }

    // Helper: exit point di farm ke house (biasanya di depan rumah)
    // (HAPUS DUPLIKAT, method ini sudah ada di bawah)

    // Helper: exit point di city ke farm (misal: tengah bawah)
    private Point findNearestFarmExit(GameMap map) {
        if (map.getName().equals(gameManager.getCityMap().getName())) {
            int sz = map.getSize();
            return new Point(sz / 2, sz - 1);
        }
        // Default fallback
        return new Point(0, 0);
    }

    /**
     * After reaching bed, trigger sleep.
    int farmEntryX = gameManager.getFarmMap().getCityExitSpawnX();
    int farmEntryY = gameManager.getFarmMap().getCityExitSpawnY();
    gameManager.getPlayer().setPosition(farmEntryX, farmEntryY);     */
    private void autopilotSleep() {
        // Optionally show animation or message
        if (forceSleepMode) {
            JOptionPane.showMessageDialog(this, "Anda pingsan di tempat tidur dan energi dipulihkan.");
        } else {
            JOptionPane.showMessageDialog(this, "Anda tidur dan energi dipulihkan.");
        }
        // Panggil sleep2() dari gameManager.getGameTime() agar sesuai dengan refactor
        gameManager.getGameTime().sleep2();
        autopilotActive = false;
        forceSleepMode = false;
        // Pastikan player tetap di house dan di posisi kasur setelah bangun
        gameManager.getPlayer().setLocation(gameManager.getHouseMap().getName());
        gameManager.getPlayer().setPosition(core.world.HouseMap.BED_LOCATION.x, core.world.HouseMap.BED_LOCATION.y);
        showScreen("HouseScreen"); // Tampilkan house setelah bangun
    }

    /**
     * Animate player movement along a path (list of Points) on the given map.
     * Calls onComplete.run() when done.
     */
    private void animateMovement(List<Point> path, GameMap map, Runnable onComplete) {
        if (path == null || path.isEmpty()) {
            onComplete.run();
            return;
        }
        Player player = gameManager.getPlayer();
        Timer timer = new Timer(80, null); // 80ms per step
        final int[] idx = {0};
        timer.addActionListener(e -> {
            if (idx[0] < path.size()) {
                Point p = path.get(idx[0]);
                player.setPosition(p.x, p.y);
                refreshActiveMapPanel();
                idx[0]++;
            } else {
                timer.stop();
                onComplete.run();
            }
        });
        timer.start();
    }

    /**
     * Refresh the currently visible map panel.
     */
    private void refreshActiveMapPanel() {
        String loc = gameManager.getPlayer().getLocation();
        if (loc.equals(gameManager.getFarmMap().getName())) {
            farmMapPanel.refreshMap();
        } else if (loc.equals(gameManager.getCityMap().getName())) {
            cityMapPanel.refreshMap();
        } else if (loc.equals(gameManager.getHouseMap().getName())) {
            houseMapPanel.refreshMap();
        }
    }

    /**
     * Find the nearest house exit on the current map (for simplicity, use house exit spawn on farm, or a fixed point on city).
     */
    private Point findNearestHouseExit(GameMap map) {
        if (map.getName().equals(gameManager.getFarmMap().getName())) {
            return new Point(gameManager.getFarmMap().getHouseExitSpawnX(), gameManager.getFarmMap().getHouseExitSpawnY());
        } else if (map.getName().equals(gameManager.getCityMap().getName())) {
            // For city, use a fixed exit (e.g., center bottom)
            int sz = map.getSize();
            return new Point(sz / 2, sz - 1);
        }
        // Default fallback
        return new Point(0, 0);
    }

    /**
     * A* pathfinding for any GameMap. Returns list of Points from start (excluded) to goal (included).
     */
    private List<Point> findPathAStar(GameMap map, Point start, Point goal) {
        int sz = map.getSize();
        boolean[][] closed = new boolean[sz][sz];
        Map<String, String> cameFrom = new HashMap<>();
        Map<String, Integer> gScore = new HashMap<>();
        PriorityQueue<Point> open = new PriorityQueue<>(Comparator.comparingInt(p -> gScore.getOrDefault(p.x+","+p.y, Integer.MAX_VALUE) + heuristic(p, goal)));
        String startKey = start.x + "," + start.y;
        String goalKey = goal.x + "," + goal.y;
        gScore.put(startKey, 0);
        open.add(new Point(start.x, start.y));
        int[][] dirs = {{0,1},{1,0},{0,-1},{-1,0}};
        while (!open.isEmpty()) {
            Point curr = open.poll();
            String currKey = curr.x + "," + curr.y;
            if (curr.x == goal.x && curr.y == goal.y) {
                // Reconstruct path
                LinkedList<Point> path = new LinkedList<>();
                String pKey = goalKey;
                while (!pKey.equals(startKey)) {
                    String[] parts = pKey.split(",");
                    int px = Integer.parseInt(parts[0]);
                    int py = Integer.parseInt(parts[1]);
                    path.addFirst(new Point(px, py));
                    pKey = cameFrom.get(pKey);
                }
                return path;
            }
            closed[curr.x][curr.y] = true;
            for (int[] d : dirs) {
                int nx = curr.x + d[0], ny = curr.y + d[1];
                if (nx < 0 || ny < 0 || nx >= sz || ny >= sz) continue;
                if (!map.isWalkable(nx, ny)) continue;
                if (closed[nx][ny]) continue;
                String npKey = nx + "," + ny;
                int tentativeG = gScore.get(currKey) + 1;
                if (tentativeG < gScore.getOrDefault(npKey, Integer.MAX_VALUE)) {
                    cameFrom.put(npKey, currKey);
                    gScore.put(npKey, tentativeG);
                    open.add(new Point(nx, ny));
                }
            }
        }
        return Collections.emptyList(); // No path found
    }

    private int heuristic(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
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
                storePanel.onShow();
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

    public void showStatisticsScreen() {
    if (statisticsPanel == null) { // Defensive check
        System.err.println("GameView: StatisticsPanel is null. Cannot show.");
        return;
    }
    statisticsPanel.refreshStatistics(); // Call method to load/update data
    showScreen("StatisticsScreen");
}
}