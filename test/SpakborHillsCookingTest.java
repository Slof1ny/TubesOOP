package test;

import com.spakborhills.game.model.*;
import com.spakborhills.game.enums.*;
import com.spakborhills.game.actions.CookingManager;
import com.spakborhills.game.data.RecipeData; // Untuk melihat resep yang ada

import java.util.List;
import java.util.Scanner;

public class SpakborHillsCookingTest {

    public static void main(String[] args) {
        // Inisialisasi Player
        Player player = new Player("Chef Asep", "Dapur Impian", "Laki-laki");
        System.out.println("Pemain " + player.getName() + " siap memasak!");

        // Beri pemain beberapa bahan awal dan fuel
        Inventory inv = player.getInventory();
        // Bahan untuk Baguette (3 Wheat)
        inv.addItem(new Item("CROP_WHEAT", "Wheat", ItemCategory.CROP, 50, 30), 5);
        // Bahan untuk Fish n' Chips (2 Ikan, 1 Wheat, 1 Potato)
        inv.addItem(new Item("FISH_CARP", "Carp", ItemCategory.FISH, 0, 30), 3); // Contoh ikan
        inv.addItem(new Item("CROP_POTATO", "Potato", ItemCategory.CROP, 0, 80), 2);
        // Fuel
        inv.addItem(new Item(Firewood.ITEM_ID, "Firewood", ItemCategory.MISC, 10, 5), 5);
        inv.addItem(new Item(Coal.ITEM_ID, "Coal", ItemCategory.MISC, 30, 15), 3);
        
        player.gainEnergy(100); // Pastikan energi penuh

        // Inisialisasi CookingManager
        CookingManager cookingManager = new CookingManager(player);
        // Buka beberapa resep untuk testing (selain yang default)
        cookingManager.unlockRecipe("recipe_1"); // Fish n' Chips (Store Bought)

        System.out.println("\n--- Status Awal Pemain ---");
        inv.displayInventory();
        System.out.println("Energi: " + player.getEnergy());
        System.out.println("Resep yang tersedia:");
        cookingManager.getAvailableRecipes().forEach(r -> System.out.println("- " + r.getRecipeName() + " (ID: " + r.getRecipeId() + ")"));

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
                    List<Recipe> availableRecipes = cookingManager.getAvailableRecipes();
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
                        selectedFuel = new Firewood();
                    } else if (fuelChoiceStr.equals("2")) {
                        selectedFuel = new Coal();
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
                    inv.displayInventory();
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
        
        cookingManager.shutdown();
        scanner.close();
        System.out.println("Simulasi memasak selesai.");
    }
}
