// TubesOOP/src/gui/MainMenu.java
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {

    private GameView gameView;
    private Image backgroundImg;

    // Store buttons as fields for dynamic layout
    private final JButton newGameButton;
    private final JButton helpButton;
    private final JButton exitButton;
    private final JButton creditsButton;

    // Reference background image's original design size (for ratio)
    private static final int BG_ORIG_WIDTH = 1000;
    private static final int BG_ORIG_HEIGHT = 700;

    public MainMenu() {
        backgroundImg = loadImage("resources/asset/png/OpeningPage.png", "Main Menu Background");
        setLayout(null);
        setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 32);

        newGameButton = new JButton();
        styleWoodenButton(newGameButton, "");
        newGameButton.setFont(buttonFont);
        newGameButton.addActionListener(e -> {
            if (gameView != null) gameView.showScreen("PlayerCreationScreen");
        });

        helpButton = new JButton();
        styleWoodenButton(helpButton, "");
        helpButton.setFont(buttonFont);
        helpButton.addActionListener(e -> {
            if (gameView != null) gameView.showScreen("HelpScreen");
            else JOptionPane.showMessageDialog(null, "Help: WASD to move, E to interact. More info in game (press H).", "Basic Help", JOptionPane.INFORMATION_MESSAGE);
        });

        exitButton = new JButton();
        styleWoodenButton(exitButton, "");
        exitButton.setFont(buttonFont);
        exitButton.addActionListener(e -> System.exit(0));

        creditsButton = new JButton();
        styleWoodenButton(creditsButton, "");
        creditsButton.setFont(buttonFont);
        creditsButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Dibuat mati-matian oleh kelompok 8 oop K-2, aidan, huga, nathan, ella", "Credits", JOptionPane.INFORMATION_MESSAGE);
        });

        add(newGameButton);
        add(helpButton);
        add(exitButton);
        add(creditsButton);
    }

    @Override
    public void doLayout() {
        super.doLayout();
        int w = getWidth();
        int h = getHeight();
        // Button positions and sizes as ratios of original background size
        // newGameButton: x=418, y=330, w=168, h=127
        setButtonBoundsByRatio(newGameButton, 417, 355, 170, 130, w, h);
        // helpButton: x=167, y=514, w=123, h=73
        setButtonBoundsByRatio(helpButton, 167, 539, 123, 73, w, h);
        // exitButton: x=443, y=514, w=123, h=73
        setButtonBoundsByRatio(exitButton, 443, 539, 123, 73, w, h);
        // creditsButton: x=715, y=514, w=123, h=73
        setButtonBoundsByRatio(creditsButton, 715, 539, 123, 73, w, h);
    }

    private void setButtonBoundsByRatio(JButton button, int origX, int origY, int origW, int origH, int panelW, int panelH) {
        int x = (int) Math.round(origX * (panelW / (double) BG_ORIG_WIDTH));
        int y = (int) Math.round(origY * (panelH / (double) BG_ORIG_HEIGHT));
        int w = (int) Math.round(origW * (panelW / (double) BG_ORIG_WIDTH));
        int h = (int) Math.round(origH * (panelH / (double) BG_ORIG_HEIGHT));
        button.setBounds(x, y, w, h);
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
        button.setFocusable(false);
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