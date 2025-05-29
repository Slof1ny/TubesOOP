package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.player.Player;
import core.world.FarmMap;
import core.world.DeployedObject;
import core.world.ShippingBin;
import core.house.House;
import core.world.Pond;
import core.world.Tile;
import fishing.FishingLocation;

import action.Action;
import item.Seed;
import item.Item;
import item.SeedRegistry;
import time.Time;
import time.GameCalendar;
import system.GameManager; // Import GameManager

public class FarmMapController extends KeyAdapter {
    private Player player;
    private FarmMap farmMap;
    private FarmMapPanel farmMapPanel;
    private Time gameTime;
    private GameCalendar gameCalendar;
    private PlayerInfoPanel playerInfoPanel;
    private GameView gameView; // GameView reference to access GameManager and switch screens

    public FarmMapController(Player player, FarmMap farmMap, FarmMapPanel farmMapPanel, Time gameTime, GameCalendar gameCalendar, PlayerInfoPanel playerInfoPanel, GameView gameView) {
        this.player = player;
        this.farmMap = farmMap;
        this.farmMapPanel = farmMapPanel;
        this.gameTime = gameTime;
        this.gameCalendar = gameCalendar;
        this.playerInfoPanel = playerInfoPanel;
        this.gameView = gameView; // Initialize GameView
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean actionTaken = false;
        GameManager gameManager = gameView.getGameManager(); // Get GameManager instance from GameView

        // Ensure this controller only acts if FarmMap is the current map in GameManager
        if (gameManager == null || !gameManager.getCurrentMap().getName().equals(farmMap.getName())) {
            return;
        }

        if (e.isConsumed()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                actionTaken = farmMap.movePlayer(player, 0, -1);
                break;
            case KeyEvent.VK_S:
                actionTaken = farmMap.movePlayer(player, 0, 1);
                break;
            case KeyEvent.VK_A:
                actionTaken = farmMap.movePlayer(player, -1, 0);
                break;
            case KeyEvent.VK_D:
                actionTaken = farmMap.movePlayer(player, 1, 0);
                break;
            case KeyEvent.VK_T: // 'T' for Tilling
                try {
                    Action.till(farmMap, player, gameTime);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Tilling Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_P: // 'P' for Planting
                actionTaken = true; // Assume an action is attempted
                List<Seed> availableSeeds = player.getInventory().getAllOwnedSeeds();

                if (availableSeeds.isEmpty()) {
                    JOptionPane.showMessageDialog(farmMapPanel, "You have no seeds to plant!", "No Seeds", JOptionPane.INFORMATION_MESSAGE);
                    actionTaken = false; // No actual action performed
                } else {
                    Seed seedToPlant = null;
                    if (availableSeeds.size() == 1) {
                        seedToPlant = availableSeeds.get(0);
                        System.out.println("Auto-selected only seed: " + seedToPlant.getName());
                    } else {
                        // Multiple seed types available, let player choose
                        String[] seedNames = new String[availableSeeds.size()];
                        for (int i = 0; i < availableSeeds.size(); i++) {
                            seedNames[i] = availableSeeds.get(i).getName() + " (x" + player.getInventory().getItemCount(availableSeeds.get(i)) + ")";
                        }

                        String chosenSeedDisplay = (String) JOptionPane.showInputDialog(
                                farmMapPanel,
                                "Choose a seed to plant:",
                                "Plant Seed",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                seedNames,
                                seedNames[0]
                        );

                        if (chosenSeedDisplay != null) {
                            // Extract base name if quantity was appended for display
                            String chosenSeedName = chosenSeedDisplay.split(" \\(x")[0];
                            Item selectedItem = player.getInventory().findItemByName(chosenSeedName);
                            if (selectedItem instanceof Seed) {
                                seedToPlant = (Seed) selectedItem;
                            } else {
                                JOptionPane.showMessageDialog(farmMapPanel, "Error selecting seed.", "Error", JOptionPane.ERROR_MESSAGE);
                                actionTaken = false;
                            }
                        } else {
                            actionTaken = false; // Player cancelled dialog
                        }
                    }

                    if (seedToPlant != null) {
                        try {
                            Action.plant(farmMap, player, gameTime, gameCalendar, seedToPlant);
                            // Action.plant itself will print success/failure or throw exception
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Planting Error", JOptionPane.WARNING_MESSAGE);
                            actionTaken = false; // Planting failed
                        }
                    } else if (actionTaken) { // If we got here due to dialog cancel or error, ensure isActionTaken is false
                        actionTaken = false;
                    }
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
            case KeyEvent.VK_I: // 'I' for Equipment
                gameView.showScreen("InventoryScreen");
                actionTaken = true;
                e.consume();
                break;
            case KeyEvent.VK_E: // Interact
                DeployedObject interactedObject = getAdjacentDeployedObject(); //
                if (interactedObject != null) { //
                    if (interactedObject instanceof ShippingBin) {
                        gameView.showScreen("ShippingBinScreen"); // THIS IS THE CORRECTED PART
                        actionTaken = true;
                    } else if (interactedObject instanceof House) { //
                        JOptionPane.showMessageDialog(farmMapPanel, "You are next to your House! (House actions coming soon)", "Interact", JOptionPane.INFORMATION_MESSAGE); //
                        actionTaken = true;
                    } else if (interactedObject instanceof Pond) {
                        FishingLocation pondLocation = gameManager.getFishingLocations().get("Pond");
                        if (pondLocation != null) {
                            FishingGUIAdapter.startFishingGUI(farmMap, pondLocation, player, gameTime, gameCalendar, farmMapPanel);
                        } else {
                             JOptionPane.showMessageDialog(farmMapPanel, "Fishing at the pond is not set up correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        actionTaken = true; // Fishing attempt is an action
                    } else {
                        JOptionPane.showMessageDialog(farmMapPanel, "You interacted with a " + interactedObject.getSymbol() + "!", "Interact", JOptionPane.INFORMATION_MESSAGE); //
                        actionTaken = true;
                    }
                } else {
                    JOptionPane.showMessageDialog(farmMapPanel, "Nothing to interact with here.", "Interact", JOptionPane.INFORMATION_MESSAGE); //
                }
                break;
            case KeyEvent.VK_M: // 'M' to switch to City Map
                if (farmMap.atEdge(player)) {
                    int confirm = JOptionPane.showConfirmDialog(farmMapPanel,
                        "You are at the edge of the farm. Do you want to go to the City?",
                        "Transition Map", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Use GameManager to handle the transition
                        if (gameManager != null) {
                            gameManager.transitionMap(gameManager.getCityMap().getName()); // Transition to CityMap
                            gameView.showScreen("CityScreen"); // Show the CityScreen
                            actionTaken = true;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(farmMapPanel, "You must be at the edge of the map to go to the City.", "Cannot Transition", JOptionPane.WARNING_MESSAGE);
                }
                break;
        }

        if (actionTaken) {
            if(e.getKeyCode() != KeyEvent.VK_I && e.getKeyCode() != KeyEvent.VK_M && e.getKeyCode() != KeyEvent.VK_E){
                e.consume();
            }
            SwingUtilities.invokeLater(() -> {
                farmMapPanel.refreshMap();
                // playerInfoPanel is refreshed globally by GameView.showScreen()
                // or specifically if an action only updates info without screen change.
                // If an action here changes player state that PlayerInfoPanel needs to show, refresh it.
                if (playerInfoPanel != null) {
                     playerInfoPanel.refreshPlayerInfo();
                }
            });
        }
    }

    private DeployedObject getAdjacentDeployedObject() {
        int px = player.getX();
        int py = player.getY();

        int[][] adjacentOffsets = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] offset : adjacentOffsets) {
            int checkX = px + offset[0];
            int checkY = py + offset[1];

            if (checkX >= 0 && checkX < FarmMap.SIZE && checkY >= 0 && checkY < FarmMap.SIZE) {
                Tile adjacentTile = farmMap.getTileAt(checkX, checkY);
                if (adjacentTile != null && adjacentTile.getType() == Tile.TileType.DEPLOYED) {
                    for (DeployedObject obj : farmMap.getDeployedObjects()) {
                        if (obj.occupies(checkX, checkY)) {
                            return obj;
                        }
                    }
                }
            }
        }
        return null;
    }
}