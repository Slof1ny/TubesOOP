package gui;

import javax.swing.*;
import java.awt.*;
import core.world.GameMap; // Use GameMap interface
import core.world.Tile;
import core.player.Player;
import system.GameManager; // For accessing current map which should be HouseMap

public class HouseMapPanel extends JPanel {
    private GameManager gameManager; // To get current map and player

    // Define colors for house elements
    private static final Color FLOOR_COLOR = new Color(210, 180, 140); // Tan/Wood
    private static final Color WALL_COLOR = new Color(139, 69, 19);   // Brown
    private static final Color BED_COLOR = new Color(135, 206, 250); // Light Blue
    private static final Color TV_COLOR = new Color(105, 105, 105);    // Dim Gray
    private static final Color EXIT_COLOR = new Color(0,100,0);       // Dark Green
    private static final Color PLAYER_COLOR = Color.RED;

    private final int TILE_SIZE = 25; // Can be different from FarmMapPanel

    public HouseMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        setPreferredSize(new Dimension(core.world.HouseMap.SIZE * TILE_SIZE, core.world.HouseMap.SIZE * TILE_SIZE));
        setBackground(Color.LIGHT_GRAY); // Fallback background

        setFocusable(true);
        addKeyListener(new HouseMapController(gameManager, this, gameView)); // Controller to be created next
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GameMap currentMap = gameManager.getHouseMap(); // We know this panel is for HouseMap
        Player player = gameManager.getPlayer();

        if (currentMap == null || !player.getLocation().equals(currentMap.getName())) {
            // If player is not in the house, don't draw house details
            // Or draw a "not in house" message
            g2d.setColor(Color.BLACK);
            g2d.drawString("Player is not currently in the house.", 50, 50);
            return;
        }

        for (int y = 0; y < currentMap.getSize(); y++) {
            for (int x = 0; x < currentMap.getSize(); x++) {
                Tile tile = currentMap.getTileAt(x, y);
                if (tile == null) continue;

                Color tileColor = FLOOR_COLOR; // Default floor
                char displayChar = tile.displayChar();

                if (tile.getType() == Tile.TileType.DEPLOYED) {
                    if (displayChar == 'B') tileColor = BED_COLOR;
                    else if (displayChar == 'T') tileColor = TV_COLOR;
                    else if (displayChar == 'X') tileColor = EXIT_COLOR;
                    // else if (displayChar == '#') tileColor = WALL_COLOR; // If you add other deployed objects like walls
                    else tileColor = WALL_COLOR; // Default for other deployed objects (like simple walls)
                }
                // Add logic for walls if not using DEPLOYED for them
                // else if (x == 0 || x == currentMap.getSize() - 1 || y == 0 || y == currentMap.getSize() - 1){
                //    tileColor = WALL_COLOR; // Simple border walls
                // }


                g2d.setColor(tileColor);
                g2d.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                g2d.setColor(Color.DARK_GRAY); // Grid lines
                g2d.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (tile.getType() == Tile.TileType.DEPLOYED && displayChar != ' ') {
                     g2d.setColor(Color.BLACK);
                     FontMetrics fm = g2d.getFontMetrics();
                     int textX = (x * TILE_SIZE) + (TILE_SIZE - fm.stringWidth(String.valueOf(displayChar))) / 2;
                     int textY = (y * TILE_SIZE) + ((TILE_SIZE - fm.getHeight()) / 2) + fm.getAscent();
                     g2d.drawString(String.valueOf(displayChar), textX, textY);
                }
            }
        }

        // Draw the player if they are on this map
        if (player.getLocation().equals(currentMap.getName())) {
            g2d.setColor(PLAYER_COLOR);
            g2d.fillOval(player.getX() * TILE_SIZE + TILE_SIZE/4, player.getY() * TILE_SIZE + TILE_SIZE/4, TILE_SIZE/2, TILE_SIZE/2); // Smaller player oval
        }
    }

    public void refreshMap() {
        repaint();
    }
}