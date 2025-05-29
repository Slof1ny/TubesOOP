
package test;

import core.player.Player;
import core.world.ShippingBin;
import item.*;
import core.world.FarmMap; 
import time.GameCalendar;
import time.Time;

import java.util.concurrent.TimeUnit;

public class ShippingBinTester {

    public static void main(String[] args) {
        System.out.println("--- Starting Shipping Bin Tester ---");

        // 1. Setup Game Environment
        System.out.println("\n--- Setup: Player, FarmMap, ShippingBin ---");
        Player player = new Player("TestFarmer", "Male");
        player.getGold().add(500); // Beri pemain sedikit emas awal
        System.out.println("Player initial gold: " + player.getGold().getAmount() + "g");
        player.getInventory().showInventory(); // Tampilkan inventaris awal

        FarmMap farmMap = new FarmMap(player); // Inisialisasi FarmMap

        ShippingBin shippingBin = new ShippingBin();
        player.setShippingBin(shippingBin); // Atur Shipping Bin ke pemain

        // Setup Time and Calendar (needed for nextDay() and processSales trigger)
        GameCalendar gameCalendar = new GameCalendar();
        // Time class requires a Player instance in its constructor
        Time gameTime = new Time(gameCalendar, player);
        System.out.println("Game Time and Calendar initialized.");


        // 2. Add some items to player's inventory for selling
        System.out.println("\n--- Adding Items to Player Inventory for Testing ---");
        Item parsnip = new Crop("Parsnip", 50, 35, 1);
        Item cauliflower = new Crop("Cauliflower", 200, 150, 1);
        Item wheat = new Crop("Wheat", 50, 30, 3);
        Item coal = Misc.getItems().get("Coal"); // Assuming Coal is a Misc item
        Item fishChips = new Food("Fish n' Chips", 150, 135, 50);

        player.getInventory().addItem(parsnip, 10);
        player.getInventory().addItem(cauliflower, 5);
        player.getInventory().addItem(wheat, 20);
        player.getInventory().addItem(coal, 15);
        player.getInventory().addItem(fishChips, 2);

        player.getInventory().showInventory();

        // 3. Test Adding Items to Shipping Bin
        System.out.println("\n--- Test Scenario 1: Add Items to Shipping Bin ---");
        shippingBin.displayContents(); // Bin should be empty

        System.out.println("\n--- Adding 5 Parsnip ---");
        player.getShippingBin().addItem(player, parsnip, 5);
        player.getInventory().showInventory();
        shippingBin.displayContents();

        System.out.println("\n--- Adding 2 Cauliflower ---");
        player.getShippingBin().addItem(player, cauliflower, 2);
        player.getInventory().showInventory();
        shippingBin.displayContents();

        System.out.println("\n--- Adding 10 Wheat ---");
        player.getShippingBin().addItem(player, wheat, 10);
        player.getInventory().showInventory();
        shippingBin.displayContents();

        System.out.println("\n--- Adding 1 Fish n' Chips ---");
        player.getShippingBin().addItem(player, fishChips, 1);
        player.getInventory().showInventory();
        shippingBin.displayContents();

        System.out.println("\n--- Attempt to add item not in inventory (3 Melon) ---");
        Item melon = new Crop("Melon", 0, 250, 1); // Player doesn't have Melon
        player.getShippingBin().addItem(player, melon, 3);
        player.getInventory().showInventory();
        shippingBin.displayContents();

        System.out.println("\n--- Test Scenario 2: Processing Sales (Simulate sleeping / end of day) ---");
        System.out.println("Player Gold before sleep: " + player.getGold().getAmount() + "g");
        System.out.println("Player Total Gold Earned (Stats) before sleep: " + player.getStats().getTotalGoldEarned() + "g");
        shippingBin.displayContents();

        // Simulate sleep, which triggers sales processing
        gameTime.sleep2();

        System.out.println("Player Gold after sleep: " + player.getGold().getAmount() + "g");
        System.out.println("Player Total Gold Earned (Stats) after sleep: " + player.getStats().getTotalGoldEarned() + "g");
        shippingBin.displayContents(); // Should be empty now
        player.getInventory().showInventory(); // Inventaris tidak berubah (hanya uangnya bertambah)
        player.getStats().printStats(); // Tampilkan statistik penuh

        System.out.println("\n--- Test Scenario 3: Add more items after sales ---");
        player.getInventory().addItem(coal, 5); // Add some coal back to inventory
        player.getInventory().showInventory();
        player.getShippingBin().addItem(player, coal, 3);
        shippingBin.displayContents();

        System.out.println("\n--- Test Scenario 4: Fill Unique Slots (if 16 items are added) ---");
        // This scenario would require adding 16 unique items to fully test.
        // For brevity, we'll just demonstrate adding a few more.
        Item cranberry = new Crop("Cranberry", 0, 25, 10);
        Item pumpkin = new Crop("Pumpkin", 300, 250, 1);
        Item grape = new Crop("Grape", 100, 10, 20);

        player.getInventory().addItem(cranberry, 5);
        player.getInventory().addItem(pumpkin, 2);
        player.getInventory().addItem(grape, 10);

        player.getShippingBin().addItem(player, cranberry, 5);
        player.getShippingBin().addItem(player, pumpkin, 2);
        player.getShippingBin().addItem(player, grape, 10);
        player.getShippingBin().addItem(player, parsnip, 1); // Add more existing item
        shippingBin.displayContents();

        // Simulate end of day via normal time progression to process sales again
        System.out.println("\n--- Test Scenario 5: Processing Sales via Time Progression ---");
        System.out.println("Player Gold before time progression: " + player.getGold().getAmount() + "g");
        System.out.println("Player Total Gold Earned (Stats) before time progression: " + player.getStats().getTotalGoldEarned() + "g");

        // Run time for 24 game hours + 5 minutes to ensure nextDay() is called
        System.out.println("Simulating 24 game hours + 5 minutes...");
        // Untuk tujuan pengujian, kita akan menjalankan scheduler sebentar
        // Dalam game sungguhan, ini akan berjalan di latar belakang secara terus menerus
        gameTime.runTime2();
        try {
            TimeUnit.SECONDS.sleep(2); // Biarkan scheduler berjalan sebentar untuk memicu nextDay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Player Gold after time progression: " + player.getGold().getAmount() + "g");
        System.out.println("Player Total Gold Earned (Stats) after time progression: " + player.getStats().getTotalGoldEarned() + "g");
        shippingBin.displayContents(); // Should be empty

        System.out.println("\n--- Shipping Bin Tester Finished ---");
        // Penting: Matikan scheduler setelah selesai pengujian jika Time.runTime() dipanggil
        // Di aplikasi nyata, ini akan ditangani saat keluar dari game.
        // Untuk TimeTester, Anda perlu menambahkan metode shutdown ke kelas Time
        // Jika tidak, program mungkin akan terus berjalan.
    }
}
