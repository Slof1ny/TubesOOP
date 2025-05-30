package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.player.Player;
import core.world.GameMap;
import core.world.DeployedObject;
import core.world.CityMap;
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
        CityMap cityMap = gameManager.getCityMap();

        boolean actionTaken = false;

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
            case KeyEvent.VK_F1:
                gameView.showScreen("HelpScreen");
                actionTaken = true;
                e.consume(); // Consume to prevent character movement
                break;
            case KeyEvent.VK_I:
                gameView.showScreen("InventoryScreen");
                actionTaken = true;
                e.consume(); // Consume to prevent character movement
                break;
            case KeyEvent.VK_E: // Interact with objects (Buildings) OR Exit Map
                DeployedObject interactedObject = getAdjacentDeployedObject(cityMap, player);
                NPC targetNpc = null;
                
                if (interactedObject != null) {
                    // NEW: Check if the object is a fence and explicitly make it non-interactable.
                    if (interactedObject instanceof CityMap.Building && ((CityMap.Building) interactedObject).getBuildingName().equals("Fence")) {
                        JOptionPane.showMessageDialog(cityMapPanel, "It's just a fence. You can't interact with it.", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        actionTaken = true; // Still counts as an action, but does nothing special.
                        break; // Exit switch after handling fence
                    }

                    actionTaken = true; // An interaction attempt was made

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
                        } else if("Orenji si Kucing Barista".equals(buildingName)){
                            targetNpc = gameManager.getNpcByName("Orenji si Kucing Barista");
                        }
                        else{
                            // Default message for other identified buildings that don't have special screens
                            JOptionPane.showMessageDialog(cityMapPanel, "You are interacting with " + buildingName + "!", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        }

                        if (targetNpc != null) {
                            gameView.showNPCInteractionScreen(targetNpc);
                        }
                    } else if (interactedObject.getSymbol() == 'X') { // Exit to Farm
                        int confirm = JOptionPane.showConfirmDialog(cityMapPanel,
                            "You are at the exit to the Farm. Do you want to go back?",
                            "Transition Map", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.YES_OPTION) {
                            gameManager.transitionMap(gameManager.getFarmMap().getName());
                            gameView.showScreen("GameScreen");
                            actionTaken = true;
                        }
                    } else {
                        // Generic interaction for other deployed objects not explicitly handled
                        JOptionPane.showMessageDialog(cityMapPanel, "You interacted with a " + interactedObject.getSymbol() + "!", "Interact", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (cityMap.atEdge(player)) {
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
            if (player.getEnergy() <= Player.MIN_ENERGY) {
                JOptionPane.showMessageDialog(
                    cityMapPanel,
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
                cityMapPanel.refreshMap();
                // Console debug messages (optional, keep if you want console output)
                // gameManager.getPlayer().getInventory().showInventory();
                // gameManager.getPlayer().getStats().printStats();
                // gameManager.getGameTime().displayTime();
                // gameManager.getGameCalendar().displayCalendar();
                gameManager.getTopInfoBarPanel().refreshInfo();
            });
        }
    }

    private DeployedObject getAdjacentDeployedObject(GameMap map, Player player) {
        int px = player.getX();
        int py = player.getY();

        int[][] adjacentOffsets = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}, // Cardinal directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonals (optional)
        };

        for (int[] offset : adjacentOffsets) {
            int checkX = px + offset[0];
            int checkY = py + offset[1];

            if (checkX >= 0 && checkX < map.getSize() && checkY >= 0 && checkY < map.getSize()) {
                for (DeployedObject obj : map.getDeployedObjects()) {
                    if (obj.occupies(checkX, checkY)) {
                        return obj;
                    }
                }
            }
        }
        return null;
    }
}