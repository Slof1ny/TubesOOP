// TubesOOP/src/gui/MainMenu.java
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {

    private GameView gameView;
    private Image backgroundImg;

    public MainMenu() {
        // Load background image (try absolute path first, then resource)
        backgroundImg = loadImage("resources/asset/png/OpeningPage.png", "Main Menu Background");
        setLayout(null); // We'll use absolute positioning to match the image
        setOpaque(false);

        // Button style: transparent, no border, white text, large font, placed to match the image
        Font buttonFont = new Font("Arial", Font.BOLD, 32);
        // Button positions and sizes are matched to the wooden sign graphics in the background image
        // These values are visually estimated from the screenshot and may need fine-tuning
        JButton newGameButton = new JButton();
        newGameButton.setBounds(418, 330, 168, 127); // Center top sign
        styleWoodenButton(newGameButton, "");
        newGameButton.setFont(buttonFont);
        newGameButton.addActionListener(e -> {
            if (gameView != null) gameView.showScreen("PlayerCreationScreen");
        });

        JButton helpButton = new JButton();
        helpButton.setBounds(167, 514, 123, 73); // Left sign
        styleWoodenButton(helpButton, "");
        helpButton.setFont(buttonFont);
        helpButton.addActionListener(e -> {
            if (gameView != null) gameView.showScreen("HelpScreen");
            else JOptionPane.showMessageDialog(null, "Help: WASD to move, E to interact. More info in game (press H).", "Basic Help", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exitButton = new JButton();
        exitButton.setBounds(443, 514, 123, 73); // Center bottom sign
        styleWoodenButton(exitButton, "");
        exitButton.setFont(buttonFont);
        exitButton.addActionListener(e -> System.exit(0));

        JButton creditsButton = new JButton();
        creditsButton.setBounds(715, 514, 123, 73); // Right sign
        styleWoodenButton(creditsButton, "");
        creditsButton.setFont(buttonFont);
        creditsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Game by Spakbor Hills Team", "Credits", JOptionPane.INFORMATION_MESSAGE);
        });

        add(newGameButton);
        add(helpButton);
        add(exitButton);
        add(creditsButton);
    }
    /**
     * Style a menu button to be transparent, white text, no border, and large.
     */
    /**
     * Style a menu button to look like a transparent overlay for a wooden sign, with white text and no border.
     */
    private void styleWoodenButton(JButton button, String text) {
        button.setText(text);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setForeground(new Color(90, 50, 10)); // Dark brown to match sign text
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 32));
        button.setBorder(null);
        button.setBackground(new Color(0,0,0,0));
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), this);
        }
    }
        /**
     * Helper to load a PNG from an absolute file path first, then fallback to classpath resource.
     * @param path The file path or resource path (e.g., "resources/asset/png/image.png").
     * @param nameForLog A descriptive name for logging purposes.
     * @return The loaded Image, or null if loading failed.
     */
    private Image loadImage(String path, String nameForLog) {
        // Try absolute path (relative to working directory)
        java.io.File file = new java.io.File(path);
        if (file.isAbsolute() && file.exists()) {
            try {
                return javax.imageio.ImageIO.read(file);
            } catch (Exception e) {
                System.err.println("Error loading image from file: " + path + " (" + nameForLog + "): " + e.getMessage());
            }
        } else {
            // Try relative to project root (working directory)
            file = new java.io.File(System.getProperty("user.dir"), path);
            if (file.exists()) {
                try {
                    return javax.imageio.ImageIO.read(file);
                } catch (Exception e) {
                    System.err.println("Error loading image from file: " + file.getAbsolutePath() + " (" + nameForLog + "): " + e.getMessage());
                }
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