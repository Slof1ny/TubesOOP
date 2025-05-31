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

public class CityMapPanel extends JPanel {
    private GameManager gameManager;
    // private CityMap cityMap; // Fetched from gameManager in paintComponent
    // private Player player;  // Fetched from gameManager in paintComponent

    private Map<Character, BufferedImage> terrainImages;
    private Map<Character, BufferedImage> objectImages; // For single-tile representations or keys
    private Map<String, BufferedImage> buildingImages; // For multi-tile buildings by name/path
    private BufferedImage playerImage;

    // This can be a default or minimum size for assets if needed,
    // but actual drawing will use dynamically calculated tile size.
    private static final int BASE_ASSET_SIZE = 16; // Assuming your assets are designed for 16x16 tiles

    public CityMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        // this.cityMap = gameManager.getCityMap(); // No longer storing as a direct field
        // this.player = gameManager.getPlayer();   // No longer storing as a direct field

        // Remove setPreferredSize to allow panel to expand
        // setPreferredSize(new Dimension(gameManager.getCityMap().getSize() * BASE_ASSET_SIZE, gameManager.getCityMap().getSize() * BASE_ASSET_SIZE));
        setBackground(Color.BLACK); //

        loadImages();

        setFocusable(true); //
        addKeyListener(new CityMapController(gameManager, this, gameView)); //
    }

    private void loadImages() {
        terrainImages = new HashMap<>(); //
        objectImages = new HashMap<>(); //
        buildingImages = new HashMap<>(); // For storing images of uniquely named buildings

        CityMap currentCityMap = gameManager.getCityMap(); // Get instance for loading

        try {
            terrainImages.put('.', loadImage("/resources/asset/png/Jalan-16px.png", "Jalan-16px.png")); //
            terrainImages.put('1', loadImage("/resources/asset/png/JalanHijau-16px.png", "JalanHijau-16px.png")); //
            terrainImages.put('2', loadImage("/resources/asset/png/Stone_Round.png", "Stone_Round.png")); //
            terrainImages.put('3', loadImage("/resources/asset/png/Kolam_air.png", "Kolam_air.png")); //
            terrainImages.put('4', loadImage("/resources/asset/png/BungaPink.png", "BungaPink.png")); //
            terrainImages.put('5', loadImage("/resources/asset/png/Tanah-16px.png", "Tanah-16px.png")); //

            if (!terrainImages.containsKey('.')) terrainImages.put('.', createFallbackImage(new Color(128, 128, 128), BASE_ASSET_SIZE)); //
            // ... (add other fallbacks for terrain if desired, using BASE_ASSET_SIZE)

            playerImage = loadImage("/resources/asset/png/PlayerBoy_idle.png", "PlayerBoy_idle.png"); //
            if (playerImage == null) playerImage = createFallbackImage(new Color(255, 0, 0), BASE_ASSET_SIZE); //

            for (DeployedObject obj : currentCityMap.getDeployedObjects()) { //
                BufferedImage img = null;
                String imagePath = null;
                String objectKey = String.valueOf(obj.getSymbol()); // Default key

                if (obj instanceof CityMap.Building) { //
                    CityMap.Building building = (CityMap.Building) obj; //
                    imagePath = building.getImagePath(); //
                    objectKey = building.getBuildingName(); // Use building name as a key for specific building images
                    if (imagePath != null && !imagePath.isEmpty()) { //
                        img = loadImage(imagePath, "Building: " + building.getBuildingName()); //
                        if (img != null) {
                            buildingImages.put(objectKey, img);
                            continue; // Skip adding to generic objectImages if specific building image loaded
                        }
                    }
                } else if (obj.getSymbol() == 'X') { //
                    imagePath = "/resources/asset/png/Door.png"; //
                } else if (obj.getSymbol() == 'F') { //
                    imagePath = "/resources/asset/png/Fence.png"; //
                }

                if (img == null && imagePath != null && !imagePath.isEmpty()) { // Try loading generic symbol if not a specific building
                     img = loadImage(imagePath, "Object for symbol '" + obj.getSymbol() + "'");
                }
                
                if (img == null) { //
                    img = createFallbackImage(getFallbackColorForSymbol(obj.getSymbol()), BASE_ASSET_SIZE); //
                }
                objectImages.put(obj.getSymbol(), img); //
            }
        } catch (IOException e) {
            e.printStackTrace(); //
            System.err.println("Critical Error loading images for CityMapPanel: " + e.getMessage()); //
            terrainImages.clear(); terrainImages.put('.', createFallbackImage(Color.GRAY, BASE_ASSET_SIZE)); //
            objectImages.clear(); objectImages.put('?', createFallbackImage(Color.RED, BASE_ASSET_SIZE)); //
            playerImage = createFallbackImage(Color.RED, BASE_ASSET_SIZE); //
        }
    }

    private BufferedImage loadImage(String path, String nameForWarning) throws IOException { //
        InputStream is = getClass().getResourceAsStream(path); //
        if (is != null) { //
            try {
                return ImageIO.read(is); //
            } catch (IOException e) {
                System.err.println("Error reading image '" + nameForWarning + "' from stream: " + e.getMessage()); //
                return null; //
            } finally {
                try { is.close(); } catch (IOException e) { System.err.println("Error closing stream for " + nameForWarning + ": " + e.getMessage());} //
            }
        } else {
            System.err.println("Warning: Image '" + nameForWarning + "' not found at path: " + path); //
            return null; //
        }
    }

    // Modified to accept a size parameter
    private BufferedImage createFallbackImage(Color color, int size) { //
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB); //
        Graphics2D g2d = img.createGraphics(); //
        g2d.setColor(color); //
        g2d.fillRect(0, 0, size, size); //
        g2d.setColor(Color.BLACK); //
        g2d.drawRect(0, 0, size - 1, size - 1); //
        g2d.dispose(); //
        return img; //
    }

    private Color getFallbackColorForSymbol(char symbol) { //
        // This method remains largely the same, used if an image fails to load.
        switch (symbol) {
            case 'S': return new Color(255, 165, 0); //
            case 'M': return new Color(139, 0, 0);   //
            // ... (other cases from your file) ...
            case 'F': return new Color(100,100,100); //
            default:  return new Color(160, 82, 45); //
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        CityMap currentCityMap = gameManager.getCityMap();
        Player currentPlayer = gameManager.getPlayer();

        if (!gameManager.getCurrentMap().getName().equals(currentCityMap.getName())) { //
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int mapSize = currentCityMap.getSize();

        if (mapSize == 0) return;

        int tileW = panelWidth / mapSize;
        int tileH = panelHeight / mapSize;
        int actualTileSize = Math.min(tileW, tileH); // Keep tiles square
        if (actualTileSize < 1) actualTileSize = 1; // Prevent size 0

        int totalMapRenderWidth = actualTileSize * mapSize;
        int totalMapRenderHeight = actualTileSize * mapSize;
        int offsetX = (panelWidth - totalMapRenderWidth) / 2;
        int offsetY = (panelHeight - totalMapRenderHeight) / 2;

        // 1. Draw base terrain tiles
        for (int y = 0; y < mapSize; y++) {
            for (int x = 0; x < mapSize; x++) {
                int drawX = offsetX + x * actualTileSize;
                int drawY = offsetY + y * actualTileSize;

                Tile tile = currentCityMap.getTileAt(x, y); //
                // Default to road if no specific terrain char or image
                char terrainChar = (tile != null) ? tile.displayChar() : '.'; 
                BufferedImage terrainImg = terrainImages.get(terrainChar); //

                if (terrainImg != null) {
                    g2d.drawImage(terrainImg, drawX, drawY, actualTileSize, actualTileSize, null); //
                } else { // Fallback if image not found
                    g2d.setColor(getFallbackColorForSymbol(terrainChar)); //
                    g2d.fillRect(drawX, drawY, actualTileSize, actualTileSize); //
                }
                // Optional: draw grid lines
                // g2d.setColor(Color.DARK_GRAY);
                // g2d.drawRect(drawX, drawY, actualTileSize, actualTileSize);
            }
        }

        // 2. Draw deployed objects
        Set<DeployedObject> drawnObjects = new HashSet<>(); //
        for (DeployedObject obj : currentCityMap.getDeployedObjects()) { //
            if (!drawnObjects.contains(obj)) { //
                BufferedImage objImageToDraw = null;
                String buildingNameKey = null;

                if (obj instanceof CityMap.Building) {
                    buildingNameKey = ((CityMap.Building) obj).getBuildingName();
                    objImageToDraw = buildingImages.get(buildingNameKey);
                }
                
                if (objImageToDraw == null) { // Fallback to symbol-based image if specific building image not found
                    objImageToDraw = objectImages.get(obj.getSymbol());
                }

                int objOriginX = offsetX + obj.getX() * actualTileSize;
                int objOriginY = offsetY + obj.getY() * actualTileSize;
                int objRenderWidth = obj.getWidth() * actualTileSize;
                int objRenderHeight = obj.getHeight() * actualTileSize;

                if (objImageToDraw != null) { //
                    g2d.drawImage(objImageToDraw, objOriginX, objOriginY, objRenderWidth, objRenderHeight, null);
                } else { // Fallback drawing for objects
                    g2d.setColor(getFallbackColorForSymbol(obj.getSymbol())); //
                    g2d.fillRect(objOriginX, objOriginY, objRenderWidth, objRenderHeight); //
                    g2d.setColor(Color.BLACK); //
                    g2d.drawRect(objOriginX, objOriginY, objRenderWidth -1, objRenderHeight -1); //
                }
                drawnObjects.add(obj); //
            }
        }

        // 3. Draw the player
        if (currentPlayer.getLocation().equals(currentCityMap.getName())) { //
            int playerDrawX = offsetX + currentPlayer.getX() * actualTileSize;
            int playerDrawY = offsetY + currentPlayer.getY() * actualTileSize;
            if (playerImage != null) { //
                g2d.drawImage(playerImage, playerDrawX, playerDrawY, actualTileSize, actualTileSize, null);
            } else { // Fallback for player
                g2d.setColor(new Color(255, 0, 0)); //
                g2d.fillOval(playerDrawX, playerDrawY, actualTileSize, actualTileSize); //
            }
        }
    }

    public void refreshMap() {
        repaint(); //
    }
}