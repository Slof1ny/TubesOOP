package gui;

import javax.swing.*;
import java.awt.*;
import core.world.GameMap;
import core.world.Tile;
import core.player.Player;
import core.world.DeployedObject;
import system.GameManager; // Import GameManager
import core.world.CityMap; // For specific building symbols, though generally use GameMap

public class CityMapPanel extends JPanel {
    private GameManager gameManager;
    private GameMap cityMap;
    private Player player;

    // Define colors for different city tile types and player
    private static final Color ROAD_COLOR = new Color(128, 128, 128); // Gray for roads/walkable areas
    private static final Color BUILDING_COLOR = new Color(160, 82, 45); // Sienna for generic buildings
    private static final Color PLAYER_COLOR = new Color(255, 0, 0); // Red for player
    private static final Color ORENJI_CAFE_COLOR = new Color(255, 140, 0);

    // Define specific colors for deployed objects if desired (optional, can use generic BUILDING_COLOR)
    private static final Color STORE_COLOR = new Color(255, 165, 0); // Orange for store
    private static final Color MAYOR_MANOR_COLOR = new Color(139, 0, 0); // Dark Red for Mayor's Manor
    private static final Color EXIT_COLOR = new Color(0, 150, 0); // Green for exit

    private final int TILE_SIZE = 20;

    public CityMapPanel(GameManager gameManager, GameView gameView) { // Constructor now takes GameManager and GameView
        this.gameManager = gameManager;
        this.cityMap = gameManager.getCityMap(); // Get CityMap from GameManager
        this.player = gameManager.getPlayer(); // Get Player from GameManager

        setPreferredSize(new Dimension(cityMap.getSize() * TILE_SIZE, cityMap.getSize() * TILE_SIZE));
        setBackground(Color.DARK_GRAY);

        setFocusable(true);
        // Add the controller for CityMap
        addKeyListener(new CityMapController(gameManager, this, gameView));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Ensure current map is CityMap before drawing
        if (!gameManager.getCurrentMap().getName().equals(cityMap.getName())) {
            return;
        }

        for (int y = 0; y < cityMap.getSize(); y++) {
            for (int x = 0; x < cityMap.getSize(); x++) {
                Tile tile = cityMap.getTileAt(x, y);
                Color tileColor;

                if (tile.getType() == Tile.TileType.DEPLOYED) {
                    char deployedChar = tile.displayChar();
                    switch (deployedChar) {
                        case 'S': // Emily's Store
                            tileColor = STORE_COLOR;
                            break;
                        case 'O': 
                            tileColor = ORENJI_CAFE_COLOR; 
                            break;
                        case 'M': // Mayor's Manor
                            tileColor = MAYOR_MANOR_COLOR;
                            break;
                        case 'X': // Exit to Farm
                            tileColor = EXIT_COLOR;
                            break;
                        default:
                            tileColor = BUILDING_COLOR; // Generic building color for others
                            break;
                    }
                } else {
                    tileColor = ROAD_COLOR; // Default for walkable areas
                }

                g2d.setColor(tileColor);
                g2d.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                g2d.setColor(Color.BLACK);
                g2d.drawRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (tile.getType() == Tile.TileType.DEPLOYED) {
                    g2d.setColor(Color.WHITE);
                    String symbol = String.valueOf(tile.displayChar());
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (x * TILE_SIZE) + (TILE_SIZE - fm.stringWidth(symbol)) / 2;
                    int textY = (y * TILE_SIZE) + ((TILE_SIZE - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(symbol, textX, textY);
                }
            }
        }

        // Draw the player only if they are on this map
        if (player.getLocation().equals(cityMap.getName())) {
            g2d.setColor(PLAYER_COLOR);
            g2d.fillOval(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            g2d.setColor(Color.WHITE);
            g2d.drawString("P", player.getX() * TILE_SIZE + 5, player.getY() * TILE_SIZE + TILE_SIZE - 5);
        }
    }

    public void refreshMap() {
        repaint();
    }
}