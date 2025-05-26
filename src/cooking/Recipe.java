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

    private final ID id;
    private final String name;
    private final Map<String,Integer> requirements;
    private final int yieldPerFuel;
    private final Predicate<Player> unlockCondition;

    public Recipe(ID id, String name, Map<String,Integer> requirements, int yieldPerFuel, Predicate<Player> unlockCondition) {
        this.id                = id;
        this.name              = name;
        this.requirements      = requirements;
        this.yieldPerFuel      = yieldPerFuel;
        this.unlockCondition   = unlockCondition;
    }

    public ID getId() { return id; }
    public String getName() { return name; }
    public int getYieldPerFuel() { return yieldPerFuel; }
    public Map<String,Integer> getRequirements() { return requirements; }
    public boolean isUnlocked(Player p) { return unlockCondition.test(p); }

    public boolean canCraft(Player p) {
        for (var entry : requirements.entrySet()) {
            if (p.getInventory().getItemCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public void consumeIngredients(Player p) {
        for (var entry : requirements.entrySet()) {
            p.getInventory().removeByName(entry.getKey(), entry.getValue());
        }
    }
}