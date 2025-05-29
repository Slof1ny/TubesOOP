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
    public void newDay(int currentDay, Season currentSeason) {
        if (growthStage < 3) {
            daysSincePlanting++;
            if (plantedSeed == null) {
                return;
            }

            if (!plantedSeed.getSeasons().contains(currentSeason)) {
                return;
            }

            boolean grew = false;
            if (lastWateredDay == currentDay -1 ) {
                if (growthStage == 0 && daysSincePlanting >= Math.ceil(daysToGrow / 3.0)) {
                    growthStage = 1;
                    grew = true;
                } else if (growthStage == 1 && daysSincePlanting >= Math.ceil(daysToGrow * 2 / 3.0)) {
                    growthStage = 2;
                    grew = true;
                } else if (growthStage == 2 && daysSincePlanting >= daysToGrow) {
                    growthStage = 3;
                    grew = true;
                }

                if (grew) {
                    System.out.println(getName() + " grew to stage " + growthStage + "!");
                }
            } else {
                return;
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