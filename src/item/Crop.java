package item;

import core.world.Season;
import java.util.List;

public class Crop extends Item implements EdibleItem {
    private int quantityPerHarvest;
    private Seed plantedSeed;
    private int growthStage; // 0 = planted, 1 = seedling, 2 = growing, 3 = mature/harvestable
    private int daysSincePlanting;
    private int lastWateredDay; 
    private int daysToGrow; 

    // Constructor for a newly planted crop
    public Crop(Seed plantedSeed) {
        super(plantedSeed.getName().replace(" Seeds", ""), 0, getSellPriceForHarvestedCrop(plantedSeed.getName().replace(" Seeds", "")));
        this.plantedSeed = plantedSeed;
        this.quantityPerHarvest = plantedSeed.getQuantityPerHarvest();
        this.growthStage = 0; // Starts at planted stage
        this.daysSincePlanting = 0;
        this.lastWateredDay = -1; // Not yet watered
        this.daysToGrow = plantedSeed.getDaysToGrow(); 
    }

    // Existing constructor for items that are just "crops" (e.g., in inventory)
    public Crop(String name, int buyPrice, int sellPrice, int quantityPerHarvest){
        super(name, buyPrice, sellPrice);
        this.quantityPerHarvest = quantityPerHarvest;
        this.growthStage = 3; // Assume this is a harvested crop, hence mature
        this.daysSincePlanting = this.daysToGrow; // Assume fully grown for existing crop items
        this.lastWateredDay = -1;
        this.plantedSeed = null; // This crop isn't "planted"
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

    /**
     * Advances the growth of the crop by one day.
     * Should be called once per game day.
     * @param currentDay The current total game day.
     * @param currentSeason The current season.
     */
    public void newDay(int currentDay, Season currentSeason) {
        if (growthStage < 3) {
            daysSincePlanting++;
            if (plantedSeed != null && !plantedSeed.getSeasons().contains(currentSeason)) {
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
                return; // Crop did not grow today, either not watered or not ready
            }
        }
    }

    /**
     * Waters the crop for the current day.
     * @param currentDay The current total game day.
     */
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

    /**
     * Checks if the crop has been watered on the given day.
     * @param day The total game day to check against.
     * @return True if the crop was watered on this day, false otherwise.
     */
    public boolean isWateredToday(int day) {
        return lastWateredDay == day;
    }

    /**
     * Checks if the crop is ready to be harvested.
     * @return True if the crop is at its final growth stage, false otherwise.
     */
    public boolean isHarvestable() {
        return growthStage >= 3; // Assuming stage 3 is the harvestable stage
    }

    /**
     * Returns the actual Item that is harvested from this crop.
     * This method will create a new Item instance based on the harvested crop's details.
     */
    public Item getHarvestedProduct() {
        String harvestedName = this.getName();
        int harvestedSellPrice = getSellPriceForHarvestedCrop(harvestedName);
        int harvestedBuyPrice = getBuyPriceForHarvestedCrop(harvestedName); // Crops can be bought from store

        return new Item(harvestedName, harvestedBuyPrice, harvestedSellPrice) {
            @Override
            public String getCategory() {
                return "Harvested Crop";
            }
        };
    }

    private static int getSellPriceForHarvestedCrop(String cropName) {
        switch (cropName) {
            case "Parsnip": return 35;
            case "Cauliflower": return 150;
            case "Potato": return 80; 
            case "Wheat": return 30; 
            case "Blueberry": return 40; 
            case "Tomato": return 60; 
            case "Hot Pepper": return 40; 
            case "Melon": return 250; 
            case "Cranberry": return 25; 
            case "Pumpkin": return 250; 
            case "Grape": return 10;
            default: return 0;
        }
    }

    private static int getBuyPriceForHarvestedCrop(String cropName) {
        switch (cropName) {
            case "Parsnip": return 50;
            case "Cauliflower": return 200; 
            case "Potato": return 0; 
            case "Wheat": return 50; 
            case "Blueberry": return 150; 
            case "Tomato": return 90; 
            case "Hot Pepper": return 0; 
            case "Melon": return 0; 
            case "Cranberry": return 0; 
            case "Pumpkin": return 300; 
            case "Grape": return 100;
            default: return 0;
        }
    }

    public int getGrowthStage() {
        return growthStage;
    }

    public Seed getPlantedSeed() {
        return plantedSeed;
    }
}