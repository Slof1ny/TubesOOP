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
            // Assuming PlayerBoy_idle.png as default for now. You might want to load based on player.getGender()
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
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                int drawX = offsetX + x * tileSize;
                int drawY = offsetY + y * tileSize;

                Tile tile = currentFarmMap.getTileAt(x, y);
                if (tile == null) { // Should not happen if map is properly initialized
                    g2d.setColor(Color.MAGENTA); // Debug color for null tiles
                    g2d.fillRect(drawX, drawY, tileSize, tileSize);
                    continue;
                }

                BufferedImage imageToDraw = null;
                char tileChar = tile.displayChar();

                if (tile.getType() == Tile.TileType.DEPLOYED) {
                    // For deployed objects (house, pond, shipping bin, fence, exit)
                    imageToDraw = objectImages.get(tileChar);
                } else if (tile.getType() == Tile.TileType.PLANTED && tile.getPlantedCrop() != null) {
                    // For planted crops, draw the tilled soil first, then the crop image
                    BufferedImage tilledSoil = terrainImages.get(Tile.TILLED_CHAR);
                    if (tilledSoil != null) {
                        g2d.drawImage(tilledSoil, drawX, drawY, tileSize, tileSize, null);
                    } else {
                        g2d.setColor(getFallbackColorForSymbol(Tile.TILLED_CHAR));
                        g2d.fillRect(drawX, drawY, tileSize, tileSize);
                    }
                    
                    // Now draw the actual crop image on top
                    Crop plantedCrop = tile.getPlantedCrop();
                    imageToDraw = cropImages.get(plantedCrop.getName()); // Get image by crop name
                    // If crop image is null, fall back to generic planted soil image
                    if (imageToDraw == null) {
                        imageToDraw = terrainImages.get(Tile.PLANTED_CHAR);
                    }

                } else {
                    // For UNTILLED, TILLED (if not planted), and other terrain types
                    imageToDraw = terrainImages.get(tileChar);
                }

                if (imageToDraw != null) {
                    g2d.drawImage(imageToDraw, drawX, drawY, tileSize, tileSize, null);
                } else { // Fallback if image not found for any reason
                    g2d.setColor(getFallbackColorForSymbol(tileChar));
                    g2d.fillRect(drawX, drawY, tileSize, tileSize);
                }
                
                // Draw a border for all tiles for grid visibility
                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawX, drawY, tileSize, tileSize);
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
