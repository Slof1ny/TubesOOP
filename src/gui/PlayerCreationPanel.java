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
    private JButton confirmButton;

    public PlayerCreationPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;

        setLayout(new GridBagLayout());
        setBackground(new Color(50, 50, 70)); // Darker background
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Create Your Character");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);

        gbc.gridwidth = 1; // Reset gridwidth
        gbc.anchor = GridBagConstraints.WEST; // Align labels to the west

        JLabel nameLabel = new JLabel("Enter Name:");
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(nameLabel, gbc);

        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));
        nameField.setText(gameManager.getPlayer().getName()); // Pre-fill with default
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(nameField, gbc);

        JLabel genderLabel = new JLabel("Select Gender:");
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(genderLabel, gbc);

        String[] genders = {"Male", "Female", "Other"};
        genderComboBox = new JComboBox<>(genders);
        genderComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        // Pre-select based on default, if applicable
        if (gameManager.getPlayer().getGender().equalsIgnoreCase("Female")) {
            genderComboBox.setSelectedItem("Female");
        } else if (gameManager.getPlayer().getGender().equalsIgnoreCase("Other")) {
            genderComboBox.setSelectedItem("Other");
        } else {
            genderComboBox.setSelectedItem("Male");
        }
        gbc.gridx = 1;
        gbc.gridy = 2;
        add(genderComboBox, gbc);

        confirmButton = new JButton("Start Adventure!");
        confirmButton.setFont(new Font("Arial", Font.BOLD, 20));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(confirmButton, gbc);

        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText().trim();
                String gender = (String) genderComboBox.getSelectedItem();

                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(PlayerCreationPanel.this,
                            "Player name cannot be empty!",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Player player = gameManager.getPlayer();
                player.setName(name); // Requires setName in Player
                player.setGender(gender); // Requires setGender in Player

                // PlayerInfoPanel is already set up with this gameManager,
                // so refreshing it will show the new name/gender.
                if (gameManager.getPlayerInfoPanel() != null) {
                    gameManager.getPlayerInfoPanel().refreshPlayerInfo();
                }
                
                // Initialize player location correctly after creation before going to farm
                gameManager.transitionMap(gameManager.getFarmMap().getName());


                gameView.showScreen("GameScreen");
            }
        });
    }
}