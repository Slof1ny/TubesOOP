// TubesOOP/src/gui/MainMenu.java
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {

    private GameView gameView; // Reference to the main JFrame

    public MainMenu() {
        setLayout(new GridBagLayout()); // Use GridBagLayout for flexible centering
        setBackground(new Color(30, 30, 30)); // Dark background

        // Title Label
        JLabel titleLabel = new JLabel("Spakbor Hills", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60)); // Larger, bold font
        titleLabel.setForeground(new Color(255, 223, 0)); // Gold color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns if needed
        gbc.insets = new Insets(80, 0, 80, 0); // Top/Bottom padding
        add(titleLabel, gbc);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10)); // 4 rows, 1 column, 10px vertical gap
        buttonPanel.setOpaque(false); // Make transparent so background color shows

        // Button styling
        Font buttonFont = new Font("Arial", Font.BOLD, 22);
        Dimension buttonSize = new Dimension(250, 60);

        JButton newGameButton = new JButton("New Game");
        newGameButton.setFont(buttonFont);
        newGameButton.setPreferredSize(buttonSize);
        newGameButton.setBackground(new Color(70, 130, 180)); // Steel blue
        newGameButton.setForeground(Color.WHITE);
        newGameButton.setFocusPainted(false); // Remove border paint
        newGameButton.setBorderPainted(false); // No border

        JButton loadGameButton = new JButton("Load Game (Bonus)");
        loadGameButton.setFont(buttonFont);
        loadGameButton.setPreferredSize(buttonSize);
        loadGameButton.setBackground(new Color(70, 130, 180));
        loadGameButton.setForeground(Color.WHITE);
        loadGameButton.setFocusPainted(false);
        loadGameButton.setBorderPainted(false);

        JButton helpButton = new JButton("Help");
        helpButton.setFont(buttonFont);
        helpButton.setPreferredSize(buttonSize);
        helpButton.setBackground(new Color(70, 130, 180));
        helpButton.setForeground(Color.WHITE);
        helpButton.setFocusPainted(false);
        helpButton.setBorderPainted(false);

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(buttonFont);
        exitButton.setPreferredSize(buttonSize);
        exitButton.setBackground(new Color(178, 34, 34)); // Firebrick red
        exitButton.setForeground(Color.WHITE);
        exitButton.setFocusPainted(false);
        exitButton.setBorderPainted(false);

        // Add action listeners for buttons
        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("New Game button clicked!"); // DEBUGGING PRINT STATEMENT
                if (gameView != null) {
                    gameView.showScreen("GameScreen"); // Corrected to "GameScreen" as per Step 6
                    // Request focus for the FarmMapPanel so it can receive key events
                    // This is handled inside showScreen() now for "GameScreen"
                }
            }
        });

        loadGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameView, "Load Game is a bonus feature and not yet implemented!", "Feature Not Available", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameView, "This is Spakbor Hills! Use WASD to move. More help coming soon!", "Help", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); // Terminate the application
            }
        });

        // Add buttons to the button panel
        buttonPanel.add(newGameButton);
        buttonPanel.add(loadGameButton);
        buttonPanel.add(helpButton);
        buttonPanel.add(exitButton);

        // Add button panel to the main menu panel
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 50, 0); // Bottom padding
        add(buttonPanel, gbc);
    }

    // Method to set the reference to the main GameView frame
    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
}