package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.player.Player;
import core.world.GameMap;
import core.world.DeployedObject;
import core.world.CityMap; // For specific building types
import system.GameManager;
import npc.NPC;

public class CityMapController extends KeyAdapter {
    private GameManager gameManager;
    private CityMapPanel cityMapPanel;
    private GameView gameView;

    public CityMapController(GameManager gameManager, CityMapPanel cityMapPanel, GameView gameView) {
        this.gameManager = gameManager;
        this.cityMapPanel = cityMapPanel;
        this.gameView = gameView;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player player = gameManager.getPlayer();
        GameMap currentMap = gameManager.getCurrentMap();
        CityMap cityMap = gameManager.getCityMap(); // Get CityMap from GameManager

        boolean actionTaken = false;

        // Ensure this controller only processes events if player is on City Map
        if (!player.getLocation().equals(cityMap.getName())) {
            return;
        }

        if (e.isConsumed()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                actionTaken = cityMap.movePlayer(player, 0, -1);
                break;
            case KeyEvent.VK_S:
                actionTaken = cityMap.movePlayer(player, 0, 1);
                break;
            case KeyEvent.VK_A:
                actionTaken = cityMap.movePlayer(player, -1, 0);
                break;
            case KeyEvent.VK_D:
                actionTaken = cityMap.movePlayer(player, 1, 0);
                break;
            case KeyEvent.VK_I: // 'I' for Equipment
                gameView.showScreen("InventoryScreen");
                actionTaken = true;
                e.consume();
                break;
            case KeyEvent.VK_E: // 'E' for Interact with objects (Buildings) OR Exit Map
                DeployedObject interactedObject = getAdjacentDeployedObject(cityMap, player);
                NPC targetNpc = null;
                if (interactedObject != null) {
                    actionTaken = true;
                    if(interactedObject instanceof CityMap.Building){
                        CityMap.Building building = (CityMap.Building) interactedObject;
                        String buildingName = building.getBuildingName();
                        if ("Emily's Store".equals(buildingName)){
                            gameView.showScreen("StoreScreen");
                        } else if ("Mayor's Manor".equals(buildingName)) {
                            targetNpc = gameManager.getNpcByName("Mayor Tadi");
                        } else if ("Caroline's Carpentry".equals(buildingName)) {
                            targetNpc = gameManager.getNpcByName("Caroline");
                        } else if ("Perry's Cabin".equals(buildingName)) {
                            targetNpc = gameManager.getNpcByName("Perry");
                        } else if ("Dasco's Gambling Den".equals(buildingName)) {
                            targetNpc = gameManager.getNpcByName("Dasco");
                        } else if ("Abigail's Tent".equals(buildingName)) {
                            targetNpc = gameManager.getNpcByName("Abigail");
                        }
                        else{
                            JOptionPane.showMessageDialog(cityMapPanel, "You are interacting with " + buildingName + "!", "Interact", JOptionPane.INFORMATION_MESSAGE);
                            actionTaken = false;
                        }

                        if (targetNpc != null) {
                            gameView.showNPCInteractionScreen(targetNpc);
                        }
                    }else if (interactedObject.getSymbol() == 'X') { // Exit to Farm
                        int confirm = JOptionPane.showConfirmDialog(cityMapPanel,
                            "You are at the exit to the Farm. Do you want to go back?",
                            "Transition Map", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            gameManager.transitionMap(gameManager.getFarmMap().getName()); // Request map transition
                            gameView.showScreen("GameScreen"); // Re-show GameScreen, it will update map
                            actionTaken = true;
                        }
                    } else {
                        // Generic interaction for other buildings/NPC houses
                        // If you made Building class public in CityMap, you can cast and get name
                        if (interactedObject instanceof CityMap.Building) {
                            JOptionPane.showMessageDialog(cityMapPanel, "You are interacting with " + ((CityMap.Building) interactedObject).getBuildingName() + "!", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(cityMapPanel, "You interacted with a " + interactedObject.getSymbol() + "!", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    actionTaken = true;
                } else if (cityMap.atEdge(player)) { // If no object interaction, check if at map edge
                    // Example: transition to Farm Map if at the top edge (exit 'X' is at bottom)
                    if (player.getY() == 0) { // Assuming top edge of city leads back to farm
                        int confirm = JOptionPane.showConfirmDialog(cityMapPanel,
                            "You are at the edge of the city. Do you want to go to the Farm?",
                            "Transition Map", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            gameManager.transitionMap(gameManager.getFarmMap().getName());
                            gameView.showScreen("GameScreen");
                            actionTaken = true;
                        }
                    } else {
                        JOptionPane.showMessageDialog(cityMapPanel, "Nothing to interact with here.", "Interact", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(cityMapPanel, "Nothing to interact with here.", "Interact", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
        }

        if (actionTaken) {
            // Check energy AFTER the action has potentially reduced it
            if (player.getEnergy() <= Player.MIN_ENERGY) {
                // Show message BEFORE forcePlayerSleep changes the screen context
                JOptionPane.showMessageDialog(
                    cityMapPanel, // Or whatever panel is currently active
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
                cityMapPanel.refreshMap();
                gameManager.getPlayer().getInventory().showInventory(); // For console debug
                gameManager.getPlayer().getStats().printStats(); // For console debug
                gameManager.getGameTime().displayTime(); // For console debug
                gameManager.getGameCalendar().displayCalendar(); // For console debug
                gameManager.getTopInfoBarPanel().refreshInfo(); // Update player info on GUI
            });
        }
    }

    /**
     * Helper method to check if the player is adjacent to any deployed object on the given map.
     */
    private DeployedObject getAdjacentDeployedObject(GameMap map, Player player) {
        int px = player.getX();
        int py = player.getY();

        int[][] adjacentOffsets = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}, // Cardinal directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonals (optional, depends on game rules)
        };

        for (int[] offset : adjacentOffsets) {
            int checkX = px + offset[0];
            int checkY = py + offset[1];

            if (checkX >= 0 && checkX < map.getSize() && checkY >= 0 && checkY < map.getSize()) {
                // Check if the tile itself is a deployed object or part of one
                // You might need to iterate through all deployed objects to see if their area contains checkX, checkY
                // if the tile.displayChar() is only for the origin point of the object.
                // Assuming Tile.getType() == DEPLOYED and tile.displayChar() is sufficient for now.
                for (DeployedObject obj : map.getDeployedObjects()) {
                    if (obj.occupies(checkX, checkY)) {
                        return obj; // Found an adjacent deployed object
                    }
                }
            }
        }
        return null;
    }
}