# TubesOOP

## CREATOR
Muhammad Aidan Fathullah 	18223002
Henrycus Hugatama Risaldy 	18223008
Nathan Priandi Lesmana 	    18223032
Farella Kamala Budianto 	18223046

## Overview

TubesOOP is a farming and life simulation game developed in Java using the Swing GUI library. Players can create their character, manage a farm, interact with various NPCs, engage in activities like fishing and cooking, and explore different game locations including a farm, a city, and their house. The game features a time and calendar system with changing seasons and weather, impacting gameplay and available activities.

## Features

* **Character Customization:** Create a player character with a name, gender, and farm name.
* **Farming System:**
    * Till land using a Hoe.
    * Plant various seeds that grow over time and seasons.
    * Water crops using a Watering Can.
    * Harvest mature crops.
    * Recover tilled land using a Pickaxe.
* **NPC Interaction:**
    * A diverse cast of NPCs with unique personalities and home locations (e.g., Emily, Mayor Tadi, Abigail, Caroline, Dasco, Orenji, Perry).
    * Chat with NPCs to build relationships.
    * Gift items to NPCs, with different reactions (loved, liked, hated, neutral) affecting heart points.
    * Relationship progression including proposing and marriage with eligible NPCs (requires Proposal Ring and max heart points).
* **Time and Calendar System:**
    * In-game time progresses in 5-minute intervals.
    * Day and night cycle.
    * Seasons (Spring, Summer, Fall, Winter), each lasting 10 days.
    * Dynamic weather (Sunny, Rainy) affecting the game.
* **Player Management:**
    * Manage player energy, which is consumed by actions and restored by eating or sleeping.
    * Manage player gold, earned by selling items and spent on purchases.
    * Inventory system to store items (seeds, crops, food, fish, miscellaneous) and equipment.
    * Equipment system: own, equip, and unequip tools like Hoe, Watering Can, Pickaxe, Fishing Rod.
* **Game World & Locations:**
    * **Farm Map:** Player's main area for farming activities, includes the player's house, a pond, and a shipping bin.
    * **City Map:** Contains various buildings and NPC homes/shops like Emily's Store, Mayor's Manor, Caroline's Carpentry, etc. Allows travel to other fishing locations.
    * **House Map:** Player's house interior with a bed (for sleeping), TV (for weather forecast), and a stove (for cooking).
* **Activities:**
    * **Fishing:** Catch different types of fish (Common, Regular, Legendary) at various locations (Pond, Mountain Lake, Forest River, Ocean). Fishing is affected by season, time, and weather. Includes a mini-game.
    * **Cooking:** Cook food items using recipes, ingredients, and fuel (Firewood, Coal) at the stove. Recipes can be unlocked through various conditions.
    * **Shopping:** Buy items (seeds, food, equipment, etc.) from Emily's Store.
    * **Selling:** Sell items by placing them in the Shipping Bin, with sales processed overnight.
* **GUI:**
    * Main Menu, Player Creation screen.
    * In-game views for Farm, City, and House maps.
    * Panels for Inventory, Store, Shipping Bin, NPC Interactions, Cooking, Help, and Statistics.
    * Top information bar displaying player stats, time, date, and weather.
    * Autopilot feature to guide the player to bed if energy is critically low or time reaches 2 AM.
* **Statistics:** Tracks various player achievements and game progress, shown when certain milestones are met (e.g., earning a specific amount of gold, getting married).

## How to Run

1.  Compile all `.java` files within the `src` directory. Ensure all resource files (images, CSVs) are correctly placed in the `resources` directory and accessible in the classpath (typically in a `classes` or `bin` directory after compilation if your build process copies them).
    ```bash
    # Example compilation (assuming you are in the TubesOOP project root)
    find src -name "*.java" > sources.txt
    javac -d classes @sources.txt
    # Ensure resources are copied to 'classes/resources' if not handled by an IDE
    # mkdir -p classes/resources
    # cp -r resources/* classes/resources/
    ```
2.  Run the `GameView` class:
    ```bash
    java -cp classes gui.GameView
    ```
    *(Note: If your project structure or build process differs, adjust the classpath and main class accordingly.)*

## Key Controls

* **WASD:** Move Player
* **E:** Interact with objects, NPCs, or locations.
* **I:** Open/Close Inventory screen.
* **P:** Plant Seed (on Farm Map, if conditions are met).
* **T:** Till Land (on Farm Map, requires Hoe).
* **H:** Harvest Crop (on Farm Map) / Access Help Screen (during gameplay).
* **R:** Water Crop (on Farm Map, requires Watering Can).
* **M:** Attempt to move to City from Farm Map edge (or vice-versa).
* **F1:** Open Help Screen.
* **ESC:** Close current menu/screen (e.g., Help, Inventory, NPC Interaction, Store).

## Project Structure (Key Packages)

* `action/`: Contains classes for player and NPC actions (e.g., `Action.java`, `NPCActions.java`).
* `cooking/`: Manages cooking mechanics, recipes, and fuel (e.g., `CookingManager.java`, `RecipeData.java`, `Fuel.java`).
* `core/`: Core game logic including:
    * `player/`: Player attributes, inventory, stats, gold, relationships (e.g., `Player.java`, `Inventory.java`).
    * `world/`: Game maps, tiles, seasons, weather, deployed objects (e.g., `FarmMap.java`, `CityMap.java`, `Tile.java`).
    * `house/`: Player's house and furniture (e.g., `House.java`, `HouseMap.java`).
* `fishing/`: Handles fishing mechanics, fish types, and locations (e.g., `FishingManager.java`, `FishRegistry.java`).
* `gui/`: All Swing-based graphical user interface components and controllers (e.g., `GameView.java`, `FarmMapPanel.java`, `CityMapController.java`).
* `item/`: Defines different types of items in the game like seeds, crops, food, equipment (e.g., `Item.java`, `Seed.java`, `Equipment.java`).
* `npc/`: Defines Non-Player Characters, their behaviors, and specific NPC classes (e.g., `NPC.java`, `Emily.java`).
* `recipe/`: Defines the structure for cooking recipes (e.g., `Recipe.java`).
* `system/`: Manages overarching game systems like game state, time, store, price lists (e.g., `GameManager.java`, `Store.java`).
* `time/`: Handles in-game time, calendar, seasons (e.g., `Time.java`, `GameCalendar.java`).
* `resources/`: Contains game assets like images, data files (e.g., `price_list.csv`).
* `test/`: Contains tester classes for various game components.