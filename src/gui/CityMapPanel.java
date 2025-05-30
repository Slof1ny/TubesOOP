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
    private CityMap cityMap;
    private Player player;

    private Map<Character, BufferedImage> terrainImages;
    private Map<Character, BufferedImage> objectImages;
    private BufferedImage playerImage;

    private final int TILE_SIZE = 16;

    public CityMapPanel(GameManager gameManager, GameView gameView) {
        this.gameManager = gameManager;
        this.cityMap = gameManager.getCityMap();
        this.player = gameManager.getPlayer();

        setPreferredSize(new Dimension(cityMap.getSize() * TILE_SIZE, cityMap.getSize() * TILE_SIZE));
        setBackground(Color.BLACK);

        loadImages();

        setFocusable(true);
        addKeyListener(new CityMapController(gameManager, this, gameView));
    }

    private void loadImages() {
        terrainImages = new HashMap<>();
        objectImages = new HashMap<>();
        try {
            // Load specific terrain images from your provided assets
            terrainImages.put('.', loadImage("/resources/asset/png/Jalan-16px.png", "Jalan-16px.png"));
            terrainImages.put('1', loadImage("/resources/asset/png/JalanHijau-16px.png", "JalanHijau-16px.png")); // Grass asset
            terrainImages.put('2', loadImage("/resources/asset/png/Stone_Round.png", "Stone_Round.png"));       // Stone asset
            terrainImages.put('3', loadImage("/resources/asset/png/Kolam_air.png", "Kolam_air.png"));            // Water asset
            terrainImages.put('4', loadImage("/resources/asset/png/BungaPink.png", "BungaPink.png"));            // Flower asset
            terrainImages.put('5', loadImage("/resources/asset/png/Tanah-16px.png", "Tanah-16px.png"));          // Tanah asset

            // Fallback for terrain images if not found (shouldn't be needed if paths are perfect)
            if (!terrainImages.containsKey('.')) terrainImages.put('.', createFallbackImage(new Color(128, 128, 128)));
            if (!terrainImages.containsKey('1')) terrainImages.put('1', createFallbackImage(new Color(100, 200, 100)));
            if (!terrainImages.containsKey('2')) terrainImages.put('2', createFallbackImage(new Color(150, 150, 150)));
            if (!terrainImages.containsKey('3')) terrainImages.put('3', createFallbackImage(new Color(50, 50, 200)));
            if (!terrainImages.containsKey('4')) terrainImages.put('4', createFallbackImage(new Color(200, 100, 200)));
            if (!terrainImages.containsKey('5')) terrainImages.put('5', createFallbackImage(new Color(139, 69, 19)));


            playerImage = loadImage("/resources/asset/png/PlayerBoy_idle.png", "PlayerBoy_idle.png"); // Assuming male player for now
            if (playerImage == null) playerImage = createFallbackImage(new Color(255, 0, 0));

            // Load specific object/building images
            for (DeployedObject obj : cityMap.getDeployedObjects()) {
                BufferedImage img = null;
                String imagePath = null;

                if (obj instanceof CityMap.Building) {
                    CityMap.Building building = (CityMap.Building) obj;
                    imagePath = building.getImagePath();
                } else if (obj.getSymbol() == 'X') {
                    imagePath = "/resources/asset/png/Door.png";
                } else if (obj.getSymbol() == 'F') { // Load Fence image
                    imagePath = "/resources/asset/png/Fence.png";
                }

                if (imagePath != null && !imagePath.isEmpty()) {
                    img = loadImage(imagePath, "Object for symbol '" + obj.getSymbol() + "'");
                }

                if (img == null) {
                    img = createFallbackImage(getFallbackColorForSymbol(obj.getSymbol()));
                }
                objectImages.put(obj.getSymbol(), img);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Critical Error loading images for CityMapPanel: " + e.getMessage());
            // Fallback if loading fails catastrophically
            terrainImages.clear(); terrainImages.put('.', createFallbackImage(Color.GRAY));
            objectImages.clear(); objectImages.put('?', createFallbackImage(Color.RED));
            playerImage = createFallbackImage(Color.RED);
        }
    }

    private BufferedImage loadImage(String path, String nameForWarning) throws IOException {
        InputStream is = getClass().getResourceAsStream(path);
        if (is != null) {
            try {
                return ImageIO.read(is);
            } catch (IOException e) {
                System.err.println("Error reading image '" + nameForWarning + "' from stream: " + e.getMessage());
                return null;
            } finally {
                try { is.close(); } catch (IOException e) { System.err.println("Error closing stream for " + nameForWarning + ": " + e.getMessage()); }
            }
        } else {
            System.err.println("Warning: Image '" + nameForWarning + "' not found at path: " + path + ". Using fallback color.");
            return null;
        }
    }

    private BufferedImage createFallbackImage(Color color) {
        BufferedImage img = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(color);
        g2d.fillRect(0, 0, TILE_SIZE, TILE_SIZE);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, TILE_SIZE - 1, TILE_SIZE - 1);
        g2d.dispose();
        return img;
    }

    private Color getFallbackColorForSymbol(char symbol) {
        switch (symbol) {
            case 'S': return new Color(255, 165, 0); // Store color
            case 'M': return new Color(139, 0, 0); // Mayor color
            case 'C': return new Color(100, 50, 0); // Carpentry (brown)
            case 'R': return new Color(150, 150, 200); // Perry (light blue)
            case 'G': return new Color(80, 0, 80); // Dasco (purple)
            case 'A': return new Color(255, 192, 203); // Abigail (pink)
            case 'O': return new Color(255, 140, 0); // Orenji (dark orange)
            case 'F': return new Color(100, 100, 100); // Fence (dark grey)
            case 'X': return new Color(0, 150, 0); // Exit (green)
            // Fallback for terrain symbols if they somehow end up in objectImages map
            case '.': return new Color(128, 128, 128); // Default road
            case '1': return new Color(100, 200, 100); // Grass
            case '2': return new Color(150, 150, 150); // Stone
            case '3': return new Color(50, 50, 200); // Water
            case '4': return new Color(200, 100, 200); // Flower
            case '5': return new Color(139, 69, 19); // Tanah (brown)
            default:  return new Color(160, 82, 45); // Generic building color
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (!gameManager.getCurrentMap().getName().equals(cityMap.getName())) {
            return;
        }

        Set<DeployedObject> drawnObjects = new HashSet<>(); // To ensure large objects are drawn once

        // 1. Draw base terrain tiles (roads, grass, stone, water, flowers)
        for (int y = 0; y < cityMap.getSize(); y++) {
            for (int x = 0; x < cityMap.getSize(); x++) {
                int drawX = x * TILE_SIZE;
                int drawY = y * TILE_SIZE;

                Tile tile = cityMap.getTileAt(x, y);
                BufferedImage terrainImg = terrainImages.get(tile.displayChar()); // Get image based on terrain char

                if (terrainImg != null) {
                    g2d.drawImage(terrainImg, drawX, drawY, TILE_SIZE, TILE_SIZE, null);
                } else {
                    g2d.setColor(getFallbackColorForSymbol(tile.displayChar()));
                    g2d.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                }
                g2d.setColor(Color.BLACK);
                g2d.drawRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
            }
        }

        // 2. Draw deployed objects (buildings, exits, fences) once, on top of the terrain tiles
        for (DeployedObject obj : cityMap.getDeployedObjects()) {
            if (!drawnObjects.contains(obj)) {
                BufferedImage objImage = objectImages.get(obj.getSymbol());
                if (objImage != null) {
                    int objWidthPixels = obj.getWidth() * TILE_SIZE;
                    int objHeightPixels = obj.getHeight() * TILE_SIZE;
                    g2d.drawImage(objImage, obj.getX() * TILE_SIZE, obj.getY() * TILE_SIZE, objWidthPixels, objHeightPixels, null);
                } else {
                    g2d.setColor(getFallbackColorForSymbol(obj.getSymbol()));
                    g2d.fillRect(obj.getX() * TILE_SIZE, obj.getY() * TILE_SIZE, obj.getWidth() * TILE_SIZE, obj.getHeight() * TILE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(obj.getX() * TILE_SIZE, obj.getY() * TILE_SIZE, obj.getWidth() * TILE_SIZE -1, obj.getHeight() * TILE_SIZE -1);
                }
                drawnObjects.add(obj);
            }
        }

        // 3. Draw the player on top of everything else
        if (player.getLocation().equals(cityMap.getName())) {
            if (playerImage != null) {
                g2d.drawImage(playerImage, player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
            } else {
                g2d.setColor(new Color(255, 0, 0));
                g2d.fillOval(player.getX() * TILE_SIZE, player.getY() * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
    }

    public void refreshMap() {
        repaint();
    }
}