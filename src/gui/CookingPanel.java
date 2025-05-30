// Path: TubesOOP/src/gui/CookingPanel.java
package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
// import java.util.Vector; // Not needed if not directly manipulating JList model with Vector

import system.GameManager;
import core.player.Player;
import recipe.Recipe;
import cooking.Fuel;
import cooking.FuelRegistry;
import cooking.CookingManager;
// import item.Item; // Not directly used for display list anmyore

public class CookingPanel extends JPanel {
    // ... (attributes remain mostly the same) ...
    private GameView gameView;
    private GameManager gameManager;
    private Player player;
    private CookingManager cookingManager;

    private JList<String> recipeJList;
    private DefaultListModel<String> recipeListModel;
    private JTextArea recipeDetailsArea;
    private JComboBox<String> fuelComboBox;
    private JButton cookButton;
    private JButton backButton;
    private JLabel feedbackLabel;

    private List<Recipe> availableRecipes;


    public CookingPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;
        this.player = gameManager.getPlayer();
        this.cookingManager = gameManager.getCookingManager();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(240, 220, 200));

        initComponents();
        addEventListeners();
        setFocusable(true);
    }

    private void initComponents() {
        // ... (Title, MainPanel, RecipeListPanel setup is the same) ...
        JLabel titleLabel = new JLabel("Let's Cook!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        mainPanel.setOpaque(false);

        JPanel recipeListPanel = new JPanel(new BorderLayout(5, 5));
        recipeListPanel.setOpaque(false);
        recipeListPanel.setBorder(BorderFactory.createTitledBorder("Available Recipes"));
        recipeListModel = new DefaultListModel<>();
        recipeJList = new JList<>(recipeListModel);
        recipeJList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        recipeJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        recipeListPanel.add(new JScrollPane(recipeJList), BorderLayout.CENTER);
        mainPanel.add(recipeListPanel);
        
        JPanel detailsActionPanel = new JPanel(new BorderLayout(5, 10));
        detailsActionPanel.setOpaque(false);
        detailsActionPanel.setBorder(BorderFactory.createTitledBorder("Recipe Details & Cooking"));

        recipeDetailsArea = new JTextArea("Select a recipe to see details.");
        recipeDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        recipeDetailsArea.setEditable(false);
        recipeDetailsArea.setLineWrap(true);
        recipeDetailsArea.setWrapStyleWord(true);
        recipeDetailsArea.setRows(10);
        detailsActionPanel.add(new JScrollPane(recipeDetailsArea), BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlsPanel.setOpaque(false);
        controlsPanel.add(new JLabel("Fuel:"));
        fuelComboBox = new JComboBox<>(new String[]{"Firewood", "Coal"});
        fuelComboBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        controlsPanel.add(fuelComboBox);

        cookButton = new JButton("Cook Selected Recipe");
        cookButton.setFont(new Font("Arial", Font.BOLD, 14));
        cookButton.setEnabled(false);
        controlsPanel.add(cookButton);
        detailsActionPanel.add(controlsPanel, BorderLayout.SOUTH);

        mainPanel.add(detailsActionPanel);
        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(10,5));
        bottomPanel.setOpaque(false);
        feedbackLabel = new JLabel(" ", SwingConstants.CENTER);
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        bottomPanel.add(feedbackLabel, BorderLayout.CENTER);

        backButton = new JButton("Back to House");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel backButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backButtonPanel.setOpaque(false);
        backButtonPanel.add(backButton);
        bottomPanel.add(backButtonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void refreshCookingPanel() {
        // ... (same as before) ...
        if (player == null || cookingManager == null) {
            recipeListModel.clear();
            recipeListModel.addElement("Error: Player or CookingManager not available.");
            recipeDetailsArea.setText("Cannot load recipes.");
            cookButton.setEnabled(false);
            return;
        }

        availableRecipes = cookingManager.getAvailableRecipes();
        recipeListModel.clear();

        if (availableRecipes.isEmpty()) {
            recipeListModel.addElement("No recipes unlocked yet.");
            recipeDetailsArea.setText("Unlock recipes to start cooking!");
            cookButton.setEnabled(false);
        } else {
            for (Recipe recipe : availableRecipes) {
                recipeListModel.addElement(recipe.getRecipeName());
            }
            if (!availableRecipes.isEmpty()) { // Check before setting index
                recipeJList.setSelectedIndex(0);
            }
        }
        updateRecipeDetails(); // This will be called after setting index
        updateButtonStates();
        feedbackLabel.setText("Select a recipe and fuel, then click 'Cook'.");

        SwingUtilities.invokeLater(this::requestFocusInWindow);
    }

    private void updateRecipeDetails() {
        int selectedIndex = recipeJList.getSelectedIndex();
        if (selectedIndex != -1 && availableRecipes != null && selectedIndex < availableRecipes.size()) {
            Recipe selectedRecipe = availableRecipes.get(selectedIndex);
            StringBuilder details = new StringBuilder();
            details.append("Recipe: ").append(selectedRecipe.getRecipeName()).append("\n");
            details.append("Output: ").append(selectedRecipe.getOutputQuantity()).append("x ").append(selectedRecipe.getOutputItemName()).append("\n\n"); // Use OutputItemName
            details.append("Ingredients:\n");
            // Iterate over Map<String, Integer> for ingredient names
            for (Map.Entry<String, Integer> entry : selectedRecipe.getRequiredIngredientNames().entrySet()) {
                String ingredientName = entry.getKey();
                // Special display for "ANY_FISH"
                if (ingredientName.equals("ANY_FISH")) {
                    ingredientName = "Any Fish";
                }
                details.append("  - ").append(ingredientName).append(": ").append(entry.getValue()).append("\n");
            }
            details.append("\nEnergy Cost to Start: ").append(Recipe.ENERGY_COST_TO_START_COOKING).append("\n");
            details.append("Cooking Time: ").append(Recipe.COOKING_DURATION_MINUTES).append(" game minutes (passive)\n"); // [cite: 219]
            recipeDetailsArea.setText(details.toString());
            recipeDetailsArea.setCaretPosition(0);
        } else {
            recipeDetailsArea.setText("Select a recipe to see details.");
        }
    }

    private void updateButtonStates() {
        // ... (same as before) ...
        cookButton.setEnabled(recipeJList.getSelectedIndex() != -1 && availableRecipes != null && !availableRecipes.isEmpty());
    }

    private void addEventListeners() {
        // ... (recipeJList selection listener is the same) ...
        recipeJList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateRecipeDetails();
                updateButtonStates();
            }
        });

        cookButton.addActionListener(e -> {
            int selectedIndex = recipeJList.getSelectedIndex();
            if (selectedIndex != -1 && selectedIndex < availableRecipes.size()) {
                Recipe selectedRecipe = availableRecipes.get(selectedIndex);
                String selectedFuelName = (String) fuelComboBox.getSelectedItem();
                Fuel fuel = FuelRegistry.getFuelByName(selectedFuelName);

                if (fuel == null) {
                    feedbackLabel.setText("Error: Selected fuel is invalid.");
                    JOptionPane.showMessageDialog(this, "Invalid fuel selected.", "Cooking Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String result = cookingManager.startCooking(selectedRecipe.getRecipeId(), fuel);
                feedbackLabel.setText(result);
                JOptionPane.showMessageDialog(this, result, "Cooking Attempt", JOptionPane.INFORMATION_MESSAGE);
                
                if (gameManager.getTopInfoBarPanel() != null) {
                    gameManager.getTopInfoBarPanel().refreshInfo();
                }
                refreshCookingPanel(); 
            }
        });

        backButton.addActionListener(e -> {
            gameView.showScreen("HouseScreen");
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    backButton.doClick();
                }
            }
        });
    }
     public void onShow() {
        refreshCookingPanel();
        this.requestFocusInWindow();
    }
}