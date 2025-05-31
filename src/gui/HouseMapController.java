package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import system.GameManager;
import core.player.Player;
import core.world.GameMap;
import core.world.HouseMap; // To access specific locations like EXIT_LOCATION
import core.world.DeployedObject;
import core.world.Tile;
import core.world.InteractionSpot;
import gui.HouseMapPanel;
// import action.Action; // Assuming your Action class has sleep and watchTV methods

public class HouseMapController extends KeyAdapter {
    // Movement cooldown in milliseconds
    private static final long MOVE_COOLDOWN_MS = 200;
    private long lastMoveTime = 0;
    private GameManager gameManager;
    private HouseMapPanel houseMapPanel;
    private GameView gameView;

    public HouseMapController(GameManager gameManager, HouseMapPanel houseMapPanel, GameView gameView) {
        this.gameManager = gameManager;
        this.houseMapPanel = houseMapPanel;
        this.gameView = gameView;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player player = gameManager.getPlayer();
        GameMap currentMap = gameManager.getHouseMap();
        if (!player.getLocation().equals(currentMap.getName())) return;
        if (e.isConsumed()) return;

        boolean actionTaken = false;
        long now = System.currentTimeMillis();
        int dx = 0, dy = 0;
        boolean moveKey = false;

        // Movement keys
        if (e.getKeyCode() == KeyEvent.VK_W) { dy -= 1; moveKey = true; }
        if (e.getKeyCode() == KeyEvent.VK_S) { dy += 1; moveKey = true; }
        if (e.getKeyCode() == KeyEvent.VK_A) { dx -= 1; moveKey = true; }
        if (e.getKeyCode() == KeyEvent.VK_D) { dx += 1; moveKey = true; }

        if (moveKey && (dx != 0 || dy != 0) && now - lastMoveTime >= MOVE_COOLDOWN_MS) {
            actionTaken = currentMap.movePlayer(player, dx, dy);
            lastMoveTime = now;
        } else if (!moveKey) {
            // Non-movement actions
            switch (e.getKeyCode()) {
                case KeyEvent.VK_I:
                    gameView.showScreen("InventoryScreen");
                    actionTaken = true;
                    e.consume();
                    break;
                case KeyEvent.VK_F1:
                    gameView.showScreen("HelpScreen");
                    actionTaken = true;
                    break;
                case KeyEvent.VK_E: {
                    actionTaken = true;
                    Tile playerTile = currentMap.getTileAt(player.getX(), player.getY());
                    DeployedObject spot = getInteractionSpotOnTile(playerTile, currentMap);
                    if (spot instanceof InteractionSpot) {
                        InteractionSpot iSpot = (InteractionSpot) spot;
                        String interactionType = iSpot.getInteractionType();
                        System.out.println("Interacting with: " + interactionType);
                        if ("BED".equals(interactionType)) {
                            gameManager.getGameTime().sleep2();
                            JOptionPane.showMessageDialog(houseMapPanel, "You slept well and woke up refreshed!", "Slept", JOptionPane.INFORMATION_MESSAGE);
                        } else if ("TV".equals(interactionType)) {
                            player.setEnergy(player.getEnergy() - 5);
                            gameManager.getGameTime().advanceGameMinutes(15);
                            String weatherForecast = "Today's weather: " + gameManager.getGameCalendar().getCurrentWeather() +
                                "\n(Energy -5, Time +15 mins)";
                            JOptionPane.showMessageDialog(houseMapPanel, weatherForecast, "Weather Forecast", JOptionPane.INFORMATION_MESSAGE);
                        } else if ("STOVE".equals(interactionType)) {
                            gameView.showScreen("CookingScreen");
                        } else if ("EXIT_TO_FARM".equals(interactionType)) {
                            gameManager.transitionMap(gameManager.getFarmMap().getName());
                            player.setPosition(gameManager.getFarmMap().getHouseExitSpawnX(), gameManager.getFarmMap().getHouseExitSpawnY());
                            gameView.showScreen("GameScreen");
                        } else {
                            actionTaken = false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(houseMapPanel, "Nothing to interact with here.", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        actionTaken = false;
                    }
                    break;
                }
                case KeyEvent.VK_ESCAPE:
                    JOptionPane.showMessageDialog(houseMapPanel, "Move to the 'X' spot to exit.", "Exit House", JOptionPane.INFORMATION_MESSAGE);
                    actionTaken = false;
                    break;
            }
        }

        if (actionTaken) {
            if (player.getEnergy() <= Player.MIN_ENERGY) {
                JOptionPane.showMessageDialog(
                    houseMapPanel,
                    "You've exhausted all your energy and fainted!",
                    "Exhausted",
                    JOptionPane.WARNING_MESSAGE
                );
                gameManager.forcePlayerSleep();
                e.consume();
                return;
            }
        }

        if (actionTaken) {
            e.consume();
            SwingUtilities.invokeLater(() -> {
                houseMapPanel.refreshMap();
                if (gameManager.getTopInfoBarPanel() != null && gameManager.getTopInfoBarPanel().isVisible()) {
                    gameManager.getTopInfoBarPanel().refreshInfo();
                }
            });
        }
    }

    // Helper to get an InteractionSpot if player is standing on one
    private DeployedObject getInteractionSpotOnTile(Tile tile, GameMap map) {
        if (tile != null && tile.getType() == Tile.TileType.DEPLOYED) {
            for (DeployedObject obj : map.getDeployedObjects()) {
                if (obj instanceof InteractionSpot && obj.occupies(tile.getX(), tile.getY())) {
                    return obj;
                }
            }
        }
        return null;
    }
}