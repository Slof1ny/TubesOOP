package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet; // Not explicitly used yet, but good for tracking drawn objects if needed
import java.util.Set;

import core.world.FarmMap;
import core.world.Tile;
import core.player.Player;
import system.GameManager;
import core.world.DeployedObject; // Import DeployedObject
import item.Crop; // Import Crop

public class FarmMapPanel extends JPanel {
    private final GameManager gameManager;
    private final FarmMap farmMap; // Reference to the FarmMap
    private final Player player; // Reference to the Player

    private final Map<Character, BufferedImage> terrainImages = new HashMap<>();
    private final Map<Character, BufferedImage> objectImages = new HashMap<>(); // For house, pond, shipping bin, fences, exit
    private final Map<String, BufferedImage> cropImages = new HashMap<>(); // For planted crops by name
    private BufferedImage playerImage;

    private static final int BASE_ASSET_SIZE = 16; // All assets are assumed to be designed for 16x16 pixels

    public FarmMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        this.farmMap = gameManager.getFarmMap(); // Get FarmMap from GameManager
        this.player = gameManager.getPlayer(); // Get Player from GameManager

        setPreferredSize(new Dimension(farmMap.getSize() * BASE_ASSET_SIZE, farmMap.getSize() * BASE_ASSET_SIZE));
        setBackground(Color.BLACK); // Default background
        setFocusable(true);
        loadImages(); // Load all images at initialization
        addKeyListener(new FarmMapController(gameManager, this, gameView));
    }

    /**
     * Load all necessary images (terrain, generic objects, building sprites, player sprite).
     */
    private void loadImages() {
        try {
            // ---------------------
            // Terrain images: (16x16)
            // ---------------------
            terrainImages.put(Tile.DEFAULT_UNTILLED_CHAR, loadImage("resources/asset/png/Soil_UNTILLED.png", "Soil_UNTILLED.png"));
            terrainImages.put(Tile.TILLED_CHAR, loadImage("resources/asset/png/Soil_TILLED.png", "Soil_TILLED.png"));
            terrainImages.put(Tile.PLANTED_CHAR, loadImage("resources/asset/png/Soil_ThrownSeeds.png", "Soil_ThrownSeeds.png")); 
            terrainImages.put(Tile.RUMPUT_HIJAU_SYMBOL, loadImage("resources/asset/png/JalanHijau-16px.png", "JalanHijau-16px.png"));
            terrainImages.put(Tile.BATU_SYMBOL, loadImage("resources/asset/png/Stone_Round.png", "Stone_Round.png"));
            terrainImages.put(Tile.AIR_SYMBOL, loadImage("resources/asset/png/Kolam_air.png", "Kolam_air.png")); // For general water/pond
            terrainImages.put(Tile.BUNGA_PINK_SYMBOL, loadImage("resources/asset/png/BungaPink.png", "BungaPink.png"));
            terrainImages.put(Tile.TANAH_SYMBOL, loadImage("resources/asset/png/Tanah-16px.png", "Tanah-16px.png"));

            // Ensure fallbacks for terrain
            for (char key : new char[]{Tile.DEFAULT_UNTILLED_CHAR, Tile.TILLED_CHAR, Tile.PLANTED_CHAR,
                                       Tile.RUMPUT_HIJAU_SYMBOL, Tile.BATU_SYMBOL, Tile.AIR_SYMBOL,
                                       Tile.BUNGA_PINK_SYMBOL, Tile.TANAH_SYMBOL}) {
                if (!terrainImages.containsKey(key) || terrainImages.get(key) == null) {
                    terrainImages.put(key, createFallbackImage(getFallbackColorForSymbol(key)));
                }
            }

            // ---------------------
            // Player image: (16x16)
            // ---------------------
            // Use the same logic as CityMapPanel for player asset
            playerImage = loadImage("resources/asset/png/PlayerBoy_idle.png", "PlayerBoy_idle.png");
            if (playerImage == null) {
                playerImage = createFallbackImage(Color.RED);
            }

            // ---------------------
            // Deployed objects (House, Pond, Shipping Bin, Fence, Exit): (16x16 or multi-tile)
            // ---------------------
            objectImages.put(Tile.FARM_HOUSE_SYMBOL, loadImage("resources/asset/png/House_Player.png", "Farm House"));
            objectImages.put(Tile.POND_SYMBOL, loadImage("resources/asset/png/Kolam_air.png", "Farm Pond")); // Reusing water image for pond object
            objectImages.put(Tile.SHIPPING_BIN_SYMBOL, loadImage("resources/asset/png/ShippingBin_Closed.png", "Shipping Bin"));
            objectImages.put(Tile.FENCE_SYMBOL, loadImage("resources/asset/png/Fence.png", "Fence"));
            objectImages.put(Tile.EXIT_SYMBOL, loadImage("resources/asset/png/Door.png", "Exit Door")); // For exit to city

            // Ensure fallbacks for objects
            for (char key : new char[]{Tile.FARM_HOUSE_SYMBOL, Tile.POND_SYMBOL, Tile.SHIPPING_BIN_SYMBOL,
                                       Tile.FENCE_SYMBOL, Tile.EXIT_SYMBOL}) {
                if (!objectImages.containsKey(key) || objectImages.get(key) == null) {
                    objectImages.put(key, createFallbackImage(getFallbackColorForSymbol(key)));
                }
            }

            // ---------------------
            // Crop images: (16x16) - mapped by crop name
            // ---------------------
            cropImages.put("Blueberry", loadImage("resources/asset/png/Blueberry.png", "Blueberry"));
            cropImages.put("Cauliflower", loadImage("resources/asset/png/Cauliflower.png", "Cauliflower"));
            cropImages.put("Cranberry", loadImage("resources/asset/png/Cranberry.png", "Cranberry"));
            cropImages.put("Grapes", loadImage("resources/asset/png/Grapes.png", "Grapes"));
            cropImages.put("Hot Pepper", loadImage("resources/asset/png/Hot_Pepper.png", "Hot Pepper"));
            cropImages.put("Melon", loadImage("resources/asset/png/Melon.png", "Melon"));
            cropImages.put("Parsnip", loadImage("resources/asset/png/Parsnip.png", "Parsnip"));
            cropImages.put("Potato", loadImage("resources/asset/png/Potato.png", "Potato"));
            cropImages.put("Pumpkin", loadImage("resources/asset/png/Pumpkin.png", "Pumpkin"));
            cropImages.put("Tomato", loadImage("resources/asset/png/Tomato.png", "Tomato"));
            cropImages.put("Wheat", loadImage("resources/asset/png/Wheat.png", "Wheat"));

            // Ensure fallbacks for crops
            for (String cropName : cropImages.keySet()) { // Iterate over keys to check if values are null
                if (cropImages.get(cropName) == null) {
                    cropImages.put(cropName, createFallbackImage(new Color(50, 150, 50))); // Generic green for crops
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Critical Error loading images for FarmMapPanel: " + e.getMessage());
            // Provide minimal fallbacks so the panel still draws something
            // Terrain fallbacks (already handled in loop above)
            // Player fallback
            playerImage = createFallbackImage(Color.RED);
            // Object fallbacks (already handled in loop above)
            // Crop fallbacks (already handled in loop above)
        }
    }

    /**
     * Helper to load a PNG from the classpath. Returns null if not found or on error.
     * @param path The classpath resource path (e.g., "resources/asset/png/image.png").
     * @param nameForLog A descriptive name for logging purposes.
     * @return The loaded BufferedImage, or null if loading failed.
     */
    private BufferedImage loadImage(String path, String nameForLog) throws IOException {
        // Try to load from absolute file path first (outside rootpath, e.g. for development override)
        java.io.File file = new java.io.File(path);
        if (file.exists()) {
            try {
                return ImageIO.read(file);
            } catch (IOException e) {
                System.err.println("Error loading image from file: " + path + " (" + nameForLog + "): " + e.getMessage());
            }
        }
        // Fallback: try to load from classpath (inside jar/resources)
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            System.err.println("Warning: Image not found: " + nameForLog + " (Path: " + path + ")");
            return null;
        }
        try {
            return ImageIO.read(is);
        } finally {
            try { is.close(); } catch (IOException e) { System.err.println("Error closing stream for " + nameForLog + ": " + e.getMessage()); }
        }
    }

    /**
     * Creates a simple solid-color square for fallback if an image fails to load.
     * Default size is BASE_ASSET_SIZE.
     */
    private BufferedImage createFallbackImage(Color c) {
        return createFallbackImage(c, BASE_ASSET_SIZE, BASE_ASSET_SIZE);
    }

    /**
     * Creates a simple solid-color rectangle of specified size for fallback.
     * @param c The color for the fallback.
     * @param width The width in pixels.
     * @param height The height in pixels.
     * @return The created BufferedImage.
     */
    private BufferedImage createFallbackImage(Color c, int width, int height) {
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setColor(c);
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.BLACK);
        g2.drawRect(0, 0, width - 1, height - 1);
        g2.dispose();
        return img;
    }

    /**
     * Returns a “fallback” color for a given symbol, used if its image fails.
     * Adapted for FarmMap specific symbols.
     */
    private Color getFallbackColorForSymbol(char symbol) {
        switch (symbol) {
            // FarmMap specific symbols
            case Tile.FARM_HOUSE_SYMBOL: return new Color(200, 100, 50); // Farm house
            case Tile.POND_SYMBOL: return new Color(0, 0, 150);   // Pond
            case Tile.SHIPPING_BIN_SYMBOL: return new Color(100, 100, 0);  // Shipping Bin
            case Tile.FENCE_SYMBOL: return new Color(100, 100, 100); // Fence (dark grey)
            case Tile.EXIT_SYMBOL: return new Color(0, 150, 0);   // Exit (green)

            // Terrain symbols used on FarmMap
            case Tile.DEFAULT_UNTILLED_CHAR: return new Color(139, 69, 19); // Default untilled soil/dirt
            case Tile.TILLED_CHAR: return new Color(101, 67, 33); // Tilled soil
            case Tile.PLANTED_CHAR: return new Color(50, 150, 50); // Generic planted soil
            case Tile.RUMPUT_HIJAU_SYMBOL: return new Color( 80, 160,  80); // Grass
            case Tile.BATU_SYMBOL: return new Color(160, 160, 160); // Stone
            case Tile.AIR_SYMBOL: return new Color(  0,   0, 180); // Water
            case Tile.BUNGA_PINK_SYMBOL: return new Color(255, 100, 255); // Flower
            case Tile.TANAH_SYMBOL: return new Color(139,  69,  19); // Tanah (dirt)

            default:  return new Color(128, 128, 128); // Generic fallback for unknown symbols
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        FarmMap currentFarmMap = gameManager.getFarmMap();
        Player currentPlayer = gameManager.getPlayer();

        // Only draw if this is the current map being displayed
        if (!gameManager.getCurrentMap().getName().equals(currentFarmMap.getName())) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int mapSize = FarmMap.SIZE;

        if (mapSize <= 0) return;

        // Calculate tile size dynamically to fit the panel
        int tileSize = Math.min(panelWidth / mapSize, panelHeight / mapSize);
        if (tileSize < 1) tileSize = 1;

        int totalMapRenderWidth = tileSize * mapSize;
        int totalMapRenderHeight = tileSize * mapSize;
        int offsetX = (panelWidth - totalMapRenderWidth) / 2;
        int offsetY = (panelHeight - totalMapRenderHeight) / 2;

        // 1) Draw every tile’s base terrain or object image
        // --- Pass 1: Draw terrain and all non-multi-tile objects ---
        // We'll skip drawing the house and shipping bin here, and draw them in a separate pass as a single image
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                int drawX = offsetX + x * tileSize;
                int drawY = offsetY + y * tileSize;

                Tile tile = currentFarmMap.getTileAt(x, y);
                if (tile == null) {
                    g2d.setColor(Color.MAGENTA);
                    g2d.fillRect(drawX, drawY, tileSize, tileSize);
                    continue;
                }

                char tileChar = tile.displayChar();
                boolean isHouseOrigin = false;
                boolean isShippingBinOrigin = false;
                // Find house and shipping bin origin (top-left)
                for (DeployedObject obj : farmMap.getDeployedObjects()) {
                    if (obj.getSymbol() == Tile.FARM_HOUSE_SYMBOL && obj.getX() == x && obj.getY() == y) {
                        isHouseOrigin = true;
                    }
                    if (obj.getSymbol() == Tile.SHIPPING_BIN_SYMBOL && obj.getX() == x && obj.getY() == y) {
                        isShippingBinOrigin = true;
                    }
                }

                // Skip drawing house/shipping bin tiles except at their origin
                if ((tileChar == Tile.FARM_HOUSE_SYMBOL && !isHouseOrigin) || (tileChar == Tile.SHIPPING_BIN_SYMBOL && !isShippingBinOrigin)) {
                    // Draw nothing, will be covered by the multi-tile image
                    continue;
                }

                BufferedImage imageToDraw = null;
                if (tile.getType() == Tile.TileType.DEPLOYED) {
                    if (tileChar == Tile.FARM_HOUSE_SYMBOL && isHouseOrigin) {
                        // Will draw in pass 2
                        continue;
                    } else if (tileChar == Tile.SHIPPING_BIN_SYMBOL && isShippingBinOrigin) {
                        // Will draw in pass 2
                        continue;
                    } else {
                        imageToDraw = objectImages.get(tileChar);
                    }
                } else if (tile.getType() == Tile.TileType.PLANTED && tile.getPlantedCrop() != null) {
                    // Draw tilled soil as background
                    BufferedImage tilledSoil = terrainImages.get(Tile.TILLED_CHAR);
                    if (tilledSoil != null) {
                        g2d.drawImage(tilledSoil, drawX, drawY, tileSize, tileSize, null);
                    } else {
                        g2d.setColor(getFallbackColorForSymbol(Tile.TILLED_CHAR));
                        g2d.fillRect(drawX, drawY, tileSize, tileSize);
                    }
                    Crop plantedCrop = tile.getPlantedCrop();
                    imageToDraw = cropImages.get(plantedCrop.getName());
                    if (imageToDraw == null) {
                        imageToDraw = terrainImages.get(Tile.PLANTED_CHAR);
                    }
                } else if (tile.getType() == Tile.TileType.TILLED) {
                    // Draw tilled soil for tilled tiles
                    imageToDraw = terrainImages.get(Tile.TILLED_CHAR);
                } else {
                    imageToDraw = terrainImages.get(tileChar);
                }

                if (imageToDraw != null) {
                    g2d.drawImage(imageToDraw, drawX, drawY, tileSize, tileSize, null);
                } else {
                    g2d.setColor(getFallbackColorForSymbol(tileChar));
                    g2d.fillRect(drawX, drawY, tileSize, tileSize);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawX, drawY, tileSize, tileSize);
            }
        }

        // --- Pass 2: Draw house and shipping bin as a single image for their full size, with context-aware background ---
        for (DeployedObject obj : farmMap.getDeployedObjects()) {
            if (obj.getSymbol() == Tile.FARM_HOUSE_SYMBOL || obj.getSymbol() == Tile.SHIPPING_BIN_SYMBOL) {
                BufferedImage img = objectImages.get(obj.getSymbol());
                if (img != null) {
                    int drawX = offsetX + obj.getX() * tileSize;
                    int drawY = offsetY + obj.getY() * tileSize;
                    // For each tile under the object, draw the background based on the nearest outside tile
                    for (int dx = 0; dx < obj.getWidth(); dx++) {
                        for (int dy = 0; dy < obj.getHeight(); dy++) {
                            int tx = obj.getX() + dx;
                            int ty = obj.getY() + dy;
                            int tileDrawX = offsetX + tx * tileSize;
                            int tileDrawY = offsetY + ty * tileSize;

                            // Find the nearest outside tile (left, right, up, down)
                            Tile neighbor = null;
                            // Prefer left, then right, then up, then down
                            if (tx - 1 >= 0 && (dx == 0)) {
                                neighbor = farmMap.getTileAt(tx - 1, ty);
                            }
                            if (neighbor == null && tx + 1 < mapSize && (dx == obj.getWidth() - 1)) {
                                neighbor = farmMap.getTileAt(tx + 1, ty);
                            }
                            if (neighbor == null && ty - 1 >= 0 && (dy == 0)) {
                                neighbor = farmMap.getTileAt(tx, ty - 1);
                            }
                            if (neighbor == null && ty + 1 < mapSize && (dy == obj.getHeight() - 1)) {
                                neighbor = farmMap.getTileAt(tx, ty + 1);
                            }
                            // If still null, try corners (diagonals)
                            if (neighbor == null && tx - 1 >= 0 && ty - 1 >= 0 && dx == 0 && dy == 0) {
                                neighbor = farmMap.getTileAt(tx - 1, ty - 1);
                            }
                            if (neighbor == null && tx + 1 < mapSize && ty - 1 >= 0 && dx == obj.getWidth() - 1 && dy == 0) {
                                neighbor = farmMap.getTileAt(tx + 1, ty - 1);
                            }
                            if (neighbor == null && tx - 1 >= 0 && ty + 1 < mapSize && dx == 0 && dy == obj.getHeight() - 1) {
                                neighbor = farmMap.getTileAt(tx - 1, ty + 1);
                            }
                            if (neighbor == null && tx + 1 < mapSize && ty + 1 < mapSize && dx == obj.getWidth() - 1 && dy == obj.getHeight() - 1) {
                                neighbor = farmMap.getTileAt(tx + 1, ty + 1);
                            }

                            char bgChar = Tile.RUMPUT_HIJAU_SYMBOL; // Default to grass
                            if (neighbor != null) {
                                // Use terrain for deployed/terrain/crop
                                if (neighbor.getType() == Tile.TileType.DEPLOYED) {
                                    // If neighbor is a fence, pond, etc, use its image
                                    bgChar = neighbor.displayChar();
                                } else if (neighbor.getType() == Tile.TileType.PLANTED && neighbor.getPlantedCrop() != null) {
                                    // Use tilled soil as background for crops
                                    bgChar = Tile.TILLED_CHAR;
                                } else {
                                    bgChar = neighbor.displayChar();
                                }
                            }
                            BufferedImage bgImg = terrainImages.get(bgChar);
                            if (bgImg != null) {
                                g2d.drawImage(bgImg, tileDrawX, tileDrawY, tileSize, tileSize, null);
                            } else {
                                g2d.setColor(getFallbackColorForSymbol(bgChar));
                                g2d.fillRect(tileDrawX, tileDrawY, tileSize, tileSize);
                            }
                        }
                    }
                    // Now draw the object image on top
                    g2d.drawImage(img, drawX, drawY, obj.getWidth() * tileSize, obj.getHeight() * tileSize, null);
                }
            }
        }

        // 2) Draw the player on top of everything else
        if (currentPlayer.getLocation().equals(farmMap.getName())) {
            int px = currentPlayer.getX();
            int py = currentPlayer.getY();
            int drawX = offsetX + px * tileSize;
            int drawY = offsetY + py * tileSize;

            if (playerImage != null) {
                g2d.drawImage(playerImage, drawX, drawY, tileSize, tileSize, null);
            } else {
                // Fallback to red oval if player image fails to load
                g2d.setColor(Color.RED);
                g2d.fillOval(drawX, drawY, tileSize, tileSize);
            }
        }
    }

    public void refreshMap() {
        repaint();
    }
}
