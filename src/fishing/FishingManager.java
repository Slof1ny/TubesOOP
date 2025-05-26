package fishing;

import java.util.Random;

public class FishingManager {
    protected FishingLocation fishingLocation;

    public static class FishingRNG {
        private Random rng;
    
        public FishingRNG(long seed) {
            this.rng = new Random(seed);
        }
        
        public FishingRNG() {
            this(System.currentTimeMillis());
        }
    
        public int getRandomNumber(int min, int max) {
            if (min > max) {
                int temp = min;
                min = max;
                max = temp;
            }
    
            return min + rng.nextInt(max - min + 1);
        }
    }
}
