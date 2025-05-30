package core.player;

import java.util.*;

import item.Fish;
import core.world.Season;
import fishing.FishType;

public class PlayerStats {
    private Map<String, Integer> itemCount;
    private static final Map<String, List<String>> CATEGORY_MAP = new HashMap<>();
    private int totalGoldEarned;
    private int totalGoldSpent = 0;
    private Map<Season, Integer> seasonalIncome = new HashMap<>();
    private Map<Season, Integer> seasonalExpenditure = new HashMap<>();
    private Map<Season, Integer> daysPlayedPerSeason = new HashMap<>();
    private Map<String, Integer> npcChatFrequency = new HashMap<>();
    private Map<String, Integer> npcGiftFrequency = new HashMap<>();
    private Map<FishType, Integer> fishCaughtByType = new HashMap<>();
    private int totalFishCaught = 0;
    private int totalCropsHarvested = 0;
    private boolean milestonesCheckedAndDisplayed = false;

    public PlayerStats() {
        itemCount = new HashMap<>();
        totalGoldEarned = 0;
        initializeAllItems();
        initializeCategories();
    }

    private void initializeAllItems() {
        for(String item : getAllItems()){
            itemCount.put(item, 0);
        }
    }

    private void initializeCategories(){
        CATEGORY_MAP.put("Seeds", Arrays.asList(
            "Parsnip Seeds", "Cauliflower Seeds", "Potato Seeds", "Wheat Seeds",
            "Blueberry Seeds", "Tomato Seeds", "Hot Pepper Seeds", "Melon Seeds",
            "Cranberry Seeds", "Pumpkin Seeds", "Grape Seeds"
        ));

        CATEGORY_MAP.put("Crops", Arrays.asList(
            "Parsnip", "Cauliflower", "Potato", "Wheat", "Blueberry", "Tomato",
            "Hot Pepper", "Melon", "Cranberry", "Pumpkin", "Grape"
        ));

        CATEGORY_MAP.put("Fish", Arrays.asList(
            "Bullhead", "Carp", "Chub", "Largemouth Bass", "Rainbow Trout",
            "Sturgeon", "Midnight Carp", "Flounder", "Halibut", "Octopus",
            "Pufferfish", "Sardine", "Super Cucumber", "Catfish", "Salmon",
            "Angler", "Crimsonfish", "Glacierfish", "Legend"
        ));

        CATEGORY_MAP.put("Food", Arrays.asList(
            "Fish n' Chips", "Baguette", "Sashimi", "Fugu", "Wine", "Pumpkin Pie",
            "Veggie Soup", "Fish Stew", "Spakbor Salad", "Fish Sandwich",
            "The Legends of Spakbor", "Cooked Pig's Head"
        ));

        CATEGORY_MAP.put("Equipment", Arrays.asList(
            "Hoe", "Watering Can", "Pickaxe", "Fishing Rod"
        ));

        CATEGORY_MAP.put("Misc", Arrays.asList("Coal", "Firewood"));
    }

    private List<String> getAllItems() {
        return CATEGORY_MAP.values()
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .toList();
    }

    public void addItem(String itemName, int amount) {
        itemCount.put(itemName, itemCount.getOrDefault(itemName, 0) + amount);
    }

    public int getItemCount(String itemName) {
        return itemCount.getOrDefault(itemName, 0);
    }

    public int getTotalInCategory(String category) {
        List<String> items = CATEGORY_MAP.getOrDefault(category, Collections.emptyList());
        int total = 0;
        for (String item : items){
            total += getItemCount(item);
        }
        return total;
    }

    public void printStats() {
        System.out.println("--- Item Statistics ---");
        // Filter untuk hanya menampilkan item dengan jumlah > 0
        // Atau item yang memang ada di CATEGORY_MAP (untuk memastikan item penting tetap terlihat walau jumlahnya 0)
        List<String> allDefinedItems = getAllItems();
        for (Map.Entry<String, Integer> entry : itemCount.entrySet()) {
            if (entry.getValue() > 0 || allDefinedItems.contains(entry.getKey())) {
                System.out.println("Item: " + entry.getKey() + " | Jumlah: " + entry.getValue());
            }
        }
        System.out.println("Total Gold Earned: " + totalGoldEarned + "g");
        System.out.println("-----------------------");
    }

    public void addGoldEarned(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative gold earned.");
        }
        this.totalGoldEarned += amount;
    }

    public int getTotalGoldEarned() {
        return totalGoldEarned;
    }

    public void addGoldSpent(int amount, Season currentSeason) {
        if (amount < 0) {
            //System.err.println("Cannot spend negative gold. Amount: " + amount);
            return; // Or throw new IllegalArgumentException("Cannot spend negative gold.");
        }
        this.totalGoldSpent += amount;
        this.seasonalExpenditure.merge(currentSeason, amount, Integer::sum);
    }

    public void incrementNpcChatFrequency(String npcName) {
        this.npcChatFrequency.merge(npcName, 1, Integer::sum);
    }

    public void incrementNpcGiftFrequency(String npcName) {
        this.npcGiftFrequency.merge(npcName, 1, Integer::sum);
    }

    public void recordFishCaught(Fish fish) {
        if (fish == null) return;
        this.totalFishCaught++;
        this.fishCaughtByType.merge(fish.getType(), 1, Integer::sum);
        // Also ensure the specific fish is tracked by addItem if not already
        // addItem(fish.getName(), 1); // This is likely already handled by inventory.addItem
    }

    public void recordCropHarvested(String cropName, int quantity) {
        this.totalCropsHarvested += quantity;
        // addItem(cropName, quantity); // This is likely already handled by inventory.addItem
    }


    public void incrementDaysPlayedInSeason(Season currentSeason) {
        this.daysPlayedPerSeason.merge(currentSeason, 1, Integer::sum);
    }

    public int getTotalGoldSpent() {
        return totalGoldSpent;
    }

    public Map<Season, Integer> getSeasonalIncome() {
        return new HashMap<>(seasonalIncome); // Return a copy
    }

    public Map<Season, Integer> getSeasonalExpenditure() {
        return new HashMap<>(seasonalExpenditure); // Return a copy
    }

    public Map<Season, Integer> getDaysPlayedPerSeason() {
        return new HashMap<>(daysPlayedPerSeason); // Return a copy
    }

    public Map<String, Integer> getNpcChatFrequency() {
        return new HashMap<>(npcChatFrequency); // Return a copy
    }

    public Map<String, Integer> getNpcGiftFrequency() {
        return new HashMap<>(npcGiftFrequency); // Return a copy
    }

    public Map<FishType, Integer> getFishCaughtByType() {
        return new HashMap<>(fishCaughtByType); // Return a copy
    }

    public int getTotalFishCaught() {
        return totalFishCaught;
    }

    public int getTotalCropsHarvested() {
        return totalCropsHarvested;
    }

    public boolean haveMilestonesBeenDisplayed() {
        return milestonesCheckedAndDisplayed;
    }

    public void setMilestonesDisplayed(boolean status) {
        this.milestonesCheckedAndDisplayed = status;
    }

    // Modification to existing addGoldEarned:
    // public void addGoldEarned(int amount) { // OLD
    public void addGoldEarned(int amount, Season currentSeason) { // NEW
        if (amount < 0) {
            throw new IllegalArgumentException("Cannot add negative gold earned.");
        }
        this.totalGoldEarned += amount;
        this.seasonalIncome.merge(currentSeason, amount, Integer::sum); // NEW LINE
    }

}