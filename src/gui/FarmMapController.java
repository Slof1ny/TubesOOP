// TubesOOP/src/gui/FarmMapController.java
package gui;

import javax.swing.JOptionPane; // To resolve JOptionPane
import javax.swing.SwingUtilities;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import core.player.Player;
import core.world.FarmMap;
import action.Action; // Your existing Action class
import item.Seed;     // Example for planting action
import time.Time;
import time.GameCalendar;

public class FarmMapController extends KeyAdapter {
    private Player player;
    private FarmMap farmMap;
    private FarmMapPanel farmMapPanel;
    private Time gameTime; // Reference to the game's Time object
    private GameCalendar gameCalendar; // Reference to the game's GameCalendar object

    public FarmMapController(Player player, FarmMap farmMap, FarmMapPanel farmMapPanel) {
        this.player = player;
        this.farmMap = farmMap;
        this.farmMapPanel = farmMapPanel;
        // For actions that require time/calendar, you need a way to pass them here
        // For simplicity in this initial setup, we'll create new instances,
        // but in a full game, these would typically be passed from GameView or a central GameState class.
        this.gameCalendar = new GameCalendar(); // Dummy calendar for initial actions
        this.gameTime = new Time(this.gameCalendar, player); // Dummy time for initial actions
    }

    // Constructor that includes Time and GameCalendar (preferred for later)
    public FarmMapController(Player player, FarmMap farmMap, FarmMapPanel farmMapPanel, Time gameTime, GameCalendar gameCalendar) {
        this.player = player;
        this.farmMap = farmMap;
        this.farmMapPanel = farmMapPanel;
        this.gameTime = gameTime;
        this.gameCalendar = gameCalendar;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean actionTaken = false; // Flag to indicate if an action occurred that requires redraw

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: // Move Up
                actionTaken = farmMap.movePlayerUp();
                break;
            case KeyEvent.VK_S: // Move Down
                actionTaken = farmMap.movePlayerDown();
                break;
            case KeyEvent.VK_A: // Move Left
                actionTaken = farmMap.movePlayerLeft();
                break;
            case KeyEvent.VK_D: // Move Right
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
            case KeyEvent.VK_R: // 'R' for Watering (Changed from VK_W to avoid duplicate case)
                try {
                    Action.water(farmMap, player, gameTime, gameCalendar);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Watering Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            // Add more cases for other actions (e.g., 'E' for eating, 'Z' for sleeping, etc.)
            // Remember to equip tools as needed before calling actions.
        }

        // If any action changed the game state (player moved, tile changed, etc.)
        if (actionTaken) {
            // All GUI updates must happen on the Event Dispatch Thread (EDT)
            SwingUtilities.invokeLater(() -> {
                farmMapPanel.refreshMap(); // Request the map to redraw
                // You would also update other panels here, e.g., player info panel
                // playerInfoPanel.refresh();
            });
        }
    }
}