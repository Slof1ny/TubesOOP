package core.world;

import core.player.Player;
import java.util.List;

/**
 * Interface for any playable map in the game.
 * Defines common map functionalities that all maps (FarmMap, CityMap) should implement.
 */
public interface GameMap {

    /**
     * Get the tile at the given coordinates.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return The Tile object at (x,y), or null if out of bounds.
     */
    Tile getTileAt(int x, int y);

    /**
     * Check if a given coordinate is walkable by the player.
     * @param x X-coordinate.
     * @param y Y-coordinate.
     * @return true if the tile is walkable, false otherwise.
     */
    boolean isWalkable(int x, int y);

    /**
     * Attempt to move the player on this map.
     * The map is responsible for checking walkability and updating the player's position
     * if the move is valid within its bounds.
     * @param player The player object to move.
     * @param dx Delta in X-direction.
     * @param dy Delta in Y-direction.
     * @return true if the player moved successfully on this map, false otherwise.
     */
    boolean movePlayer(Player player, int dx, int dy);

    /**
     * Check if the player is currently at any edge of the map, typically for map transitions.
     * @param player The player object.
     * @return true if player is at an edge of this map, false otherwise.
     */
    boolean atEdge(Player player);

    /**
     * Display the map in a text-based format (for debugging/console).
     * @param player The player object, whose position should be marked on the map.
     */
    void displayMap(Player player);

    /**
     * Get a list of all deployed objects on this map.
     * @return List of DeployedObject.
     */
    List<DeployedObject> getDeployedObjects();

    /**
     * Get the name of the map.
     * @return The map name (e.g., "Farm Map", "City Map").
     */
    String getName();

    /**
     * Get the size of the map (e.g., FarmMap.SIZE).
     * @return The side length of the square map.
     */
    int getSize();
}