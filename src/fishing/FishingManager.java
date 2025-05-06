package fishing;

import java.util.Random;
import core.world.*;

public class FishingManager {
    protected FarmMap farm;

    class FishingRNG {
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
