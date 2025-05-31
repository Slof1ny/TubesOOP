// Path: TubesOOP/src/gui/HouseMapPanel.java
package gui;

import javax.swing.*;
import java.awt.*;
import core.world.GameMap;
import core.world.Tile;
import core.player.Player;
import system.GameManager;
import core.world.HouseMap; // For static HouseMap.SIZE

public class HouseMapPanel extends JPanel {
    private GameManager gameManager;

    private static final Color FLOOR_COLOR = new Color(210, 180, 140); //
    private static final Color WALL_COLOR = new Color(139, 69, 19);   //
    private static final Color BED_COLOR = new Color(135, 206, 250); //
    private static final Color TV_COLOR = new Color(105, 105, 105);    //
    private static final Color STOVE_COLOR = new Color(128,128,128); // Example: Gray for stove
    private static final Color EXIT_COLOR = new Color(0,100,0);       //
    private static final Color PLAYER_COLOR = Color.RED; //

    // REMOVE fixed TILE_SIZE
    // private final int TILE_SIZE = 25;

    public HouseMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        // Remove setPreferredSize
        // setPreferredSize(new Dimension(core.world.HouseMap.SIZE * TILE_SIZE, core.world.HouseMap.SIZE * TILE_SIZE));
        setBackground(Color.LIGHT_GRAY); //

        setFocusable(true); //
        addKeyListener(new HouseMapController(gameManager, this, gameView)); //
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GameMap currentHouseMap = gameManager.getHouseMap();
        Player currentPlayer = gameManager.getPlayer();

        if (currentHouseMap == null || !currentPlayer.getLocation().equals(currentHouseMap.getName())) { //
            g2d.setColor(Color.BLACK); //
            g2d.drawString("Player is not currently in the house.", 50, 50); //
            return; //
        }
        
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int mapSize = HouseMap.SIZE; // HouseMap.SIZE is static

        if (mapSize == 0) return;

        int tileW = panelWidth / mapSize;
        int tileH = panelHeight / mapSize;
        int actualTileSize = Math.min(tileW, tileH);
        if (actualTileSize < 1) actualTileSize = 1;

        int totalMapRenderWidth = actualTileSize * mapSize;
        int totalMapRenderHeight = actualTileSize * mapSize;
        int offsetX = (panelWidth - totalMapRenderWidth) / 2;
        int offsetY = (panelHeight - totalMapRenderHeight) / 2;

        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                Tile tile = currentHouseMap.getTileAt(x, y);
                if (tile == null) continue; //

                Color tileColor = FLOOR_COLOR; //
                char displayChar = tile.displayChar(); //

                if (tile.getType() == Tile.TileType.DEPLOYED) { //
                    if (displayChar == 'B') tileColor = BED_COLOR; //
                    else if (displayChar == 'T') tileColor = TV_COLOR; //
                    else if (displayChar == 'S') tileColor = STOVE_COLOR; // 'S' for Stove
                    else if (displayChar == 'X') tileColor = EXIT_COLOR; //
                    else tileColor = WALL_COLOR; //
                }
                // Simple boundary walls (can be made more sophisticated)
                else if (x == 0 || x == mapSize - 1 || y == 0 || y == mapSize - 1) {
                    // Check if it's an exit tile first
                    boolean isExitTile = (x == HouseMap.EXIT_LOCATION.x && y == HouseMap.EXIT_LOCATION.y);
                    if (!isExitTile || displayChar != 'X') { // Don't draw wall over explicit exit
                         tileColor = WALL_COLOR;
                    }
                }


                g2d.setColor(tileColor);
                g2d.fillRect(offsetX + x * actualTileSize, offsetY + y * actualTileSize, actualTileSize, actualTileSize);

                g2d.setColor(Color.DARK_GRAY); //
                g2d.drawRect(offsetX + x * actualTileSize, offsetY + y * actualTileSize, actualTileSize, actualTileSize);

                if (tile.getType() == Tile.TileType.DEPLOYED && displayChar != ' ') { //
                     g2d.setColor(Color.BLACK); //
                     int fontSize = Math.max(8, actualTileSize / 2);
                     g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, fontSize));
                     FontMetrics fm = g2d.getFontMetrics(); //
                     int textX = (offsetX + x * actualTileSize) + (actualTileSize - fm.stringWidth(String.valueOf(displayChar))) / 2; //
                     int textY = (offsetY + y * actualTileSize) + ((actualTileSize - fm.getHeight()) / 2) + fm.getAscent(); //
                     g2d.drawString(String.valueOf(displayChar), textX, textY); //
                }
            }
        }

        if (currentPlayer.getLocation().equals(currentHouseMap.getName())) { //
            g2d.setColor(PLAYER_COLOR); //
            int playerOvalSize = actualTileSize * 3 / 4; 
            int playerOffsetX = (actualTileSize - playerOvalSize) / 2;
            int playerOffsetY = (actualTileSize - playerOvalSize) / 2;
            g2d.fillOval(offsetX + currentPlayer.getX() * actualTileSize + playerOffsetX, 
                         offsetY + currentPlayer.getY() * actualTileSize + playerOffsetY, 
                         playerOvalSize, playerOvalSize); //
        }
    }

    public void refreshMap() {
        repaint(); //
    }
}