// Path: TubesOOP/src/gui/HouseMapPanel.java
package gui;

import javax.swing.*;
import java.awt.*;
import core.world.GameMap;
import core.world.Tile;
import core.player.Player;
import system.GameManager;
import core.world.HouseMap; // For static HouseMap.SIZE


import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class HouseMapPanel extends JPanel {
    private GameManager gameManager;
    private Map<Character, BufferedImage> assetImages = new HashMap<>();
    private BufferedImage floorImage;
    private BufferedImage borderImage;
    private BufferedImage playerImage;

    public HouseMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        setBackground(Color.LIGHT_GRAY);
        setFocusable(true);
        addKeyListener(new HouseMapController(gameManager, this, gameView));
        loadAssetImages();
    }

    private void loadAssetImages() {
        floorImage = loadImage("resources/asset/png/WoodFloor.png", "Floor");
        borderImage = loadImage("resources/asset/png/WoodFloor_type2.png", "Border");
        assetImages.put('B', loadImage("resources/asset/png/Bed.png", "Bed"));
        assetImages.put('S', loadImage("resources/asset/png/Stove.png", "Stove"));
        assetImages.put('T', loadImage("resources/asset/png/TV.png", "TV"));
        // Player asset (same as CityMapPanel/FarmMapPanel)
        playerImage = loadImage("resources/asset/png/PlayerBoy_idle.png", "PlayerBoy_idle.png");
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        GameMap currentHouseMap = gameManager.getHouseMap();
        Player currentPlayer = gameManager.getPlayer();

        if (currentHouseMap == null || !currentPlayer.getLocation().equals(currentHouseMap.getName())) {
            g2d.setColor(Color.BLACK);
            g2d.drawString("Player is not currently in the house.", 50, 50);
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int mapSize = HouseMap.SIZE;
        if (mapSize == 0) return;

        int tileW = panelWidth / mapSize;
        int tileH = panelHeight / mapSize;
        int actualTileSize = Math.min(tileW, tileH);
        if (actualTileSize < 1) actualTileSize = 1;

        int totalMapRenderWidth = actualTileSize * mapSize;
        int totalMapRenderHeight = actualTileSize * mapSize;
        int offsetX = (panelWidth - totalMapRenderWidth) / 2;
        int offsetY = (panelHeight - totalMapRenderHeight) / 2;

        // Draw floor and border first
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                int drawX = offsetX + x * actualTileSize;
                int drawY = offsetY + y * actualTileSize;
                boolean isBorder = (x == 0 || x == mapSize - 1 || y == 0 || y == mapSize - 1);
                boolean isExit = (x == HouseMap.EXIT_LOCATION.x && y == HouseMap.EXIT_LOCATION.y);
                if (isBorder && !isExit) {
                    if (borderImage != null) {
                        g2d.drawImage(borderImage, drawX, drawY, actualTileSize, actualTileSize, null);
                    } else {
                        g2d.setColor(new Color(139, 69, 19));
                        g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize);
                    }
                } else {
                    if (floorImage != null) {
                        g2d.drawImage(floorImage, drawX, drawY, actualTileSize, actualTileSize, null);
                    } else {
                        g2d.setColor(new Color(210, 180, 140));
                        g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize);
                    }
                }
            }
        }

        // Draw assets (bed, stove, tv, exit, etc.)
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                Tile tile = currentHouseMap.getTileAt(x, y);
                if (tile == null) continue;
                char displayChar = tile.displayChar();
                int drawX = offsetX + x * actualTileSize;
                int drawY = offsetY + y * actualTileSize;

                // Bed and Stove are 1x2 (draw only at origin tile)
                if (displayChar == 'B') {
                    // Only draw at the top tile of the bed (origin)
                    if (y == HouseMap.BED_LOCATION.y && x == HouseMap.BED_LOCATION.x) {
                        BufferedImage bedImg = assetImages.get('B');
                        if (bedImg != null) {
                            g2d.drawImage(bedImg, drawX, drawY, actualTileSize, actualTileSize * 2, null);
                        } else {
                            g2d.setColor(new Color(135, 206, 250));
                            g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize * 2);
                        }
                    }
                } else if (displayChar == 'S') {
                    // Only draw at the top tile of the stove (origin)
                    if (y == HouseMap.STOVE_LOCATION.y && x == HouseMap.STOVE_LOCATION.x) {
                        BufferedImage stoveImg = assetImages.get('S');
                        if (stoveImg != null) {
                            g2d.drawImage(stoveImg, drawX, drawY, actualTileSize, actualTileSize * 2, null);
                        } else {
                            g2d.setColor(new Color(128, 128, 128));
                            g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize * 2);
                        }
                    }
                } else if (displayChar == 'T') {
                    BufferedImage tvImg = assetImages.get('T');
                    if (tvImg != null) {
                        g2d.drawImage(tvImg, drawX, drawY, actualTileSize, actualTileSize, null);
                    } else {
                        g2d.setColor(new Color(105, 105, 105));
                        g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize);
                    }
                } else if (displayChar == 'X') {
                    // Exit: just draw a green rectangle or a door image if you have one
                    g2d.setColor(new Color(0, 100, 0));
                    g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize);
                }

                // Draw grid border for all tiles
                g2d.setColor(Color.DARK_GRAY);
                g2d.drawRect(drawX, drawY, actualTileSize, actualTileSize);
            }
        }

        // Draw player
        if (currentPlayer.getLocation().equals(currentHouseMap.getName())) {
            int px = currentPlayer.getX();
            int py = currentPlayer.getY();
            int drawX = offsetX + px * actualTileSize;
            int drawY = offsetY + py * actualTileSize;
            if (playerImage != null) {
                g2d.drawImage(playerImage, drawX, drawY, actualTileSize, actualTileSize, null);
            } else {
                g2d.setColor(Color.RED);
                g2d.fillOval(drawX, drawY, actualTileSize, actualTileSize);
            }
        }
    }

    public void refreshMap() {
        repaint(); //
    }
        /**
     * Helper to load a PNG from an absolute file path first, then fallback to classpath resource.
     * @param path The file path or resource path (e.g., "resources/asset/png/image.png").
     * @param nameForLog A descriptive name for logging purposes.
     * @return The loaded BufferedImage, or null if loading failed.
     */
    private BufferedImage loadImage(String path, String nameForLog) {
        // Try to load from absolute file path first (outside rootpath, e.g. for development override)
        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            try {
                return javax.imageio.ImageIO.read(file);
            } catch (Exception e) {
                System.err.println("Error loading image from file: " + path + " (" + nameForLog + "): " + e.getMessage());
            }
        }
        // Fallback: try to load from classpath (inside jar/resources)
        java.io.InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Warning: Image not found: " + nameForLog + " (Path: " + path + ")");
            return null;
        }
        try {
            return javax.imageio.ImageIO.read(is);
        } catch (Exception e) {
            System.err.println("Error loading image from resource: " + path + " (" + nameForLog + "): " + e.getMessage());
            return null;
        } finally {
            try { is.close(); } catch (Exception e) { System.err.println("Error closing stream for " + nameForLog + ": " + e.getMessage()); }
        }
    }
}