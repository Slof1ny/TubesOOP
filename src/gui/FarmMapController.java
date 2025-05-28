// TubesOOP/src/gui/FarmMapController.java
package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.player.Player;
import core.world.FarmMap;
import core.world.DeployedObject; // Import DeployedObject
import core.world.ShippingBin;    // Import ShippingBin class for specific check
import core.house.House;          // Import House class for specific check
import core.world.Pond;
import core.world.Tile;           // Import Tile class

import action.Action;
import item.Seed;
import time.Time;
import time.GameCalendar;

public class FarmMapController extends KeyAdapter {
    private Player player;
    private FarmMap farmMap;
    private FarmMapPanel farmMapPanel;
    private Time gameTime;
    private GameCalendar gameCalendar;
    private PlayerInfoPanel playerInfoPanel;

    public FarmMapController(Player player, FarmMap farmMap, FarmMapPanel farmMapPanel, Time gameTime, GameCalendar gameCalendar, PlayerInfoPanel playerInfoPanel) {
        this.player = player;
        this.farmMap = farmMap;
        this.farmMapPanel = farmMapPanel;
        this.gameTime = gameTime;
        this.gameCalendar = gameCalendar;
        this.playerInfoPanel = playerInfoPanel;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean actionTaken = false;

        // Consume the event to prevent it from being processed by other listeners or components
        if (e.isConsumed()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                actionTaken = farmMap.movePlayerUp();
                break;
            case KeyEvent.VK_S:
                actionTaken = farmMap.movePlayerDown();
                break;
            case KeyEvent.VK_A:
                actionTaken = farmMap.movePlayerLeft();
                break;
            case KeyEvent.VK_D:
                actionTaken = farmMap.movePlayerRight();
                break;
            case KeyEvent.VK_T: // 'T' for Tilling
                try {
                    Action.till(farmMap, player, gameTime);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Tilling Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_P: // 'P' for Planting (Example: Parsnip Seeds)
                Seed parsnipSeed = Seed.getSeedByName("Parsnip Seeds");
                if (parsnipSeed != null) {
                    try {
                        Action.plant(farmMap, player, gameTime, gameCalendar, parsnipSeed);
                        actionTaken = true;
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Planting Error", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(farmMapPanel, "Parsnip Seeds item not found in game data.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                break;
            case KeyEvent.VK_H: // 'H' for Harvesting
                try {
                    Action.harvest(farmMap, player, gameTime);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Harvesting Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_R: // 'R' for Watering
                try {
                    Action.water(farmMap, player, gameTime, gameCalendar);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Watering Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_E: // 'E' for Interact with objects (Shipping Bin, House)
                // Check if player is adjacent to Shipping Bin
                DeployedObject interactedObject = getAdjacentDeployedObject();
                if (interactedObject != null) {
                    if (interactedObject instanceof ShippingBin) {
                        JOptionPane.showMessageDialog(farmMapPanel, "You are next to the Shipping Bin! (Selling interface coming soon)", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        // TODO: In next step, replace this with opening the actual selling GUI
                    } else if (interactedObject instanceof House) {
                        JOptionPane.showMessageDialog(farmMapPanel, "You are next to your House! (House actions coming soon)", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        // TODO: In next step, replace this with opening house interior/menu
                    } else if (interactedObject instanceof Pond) {
                        JOptionPane.showMessageDialog(farmMapPanel, "You are next to the Pond! (fishing actions coming soon)", "Interact", JOptionPane.INFORMATION_MESSAGE);
                        // TODO: In next step, replace this with opening house interior/menu
                    }
                     else {
                        JOptionPane.showMessageDialog(farmMapPanel, "You interacted with a " + interactedObject.getSymbol() + "!", "Interact", JOptionPane.INFORMATION_MESSAGE);
                    }
                    actionTaken = true; // Interaction counts as an action
                } else {
                    JOptionPane.showMessageDialog(farmMapPanel, "Nothing to interact with here.", "Interact", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
        }

        if (actionTaken) {
            e.consume(); // Consume the event to prevent multiple processing
            SwingUtilities.invokeLater(() -> {
                farmMapPanel.refreshMap();
                playerInfoPanel.refreshPlayerInfo();
            });
        }
    }

    /**
     * Helper method to check if the player is adjacent to any deployed object.
     * Returns the DeployedObject if found, null otherwise.
     */
    private DeployedObject getAdjacentDeployedObject() {
        int px = player.getX();
        int py = player.getY();

        // Define relative coordinates for adjacent tiles (N, S, E, W, and diagonals if desired)
        int[][] adjacentOffsets = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}, // Cardinal directions
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1} // Diagonals (optional, depends on game rules)
        };

        for (int[] offset : adjacentOffsets) {
            int checkX = px + offset[0];
            int checkY = py + offset[1];

            // Ensure check coordinates are within map bounds
            if (checkX >= 0 && checkX < FarmMap.SIZE && checkY >= 0 && checkY < FarmMap.SIZE) {
                Tile adjacentTile = farmMap.getTileAt(checkX, checkY);
                if (adjacentTile != null && adjacentTile.getType() == Tile.TileType.DEPLOYED) {
                    // Find the deployed object that occupies this tile
                    for (DeployedObject obj : farmMap.getDeployedObjects()) { // Assuming FarmMap has getDeployedObjects()
                        if (obj.occupies(checkX, checkY)) {
                            return obj; // Found an adjacent deployed object
                        }
                    }
                }
            }
        }
        return null; // No adjacent deployed object found
    }
}