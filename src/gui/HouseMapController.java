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
        GameMap currentMap = gameManager.getHouseMap(); // We are in the HouseMap

        if (!player.getLocation().equals(currentMap.getName())) {
            return; // Safety check, should not happen if this controller is active
        }
        if (e.isConsumed()) {
            return;
        }

        boolean actionTaken = false;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                actionTaken = currentMap.movePlayer(player, 0, -1);
                break;
            case KeyEvent.VK_S:
                actionTaken = currentMap.movePlayer(player, 0, 1);
                break;
            case KeyEvent.VK_A:
                actionTaken = currentMap.movePlayer(player, -1, 0);
                break;
            case KeyEvent.VK_D:
                actionTaken = currentMap.movePlayer(player, 1, 0);
                break;
            case KeyEvent.VK_E: // Interact
                actionTaken = true; // Assume an attempt
                Tile playerTile = currentMap.getTileAt(player.getX(), player.getY());
                DeployedObject spot = getInteractionSpotOnTile(playerTile, currentMap);

                if (spot instanceof InteractionSpot) {
                    InteractionSpot iSpot = (InteractionSpot) spot;
                    String interactionType = iSpot.getInteractionType();
                    System.out.println("Interacting with: " + interactionType);

                    if ("BED".equals(interactionType)) {
                        // Trigger Sleeping Action
                        // Action.sleep(player, gameManager.getGameTime(), gameManager.getGameCalendar()); // You'll need to implement this
                        gameManager.getGameTime().sleep2(); // Using existing sleep2 which calls processNewDayUpdates
                        JOptionPane.showMessageDialog(houseMapPanel, "You slept well and woke up refreshed!", "Slept", JOptionPane.INFORMATION_MESSAGE);
                        // Player is still in the house after sleeping, at the bed or entry.
                        // For simplicity, let's keep them where they are. Time will update.
                    } else if ("TV".equals(interactionType)) {
                        // Trigger Watching TV Action
                        // String weatherForecast = Action.watchTV(player, gameManager.getGameTime(), gameManager.getGameCalendar());
                        player.setEnergy(player.getEnergy() - 5); // As per spec [cite: 1]
                        gameManager.getGameTime().advanceGameMinutes(15); // As per spec [cite: 1]
                        String weatherForecast = "Today's weather: " + gameManager.getGameCalendar().getCurrentWeather() +
                                               "\n(Energy -5, Time +15 mins)";
                        JOptionPane.showMessageDialog(houseMapPanel, weatherForecast, "Weather Forecast", JOptionPane.INFORMATION_MESSAGE);
                    } else if ("EXIT_TO_FARM".equals(interactionType)) {
                        gameManager.transitionMap(gameManager.getFarmMap().getName());
                        // Player position on farm map is set by FarmMap's spawn or transition logic
                        // For now, let's assume FarmMap's default spawn is okay when coming from house.
                        // You might want specific exit->entry coordinates.
                        player.setPosition(gameManager.getFarmMap().getHouseExitSpawnX(), gameManager.getFarmMap().getHouseExitSpawnY()); // Needs methods in FarmMap
                        gameView.showScreen("GameScreen");
                    } else {
                        actionTaken = false; // No defined interaction
                    }
                } else {
                    JOptionPane.showMessageDialog(houseMapPanel, "Nothing to interact with here.", "Interact", JOptionPane.INFORMATION_MESSAGE);
                    actionTaken = false;
                }
                break;
            case KeyEvent.VK_ESCAPE: // Option to leave house with ESC
                 // gameView.showScreen("GameScreen"); // Or specific exit logic
                 // For consistency, use the EXIT_SYMBOL spot
                 JOptionPane.showMessageDialog(houseMapPanel, "Move to the 'X' spot to exit.", "Exit House", JOptionPane.INFORMATION_MESSAGE);
                 actionTaken = false;
                 break;
        }

        if (actionTaken) {
            // Check energy AFTER the action has potentially reduced it
            if (player.getEnergy() <= Player.MIN_ENERGY) {
                // Show message BEFORE forcePlayerSleep changes the screen context
                JOptionPane.showMessageDialog(
                    houseMapPanel, // Or whatever panel is currently active
                    "You've exhausted all your energy and fainted!",
                    "Exhausted",
                    JOptionPane.WARNING_MESSAGE
                );
                gameManager.forcePlayerSleep(); // This will call time.sleep2()
                e.consume(); // The event is fully handled by fainting
                // UI will be refreshed by sleep2 -> onGameTimeTick -> TopInfoBar & potentially screen change
                return; // IMPORTANT: Stop further processing for THIS key event
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