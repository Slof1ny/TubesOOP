package system;

import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PriceList {
    private static final Map<String, Integer> buyPrices = new HashMap<>();
    private static final Map<String, Integer> sellPrices = new HashMap<>();

    public static void loadPrices(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // skip header
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split(",");
                String name = tokens[0];
                int buy = tokens[2].equals("-") ? -1 : Integer.parseInt(tokens[2]);
                int sell = tokens[3].equals("-") ? -1 : Integer.parseInt(tokens[3]);
                buyPrices.put(name, buy);
                sellPrices.put(name, sell);
            }
        }
    }

    public static int getBuyPrice(String itemName) {
        return buyPrices.getOrDefault(itemName, -1);
    }

    public static int getSellPrice(String itemName) {
        return sellPrices.getOrDefault(itemName, -1);
    }
}

