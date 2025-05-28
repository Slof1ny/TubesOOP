// TubesOOP/src/gui/FarmMapPanel.java
package gui;

import javax.swing.*;
import java.awt.*; // Make sure this import is present at the top

import core.world.FarmMap;
import core.world.Tile;
import core.player.Player;

// Add these imports for the FarmMapController constructor
import time.Time;
import time.GameCalendar;

public class FarmMapPanel extends JPanel {
    private FarmMap farmMap;
    private Player player;

    // Define colors for different tile types and player
    private static final Color UNTILLED_COLOR = new Color(139, 69, 19); // Brown
    private static final Color TILLED_COLOR = new Color(101, 67, 33); // Darker Brown
    private static final Color PLANTED_COLOR = new Color(50, 150, 50); // Green for planted crops
    private static final Color DEPLOYED_COLOR_HOUSE = new Color(150, 75, 0); // Wood brown for house
    private static final Color DEPLOYED_COLOR_POND = new Color(0, 100, 200); // Blue for pond
    private static final Color DEPLOYED_COLOR_SHIPPINGBIN = new Color(100, 50, 0); // Darker brown for shipping bin
    private static final Color PLAYER_COLOR = new Color(255, 0, 0); // Red for player

    // Define DEPLOYED_COLOR as a generic fallback if needed, or remove it from the switch
    private static final Color DEPLOYED_COLOR = Color.LIGHT_GRAY; // Generic deployed color

    // Define CUSTOM_DARK_GREEN explicitly, since Color.DARK_GREEN doesn't exist
    private static final Color CUSTOM_DARK_GREEN = new Color(0, 100, 0); // A custom dark green

    private final int TILE_SIZE = 20; // Size of each tile in pixels (20x20 pixels per tile)

    // Modify constructor to accept Time, GameCalendar, and PlayerInfoPanel
    public FarmMapPanel(FarmMap farmMap, Player player, Time gameTime, GameCalendar gameCalendar, PlayerInfoPanel playerInfoPanel) {
        this.farmMap = farmMap;
        this.player = player;

        // Set the preferred size of the panel based on map size and tile size
        setPreferredSize(new Dimension(FarmMap.SIZE * TILE_SIZE, FarmMap.SIZE * TILE_SIZE));
        setBackground(Color.LIGHT_GRAY); // Background for the panel itself

        // This panel needs to be focusable to receive keyboard input for movement
        setFocusable(true);
        // Add the controller, passing all necessary arguments
        addKeyListener(new FarmMapController(player, farmMap, this, gameTime, gameCalendar, playerInfoPanel));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Iterate through each tile in the farm map grid
        for (int y = 0; y < FarmMap.SIZE; y++) {
            for (int x = 0; x < FarmMap.SIZE; x++) {
                Tile tile = farmMap.getTileAt(x, y);
                Color tileColor;

                // Determine color based on tile type
                switch (tile.getType()) {
                    case UNTILLED:
                        tileColor = UNTILLED_COLOR;
                        break;
                    case TILLED:
                        tileColor = TILLED_COLOR;
                        break;
                    case PLANTED:
                        tileColor = PLANTED_COLOR;
                        break;
                    case DEPLOYED:
                        // For deployed objects, use specific colors based on their symbol
                        char deployedChar = tile.displayChar();
                        if (deployedChar == 'h') {
                            tileColor = DEPLOYED_COLOR_HOUSE;
                        } else if (deployedChar == 'o') {
                            tileColor = DEPLOYED_COLOR_POND;
                        } else if (deployedChar == 's') {
                            tileColor = DEPLOYED_COLOR_SHIPPINGBIN;
                        } else {
                            tileColor = DEPLOYED_COLOR; // Fallback for other deployed objects or if symbol not recognized
                        }
                        break;
                    default:
                        tileColor = Color.WHITE; // Fallback, should not be reached
                }

                // Draw the tile's background color
                g2d.setColor(tileColor);
                g2d.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Draw tile borders to make them distinct
                g2d.setColor(Color.BLACK);
                g2d.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Optionally, draw a character symbol for deployed objects on the tile
                if (tile.getType() == Tile.TileType.DEPLOYED) {
                    g2d.setColor(Color.WHITE); // Text color
                    String symbol = String.valueOf(tile.displayChar());
                    // Center the text within the tile
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (x * TILE_SIZE) + (TILE_SIZE - fm.stringWidth(symbol)) / 2;
                    int textY = (y * TILE_SIZE) + ((TILE_SIZE - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(symbol, textX, textY);
                }
                 // If it's a planted crop, you might want to draw a small symbol or image
                 if (tile.getType() == Tile.TileType.PLANTED && tile.getPlantedCrop() != null) {
                    g2d.setColor(CUSTOM_DARK_GREEN); // Use the custom defined dark green
                    g2d.drawString("C", x * TILE_SIZE + 5, y * TILE_SIZE + TILE_SIZE - 5);
                }
            }
        }

        // Draw the player. Ensure player is drawn last so it's on top of tiles.
        g2d.setColor(PLAYER_COLOR);
        g2d.fillOval(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE); // Draw player as a circle
        g2d.setColor(Color.WHITE);
        g2d.drawString("P", player.getX() * TILE_SIZE + 5, player.getY() * TILE_SIZE + TILE_SIZE - 5); // Label player with 'P'
    }

    // Method to request a redraw of the map when game state changes
    public void refreshMap() {
        repaint(); // This tells Swing to call paintComponent again soon
    }
}