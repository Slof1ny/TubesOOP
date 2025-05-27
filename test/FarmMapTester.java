package test;

import core.world.FarmMap;
import core.player.Player;
import java.util.Scanner;

// javac -cp src -d out test/DemoFarmMap.java
// java -cp out test.DemoFarmMap

public class FarmMapTester {
    public static void main(String[] args) {
        Player player = new Player("budi", "MALE") {
            private int x, y;

            @Override public void setPosition(int x, int y) { 
                this.x = x; this.y = y; 
            }

            @Override public int getX() { 
                return x; 
            }

            @Override public int getY() { 
                return y; 
            }

            @Override public String getLocation() { 
                return "Farm Map"; 
            }
            
            @Override public core.world.Tile getCurrentTile() {
                return new FarmMap(this).getTileAt(x, y);
            }
        };

        // Build & display the map
        FarmMap farm = new FarmMap(player);
        
        System.out.println("=== Farm Map Demo ===");
        System.out.println("Legend: P = Player, h = House, o = Pond, s = Shipping Bin, . = Untilled, t = Tilled, l = Planted");
        System.out.println();
        
        farm.displayFarmMap();
        System.out.printf("Player spawned at (%d, %d)%n", player.getX(), player.getY());
        
        // Interactive movement demo
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== Movement Test ===");
        System.out.println("Use WASD to move (W=up, A=left, S=down, D=right), Q to quit:");
        
        while (true) {
            System.out.print("Move: ");
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.equals("q")) break;
            
            boolean moved = false;
            switch (input) {
                case "w":
                    moved = farm.movePlayerUp();
                    break;
                case "a":
                    moved = farm.movePlayerLeft();
                    break;
                case "s":
                    moved = farm.movePlayerDown();
                    break;
                case "d":
                    moved = farm.movePlayerRight();
                    break;
                default:
                    System.out.println("Invalid input. Use W/A/S/D or Q to quit.");
                    continue;
            }
            
            if (moved) {
                System.out.println("Moved to (" + player.getX() + ", " + player.getY() + ")");
            } else {
                System.out.println("Can't move there - blocked or out of bounds!");
            }
            
            System.out.println();
            farm.displayFarmMap();
        }
        
        scanner.close();
        System.out.println("Demo ended.");
    }
}