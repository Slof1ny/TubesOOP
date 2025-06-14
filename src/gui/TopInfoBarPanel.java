package gui;

import javax.swing.*;
import java.awt.*;
import system.GameManager;
import core.player.Player;
import time.GameCalendar;
import time.Time;

public class TopInfoBarPanel extends JPanel {
    private GameManager gameManager;

    private JLabel nameLabel;
    private JLabel farmNameLabel;
    private JLabel energyLabel;
    private JLabel goldLabel;
    private JLabel locationLabel;
    private JLabel timeLabel;
    private JLabel seasonLabel;
    private JLabel dayLabel; 
    private JLabel weatherLabel;

    public TopInfoBarPanel(GameManager gameManager) {
        this.gameManager = gameManager;
        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 5)); // Arrange labels horizontally
        setBackground(new Color(50, 50, 70)); // Darker background like PlayerCreation
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        Font infoFont = new Font("Arial", Font.BOLD, 12);
        Color fontColor = Color.WHITE;

        nameLabel = new JLabel("Name: N/A");
        farmNameLabel = new JLabel("Farm: N/A");
        energyLabel = new JLabel("Energy: N/A");
        goldLabel = new JLabel("Gold: N/A");
        locationLabel = new JLabel("Location: N/A");
        timeLabel = new JLabel("Time: N/A");
        dayLabel = new JLabel("Day: N/A");
        seasonLabel = new JLabel("Season: N/A");
        weatherLabel = new JLabel("Weather: N/A");

        JLabel[] labels = {nameLabel, farmNameLabel, energyLabel, goldLabel, locationLabel, timeLabel,dayLabel, seasonLabel, weatherLabel};
        for (JLabel label : labels) {
            label.setFont(infoFont);
            label.setForeground(fontColor);
            add(label);
        }
        refreshInfo(); // Initial population
    }

    public void refreshInfo() {
        if (gameManager == null) return;

        Player player = gameManager.getPlayer();
        Time gameTime = gameManager.getGameTime();
        GameCalendar gameCalendar = gameManager.getGameCalendar();

        SwingUtilities.invokeLater(() -> { // This is important!
            if (player != null) {
                nameLabel.setText("Name: " + player.getName());
                farmNameLabel.setText("Farm: " + player.getFarmName());
                energyLabel.setText("Energy: " + player.getEnergy() + "/100");
                goldLabel.setText("Gold: " + player.getGold().getAmount() + "g");
                locationLabel.setText("Location: " + player.getLocation());
            }
            if (gameTime != null) {
                timeLabel.setText(String.format("Time: %02d:%02d", gameTime.getHour(), gameTime.getMinute()));
            }
            if (gameCalendar != null) {
                dayLabel.setText("Day: " + gameCalendar.getTotalDay());
                seasonLabel.setText("Season: " + gameCalendar.getCurrentSeason());
                weatherLabel.setText("Weather: " + gameCalendar.getCurrentWeather());
            }
        });
    }
}