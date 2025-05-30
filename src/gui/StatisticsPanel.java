package gui;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

import system.GameManager;
import core.player.Player;
import core.player.PlayerStats;
import core.player.RelationshipStatus;
import core.world.Season;
import fishing.FishType;
import npc.NPC;
import time.GameCalendar;

public class StatisticsPanel extends JPanel {
    private GameView gameView;
    private GameManager gameManager;
    private JTextArea statsTextArea;
    private JButton closeButton;

    public StatisticsPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(240, 245, 250)); // Light, clean background

        JLabel titleLabel = new JLabel("Spakbor Hills - Game Statistics", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        statsTextArea = new JTextArea("Loading statistics...");
        statsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsTextArea.setEditable(false);
        statsTextArea.setLineWrap(true);
        statsTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(statsTextArea);
        add(scrollPane, BorderLayout.CENTER);

        closeButton = new JButton("Continue Playing");
        closeButton.setFont(new Font("Arial", Font.BOLD, 16));
        closeButton.addActionListener(e -> {
            // Return to the screen player was on before stats, or a default game screen
            if (gameManager.getPlayer().getLocation().equals(gameManager.getFarmMap().getName())) {
                gameView.showScreen("GameScreen");
            } else if (gameManager.getPlayer().getLocation().equals(gameManager.getCityMap().getName())) {
                gameView.showScreen("CityScreen");
            } else if (gameManager.getPlayer().getLocation().equals(gameManager.getHouseMap().getName())) {
                gameView.showScreen("HouseScreen");
            } else {
                gameView.showScreen("GameScreen"); // Fallback
            }
        });

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(closeButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setFocusable(true); // For potential key listeners if needed
    }

    public void refreshStatistics() {
        Player player = gameManager.getPlayer();
        PlayerStats stats = player.getStats();
        GameCalendar calendar = gameManager.getGameCalendar();
        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#,##0.00");

        sb.append("--- Player & Game Overview ---\n");
        sb.append(String.format("Player Name: %s\n", player.getName()));
        sb.append(String.format("Farm Name: %s\n", player.getFarmName()));
        sb.append(String.format("Total Days Played: %d\n\n", calendar.getTotalDay()));

        sb.append("--- Financial Statistics ---\n");
        sb.append(String.format("Total Income: %dg\n", stats.getTotalGoldEarned()));
        sb.append(String.format("Total Expenditure: %dg\n", stats.getTotalGoldSpent()));
        sb.append(String.format("Net Worth (Current Gold): %dg\n\n", player.getGold().getAmount()));

        sb.append("--- Seasonal Averages ---\n");
        for (Season season : Season.values()) {
            int income = stats.getSeasonalIncome().getOrDefault(season, 0);
            int expenditure = stats.getSeasonalExpenditure().getOrDefault(season, 0);
            int days = stats.getDaysPlayedPerSeason().getOrDefault(season, 0);

            if (days > 0) {
                sb.append(String.format("Season: %s (Played %d days)\n", season, days));
                sb.append(String.format("  Avg Income/Day: %sg\n", df.format((double) income / days)));
                sb.append(String.format("  Avg Expenditure/Day: %sg\n", df.format((double) expenditure / days)));
                // Average per 10-day season
                sb.append(String.format("  Projected Avg Income/Full Season: %sg\n", df.format(((double) income / days) * 10.0)));
                sb.append(String.format("  Projected Avg Expenditure/Full Season: %sg\n\n", df.format(((double) expenditure / days) * 10.0)));
            }
        }

        sb.append("--- NPC Relationships & Interactions ---\n");
        if (gameManager.getAllNpcs() == null || gameManager.getAllNpcs().isEmpty()) {
            sb.append("No NPC data available.\n");
        } else {
            for (NPC npc : gameManager.getAllNpcs()) {
                sb.append(String.format("%s:\n", npc.getName()));
                sb.append(String.format("  Relationship: %s (%d Hearts)\n", player.getRelationshipStatus(npc), npc.getHeartPoints()));
                sb.append(String.format("  Chats: %d\n", stats.getNpcChatFrequency().getOrDefault(npc.getName(), 0)));
                sb.append(String.format("  Gifts Given: %d\n", stats.getNpcGiftFrequency().getOrDefault(npc.getName(), 0)));
                // Visiting frequency is not explicitly tracked as a distinct action yet.
            }
        }
        sb.append("\n");

        sb.append("--- Farming & Production ---\n");
        sb.append(String.format("Total Crops Harvested: %d\n", stats.getTotalCropsHarvested()));
        // Could add breakdown per crop type if PlayerStats.getItemCount() is used for specific crops.

        sb.append(String.format("\nTotal Fish Caught: %d\n", stats.getTotalFishCaught()));
        sb.append(String.format("  Common Fish: %d\n", stats.getFishCaughtByType().getOrDefault(FishType.COMMON, 0)));
        sb.append(String.format("  Regular Fish: %d\n", stats.getFishCaughtByType().getOrDefault(FishType.REGULAR, 0)));
        sb.append(String.format("  Legendary Fish: %d\n", stats.getFishCaughtByType().getOrDefault(FishType.LEGENDARY, 0)));
        // Could add breakdown per fish type if PlayerStats.getItemCount() is used.

        statsTextArea.setText(sb.toString());
        statsTextArea.setCaretPosition(0); // Scroll to top
        this.requestFocusInWindow();
    }
}