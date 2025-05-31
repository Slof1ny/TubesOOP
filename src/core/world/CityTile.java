package core.world;

/**
 * CityTile extends Tile and adds City-specific terrain and building symbols.
 *
 * In particular:
 *  - City-only terrain symbols ('1'..'5') are treated as UNTILLED ground types.
 *  - City-only building/object symbols ('S','M','C','R','G','A','X','O','F', etc.) are DEPLOYED.
 *  - Anything else falls back to the base Tile behavior (Farm symbols or default '.').
 */
public class CityTile extends Tile {
    // Re-declare all CityMap symbols:
    public static final char DEFAULT_UNTILLED_CHAR = '.';  // Road
    public static final char RUMPUT_HIJAU_SYMBOL  = '1';   // Grass
    public static final char BATU_SYMBOL          = '2';   // Stone
    public static final char AIR_SYMBOL           = '3';   // Water
    public static final char BUNGA_PINK_SYMBOL    = '4';   // Flower
    public static final char TANAH_SYMBOL         = '5';   // Dirt

    // City building/object symbols
    public static final char STORE_SYMBOL         = 'S';
    public static final char MAYOR_MANOR_SYMBOL   = 'M';
    public static final char CARPENTRY_SYMBOL     = 'C';
    public static final char PERRY_CABIN_SYMBOL   = 'R';
    public static final char GAMBLING_DEN_SYMBOL  = 'G';
    public static final char ABIGAIL_TENT_SYMBOL  = 'A';
    public static final char EXIT_SYMBOL          = 'X';
    public static final char ORENJI_SYMBOL        = 'O';
    public static final char FENCE_SYMBOL         = 'F';
    public static final char GENERIC_B_SYMBOL     = 'B'; // If used
    public static final char GENERIC_T_SYMBOL     = 'T'; // If used

    public CityTile(int x, int y) {
        super(x, y);
        // Initialize as UNTILLED road ('.')
        super.deployObject(DEFAULT_UNTILLED_CHAR);
    }

    /**
     * Override deployObject so that CityTile can accept:
     *   - City building/object symbols: S, M, C, R, G, A, X, O, F, B, T
     *   - City terrain symbols: 1..5
     * Everything else delegates to super.deployObject (FarmMap or default '.').
     */
    @Override
    public void deployObject(char c) {
        switch (c) {
            // ---- City building/object symbols ----
            case STORE_SYMBOL:
            case MAYOR_MANOR_SYMBOL:
            case CARPENTRY_SYMBOL:
            case PERRY_CABIN_SYMBOL:
            case GAMBLING_DEN_SYMBOL:
            case ABIGAIL_TENT_SYMBOL:
            case EXIT_SYMBOL:
            case ORENJI_SYMBOL:
            case FENCE_SYMBOL:
            case GENERIC_B_SYMBOL:
            case GENERIC_T_SYMBOL:
                // These must become DEPLOYED. We cannot call setType(DEPLOYED).
                // Instead, use reflection to set the private 'type' field directly:
                try {
                    java.lang.reflect.Field typeField = Tile.class.getDeclaredField("type");
                    typeField.setAccessible(true);
                    typeField.set(this, TileType.DEPLOYED);
                } catch (Exception ex) {
                    System.err.println("CityTile: failed to set type via reflection: " + ex.getMessage());
                }
                // Now set deployedChar via reflection:
                try {
                    java.lang.reflect.Field dcField = Tile.class.getDeclaredField("deployedChar");
                    dcField.setAccessible(true);
                    dcField.setChar(this, c);
                } catch (Exception ex) {
                    System.err.println("CityTile: failed to set deployedChar via reflection: " + ex.getMessage());
                }
                // Clear plantedCrop if present
                try {
                    java.lang.reflect.Field cropField = Tile.class.getDeclaredField("plantedCrop");
                    cropField.setAccessible(true);
                    cropField.set(this, null);
                } catch (Exception ex) {
                    // ignore
                }
                return;

            // ---- City terrain symbols (UNTILLED ground) ----
            case DEFAULT_UNTILLED_CHAR: // '.'
            case RUMPUT_HIJAU_SYMBOL:  // '1'
            case BATU_SYMBOL:          // '2'
            case AIR_SYMBOL:           // '3'
            case BUNGA_PINK_SYMBOL:    // '4'
            case TANAH_SYMBOL:         // '5'
                // These are all treated as UNTILLED. So set type=UNTILLED via reflection:
                try {
                    java.lang.reflect.Field typeField = Tile.class.getDeclaredField("type");
                    typeField.setAccessible(true);
                    typeField.set(this, TileType.UNTILLED);
                } catch (Exception ex) {
                    System.err.println("CityTile: failed to set type via reflection: " + ex.getMessage());
                }
                // Now set deployedChar via reflection:
                try {
                    java.lang.reflect.Field dcField = Tile.class.getDeclaredField("deployedChar");
                    dcField.setAccessible(true);
                    dcField.setChar(this, c);
                } catch (Exception ex) {
                    System.err.println("CityTile: failed to set deployedChar via reflection: " + ex.getMessage());
                }
                // Clear plantedCrop if present:
                try {
                    java.lang.reflect.Field cropField = Tile.class.getDeclaredField("plantedCrop");
                    cropField.setAccessible(true);
                    cropField.set(this, null);
                } catch (Exception ex) {
                    // ignore
                }
                return;

            default:
                // Delegate Farm symbols (h, o, s) or '.' back to the base Tile class
                super.deployObject(c);
        }
    }

    /**
     * Override isWalkable so that:
     *  - If getType() == DEPLOYED, return false (objects/buildings override).
     *  - If deployedChar == AIR_SYMBOL ('3'), not walkable.
     *  - Otherwise, walkable.
     */
    @Override
    public boolean isWalkable() {
        if (getType() == TileType.DEPLOYED) return false;
        char dc = this.displayChar();
        if (dc == AIR_SYMBOL) return false;
        return true;
    }

    /**
     * Override displayChar so that:
     *  - If type == UNTILLED or type == DEPLOYED, read the private deployedChar field.
     *  - If type == TILLED, return 't'; if type == PLANTED, return 'l'.
     */
    @Override
    public char displayChar() {
        switch (getType()) {
            case UNTILLED: {
                try {
                    java.lang.reflect.Field dcField = Tile.class.getDeclaredField("deployedChar");
                    dcField.setAccessible(true);
                    return dcField.getChar(this);
                } catch (Exception ex) {
                    return DEFAULT_UNTILLED_CHAR;
                }
            }
            case TILLED:
                return 't';
            case PLANTED:
                return 'l';
            case DEPLOYED: {
                try {
                    java.lang.reflect.Field dcField = Tile.class.getDeclaredField("deployedChar");
                    dcField.setAccessible(true);
                    return dcField.getChar(this);
                } catch (Exception ex) {
                    return '?';
                }
            }
        }
        return '?';
    }
}
