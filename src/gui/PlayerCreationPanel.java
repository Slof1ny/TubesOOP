package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import system.GameManager;
import core.player.Player;

public class PlayerCreationPanel extends JPanel {
    private GameView gameView;
    private GameManager gameManager;

    private JTextField nameField;
    private JComboBox<String> genderComboBox;
    private JTextField farmNameField;
    private JLabel farmNameLabel; // Make it a field to ensure it's being handled
    private JButton confirmButton;

    public PlayerCreationPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;

        setLayout(new GridBagLayout());
        setBackground(new Color(50, 50, 70)); // Darker background
        GridBagConstraints gbc = new GridBagConstraints();

        // Default insets for most components
        gbc.insets = new Insets(5, 10, 5, 10); // Reduced bottom inset for tighter packing between rows

        // Title
        JLabel titleLabel = new JLabel("Create Your Character & Farm");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Allow title to use horizontal space
        gbc.insets = new Insets(20, 10, 20, 10); // More vertical padding for title
        add(titleLabel, gbc);

        // Reset gridwidth and insets for subsequent components
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);


        // Player Name Label
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_START; // Align label text to the start of its cell
        gbc.fill = GridBagConstraints.NONE;        // Label should not expand
        gbc.weightx = 0.0;                         // Label doesn't take extra horizontal space
        add(nameLabel, gbc);

        // Player Name Field
        nameField = new JTextField(15); // Adjusted columns slightly
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setText(gameManager.getPlayer().getName());
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.LINE_END; // Align field to the end of its cell
        gbc.fill = GridBagConstraints.HORIZONTAL; // Field should expand horizontally
        gbc.weightx = 1.0;                        // Field takes available extra horizontal space
        add(nameField, gbc);

        // Gender Label
        JLabel genderLabel = new JLabel("Select Gender:");
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        add(genderLabel, gbc);

        // Gender ComboBox
        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        // Pre-select gender logic
        Player defaultPlayer = gameManager.getPlayer();
        if (defaultPlayer.getGender().equalsIgnoreCase("Female")) genderComboBox.setSelectedItem("Female");
        else if (defaultPlayer.getGender().equalsIgnoreCase("Other")) genderComboBox.setSelectedItem("Other");
        else genderComboBox.setSelectedItem("Male");
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(genderComboBox, gbc);

        // Farm Name Label
        farmNameLabel = new JLabel("Farm Name:"); // Ensure text is correct
        farmNameLabel.setForeground(Color.WHITE);
        farmNameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        // farmNameLabel.setBorder(BorderFactory.createLineBorder(Color.RED)); // DEBUG line
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        add(farmNameLabel, gbc);

        // Farm Name Field
        farmNameField = new JTextField(15);
        farmNameField.setFont(new Font("Arial", Font.PLAIN, 18));
        farmNameField.setText(defaultPlayer.getFarmName() != null ? defaultPlayer.getFarmName() : "My Farm");
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        add(farmNameField, gbc);

        // Confirm Button
        confirmButton = new JButton("Start Adventure!");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 20));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.BLACK);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span both columns
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // Button should not expand, use its preferred size
        gbc.weightx = 0.0; // Button doesn't take extra horizontal space
        gbc.insets = new Insets(30, 10, 20, 10); // << INCREASED TOP INSET to 30 for more spacing
        add(confirmButton, gbc);

        // ActionListener for confirmButton (remains the same)
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String playerName = nameField.getText().trim();
                String playerGender = (String) genderComboBox.getSelectedItem();
                String playerFarmName = farmNameField.getText().trim();

                if (playerName.isEmpty()) {
                    JOptionPane.showMessageDialog(PlayerCreationPanel.this, "Player name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (playerFarmName.isEmpty()) {
                    JOptionPane.showMessageDialog(PlayerCreationPanel.this, "Farm name cannot be empty!", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Player player = gameManager.getPlayer();
                player.setName(playerName);
                player.setGender(playerGender);
                player.setFarmName(playerFarmName);

                if (gameManager.getTopInfoBarPanel() != null) {
                    gameManager.getTopInfoBarPanel().refreshInfo();
                }
                
                gameManager.transitionMap(gameManager.getFarmMap().getName());
                gameView.showScreen("GameScreen");
            }
        });
    }
}