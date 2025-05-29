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

        if (player.getEnergy() < 5){ 
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

    public static void recoverLand(FarmMap farm, Player player, Time time) throws IllegalArgumentException { // Removed Calendar as it's not used directly
        if (!player.isEquipped("Pickaxe")) {
            throw new IllegalArgumentException("Pickaxe must be equipped to recover land.");
        }

        if (player.getEnergy() < 5){ 
            System.out.println("Not enough energy to recover land.");
            return;
        }

        Tile tile = farm.getTileAt(player.getX(), player.getY());
        if (tile == null) {
            System.out.println("Invalid tile position.");
            return;
        }
        if (tile.getType() != Tile.TileType.TILLED && tile.getType() != Tile.TileType.PLANTED) { // can recover tilled or planted
             System.out.println("Cannot recover this tile. It's not tilled soil or planted crop.");
             return;
        }
        if (tile.getType() == Tile.TileType.PLANTED) {
            tile.clearPlantedCrop(); 
        }

        tile.setType(Tile.TileType.UNTILLED); 
        System.out.println("You recovered the land at (" + player.getX() + ", " + player.getY() + ").");
        player.setEnergy(player.getEnergy() - 5); 
        time.advanceGameMinutes(5); 
    }

    public static void plant(FarmMap farm, Player player, Time time, GameCalendar calendar, Seed seed) throws IllegalArgumentException {
        if (seed == null) {
            throw new IllegalArgumentException("No seed provided to plant.");
        }

        if (player.getInventory().getItemCount(seed) < 1) {
            System.out.println("You don't have " + seed.getName() + " in your inventory.");
            return;
        }

        if (player.getEnergy() < 5) { 
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

        if (player.getEnergy() < 5) {
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
        if (player.getEnergy() < 5) {
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