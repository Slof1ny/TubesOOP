// TubesOOP/src/gui/FarmMapPanel.java
package gui;

import javax.swing.*;
import java.awt.*;

import core.world.FarmMap;
import core.world.Tile;
import core.player.Player;

import time.Time;
import time.GameCalendar;

public class FarmMapPanel extends JPanel {
    private FarmMap farmMap;
    private Player player;

    private static final Color UNTILLED_COLOR = new Color(139, 69, 19);
    private static final Color TILLED_COLOR = new Color(101, 67, 33);
    private static final Color PLANTED_COLOR = new Color(50, 150, 50);
    private static final Color DEPLOYED_COLOR_HOUSE = new Color(150, 75, 0);
    private static final Color DEPLOYED_COLOR_POND = new Color(0, 100, 200);
    private static final Color DEPLOYED_COLOR_SHIPPINGBIN = new Color(100, 50, 0);
    private static final Color PLAYER_COLOR = new Color(255, 0, 0);

    private static final Color DEPLOYED_COLOR = Color.LIGHT_GRAY;

    private static final Color CUSTOM_DARK_GREEN = new Color(0, 100, 0);

    private final int TILE_SIZE = 20;

    // Modify constructor to accept Time, GameCalendar, PlayerInfoPanel, and GameView
    public FarmMapPanel(FarmMap farmMap, Player player, Time gameTime, GameCalendar gameCalendar, PlayerInfoPanel playerInfoPanel, GameView gameView) { // MODIFIED LINE
        this.farmMap = farmMap;
        this.player = player;

        setPreferredSize(new Dimension(FarmMap.SIZE * TILE_SIZE, FarmMap.SIZE * TILE_SIZE));
        setBackground(Color.LIGHT_GRAY);

        setFocusable(true);
        // Add the controller, passing all necessary arguments, including gameView
        addKeyListener(new FarmMapController(player, farmMap, this, gameTime, gameCalendar, playerInfoPanel, gameView)); // MODIFIED LINE
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int y = 0; y < FarmMap.SIZE; y++) {
            for (int x = 0; x < FarmMap.SIZE; x++) {
                Tile tile = farmMap.getTileAt(x, y);
                Color tileColor;

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
                        char deployedChar = tile.displayChar();
                        if (deployedChar == 'h') {
                            tileColor = DEPLOYED_COLOR_HOUSE;
                        } else if (deployedChar == 'o') {
                            tileColor = DEPLOYED_COLOR_POND;
                        } else if (deployedChar == 's') {
                            tileColor = DEPLOYED_COLOR_SHIPPINGBIN;
                        } else {
                            tileColor = DEPLOYED_COLOR;
                        }
                        break;
                    default:
                        tileColor = Color.WHITE;
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
                if (tile.getType() == Tile.TileType.PLANTED && tile.getPlantedCrop() != null) {
                    g2d.setColor(CUSTOM_DARK_GREEN);
                    g2d.drawString("C", x * TILE_SIZE + 5, y * TILE_SIZE + TILE_SIZE - 5);
                }
            }
        }

        g2d.setColor(PLAYER_COLOR);
        g2d.fillOval(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g2d.setColor(Color.WHITE);
        g2d.drawString("P", player.getX() * TILE_SIZE + 5, player.getY() * TILE_SIZE + TILE_SIZE - 5);
    }

    public void refreshMap() {
        repaint();
    }
}