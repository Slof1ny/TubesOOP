//  TubesOOP/src/gui/FarmMapPanel.java
// Path: TubesOOP/src/gui/FarmMapPanel.java
package gui;

import javax.swing.*;
import java.awt.*;
import core.world.FarmMap;
import core.world.Tile;
import core.player.Player;
import system.GameManager; // Import GameManager
// import time.Time; // No longer needed directly by constructor
// import time.GameCalendar; // No longer needed directly by constructor
// import gui.PlayerInfoPanel; // No longer needed directly by constructor


public class FarmMapPanel extends JPanel {
    // private FarmMap farmMap; // Will get from GameManager
    // private Player player; // Will get from GameManager
    private GameManager gameManager; // Add GameManager

    private static final Color UNTILLED_COLOR = new Color(139, 69, 19); //
    private static final Color TILLED_COLOR = new Color(101, 67, 33); //
    private static final Color PLANTED_COLOR = new Color(50, 150, 50); //
    private static final Color DEPLOYED_COLOR_HOUSE = new Color(150, 75, 0); //
    private static final Color DEPLOYED_COLOR_POND = new Color(0, 100, 200); //
    private static final Color DEPLOYED_COLOR_SHIPPINGBIN = new Color(100, 50, 0); //
    private static final Color PLAYER_COLOR = new Color(255, 0, 0); //
    private static final Color DEPLOYED_COLOR = Color.LIGHT_GRAY; //
    private static final Color CUSTOM_DARK_GREEN = new Color(0, 100, 0); //

    // REMOVE fixed TILE_SIZE
    // private final int TILE_SIZE = 20;

    // Modify constructor
    public FarmMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager; // Store the GameManager instance

        setBackground(Color.LIGHT_GRAY); //
        setFocusable(true); //

        // Instantiate FarmMapController passing GameManager, this panel, and GameView
        // This is likely the corrected line 46 from your screenshot context.
        addKeyListener(new FarmMapController(gameManager, this, gameView));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        FarmMap currentFarmMap = gameManager.getFarmMap();
        Player currentPlayer = gameManager.getPlayer();

        if (!gameManager.getCurrentMap().getName().equals(currentFarmMap.getName())) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int mapSize = FarmMap.SIZE; // FarmMap.SIZE is static

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
                Tile tile = currentFarmMap.getTileAt(x, y);
                Color tileColor;

                switch (tile.getType()) {
                    case UNTILLED: tileColor = UNTILLED_COLOR; break; //
                    case TILLED: tileColor = TILLED_COLOR; break; //
                    case PLANTED: tileColor = PLANTED_COLOR; break; //
                    case DEPLOYED: //
                        char deployedChar = tile.displayChar(); //
                        if (deployedChar == 'h') tileColor = DEPLOYED_COLOR_HOUSE; //
                        else if (deployedChar == 'o') tileColor = DEPLOYED_COLOR_POND; //
                        else if (deployedChar == 's') tileColor = DEPLOYED_COLOR_SHIPPINGBIN; //
                        else tileColor = DEPLOYED_COLOR; //
                        break;
                    default: tileColor = Color.WHITE; //
                }

                g2d.setColor(tileColor);
                g2d.fillRect(offsetX + x * actualTileSize, offsetY + y * actualTileSize, actualTileSize, actualTileSize);

                g2d.setColor(Color.BLACK);
                g2d.drawRect(offsetX + x * actualTileSize, offsetY + y * actualTileSize, actualTileSize, actualTileSize);

                // Adjust font size for symbols
                int fontSize = Math.max(8, actualTileSize / 2);
                Font symbolFont = new Font(g2d.getFont().getName(), Font.BOLD, fontSize);

                if (tile.getType() == Tile.TileType.DEPLOYED) { //
                    g2d.setColor(Color.WHITE); //
                    String symbol = String.valueOf(tile.displayChar()); //
                    g2d.setFont(symbolFont);
                    FontMetrics fm = g2d.getFontMetrics(); //
                    int textX = (offsetX + x * actualTileSize) + (actualTileSize - fm.stringWidth(symbol)) / 2; //
                    int textY = (offsetY + y * actualTileSize) + ((actualTileSize - fm.getHeight()) / 2) + fm.getAscent(); //
                    g2d.drawString(symbol, textX, textY); //
                }
                if (tile.getType() == Tile.TileType.PLANTED && tile.getPlantedCrop() != null) { //
                    g2d.setColor(CUSTOM_DARK_GREEN); //
                    String cropSymbol = "C"; // Or get a symbol from crop stage
                    g2d.setFont(symbolFont);
                    FontMetrics fm = g2d.getFontMetrics();
                    int textX = (offsetX + x * actualTileSize) + (actualTileSize - fm.stringWidth(cropSymbol)) / 2;
                    int textY = (offsetY + y * actualTileSize) + ((actualTileSize - fm.getHeight()) / 2) + fm.getAscent();
                    g2d.drawString(cropSymbol, textX, textY); //
                }
            }
        }
        
        if (currentPlayer.getLocation().equals(currentFarmMap.getName())) {
            g2d.setColor(PLAYER_COLOR); //
            int playerOvalSize = actualTileSize * 3 / 4;
            int playerOffsetX = (actualTileSize - playerOvalSize) / 2;
            int playerOffsetY = (actualTileSize - playerOvalSize) / 2;
            g2d.fillOval(offsetX + currentPlayer.getX() * actualTileSize + playerOffsetX,
                         offsetY + currentPlayer.getY() * actualTileSize + playerOffsetY,
                         playerOvalSize, playerOvalSize);

            g2d.setColor(Color.WHITE); //
            String playerSymbol = "P";
            int playerFontSize = Math.max(8, actualTileSize / 2);
            g2d.setFont(new Font(g2d.getFont().getName(), Font.BOLD, playerFontSize));
            FontMetrics fm = g2d.getFontMetrics();
            int textX = (offsetX + currentPlayer.getX() * actualTileSize) + (actualTileSize - fm.stringWidth(playerSymbol)) / 2;
            int textY = (offsetY + currentPlayer.getY() * actualTileSize) + ((actualTileSize - fm.getHeight()) / 2) + fm.getAscent();
            g2d.drawString(playerSymbol, textX, textY); //
        }
    }

    public void refreshMap() {
        repaint(); //
    }
}