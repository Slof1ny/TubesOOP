package cooking;

import java.util.*;

public class RecipeBook {
    private static final List<Recipe> all = List.of(
        new Recipe(Recipe.ID.BAGUETTE, "Baguette",
            Map.of("Wheat", 3), 1,
            p -> true
        ),
        new Recipe(Recipe.ID.FISH_N_CHIPS, "Fish n' Chips",
            Map.of("Fish", 2, "Wheat", 1, "Potato", 1), 1,
            p -> true
        ),
        new Recipe(Recipe.ID.SASHIMI, "Sashimi",
            Map.of("Salmon", 3), 1,
            p -> p.getStats().getTotalInCategory("Fish") >= 10
        ),
        new Recipe(Recipe.ID.FUGU, "Fugu",
            Map.of("Pufferfish", 1), 1,
            p -> p.getStats().getItemCount("Pufferfish") > 0
        ),
        new Recipe(Recipe.ID.WINE, "Wine",
            Map.of("Grape", 2), 1,
            p -> true
        ),
        new Recipe(Recipe.ID.PUMPKIN_PIE, "Pumpkin Pie",
            Map.of("Egg", 1, "Wheat", 1, "Pumpkin", 1), 1,
            p -> true
        ),
        new Recipe(Recipe.ID.VEGGIE_SOUP, "Veggie Soup",
            Map.of("Cauliflower", 1, "Parsnip", 1, "Potato", 1, "Tomato", 1), 1,
            p -> p.getStats().getItemCount("Cauliflower") > 0 || 
                  p.getStats().getItemCount("Parsnip") > 0 || 
                  p.getStats().getItemCount("Potato") > 0 || 
                  p.getStats().getItemCount("Tomato") > 0
        ),
        new Recipe(Recipe.ID.FISH_STEW, "Fish Stew",
            Map.of("Fish", 2, "Hot Pepper", 1, "Cauliflower", 2), 1,
            p -> p.getStats().getItemCount("Hot Pepper") > 0
        ),
        new Recipe(Recipe.ID.SALAD_SPAKBOR, "Spakbor Salad",
            Map.of("Melon", 1, "Cranberry", 1, "Blueberry", 1, "Tomato", 1), 1,
            p -> true
        ),
        new Recipe(Recipe.ID.FISH_SANDWICH, "Fish Sandwich",
            Map.of("Fish", 1, "Wheat", 2, "Tomato", 1, "Hot Pepper", 1), 1,
            p -> true
        ),
        new Recipe(Recipe.ID.LEGENDS_SPAKBOR, "The Legends of Spakbor",
            Map.of("Legend Fish", 1, "Potato", 2, "Parsnip", 1, "Tomato", 1, "Eggplant", 1), 1,
            p -> p.getStats().getItemCount("Legend Fish") > 0
        )
    );

    public static List<Recipe> values() { return all; }

    public static Recipe find(Recipe.ID id) {
        return all.stream()
                  .filter(r -> r.getId() == id)
                  .findFirst()
                  .orElseThrow(() -> 
                     new IllegalArgumentException("Recipe not found: " + id)
                  );
    }
}
