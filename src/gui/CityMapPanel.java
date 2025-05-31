package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

import core.world.GameMap;
import core.world.Tile;
import core.player.Player;
import core.world.DeployedObject;
import system.GameManager;
import core.world.CityMap;
import core.world.Tile.TileType;

public class CityMapPanel extends JPanel {
    private final GameManager gameManager;
    private final CityMap cityMap;
    private final Player player;

    private final Map<Character, BufferedImage> terrainImages = new HashMap<>();
    private final Map<Character, BufferedImage> objectImages = new HashMap<>(); // For generic 1x1 objects (like Fence, Door)
    private final Map<String, BufferedImage> buildingImages = new HashMap<>(); // For multi-tile buildings by name/path
    private BufferedImage playerImage;

    private static final int BASE_ASSET_SIZE = 16; // All assets are assumed to be designed for 16x16 pixels

    public CityMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        this.cityMap = gameManager.getCityMap();
        this.player = gameManager.getPlayer();

        setPreferredSize(new Dimension(cityMap.getSize() * BASE_ASSET_SIZE, cityMap.getSize() * BASE_ASSET_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        loadImages(); // Load all images at initialization
        addKeyListener(new CityMapController(gameManager, this, gameView));
    }

    /**
     * Load all necessary images (terrain, generic objects, building sprites, player sprite).
     */
    private void loadImages() {
        try {
            // ---------------------
            // Terrain images: (16x16)
            // ---------------------

            terrainImages.put(Tile.DEFAULT_UNTILLED_CHAR, loadImage("resources/asset/png/Jalan-16px.png", "Jalan-16px.png"));
            terrainImages.put(Tile.RUMPUT_HIJAU_SYMBOL, loadImage("resources/asset/png/JalanHijau-16px.png", "JalanHijau-16px.png"));
            terrainImages.put(Tile.BATU_SYMBOL, loadImage("resources/asset/png/Stone_Round.png", "Stone_Round.png"));
            terrainImages.put(Tile.AIR_SYMBOL, loadImage("resources/asset/png/Kolam_air.png", "Kolam_air.png"));
            terrainImages.put(Tile.BUNGA_PINK_SYMBOL, loadImage("resources/asset/png/BungaPink.png", "BungaPink.png"));
            terrainImages.put(Tile.TANAH_SYMBOL, loadImage("resources/asset/png/Tanah-16px.png", "Tanah-16px.png"));
            terrainImages.put(Tile.TILLED_CHAR, loadImage("resources/asset/png/Soil_TILLED.png", "Soil_TILLED.png"));
            terrainImages.put(Tile.PLANTED_CHAR, loadImage("resources/asset/png/Soil_ThrownSeeds.png", "Soil_ThrownSeeds.png")); // For planted (generic)

            // NEW: Load specific 2-tile wide road segment images
            terrainImages.put(Tile.JALAN_COKLAT_KIRI_SYMBOL, loadImage("resources/asset/png/jalan_coklatKiri.png", "jalan_coklatKiri.png"));
            terrainImages.put(Tile.JALAN_COKLAT_KANAN_SYMBOL, loadImage("resources/asset/png/jalan_coklatKanan.png", "jalan_coklatKanan.png"));
            terrainImages.put(Tile.JALAN_COKLAT_ATAS_SYMBOL, loadImage("resources/asset/png/jalan_coklatAtas.png", "jalan_coklatAtas.png")); 
            terrainImages.put(Tile.JALAN_COKLAT_BAWAH_SYMBOL, loadImage("resources/asset/png/jalan_coklatBawah.png", "jalan_coklatBawah.png"));
            terrainImages.put(Tile.JALAN_TENGAH_SYMBOL, loadImage("resources/asset/png/jalan_coklatTengah.png", "jalan_coklatTengah.png"));

            // Provide fallbacks if any terrain image is missing:
            for (char key : new char[]{Tile.DEFAULT_UNTILLED_CHAR, Tile.RUMPUT_HIJAU_SYMBOL, Tile.BATU_SYMBOL, Tile.AIR_SYMBOL,
                                       Tile.BUNGA_PINK_SYMBOL, Tile.TANAH_SYMBOL, Tile.TILLED_CHAR, Tile.PLANTED_CHAR,
                                       Tile.JALAN_COKLAT_KIRI_SYMBOL, Tile.JALAN_COKLAT_KANAN_SYMBOL,
                                       Tile.JALAN_COKLAT_ATAS_SYMBOL, Tile.JALAN_COKLAT_BAWAH_SYMBOL, }) {
                if (!terrainImages.containsKey(key) || terrainImages.get(key) == null) {
                    terrainImages.put(key, createFallbackImage(getFallbackColorForSymbol(key)));
                }
            }

            // ---------------------
            // Player image: (16x16)
            // ---------------------
            playerImage = loadImage("resources/asset/png/PlayerBoy_idle.png", "PlayerBoy_idle.png");
            if (playerImage == null) {
                playerImage = createFallbackImage(Color.RED);
            }

            // ---------------------
            // Deployed objects (Fence, Exit, etc.): (16x16)
            // ---------------------
            // Load specific 1x1 object images directly into objectImages map
            BufferedImage fenceImg = loadImage("resources/asset/png/Fence.png", "Fence.png");
            if (fenceImg == null) {
                fenceImg = createFallbackImage(new Color(100, 100, 100));
            }
            objectImages.put(Tile.FENCE_SYMBOL, fenceImg); // Store by symbol
            
            BufferedImage doorImg = loadImage("resources/asset/png/Door.png", "Door.png"); // The Exit image
            if (doorImg == null) {
                doorImg = createFallbackImage(new Color(0, 150, 0)); // Green for Exit
            }
            objectImages.put(Tile.EXIT_SYMBOL, doorImg); // Store by symbol for Exit

            // For other generic 1x1 deployed objects (like farm house, pond, shipping bin, generic furniture from Tile.java)
            objectImages.put(Tile.FARM_HOUSE_SYMBOL, loadImage("resources/asset/png/House_Player.png", "Farm House")); // Assuming this is player's farm house
            objectImages.put(Tile.POND_SYMBOL, loadImage("resources/asset/png/Kolam_air.png", "Farm Pond")); // Reusing Kolam_air for farm pond
            objectImages.put(Tile.SHIPPING_BIN_SYMBOL, loadImage("resources/asset/png/ShippingBin_Closed.png", "Shipping Bin"));
            objectImages.put(Tile.GENERIC_FARM_FURNITURE_B, createFallbackImage(new Color(180, 100, 180))); // Fallback for generic B
            objectImages.put(Tile.GENERIC_FARM_FURNITURE_T, createFallbackImage(new Color(100, 180, 180))); // Fallback for generic T

            // ---------------------
            // Building images: (multi-tile)
            // ---------------------
            for (DeployedObject obj : cityMap.getDeployedObjects()) {
                if (obj instanceof CityMap.Building) {
                    CityMap.Building b = (CityMap.Building) obj;
                    String bName = b.getBuildingName();
                    String imgPath = b.getImagePath();
                    if (imgPath != null && !imgPath.isEmpty()) {
                        String resolvedImgPath = imgPath.startsWith("/") ? imgPath.substring(1) : imgPath;
                        BufferedImage bimg = loadImage(resolvedImgPath, "Building: " + bName);
                        if (bimg != null) {
                            buildingImages.put(bName, bimg);
                        } else {
                            buildingImages.put(bName, createFallbackImage(getFallbackColorForSymbol(obj.getSymbol()), b.getWidth() * BASE_ASSET_SIZE, b.getHeight() * BASE_ASSET_SIZE));
                        }
                    } else {
                        buildingImages.put(bName, createFallbackImage(getFallbackColorForSymbol(obj.getSymbol()), b.getWidth() * BASE_ASSET_SIZE, b.getHeight() * BASE_ASSET_SIZE));
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            System.err.println("Critical Error loading images for CityMapPanel: " + e.getMessage());
            // Provide a minimal fallback so the panel still draws something.
            for (char key : new char[]{Tile.DEFAULT_UNTILLED_CHAR, Tile.RUMPUT_HIJAU_SYMBOL, Tile.BATU_SYMBOL, Tile.AIR_SYMBOL,
                                       Tile.BUNGA_PINK_SYMBOL, Tile.TANAH_SYMBOL, Tile.TILLED_CHAR, Tile.PLANTED_CHAR,
                                       Tile.JALAN_COKLAT_KIRI_SYMBOL, Tile.JALAN_COKLAT_KANAN_SYMBOL,
                                       Tile.JALAN_COKLAT_ATAS_SYMBOL, Tile.JALAN_COKLAT_BAWAH_SYMBOL}) {
                terrainImages.put(key, createFallbackImage(getFallbackColorForSymbol(key)));
            }
            objectImages.put(Tile.FENCE_SYMBOL, createFallbackImage(new Color(100, 100, 100)));
            objectImages.put(Tile.EXIT_SYMBOL, createFallbackImage(new Color(0, 150, 0)));
            playerImage = createFallbackImage(Color.RED);
            for (DeployedObject obj : cityMap.getDeployedObjects()) {
                if (obj instanceof CityMap.Building) {
                    CityMap.Building b = (CityMap.Building) obj;
                    buildingImages.put(b.getBuildingName(), createFallbackImage(getFallbackColorForSymbol(b.getSymbol()), b.getWidth() * BASE_ASSET_SIZE, b.getHeight() * BASE_ASSET_SIZE));
                }
            }
        }
    }

    /**
     * Helper to load a PNG from the classpath. Returns null if not found or on error.
     * @param path The classpath resource path (e.g., "/resources/asset/png/image.png").
     * @param nameForLog A descriptive name for logging purposes.
     * @return The loaded BufferedImage, or null if loading failed.
     */
    private BufferedImage loadImage(String path, String nameForLog) throws IOException {
        // Try classpath first (for compatibility)
        InputStream is = getClass().getResourceAsStream(path.startsWith("/") ? path : "/" + path);
        if (is == null) {
            // Try loading from file system (relative to project root)
            java.io.File file = new java.io.File(path);
            if (!file.exists()) {
                // Try with project root prefix if not found
                file = new java.io.File("resources/" + path);
            }
            if (file.exists()) {
                is = new java.io.FileInputStream(file);
            } else {
                System.err.println("Warning: Image not found: " + nameForLog + " (Path: " + path + ")");
                return null;
            }
        }
        try {
            return ImageIO.read(is);
        } finally {
            try { is.close(); } catch (IOException e) { }
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
     */
    private Color getFallbackColorForSymbol(char symbol) {
        switch (symbol) {
            // Building/Object symbols
            case Tile.STORE_SYMBOL: return new Color(255, 165, 0); // Store color
            case Tile.MAYOR_MANOR_SYMBOL: return new Color(139, 0, 0);   // Mayor color
            case Tile.CARPENTRY_SYMBOL: return new Color(100, 50, 0);  // Carpentry (brown)
            case Tile.PERRY_CABIN_SYMBOL: return new Color(150, 150, 200); // Perry (light blue)
            case Tile.GAMBLING_DEN_SYMBOL: return new Color( 80,   0,  80); // Dasco (purple)
            case Tile.ABIGAIL_TENT_SYMBOL: return new Color(255, 192, 203); // Abigail (pink)
            case Tile.ORENJI_SYMBOL: return new Color(255, 140, 0); // Orenji (dark orange)
            case Tile.FENCE_SYMBOL: return new Color(100, 100, 100); // Fence (dark grey)
            case Tile.EXIT_SYMBOL: return new Color(0, 150, 0);   // Exit (green)

            // Terrain symbols
            case Tile.DEFAULT_UNTILLED_CHAR: return new Color(128, 128, 128); // Default road
            case Tile.RUMPUT_HIJAU_SYMBOL: return new Color( 80, 160,  80); // Grass (JalanHijau-16px.png)
            case Tile.BATU_SYMBOL: return new Color(160, 160, 160); // Stone (Stone_Round.png)
            case Tile.AIR_SYMBOL: return new Color(  0,   0, 180); // Water (Kolam_air.png)
            case Tile.BUNGA_PINK_SYMBOL: return new Color(255, 100, 255); // Flower (BungaPink.png)
            case Tile.TANAH_SYMBOL: return new Color(139,  69,  19); // Tanah (Tanah-16px.png)
            case Tile.JALAN_COKLAT_KIRI_SYMBOL: return new Color(130, 80, 0); // Jalan Coklat Kiri
            case Tile.JALAN_COKLAT_KANAN_SYMBOL: return new Color(130, 80, 0); // Jalan Coklat Kanan
            case Tile.JALAN_COKLAT_ATAS_SYMBOL: return new Color(130, 80, 0); // Jalan Coklat Atas
            case Tile.JALAN_COKLAT_BAWAH_SYMBOL: return new Color(130, 80, 0); // Jalan Coklat Bawah

            // Farm specific symbols
            case Tile.FARM_HOUSE_SYMBOL: return new Color(200, 100, 50); // Farm house
            case Tile.POND_SYMBOL: return new Color(0, 0, 150);   // Pond
            case Tile.SHIPPING_BIN_SYMBOL: return new Color(100, 100, 0);  // Shipping Bin
            case Tile.TILLED_CHAR: return new Color(139, 69, 19).brighter();
            case Tile.PLANTED_CHAR: return new Color(0, 150, 0).brighter();
            case Tile.GENERIC_FARM_FURNITURE_B: return new Color(180, 100, 180);
            case Tile.GENERIC_FARM_FURNITURE_T: return new Color(100, 180, 180);

            default:  return new Color(128, 128, 128); // Generic fallback for unknown symbols
        }
    }

    /**
     * Paints the CityMap:
     * 1) Draw all terrain (including water, grass, stone, flower, dirt, road, tilled, planted).
     * 2) For each Building, re-draw its footprint as grass to guarantee a consistent “house-on-grass” look.
     * 3) Draw each Building’s multi-tile image on top of that forced-grass layer.
     * 4) Draw any remaining 1×1 objects (fences/exit) on top.
     * 5) Draw the player last.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        CityMap currentCityMap = gameManager.getCityMap();
        Player currentPlayer = gameManager.getPlayer();

        if (!gameManager.getCurrentMap().getName().equals(currentCityMap.getName())) {
            return;
        }

        int panelW = getWidth();
        int panelH = getHeight();
        int mapSize = currentCityMap.getSize();

        if (mapSize <= 0) return;

        // Calculate tile size dynamically
        int tileSize = Math.min(panelW / mapSize, panelH / mapSize);
        if (tileSize < 1) tileSize = 1;

        int totalW = tileSize * mapSize;
        int totalH = tileSize * mapSize;
        int offsetX = (panelW - totalW) / 2;
        int offsetY = (panelH - totalH) / 2;

        // 1) Draw every tile’s base terrain
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                int drawX = offsetX + x * tileSize;
                int drawY = offsetY + y * tileSize;

                Tile t = currentCityMap.getTileAt(x, y);
                char c = (t != null ? t.displayChar() : Tile.DEFAULT_UNTILLED_CHAR);

                BufferedImage terrImg = terrainImages.get(c);
                if (terrImg != null) {
                    g2d.drawImage(terrImg, drawX, drawY, tileSize, tileSize, null);
                } else { // Fallback if image not found
                    g2d.setColor(getFallbackColorForSymbol(c));
                    g2d.fillRect(drawX, drawY, tileSize, tileSize);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawX, drawY, tileSize, tileSize);
            }
        }

        // 2) For each Building: repaint its entire footprint as grass
        Set<DeployedObject> drawnObjects = new HashSet<>();
        BufferedImage grassImg = terrainImages.get(Tile.RUMPUT_HIJAU_SYMBOL);
        Color grassFallbackColor = getFallbackColorForSymbol(Tile.RUMPUT_HIJAU_SYMBOL);

        for (DeployedObject obj : currentCityMap.getDeployedObjects()) {
            if (!(obj instanceof CityMap.Building)) continue;
            CityMap.Building b = (CityMap.Building) obj;
            int bx = b.getX(), by = b.getY(), bw = b.getWidth(), bh = b.getHeight();

            for (int dx = 0; dx < bw; dx++) {
                for (int dy = 0; dy < bh; dy++) {
                    int tx = bx + dx, ty = by + dy;
                    if (tx >= 0 && tx < mapSize && ty >= 0 && ty < mapSize) {
                        int drawX = offsetX + tx * tileSize;
                        int drawY = offsetY + ty * tileSize;
                        if (grassImg != null) {
                            g2d.drawImage(grassImg, drawX, drawY, tileSize, tileSize, null);
                        } else {
                            g2d.setColor(grassFallbackColor);
                            g2d.fillRect(drawX, drawY, tileSize, tileSize);
                        }
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(drawX, drawY, tileSize, tileSize);
                    }
                }
            }
        }

        // 3) Draw each Building’s sprite on top of that forced-grass layer
        for (DeployedObject obj : currentCityMap.getDeployedObjects()) {
            if (!(obj instanceof CityMap.Building)) continue;
            CityMap.Building b = (CityMap.Building) obj;
            String bName = b.getBuildingName();
            BufferedImage bImg = buildingImages.get(bName);

            int bx = b.getX(), by = b.getY(), bw = b.getWidth(), bh = b.getHeight();
            int drawX = offsetX + bx * tileSize;
            int drawY = offsetY + by * tileSize;
            int drawW = bw * tileSize;
            int drawH = bh * tileSize;

            if (bImg != null) {
                g2d.drawImage(bImg, drawX, drawY, drawW, drawH, null);
            } else {
                g2d.setColor(getFallbackColorForSymbol(b.getSymbol()));
                g2d.fillRect(drawX, drawY, drawW, drawH);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawX, drawY, drawW - 1, drawH - 1);
            }
            drawnObjects.add(obj);
        }

        // 4) Draw any remaining 1×1 objects (fences 'F' or exit 'X') that were not part of multi-tile buildings
        for (DeployedObject obj : currentCityMap.getDeployedObjects()) {
            if (drawnObjects.contains(obj)) continue;

            char sym = obj.getSymbol();
            BufferedImage oImg = objectImages.get(sym);
            int ox = obj.getX(), oy = obj.getY();
            int drawX = offsetX + ox * tileSize;
            int drawY = offsetY + oy * tileSize;
            if (oImg != null) {
                g2d.drawImage(oImg, drawX, drawY, tileSize, tileSize, null);
            } else {
                g2d.setColor(getFallbackColorForSymbol(sym));
                g2d.fillRect(drawX, drawY, tileSize, tileSize);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawX, drawY, tileSize - 1, tileSize - 1);
            }
        }

        // 5) Draw the player on top
        if (player.getLocation().equals(cityMap.getName())) {
            int px = player.getX();
            int py = player.getY();
            int drawX = offsetX + px * tileSize;
            int drawY = offsetY + py * tileSize;
            if (playerImage != null) {
                g2d.drawImage(playerImage, drawX, drawY, tileSize, tileSize, null);
            } else {
                g2d.setColor(Color.RED);
                g2d.fillOval(drawX, drawY, tileSize, tileSize);
            }
        }
    }

    public void refreshMap() {
        repaint();
    }
}