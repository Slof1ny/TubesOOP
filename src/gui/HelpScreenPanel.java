package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import system.GameManager; // Not strictly needed if content is static, but good for consistency

public class HelpScreenPanel extends JPanel {
    private GameView gameView;
    // private GameManager gameManager; // If help content needs to be dynamic

    public HelpScreenPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        // this.gameManager = gameManager;

        setLayout(new BorderLayout(10,10));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        setBackground(new Color(230, 240, 250)); // Light background

        JLabel titleLabel = new JLabel("Spakbor Hills - Help & Actions", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        JTextArea helpTextArea = new JTextArea();
        helpTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        helpTextArea.setEditable(false);
        helpTextArea.setLineWrap(true);
        helpTextArea.setWrapStyleWord(true);
        helpTextArea.setText(getHelpContent()); // Populate with actions

        JScrollPane scrollPane = new JScrollPane(helpTextArea);
        add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Game");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        backButton.addActionListener(e -> goBack());

        // Key listener for ESC
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE || evt.getKeyCode() == KeyEvent.VK_H || evt.getKeyCode() == KeyEvent.VK_F1 ) {
                    goBack();
                }
            }
        });
        setFocusable(true);
    }

    private void goBack() {
        gameView.returnToPreviousScreen(); // << USE THE NEW GameView METHOD
    }
    
    public void onShow() { // Call this when panel is made visible
        this.requestFocusInWindow();
    }

    private String getHelpContent() {
        // Based on your specification document (pages 25-27) [cite: 542, 545, 548, 551, 554]
        // Format this nicely.
        return "--- Game Actions ---\n\n" +
               "1.  Tilling: Converts land to soil. Needs Hoe. (-5 energy, -5 mins)\n" +
               "2.  Recover Land: Converts soil back to land. Needs Pickaxe. (-5 energy, -5 mins)\n" +
               "3.  Planting: Plant seeds on tilled soil. Needs Seeds. (-5 energy, -5 mins)\n" +
               "4.  Watering: Water planted crops. Needs Watering Can. (-5 energy, -5 mins)\n" +
               "5.  Harvesting: Harvest mature crops. (-5 energy, -5 mins)\n" +
               "6.  Eating: Consume edible items to restore energy. (+energy, -5 mins)\n" +
               "7.  Sleeping: Pass time to next morning (06:00). Restores energy (rules apply for low energy). Needs Bed (House).\n" +
               "8.  Cooking: Cook food from recipes. Needs ingredients, fuel, House/Stove. (-10 energy, 1 hr passive cooking)\n" +
               "9.  Fishing: Catch fish at various locations. Needs Fishing Rod. (-5 energy, time stops, +15 mins after)\n" +
               "10. Proposing: Propose to an NPC (max hearts). Needs Proposal Ring. (-10 or -20 energy, -1 hr)\n" +
               "11. Marry: Marry your fiance. Needs Proposal Ring. (-80 energy, time skips to 22:00)\n" +
               "12. Watching TV: See today's weather. Needs TV (House). (-5 energy, -15 mins)\n" +
               "13. Visiting: Go to World Map locations from Farm Map edge. (-10 energy, -15 mins)\n" +
               "14. Chatting: Talk to NPCs at their homes/locations. (-10 energy, +10 hearts, -10 mins)\n" +
               "15. Gifting: Give items to NPCs. Effects vary. (-5 energy, -10 mins)\n" +
               "16. Selling: Place items in Shipping Bin to sell overnight. (-15 mins after finishing)\n\n" +
               "--- Game Controls ---\n" +
               "WASD: Move Player\n" +
               "E: Interact with objects/NPCs/locations\n" +
               "I: Open/Close Inventory\n" +
               "P: Plant Seed (on Farm Map)\n" +
               "T: Till Land (on Farm Map)\n" +
               "H: Harvest Crop (on Farm Map) / Access Help Screen (from game)\n" +
               "R: Water Crop (on Farm Map)\n" +
               "M: Attempt to move to City from Farm edge (or vice-versa)\n" +
               "ESC: Close current menu/screen (e.g., Help, Inventory, NPC Interaction)\n\n" +
               "--- Objective ---\n" +
               "Continue playing indefinitely. Statistics will be shown if:\n" +
               "- You earn 17,209 Gold.\n" +
               "- You get married.\n";
    }
}