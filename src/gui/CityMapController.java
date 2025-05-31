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

/**
 * Controller for CityMap: handles player movement and interactions (key presses).
 */
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

        // Only respond to keys if the player is currently on the CityMap
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
            case KeyEvent.VK_F1: // 'F1' for Help
                gameView.showScreen("HelpScreen");
                actionTaken = true;
                e.consume();
                break;
            case KeyEvent.VK_I: // 'I' for Inventory
                gameView.showScreen("InventoryScreen");
                actionTaken = true;
                e.consume();
                break;

            case KeyEvent.VK_E: // 'E' for Interact with objects (Buildings) OR Exit Map
                DeployedObject interactedObject = null;

                // STEP 1: Check if player is standing on a deployable object
                for (DeployedObject obj : cityMap.getDeployedObjects()) {
                    if (obj.occupies(player.getX(), player.getY())) {
                        // If it's the Exit ('X'), return immediately:
                        if (obj.getSymbol() == 'X') {
                            interactedObject = obj;
                            break;
                        }
                        // Otherwise, if it's a fence, skip:
                        if (obj instanceof CityMap.Building) {
                            CityMap.Building b = (CityMap.Building) obj;
                            if ("Fence".equals(b.getBuildingName())) {
                                continue;
                            }
                        }
                        // Found a non-fence building/object underfoot:
                        interactedObject = obj;
                        break;
                    }
                }

                // STEP 2: If nothing underfoot, fall back to adjacent check
                if (interactedObject == null) {
                    interactedObject = getAdjacentDeployedObject(cityMap, player);
                }

                if (interactedObject != null) {
                    actionTaken = true;
                    // Handle Exit first:
                    if (interactedObject.getSymbol() == 'X') {
                        int confirm = JOptionPane.showConfirmDialog(
                            cityMapPanel,
                            "You are at the exit to the Farm. Do you want to go back?",
                            "Transition Map",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            gameManager.transitionMap(gameManager.getFarmMap().getName());
                            gameView.showScreen("GameScreen");
                        } else {
                            actionTaken = false; // Player cancelled
                        }
                    }
                     else if (interactedObject instanceof CityMap.Building) {
                        CityMap.Building building = (CityMap.Building) interactedObject;
                        String buildingName = building.getBuildingName();
                        System.out.println("Interacting with building: " + buildingName); // Debug print

                        if ("Emily's Store".equals(buildingName)) {
                            NPC emilyNpc = gameManager.getNpcByName("Emily");

                            if (emilyNpc != null) {
                                Object[] options = {"Talk to Emily", "Shop", "Cancel"};
                                int choice = JOptionPane.showOptionDialog(cityMapPanel,
                                        "What would you like to do at " + buildingName + "?",
                                        "Emily's Store",
                                        JOptionPane.YES_NO_CANCEL_OPTION,
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        options,
                                        options[0]);

                                if (choice == JOptionPane.YES_OPTION) { // Talk to Emily
                                    System.out.println("Player chose to talk to Emily.");
                                    gameView.showNPCInteractionScreen(emilyNpc);
                                    // Apply energy cost for interaction
                                    if (player.getEnergy() >= 5) {
                                        player.setEnergy(player.getEnergy() - 5);
                                    } else {
                                        JOptionPane.showMessageDialog(cityMapPanel, "Not enough energy to interact with Emily.", "Energy Low", JOptionPane.WARNING_MESSAGE);
                                        actionTaken = false;
                                    }
                                } else if (choice == JOptionPane.NO_OPTION) { // Shop
                                    System.out.println("Player chose to shop at Emily's Store.");
                                    gameView.showScreen("StoreScreen");
                                    if (player.getEnergy() >= 5) { // Cost for entering store to shop
                                        player.setEnergy(player.getEnergy() - 5);
                                    }
                                } else { // Cancel or closed dialog
                                    actionTaken = false;
                                }
                            } else {
                                // Fallback if Emily NPC somehow not found, just open store
                                System.out.println("Emily NPC object not found, defaulting to store screen for: " + buildingName);
                                gameView.showScreen("StoreScreen");
                                if (player.getEnergy() >= 5) {
                                    player.setEnergy(player.getEnergy() - 5);
                                }
                            }
                        } else {
                            String baseNpcName = buildingName;
                            if ("Mayor's Manor".equals(buildingName)) {
                                baseNpcName = "Mayor Tadi";
                            }
                            else if ("Orenji si Kucing Barista".equals(buildingName)) {
                                baseNpcName = "Orenji";
                            }
                            else {
                                // Generic parsing for other NPC names from building names
                                baseNpcName = buildingName
                                    .replace("'s Manor", "")
                                    .replace("'s Carpentry", "")
                                    .replace("'s Cabin", "")
                                    .replace("'s Gambling Den", "")
                                    .replace("'s Tent", "")
                                    .replace("si Kucing Barista", "") // Ensure this is also removed if not caught by specific if
                                    .trim();
                            }
                            System.out.println("Attempting to find NPC with name: " + baseNpcName); // Debug print
                            NPC targetNpc = gameManager.getNpcByName(baseNpcName);

                            if (targetNpc != null) {
                                System.out.println("NPC found: " + targetNpc.getName() + ". Showing interaction screen."); // Debug print
                                gameView.showNPCInteractionScreen(targetNpc);
                                if (player.getEnergy() >= 5) { // Assuming a cost for meeting NPC
                                    player.setEnergy(player.getEnergy() - 5);
                                } else {
                                    JOptionPane.showMessageDialog(
                                        cityMapPanel,
                                        "Not enough energy to interact with " + targetNpc.getName() + ".",
                                        "Energy Low",
                                        JOptionPane.WARNING_MESSAGE
                                    );
                                    actionTaken = false;
                                }
                            } else {
                                System.out.println("NPC not found for building: " + buildingName + ". Looked for NPC named: " + baseNpcName); // Debug print
                                // Generic message for other buildings that don't have special screens or recognized NPCs
                                JOptionPane.showMessageDialog(
                                    cityMapPanel,
                                    "You are interacting with " + buildingName + ", but no NPC found for interaction.",
                                    "Interact",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                                actionTaken = false; // General interaction doesn't consume energy/time
                            }
                        }
                    }
                    else {
                        // Generic interaction for other DeployedObjects that are not Buildings or 'X'
                        JOptionPane.showMessageDialog(
                            cityMapPanel,
                            "You interacted with a " + interactedObject.getSymbol() + "!",
                            "Interact",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        actionTaken = false;
                    }
                }
                else if (cityMap.atEdge(player)) { // If no adjacent object, check if at map edge for transitions
                    if (player.getY() == 0) { // Top edge leads to Farm
                        int confirm = JOptionPane.showConfirmDialog(
                            cityMapPanel,
                            "You are at the top edge of the city. Do you want to go to the Farm?",
                            "Transition Map",
                            JOptionPane.YES_NO_OPTION
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            gameManager.transitionMap(gameManager.getFarmMap().getName());
                            gameView.showScreen("GameScreen");
                            actionTaken = true;
                        } else {
                            actionTaken = false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                            cityMapPanel,
                            "Nothing to interact with here.",
                            "Interact",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        actionTaken = false;
                    }
                }
                else {
                    JOptionPane.showMessageDialog(
                        cityMapPanel,
                        "Nothing to interact with here.",
                        "Interact",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    actionTaken = false;
                }
                break;
        }

        // --- Post-Action Processing ---
        if (actionTaken) {
            e.consume(); // Consume the event if an action was actually taken

            if (player.getEnergy() <= Player.MIN_ENERGY) {
                JOptionPane.showMessageDialog(
                    cityMapPanel,
                    "You've exhausted all your energy and fainted!",
                    "Exhausted",
                    JOptionPane.WARNING_MESSAGE
                );
                gameManager.forcePlayerSleep();
                return;
            }

            SwingUtilities.invokeLater(() -> {
                cityMapPanel.refreshMap();
                gameManager.getTopInfoBarPanel().refreshInfo();
            });
        }
    }

    /**
     * Helper method to check if the player is adjacent to any deployed object on the given map.
     * Fences are explicitly filtered out.
     */
    private DeployedObject getAdjacentDeployedObject(GameMap map, Player player) {
        int px = player.getX();
        int py = player.getY();

        int[][] adjacentOffsets = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0},    // Cardinal directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}   // Diagonals (optional)
        };

        for (int[] offset : adjacentOffsets) {
            int checkX = px + offset[0];
            int checkY = py + offset[1];

            if (checkX >= 0 && checkX < map.getSize() && checkY >= 0 && checkY < map.getSize()) {
                for (DeployedObject obj : map.getDeployedObjects()) {
                    if (obj.occupies(checkX, checkY)) {
                        // Always allow the exit object (symbol 'X')
                        if (obj.getSymbol() == 'X') {
                            return obj;
                        }
                        // Skip fences
                        if (obj instanceof CityMap.Building) {
                            CityMap.Building building = (CityMap.Building) obj;
                            if ("Fence".equals(building.getBuildingName())) {
                                continue;
                            }
                        }
                        // Return any other non-fence object
                        return obj;
                    }
                }
            }
        }
        return null; // No adjacent, non-fence, interactable object found
    }
}
