package action;

import fishing.FishingManager;
import fishing.FishingLocation;
import core.player.Player;
import core.world.FarmMap;
import core.world.Tile;
import item.Crop;
import item.Seed;
import time.GameCalendar;
import time.Time;

import java.util.Scanner;
import java.util.concurrent.Future;

public class Action {
    public static Future<?> fish(FarmMap farm, FishingLocation location, Player player, Time time, GameCalendar calendar, Scanner scanner) throws IllegalArgumentException {
        if (!player.isEquipped("Fishing Rod")) {
            throw new IllegalArgumentException("Fishing rod must be equipped to fish.");
        }

        return FishingManager.fish(farm, location, player, time, calendar, scanner);
    }

    // public static Future<?> cook() {
    // }

    public static void till (FarmMap farm, Player player, Time time) throws IllegalArgumentException {
        if (!player.isEquipped("Hoe")) {
            throw new IllegalArgumentException("Hoe must be equipped to till.");
        }

        if (player.getEnergy() < (Player.MIN_ENERGY + 5)){ 
            System.out.println("Not enough energy to till.");
            return;
        }

        Tile tile = farm.getTileAt(player.getX(), player.getY());
        if (tile == null) {
            System.out.println("Invalid tile position.");
            return;
        }
        if (tile.getType() != Tile.TileType.UNTILLED) { 
            System.out.println("Cannot till this tile. It's not untilled land.");
            return;
        }

        tile.setType(Tile.TileType.TILLED);
        System.out.println("You tilled the land at (" + player.getX() + ", " + player.getY() + ").");

        player.setEnergy(player.getEnergy() - 5); 
        time.advanceGameMinutes(5); 
    }

    public static void recoverLand(FarmMap farm, Player player, Time time) throws IllegalArgumentException {
        if (!player.isEquipped("Pickaxe")) {
            throw new IllegalArgumentException("Pickaxe must be equipped to recover land.");
        }

        if (player.getEnergy() < 5) { // Check if player has at least 5 energy to spend. MIN_ENERGY is -20.
                                      // Spending 5 energy should be possible even if current energy is 0 down to -15.
                                      // Let's adjust to ensure they can go into negative if above MIN_ENERGY + cost
            if (player.getEnergy() - 5 < Player.MIN_ENERGY) {
                 System.out.println("Not enough energy to recover land. (Would go below " + Player.MIN_ENERGY + ")");
                 return;
            }
        }


        Tile tile = farm.getTileAt(player.getX(), player.getY());
        if (tile == null) {
            System.out.println("Invalid tile position. Cannot recover land here.");
            throw new IllegalStateException("Player is on an invalid tile position."); // Or handle more gracefully
        }

        if (tile.getType() != Tile.TileType.TILLED && tile.getType() != Tile.TileType.PLANTED) {
             System.out.println("Cannot recover this tile. It's not tilled soil or has no planted crop.");
             return;
        }

        // If there's a crop, it will be destroyed.
        if (tile.getType() == Tile.TileType.PLANTED) {
            System.out.println("The crop on this tile will be destroyed by recovering the land.");
            tile.clearPlantedCrop(); // Removes the crop reference
        }

        tile.setType(Tile.TileType.UNTILLED); // Change tile type to untilled land
        // Note: Tile.setType should handle resetting its internal state (like deployedChar if any, though not for tilled/planted)

        System.out.println("You recovered the land at (" + player.getX() + ", " + player.getY() + ") back to untilled land.");
        
        player.setEnergy(player.getEnergy() - 5); // Deduct energy [cite: 188]
        time.advanceGameMinutes(5); // Advance time [cite: 188]
    }

    public static void plant(FarmMap farm, Player player, Time time, GameCalendar calendar, Seed seed) throws IllegalArgumentException {
        if (seed == null) {
            throw new IllegalArgumentException("No seed provided to plant.");
        }

        if (player.getInventory().getItemCount(seed) < 1) {
            System.out.println("You don't have " + seed.getName() + " in your inventory.");
            return;
        }

        if (player.getEnergy() < (Player.MIN_ENERGY + 5)) { 
            System.out.println("Not enough energy to plant.");
            return;
        }

        Tile tile = farm.getTileAt(player.getX(), player.getY());
        if (tile == null) {
            System.out.println("Invalid tile position.");
            return;
        }
        if (tile.getType() != Tile.TileType.TILLED) { 
            System.out.println("Cannot plant here. This tile is not tilled.");
            return;
        }

        if (!seed.getSeasons().contains(calendar.getCurrentSeason())) {
            System.out.println("You cannot plant " + seed.getName() + " in " + calendar.getCurrentSeason() + ".");
            return;
        }

        Crop newCrop = new Crop(seed); 
        tile.plantCrop(newCrop);
        player.getInventory().removeItem(seed, 1);
        System.out.println("You planted " + seed.getName() + " at (" + player.getX() + ", " + player.getY() + ").");
        player.setEnergy(player.getEnergy() - 5); 
        time.advanceGameMinutes(5); 
    }

    public static void water (FarmMap farm, Player player, Time time, GameCalendar calendar) throws IllegalArgumentException {
        if (!player.isEquipped("Watering Can")) {
            throw new IllegalArgumentException("Watering Can must be equipped to water.");
        }

        if (player.getEnergy() < (Player.MIN_ENERGY + 5)) {
            System.out.println("Not enough energy to water.");
            return;
        }

        Tile tile = farm.getTileAt(player.getX(), player.getY());
        if (tile == null) {
            System.out.println("Invalid tile position.");
            return;
        }
        if (tile.getType() != Tile.TileType.PLANTED || tile.getPlantedCrop() == null) {
            System.out.println("Nothing to water here. This tile does not have a planted crop.");
            return;
        }

        Crop crop = tile.getPlantedCrop();
        if (crop.isWateredToday(calendar.getTotalDay())) {
            System.out.println("This crop has already been watered today.");
            return;
        }

        crop.water(calendar.getTotalDay());
        System.out.println("You watered the crop at (" + player.getX() + ", " + player.getY() + ").");
        player.setEnergy(player.getEnergy() - 5);
        time.advanceGameMinutes(5); 
    }

    public static void harvest(FarmMap farm, Player player, Time time) {
        if (player.getEnergy() < (Player.MIN_ENERGY + 5)) {
            System.out.println("Not enough energy to harvest.");
            return;
        }

        Tile tile = farm.getTileAt(player.getX(), player.getY());
        if (tile == null) {
            System.out.println("Invalid tile position.");
            return;
        }
        if (tile.getType() != Tile.TileType.PLANTED || tile.getPlantedCrop() == null) {
            System.out.println("Nothing to harvest here. This tile does not have a planted crop.");
            return;
        }

        Crop crop = tile.getPlantedCrop();
        if (!crop.isHarvestable()) {
            System.out.println("This crop is not ready to be harvested yet.");
            return;
        }

        player.getInventory().addItem(crop.getHarvestedProduct(), crop.getQuantityPerHarvest());
        System.out.println("You harvested " + crop.getQuantityPerHarvest() + " " + crop.getHarvestedProduct().getName() + " from (" + player.getX() + ", " + player.getY() + ").");

        tile.setType(Tile.TileType.TILLED);
        tile.clearPlantedCrop();
        player.setEnergy(player.getEnergy() - 5); 
        time.advanceGameMinutes(5); 
    }
}