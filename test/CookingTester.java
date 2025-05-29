package test;


import item.CropRegistry;
import item.Misc;
import cooking.Fuel;
import recipe.Recipe;
import cooking.CookingManager;
import cooking.RecipeData;
import core.player.Player;
import core.player.Inventory;

import java.util.List;
import java.util.Scanner;

public class CookingTester {

    public static void main(String[] args) {
        // Inisialisasi Player
        Player player = new Player("Chef Asep", "Male");
        System.out.println("Pemain " + player.getName() + " siap memasak!");

        // Beri pemain beberapa bahan awal dan fuel
        Inventory inv = player.getInventory();
        // Bahan untuk Baguette (3 Wheat)
        inv.addItem(CropRegistry.getHarvestedCropByName("Wheat"), 5);
        // Bahan untuk Fish n' Chips (2 Ikan, 1 Wheat, 1 Potato)
        inv.addItem(Misc.get("Carp"), 3); // Contoh ikan
        inv.addItem(CropRegistry.getHarvestedCropByName("Potato"), 2);
        // Fuel
        inv.addItem(Misc.get("Firewood"), 5);
        inv.addItem(Misc.get("Coal"), 3);

        player.setEnergy(Player.getMaxEnergy()); // Pastikan energi penuh

        // Inisialisasi CookingManager
        CookingManager cookingManager = new CookingManager(player);
        // Buka beberapa resep untuk testing (selain yang default)
        // If unlocking recipes is needed, implement it here. Otherwise, all recipes may be available by default.

        System.out.println("\n--- Status Awal Pemain ---");
        inv.showInventory();
        System.out.println("Energi: " + player.getEnergy());
        System.out.println("Resep yang tersedia:");
        for (Recipe r : RecipeData.getAllRecipes()) {
            System.out.println("- " + r.getRecipeName() + " (ID: " + r.getRecipeId() + ")");
        }

        // Skenario Memasak
        Scanner scanner = new Scanner(System.in);
        boolean continueCooking = true;

        while(continueCooking) {
            System.out.println("\n--- Pilih Aksi ---");
            System.out.println("1. Masak Resep");
            System.out.println("2. Lihat Inventory");
            System.out.println("3. Keluar");
            System.out.print("Pilihanmu: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\nResep yang bisa dimasak:");
                    List<Recipe> availableRecipes = RecipeData.getAllRecipes();
                    if (availableRecipes.isEmpty()) {
                        System.out.println("Tidak ada resep yang bisa kamu masak saat ini.");
                        break;
                    }
                    for (int i = 0; i < availableRecipes.size(); i++) {
                        System.out.println((i + 1) + ". " + availableRecipes.get(i).getRecipeName());
                    }
                    System.out.print("Pilih nomor resep untuk dimasak: ");
                    int recipeChoiceIdx;
                    try {
                        recipeChoiceIdx = Integer.parseInt(scanner.nextLine()) - 1;
                        if (recipeChoiceIdx < 0 || recipeChoiceIdx >= availableRecipes.size()) {
                            System.out.println("Pilihan resep tidak valid.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Input tidak valid.");
                        break;
                    }
                    Recipe selectedRecipe = availableRecipes.get(recipeChoiceIdx);

                    System.out.println("Pilih bahan bakar:");
                    System.out.println("1. Firewood");
                    System.out.println("2. Coal");
                    System.out.print("Pilihan bahan bakar: ");
                    String fuelChoiceStr = scanner.nextLine();
                    Fuel selectedFuel;
                    if (fuelChoiceStr.equals("1")) {
                        selectedFuel = new cooking.Firewood();
                    } else if (fuelChoiceStr.equals("2")) {
                        selectedFuel = new cooking.Coal();
                    } else {
                        System.out.println("Pilihan bahan bakar tidak valid.");
                        break;
                    }

                    System.out.println("\nMemulai memasak " + selectedRecipe.getRecipeName() + " dengan " + selectedFuel.getName() + "...");
                    String result = cookingManager.startCooking(selectedRecipe.getRecipeId(), selectedFuel);
                    System.out.println(result);
                    break;
                case "2":
                    System.out.println("\n--- Inventory Saat Ini ---");
                    inv.showInventory();
                    System.out.println("Energi: " + player.getEnergy());
                    break;
                case "3":
                    continueCooking = false;
                    System.out.println("Keluar dari simulasi memasak...");
                    break;
                default:
                    System.out.println("Pilihan tidak dikenal.");
                    break;
            }
            
            // Beri sedikit waktu agar task masak bisa berjalan jika ada
            if (!choice.equals("3")) {
                System.out.println("(Menunggu sebentar untuk proses masak di background jika ada...)");
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
            }
        }

        // Tunggu beberapa saat agar semua task masak selesai sebelum shutdown (untuk demo)
        System.out.println("\nMenunggu semua proses memasak selesai sebelum keluar (maks 5 detik)...");
        try {
            Thread.sleep(5000); // Sesuaikan durasi ini
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // If CookingManager has a shutdown method, call it. Otherwise, remove this line.
        scanner.close();
        System.out.println("Simulasi memasak selesai.");
    }
}
