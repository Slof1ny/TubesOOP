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
        if (growthStage >= 3) { // Already mature/harvestable
            return;
        }

        // If the crop is out of season, it might wither (optional: add specific logic for this)
        if (plantedSeed != null && !plantedSeed.getSeasons().contains(currentSeason)) {
            // System.out.println(getName() + " is out of season (" + currentSeason + ") and may have withered.");
            // For now, let's assume it just stops growing or you handle withering elsewhere.
            // If it should die, you'd clear the crop from the tile.
            // this.type = TileType.TILLED; this.plantedCrop = null; (This logic would be in Tile if it dies)
            return;
        }

        daysSincePlanting++;

        // If yesterday was rainy, it counts as being watered for that day (day currentDay - 1)
        if (wasYesterdayRainy) {
            this.lastWateredDay = currentDay - 1;
            // System.out.println(getName() + " was considered watered due to rain on day " + (currentDay - 1));
        }

        boolean grewThisDay = false;
        // Check if watered the day before (currentDay - 1)
        if (this.lastWateredDay == (currentDay - 1)) {
            int prevGrowthStage = growthStage;
            if (growthStage == 0 && daysSincePlanting >= Math.ceil(daysToGrow / 3.0)) {
                growthStage = 1;
            } else if (growthStage == 1 && daysSincePlanting >= Math.ceil(daysToGrow * 2.0 / 3.0)) {
                growthStage = 2;
            } else if (growthStage == 2 && daysSincePlanting >= daysToGrow) {
                growthStage = 3; // Harvestable
            }

            if (growthStage > prevGrowthStage) {
                grewThisDay = true;
                System.out.println("Crop '" + getName() + "' grew to stage " + growthStage + " on day " + currentDay + ". (Days planted: " + daysSincePlanting + "/" + daysToGrow + ")");
            } else if (growthStage < 3) {
                 // System.out.println("Crop '" + getName() + "' was watered, but not enough days passed to advance growth stage. Stage: " + growthStage + ", Days planted: " + daysSincePlanting + "/" + daysToGrow);
            }
        } else if (growthStage < 3) { // Not watered and not yet mature
            System.out.println("Crop '" + getName() + "' did not grow on day " + currentDay + " because it was not watered on day " + (currentDay - 1) + " (Last watered: " + this.lastWateredDay + ") and yesterday was not rainy.");
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