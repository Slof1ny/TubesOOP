// TubesOOP/src/gui/MainMenu.java
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {

    private GameView gameView;

    public MainMenu() {
        setLayout(new GridBagLayout());
        setBackground(new Color(30, 30, 30));

        JLabel titleLabel = new JLabel("Spakbor Hills", SwingConstants.CENTER); //
        titleLabel.setFont(new Font("Serif", Font.BOLD, 60)); //
        titleLabel.setForeground(new Color(255, 223, 0)); //
        GridBagConstraints gbc = new GridBagConstraints(); //
        gbc.gridx = 0; //
        gbc.gridy = 0; //
        gbc.gridwidth = 2; //
        gbc.insets = new Insets(80, 0, 80, 0); //
        add(titleLabel, gbc); //

        JPanel buttonPanel = new JPanel(); //
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 10)); //
        buttonPanel.setOpaque(false); //

        Font buttonFont = new Font("Arial", Font.BOLD, 22); //
        Dimension buttonSize = new Dimension(250, 60); //

        JButton newGameButton = new JButton("New Game"); //
        newGameButton.setFont(buttonFont); //
        newGameButton.setPreferredSize(buttonSize); //
        newGameButton.setBackground(new Color(70, 130, 180)); //
        newGameButton.setForeground(Color.WHITE); //
        newGameButton.setFocusPainted(false); //
        newGameButton.setBorderPainted(false); //

        // ... (Load Game, Help, Exit buttons remain the same) ...
        JButton loadGameButton = new JButton("Load Game (Bonus)"); //
        loadGameButton.setFont(buttonFont); //
        loadGameButton.setPreferredSize(buttonSize); //
        loadGameButton.setBackground(new Color(70, 130, 180)); //
        loadGameButton.setForeground(Color.WHITE); //
        loadGameButton.setFocusPainted(false); //
        loadGameButton.setBorderPainted(false); //

        JButton helpButton = new JButton("Help"); //
        helpButton.setFont(buttonFont); //
        helpButton.setPreferredSize(buttonSize); //
        helpButton.setBackground(new Color(70, 130, 180)); //
        helpButton.setForeground(Color.WHITE); //
        helpButton.setFocusPainted(false); //
        helpButton.setBorderPainted(false); //

        JButton exitButton = new JButton("Exit"); //
        exitButton.setFont(buttonFont); //
        exitButton.setPreferredSize(buttonSize); //
        exitButton.setBackground(new Color(178, 34, 34)); //
        exitButton.setForeground(Color.WHITE); //
        exitButton.setFocusPainted(false); //
        exitButton.setBorderPainted(false); //


        newGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameView != null) {
                    gameView.showScreen("PlayerCreationScreen"); // MODIFIED LINE
                }
            }
        });

        loadGameButton.addActionListener(new ActionListener() { //
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameView, "Load Game is a bonus feature and not yet implemented!", "Feature Not Available", JOptionPane.INFORMATION_MESSAGE); //
            }
        });

        helpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameView != null) {
                    gameView.showScreen("HelpScreen"); // << SHOW THE NEW HELP PANEL
                } else { // Fallback if gameView somehow not set (should not happen)
                    JOptionPane.showMessageDialog(null, 
                        "Help: WASD to move, E to interact. More info in game (press H).", 
                        "Basic Help", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        exitButton.addActionListener(new ActionListener() { //
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0); //
            }
        });

        buttonPanel.add(newGameButton); //
        buttonPanel.add(loadGameButton); //
        buttonPanel.add(helpButton); //
        buttonPanel.add(exitButton); //

        gbc.gridy = 1; //
        gbc.insets = new Insets(0, 0, 50, 0); //
        add(buttonPanel, gbc); //
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
}