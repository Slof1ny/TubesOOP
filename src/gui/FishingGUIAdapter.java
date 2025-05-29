package gui;

import core.player.Player;
import core.world.FarmMap;
import fishing.FishingLocation;
import fishing.FishingManager; // Assuming FishType is accessible
import fishing.FishRegistry;  // Assuming FishType is accessible
import item.Fish;
import time.GameCalendar;
import time.Time;
import fishing.FishType; // Explicit import
import java.awt.Component;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.Random;

public class FishingGUIAdapter {

    public static void startFishingGUI(FarmMap farm, FishingLocation location, Player player, Time time, GameCalendar calendar, Component parentComponent) {
        if (!player.isEquipped("Fishing Rod")) { //
            JOptionPane.showMessageDialog(parentComponent, "You need to equip a Fishing Rod to fish!", "Cannot Fish", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!location.canFishAt(player)) {
            JOptionPane.showMessageDialog(parentComponent, "You can't fish here from this spot.", "Cannot Fish", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (player.getEnergy() < 5) { //
            JOptionPane.showMessageDialog(parentComponent, "Not enough energy to fish.", "Cannot Fish", JOptionPane.WARNING_MESSAGE);
            return;
        }

        player.setEnergy(player.getEnergy() - 5); //
        time.advanceGameMinutes(15); //
        // Refresh player info if a panel reference is available, or rely on next general refresh
        // For now, assume PlayerInfoPanel will catch up or refresh on next key action.

        List<Fish> catchables = location.getPossibleFish(calendar.getCurrentSeason(), time, calendar.getCurrentWeather(), location); //
        if (catchables.isEmpty()) { //
            JOptionPane.showMessageDialog(parentComponent, "No fish seem to be biting right now.", "Fishing", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        FishingManager.FishingRNG rng = new FishingManager.FishingRNG(); //
        Fish chosenFish = catchables.get(rng.getRandomNumber(0, catchables.size() - 1)); //

        int bound = switch (chosenFish.getType()) { //
            case COMMON    -> 10; //
            case REGULAR   -> 100; //
            case LEGENDARY -> 500; //
        };
        int tries = chosenFish.getType() == FishType.LEGENDARY ? 7 : 10; //
        int secret = rng.getRandomNumber(1, bound); //

        JOptionPane.showMessageDialog(parentComponent, "You cast your line...\nA fish is on the hook! Try to guess its number.", "Fishing Minigame", JOptionPane.INFORMATION_MESSAGE);

        boolean success = false;
        for (int i = 0; i < tries; i++) {
            String input = JOptionPane.showInputDialog(parentComponent,
                    "Guess a number between 1 and " + bound + ".\nAttempts remaining: " + (tries - i),
                    "Catch the Fish!", JOptionPane.QUESTION_MESSAGE);

            if (input == null) { // Player cancelled
                JOptionPane.showMessageDialog(parentComponent, "The fish got away...", "Fishing", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int guess = Integer.parseInt(input.trim());
                if (guess == secret) { //
                    success = true;
                    break;
                } else {
                    String feedback = guess < secret ? "Too low!" : "Too high!"; //
                    if (i < tries - 1) { // Don't show feedback on last failed attempt
                        JOptionPane.showMessageDialog(parentComponent, feedback, "Try Again", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parentComponent, "Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
                i--; // Decrement to allow another attempt for invalid input
            }
        }

        if (success) {
            player.getInventory().addItem(chosenFish, 1); //
            JOptionPane.showMessageDialog(parentComponent, "Congratulations! You caught: " + chosenFish.getName() + "!", "Fish Caught!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(parentComponent, "Oh no! The fish got away...", "Fishing Failed", JOptionPane.WARNING_MESSAGE); //
        }
        // PlayerInfoPanel should be refreshed after this action by FarmMapController
    }
}