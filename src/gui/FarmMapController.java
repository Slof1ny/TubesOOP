package gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import core.player.Player;
import core.world.FarmMap;
import core.world.DeployedObject;
import core.world.ShippingBin;
import core.house.House;
import core.world.Pond;
import core.world.Tile;
import fishing.FishingLocation;

import action.Action;
import item.Seed;
import item.Item;
import item.SeedRegistry;
import time.Time;
import time.GameCalendar;
import system.GameManager; // Import GameManager

public class FarmMapController extends KeyAdapter {
    private Player player;
    private FarmMap farmMap;
    private FarmMapPanel farmMapPanel;
    private Time gameTime;
    private GameCalendar gameCalendar;
    private PlayerInfoPanel playerInfoPanel;
    private GameView gameView; // GameView reference to access GameManager and switch screens

    public FarmMapController(Player player, FarmMap farmMap, FarmMapPanel farmMapPanel, Time gameTime, GameCalendar gameCalendar, PlayerInfoPanel playerInfoPanel, GameView gameView) {
        this.player = player;
        this.farmMap = farmMap;
        this.farmMapPanel = farmMapPanel;
        this.gameTime = gameTime;
        this.gameCalendar = gameCalendar;
        this.playerInfoPanel = playerInfoPanel;
        this.gameView = gameView; // Initialize GameView
    }

    @Override
    public void keyPressed(KeyEvent e) {
        boolean actionTaken = false;
        GameManager gameManager = gameView.getGameManager(); // Get GameManager instance from GameView

        // Ensure this controller only acts if FarmMap is the current map in GameManager
        if (gameManager == null || !gameManager.getCurrentMap().getName().equals(farmMap.getName())) {
            return;
        }

        if (e.isConsumed()) {
            return;
        }

        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                actionTaken = farmMap.movePlayer(player, 0, -1);
                break;
            case KeyEvent.VK_S:
                actionTaken = farmMap.movePlayer(player, 0, 1);
                break;
            case KeyEvent.VK_A:
                actionTaken = farmMap.movePlayer(player, -1, 0);
                break;
            case KeyEvent.VK_D:
                actionTaken = farmMap.movePlayer(player, 1, 0);
                break;
            case KeyEvent.VK_T: // 'T' for Tilling
                try {
                    Action.till(farmMap, player, gameTime);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Tilling Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_P: // 'P' for Planting
                actionTaken = true; // Assume an action is attempted
                List<Seed> availableSeeds = player.getInventory().getAllOwnedSeeds();

                if (availableSeeds.isEmpty()) {
                    JOptionPane.showMessageDialog(farmMapPanel, "You have no seeds to plant!", "No Seeds", JOptionPane.INFORMATION_MESSAGE);
                    actionTaken = false; // No actual action performed
                } else {
                    Seed seedToPlant = null;
                    if (availableSeeds.size() == 1) {
                        seedToPlant = availableSeeds.get(0);
                        System.out.println("Auto-selected only seed: " + seedToPlant.getName());
                    } else {
                        // Multiple seed types available, let player choose
                        String[] seedNames = new String[availableSeeds.size()];
                        for (int i = 0; i < availableSeeds.size(); i++) {
                            seedNames[i] = availableSeeds.get(i).getName() + " (x" + player.getInventory().getItemCount(availableSeeds.get(i)) + ")";
                        }

                        String chosenSeedDisplay = (String) JOptionPane.showInputDialog(
                                farmMapPanel,
                                "Choose a seed to plant:",
                                "Plant Seed",
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                seedNames,
                                seedNames[0]
                        );

                        if (chosenSeedDisplay != null) {
                            // Extract base name if quantity was appended for display
                            String chosenSeedName = chosenSeedDisplay.split(" \\(x")[0];
                            Item selectedItem = player.getInventory().findItemByName(chosenSeedName);
                            if (selectedItem instanceof Seed) {
                                seedToPlant = (Seed) selectedItem;
                            } else {
                                JOptionPane.showMessageDialog(farmMapPanel, "Error selecting seed.", "Error", JOptionPane.ERROR_MESSAGE);
                                actionTaken = false;
                            }
                        } else {
                            actionTaken = false; // Player cancelled dialog
                        }
                    }

                    if (seedToPlant != null) {
                        try {
                            Action.plant(farmMap, player, gameTime, gameCalendar, seedToPlant);
                            // Action.plant itself will print success/failure or throw exception
                        } catch (IllegalArgumentException ex) {
                            JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Planting Error", JOptionPane.WARNING_MESSAGE);
                            actionTaken = false; // Planting failed
                        }
                    } else if (actionTaken) { // If we got here due to dialog cancel or error, ensure isActionTaken is false
                        actionTaken = false;
                    }
                }
                break;
            case KeyEvent.VK_H: // 'H' for Harvesting
                try {
                    Action.harvest(farmMap, player, gameTime);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Harvesting Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_R: // 'R' for Watering
                try {
                    Action.water(farmMap, player, gameTime, gameCalendar);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Watering Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_I: // 'I' for Equipment
                gameView.showScreen("InventoryScreen");
                actionTaken = true;
                e.consume();
                break;
            // Inside gui/FarmMapController.java
// In the keyPressed method:

            case KeyEvent.VK_E:
                actionTaken = false; // Inisialisasi ulang untuk setiap penekanan tombol
                String farmMapName = farmMap.getName();
                // Ambil lokasi pemain SAAT INI, SEBELUM ada perubahan karena aksi perjalanan
                String initialPlayerLocationForThisKeyPress = player.getLocation();

                // 1. AKSI: Perjalanan & Langsung Memancing dari Batas Kiri FarmMap
                if (player.getX() == 0 && initialPlayerLocationForThisKeyPress.equals(farmMapName)) {
                    actionTaken = true; // Interaksi perjalanan sedang diupayakan
                    String[] fishingSpotOptions = {"Mountain Lake", "Forest River", "Ocean", "Cancel"};
                    String chosenSpotName = (String) JOptionPane.showInputDialog(
                            farmMapPanel,
                            "Pilih lokasi untuk memancing:\n(Biaya perjalanan: -10 Energi, +15 Menit Waktu Game)\nSesi memancing akan langsung dimulai setelah tiba.",
                            "Pergi Memancing",
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            fishingSpotOptions,
                            fishingSpotOptions[0]
                    );

                    if (chosenSpotName != null && !chosenSpotName.equals("Cancel")) {
                        // Cek energi untuk perjalanan
                        if (player.getEnergy() - 10 < Player.MIN_ENERGY) {
                            JOptionPane.showMessageDialog(farmMapPanel, "Energi tidak cukup untuk melakukan perjalanan.", "Tidak Bisa Pergi", JOptionPane.WARNING_MESSAGE);
                            actionTaken = false; // Perjalanan gagal
                        } else {
                            // Kurangi energi dan majukan waktu untuk perjalanan
                            player.setEnergy(player.getEnergy() - 10);
                            gameTime.advanceGameMinutes(15);

                            // Update lokasi pemain (sementara) untuk sesi memancing ini
                            player.setLocation(chosenSpotName);

                            // (Opsional) Refresh info bar untuk menunjukkan lokasi baru sebelum memancing
                            if (gameManager.getTopInfoBarPanel() != null) {
                                gameManager.getTopInfoBarPanel().refreshInfo();
                            }
                            // (Opsional) Anda bisa menampilkan dialog "Travel Complete" singkat di sini jika diinginkan
                            // JOptionPane.showMessageDialog(farmMapPanel, "Anda telah tiba di " + chosenSpotName + ". Memulai memancing...", "Perjalanan Selesai", JOptionPane.INFORMATION_MESSAGE);

                            // Langsung mulai memancing
                            FishingLocation targetFishingLocation = gameManager.getFishingLocations().get(chosenSpotName);

                            if (targetFishingLocation != null && targetFishingLocation.canFishAt(player)) {
                                // FishingGUIAdapter akan menangani pengurangan energi & waktu untuk aksi memancing itu sendiri
                                FishingGUIAdapter.startFishingGUI(farmMap, targetFishingLocation, player, gameTime, gameCalendar, farmMapPanel);
                            } else {
                                String errorMsg = "Tidak bisa memulai memancing di " + chosenSpotName + ".";
                                if (targetFishingLocation == null) {
                                    errorMsg += " Data lokasi tidak ditemukan.";
                                    System.err.println("DEBUG: FishingLocation tidak ditemukan untuk key: " + chosenSpotName);
                                } else { // targetFishingLocation ada tapi canFishAt() false
                                    errorMsg += " Tidak bisa memancing di spot ini sekarang (canFishAt gagal).";
                                    System.err.println("DEBUG: canFishAt gagal untuk " + chosenSpotName + " meskipun player.location sudah diupdate.");
                                }
                                JOptionPane.showMessageDialog(farmMapPanel, errorMsg, "Gagal Memancing", JOptionPane.WARNING_MESSAGE);
                            }

                            // PENTING: Setelah sesi memancing selesai (berhasil atau tidak),
                            // kembalikan lokasi logis pemain ke Farm Map.
                            player.setLocation(farmMapName);
                            // Tidak perlu pop-up untuk pengembalian lokasi ini, ini perubahan state internal.
                            // Info bar akan di-refresh di akhir blok actionTaken.
                        }
                    } else { // Pemain membatalkan dialog pemilihan lokasi
                        actionTaken = false;
                    }
                    // Event perjalanan dan memancing sudah ditangani, konsumsi event.
                    if (!e.isConsumed()) e.consume();
                }
                // 2. AKSI: Interaksi dengan Objek di FarmMap (Rumah, Kolam, Shipping Bin)
                //    Hanya jika pemain berada di "Farm Map" secara logis DAN tidak sedang melakukan aksi perjalanan dari batas.
                else if (initialPlayerLocationForThisKeyPress.equals(farmMapName)) {
                    DeployedObject interactedObject = getAdjacentDeployedObject();
                    if (interactedObject != null) {
                        actionTaken = true; // Interaksi dengan objek adalah sebuah aksi
                        if (interactedObject instanceof ShippingBin) {
                            gameView.showScreen("ShippingBinScreen");
                        } else if (interactedObject instanceof core.house.House) {
                            gameManager.transitionMap(gameManager.getHouseMap().getName());
                            gameView.showScreen("HouseScreen");
                        } else if (interactedObject instanceof Pond) { // Kolam di farm
                            FishingLocation pondLocation = gameManager.getFishingLocations().get("Pond");
                            if (pondLocation != null && pondLocation.canFishAt(player)) {
                                FishingGUIAdapter.startFishingGUI(farmMap, pondLocation, player, gameTime, gameCalendar, farmMapPanel);
                            } else {
                                JOptionPane.showMessageDialog(farmMapPanel, "Anda harus berada di sebelah kolam untuk memancing.", "Tidak Bisa Memancing Disini", JOptionPane.INFORMATION_MESSAGE);
                                actionTaken = false;
                            }
                        } else { // Objek lain di farm
                            JOptionPane.showMessageDialog(farmMapPanel, "Anda berinteraksi dengan " + interactedObject.getSymbol() + "!", "Interaksi", JOptionPane.INFORMATION_MESSAGE);
                        }
                        if (!e.isConsumed()) e.consume();
                    } else {
                        // Tidak ada objek untuk berinteraksi di farm
                        JOptionPane.showMessageDialog(farmMapPanel, "Tidak ada yang bisa diinteraksikan di sini (di farm).", "Interaksi", JOptionPane.INFORMATION_MESSAGE);
                        actionTaken = false; // Tidak ada aksi nyata yang terjadi
                    }
                }
                // 3. FALLBACK: Jika pemain tidak di Farm Map (misal, Kota, Rumah) atau kondisi lain tidak terpenuhi.
                //    Dengan logika pengembalian lokasi setelah memancing, blok ini seharusnya jarang sekali dijangkau
                //    kecuali ada kondisi tak terduga atau pemain berada di map lain yang controllernya belum aktif.
                else {
                    JOptionPane.showMessageDialog(farmMapPanel, "Tidak ada yang bisa diinteraksikan di sini (lokasi saat ini: " + initialPlayerLocationForThisKeyPress + ").", "Interaksi", JOptionPane.INFORMATION_MESSAGE);
                    actionTaken = false;
                }

                // Logika refresh umum setelah aksi (jika ada aksi yang mengubah state)
                if (actionTaken) {
                    // Cek kondisi pingsan karena energi habis setelah aksi
                    if (player.getEnergy() <= Player.MIN_ENERGY) {
                        if (!e.isConsumed()) { // Hanya consume jika belum di-consume oleh fainting logic lain (jika ada)
                            e.consume();
                        }
                        JOptionPane.showMessageDialog(farmMapPanel, "Energi Anda habis dan Anda pingsan!", "Pingsan", JOptionPane.WARNING_MESSAGE);
                        gameManager.forcePlayerSleep(); // Ini juga akan me-refresh UI
                    } else {
                        // Jika tidak pingsan, pastikan UI di-refresh
                        if (!e.isConsumed()) { // Consume event jika aksi terjadi dan belum di-consume
                            e.consume();
                        }
                        // Update GUI
                        SwingUtilities.invokeLater(() -> {
                            farmMapPanel.refreshMap();
                            if (gameManager.getTopInfoBarPanel() != null && gameManager.getTopInfoBarPanel().isVisible()) {
                                gameManager.getTopInfoBarPanel().refreshInfo();
                            }
                        });
                    }
                }
                // Jika actionTaken false (misal, dialog dibatalkan atau "nothing to interact with"),
                // tidak perlu refresh spesifik atau consume event di sini, kecuali sudah di-consume di path spesifik.
                break;
            case KeyEvent.VK_K: // 'K' for reKover Land (Recover Land)
                try {
                    Action.recoverLand(farmMap, player, gameTime);
                    actionTaken = true;
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(farmMapPanel, ex.getMessage(), "Recover Land Error", JOptionPane.WARNING_MESSAGE);
                }
                break;
            case KeyEvent.VK_F1: // 'H' for Help
                gameView.showScreen("HelpScreen");
                actionTaken = true; // It's a screen change
            break;    
            case KeyEvent.VK_M: // 'M' to switch to City Map
                if (farmMap.atEdge(player)) {
                    int confirm = JOptionPane.showConfirmDialog(farmMapPanel,
                        "You are at the edge of the farm. Do you want to go to the City?",
                        "Transition Map", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        // Use GameManager to handle the transition
                        if (gameManager != null) {
                            gameManager.transitionMap(gameManager.getCityMap().getName()); // Transition to CityMap
                            gameView.showScreen("CityScreen"); // Show the CityScreen
                            actionTaken = true;
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(farmMapPanel, "You must be at the edge of the map to go to the City.", "Cannot Transition", JOptionPane.WARNING_MESSAGE);
                }
                break;
        }

        if (actionTaken) {
            // Check energy AFTER the action has potentially reduced it
            if (player.getEnergy() <= Player.MIN_ENERGY) {
                // Show message BEFORE forcePlayerSleep changes the screen context
                JOptionPane.showMessageDialog(
                    farmMapPanel, // Or whatever panel is currently active
                    "You've exhausted all your energy and fainted!",
                    "Exhausted",
                    JOptionPane.WARNING_MESSAGE
                );
                gameManager.forcePlayerSleep(); // This will call time.sleep2()
                e.consume(); // The event is fully handled by fainting
                // UI will be refreshed by sleep2 -> onGameTimeTick -> TopInfoBar & potentially screen change
                return; // IMPORTANT: Stop further processing for THIS key event
            }
        }

        if (actionTaken) {
            if(e.getKeyCode() != KeyEvent.VK_I && e.getKeyCode() != KeyEvent.VK_M && e.getKeyCode() != KeyEvent.VK_E){
                e.consume();
            }
            SwingUtilities.invokeLater(() -> {
                farmMapPanel.refreshMap();
                // playerInfoPanel is refreshed globally by GameView.showScreen()
                // or specifically if an action only updates info without screen change.
                // If an action here changes player state that PlayerInfoPanel needs to show, refresh it.
                if (playerInfoPanel != null) {
                     playerInfoPanel.refreshPlayerInfo();
                }
            });
        }
    }

    private DeployedObject getAdjacentDeployedObject() {
        int px = player.getX();
        int py = player.getY();

        int[][] adjacentOffsets = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0},
            {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
        };

        for (int[] offset : adjacentOffsets) {
            int checkX = px + offset[0];
            int checkY = py + offset[1];

            if (checkX >= 0 && checkX < FarmMap.SIZE && checkY >= 0 && checkY < FarmMap.SIZE) {
                Tile adjacentTile = farmMap.getTileAt(checkX, checkY);
                if (adjacentTile != null && adjacentTile.getType() == Tile.TileType.DEPLOYED) {
                    for (DeployedObject obj : farmMap.getDeployedObjects()) {
                        if (obj.occupies(checkX, checkY)) {
                            return obj;
                        }
                    }
                }
            }
        }
        return null;
    }
}