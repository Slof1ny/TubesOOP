package item;

import core.world.Season;

public class Crop extends Item implements EdibleItem {
    private int quantityPerHarvest;
    private Seed plantedSeed;
    private int growthStage; // 0 = planted, 1 = seedling, 2 = growing, 3 = mature/harvestable
    private int daysSincePlanting;
    private int lastWateredDay;
    private int daysToGrow;

    public Crop(Seed plantedSeed) {
        super(plantedSeed.getName().replace(" Seeds", ""),
              CropRegistry.getHarvestedCropByName(plantedSeed.getName().replace(" Seeds", "")).getBuyPrice(),
              CropRegistry.getHarvestedCropByName(plantedSeed.getName().replace(" Seeds", "")).getSellPrice());
        this.plantedSeed = plantedSeed;
        this.quantityPerHarvest = plantedSeed.getQuantityPerHarvest();
        this.growthStage = 0;
        this.daysSincePlanting = 0;
        this.lastWateredDay = -1;
        this.daysToGrow = plantedSeed.getDaysToGrow();
    }

    public Crop(String name, int buyPrice, int sellPrice, int quantityPerHarvest){
        super(name, buyPrice, sellPrice);
        this.quantityPerHarvest = quantityPerHarvest;
        this.growthStage = 3; 
        this.daysSincePlanting = 0; 
        this.lastWateredDay = -1;
        this.plantedSeed = null; 
    }

    public int getQuantityPerHarvest(){
        return quantityPerHarvest;
    }

    @Override
    public String getCategory(){
        return "Crop";
    }

    @Override
    public int getEnergyRestored(){
        return 3; // All crops restore 3 energy
    }

    /* Advances the growth of the crop by one day.*/
    public void newDay(int currentDay, Season currentSeason, boolean wasYesterdayRainy) {
        if (growthStage >= 3) { // Already mature/harvestable, no further changes
            return;
        }

        // Check if out of season
        if (plantedSeed != null && !plantedSeed.getSeasons().contains(currentSeason)) {
            // System.out.println("Crop '" + getName() + "' is out of season (" + currentSeason + ") and will not grow further.");
            // Optionally, implement logic for the crop to die/disappear here.
            return; // For now, just stops growing.
        }

        // Increment age of the plant. daysSincePlanting counts how many newDay calls it has received.
        // If planted on Day X, first newDay call is for morning of Day X+1, daysSincePlanting becomes 1.
        daysSincePlanting++;

        boolean effectivelyWateredYesterday = (this.lastWateredDay == (currentDay - 1)) || wasYesterdayRainy;

        if (effectivelyWateredYesterday) {
            if (wasYesterdayRainy && this.lastWateredDay != (currentDay - 1)) {
                // If rain watered it, and player didn't, update lastWateredDay to reflect this.
                this.lastWateredDay = currentDay - 1;
                 System.out.println("Crop '" + getName() + "' was watered by rain on day " + (currentDay -1) + ".");
            }

            // Check for harvestable state
            // If daysSincePlanting has reached daysToGrow, it's ready.
            // e.g., daysToGrow = 1. Plant day X (dsp=0). Next morning (day X+1), newDay called, dsp becomes 1. 1 >= 1, so harvestable.
            if (daysSincePlanting >= daysToGrow) {
                if (growthStage < 3) { // To prevent multiple "now harvestable" messages
                    growthStage = 3; // Directly to harvestable
                    System.out.println("Crop '" + getName() + "' is now HARVESTABLE (Stage 3) on day " + currentDay +
                                       ". (Days since planting: " + daysSincePlanting + ", Needs to grow for: " + daysToGrow + " days)");
                }
            } else if (daysToGrow > 1) { // Handle intermediate visual stages for multi-day crops
                int prevGrowthStage = growthStage;
                // Example for visual stages (can be adjusted based on number of desired sprites/stages)
                // Stage 0: Planted
                // Stage 1: Small sprout (e.g., after 1/3 of growth time)
                // Stage 2: Medium plant (e.g., after 2/3 of growth time)
                // Stage 3: Harvestable
                if (daysSincePlanting >= Math.ceil(daysToGrow * 0.67)) { // approx 2/3
                    growthStage = 2;
                } else if (daysSincePlanting >= Math.ceil(daysToGrow * 0.34)) { // approx 1/3
                    growthStage = 1;
                }
                // else it stays at stage 0 (just planted but not yet sprouted)

                if (growthStage > prevGrowthStage) {
                    System.out.println("Crop '" + getName() + "' grew to visual stage " + growthStage + " on day " + currentDay +
                                       ". (Days since planting: " + daysSincePlanting + ")");
                }
            }
             // If daysToGrow is 1, and it was watered, the above (daysSincePlanting >= daysToGrow) handles it.
        } else { // Not watered and no rain yesterday
            if (growthStage < 3) {
                System.out.println("Crop '" + getName() + "' did not advance growth on day " + currentDay +
                                   " because it was not effectively watered on day " + (currentDay - 1) + " (Last watered: " + this.lastWateredDay + ").");
            }
        }
    }

    /* Waters the crop for the current day.*/
    public void water(int currentDay) {
        if (growthStage < 3) {
            if (lastWateredDay != currentDay) {
                this.lastWateredDay = currentDay;
                System.out.println(getName() + " has been watered.");
            } else {
                System.out.println(getName() + " has already been watered today.");
            }
        } else {
            System.out.println(getName() + " is already harvestable, no need to water.");
        }
    }

    public boolean isWateredToday(int day) {
        return lastWateredDay == day;
    }

    
    public boolean isHarvestable() {
        return growthStage >= 3;
    }

    public Item getHarvestedProduct() {
        return CropRegistry.getHarvestedCropByName(this.getName());
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public Seed getPlantedSeed() {
        return plantedSeed;
    }
}