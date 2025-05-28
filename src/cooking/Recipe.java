package cooking;

import java.util.Map;
import java.util.function.Predicate;
import core.player.Player;

public class Recipe {
    public enum ID {
        FISH_N_CHIPS,
        BAGUETTE,
        SASHIMI,
        FUGU,
        WINE,
        PUMPKIN_PIE,
        VEGGIE_SOUP,
        FISH_STEW,
        SALAD_SPAKBOR,
        FISH_SANDWICH,
        LEGENDS_SPAKBOR
    }

    public enum Fuel {
        FIREWOOD(1),   // 1 piece → 1 food
        COAL(2);       // 1 piece → 2 food

        private final int yieldPerUnit;
        Fuel(int y) { this.yieldPerUnit = y; }
        public int getYield() { return yieldPerUnit; }
    }

    private final ID id;
    private final String name;
    private final Map<String,Integer> requirements; // ingredient name → count
    private final int yieldPerFuel;                 // how many servings one unit of fuel cooks
    private final Predicate<Player> unlockCondition;

    public Recipe(ID id,
                  String name,
                  Map<String,Integer> requirements,
                  int yieldPerFuel,
                  Predicate<Player> unlockCondition)
    {
        this.id              = id;
        this.name            = name;
        this.requirements    = requirements;
        this.yieldPerFuel    = yieldPerFuel;
        this.unlockCondition = unlockCondition;
    }

    public ID getId()                 { return id; }
    public String getName()           { return name; }
    public Map<String,Integer> getRequirements() { return requirements; }
    public boolean isUnlocked(Player p) { return unlockCondition.test(p); }

    public boolean canCraft(Player p) {
        for (var e : requirements.entrySet()) {
            if (p.getInventory().getItemCount(e.getKey()) < e.getValue())
                return false;
        }
        return true;
    }

    public void consumeIngredients(Player p) {
        for (var e : requirements.entrySet()) {
            p.getInventory().removeByName(e.getKey(), e.getValue());
        }
    }

    public boolean cookableWith(Fuel fuel) {
        return fuel.getYield() >= 1;
    }
}
