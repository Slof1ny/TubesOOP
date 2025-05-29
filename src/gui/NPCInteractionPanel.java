package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import system.GameManager;
import npc.NPC;
import action.NPCActions; // Ensure this is your correct NPCActions class
import core.player.Player;
import core.player.RelationshipStatus; // Import for relationship status enum
import item.Item;

public class NPCInteractionPanel extends JPanel {
    private GameView gameView;
    private GameManager gameManager;
    private NPCActions npcActions;
    private NPC currentNpc;

    private JLabel npcNameLabel;
    private JTextArea dialogueArea;
    private JButton chatButton;
    private JButton giftButton;
    private JButton proposeButton;
    private JButton marryButton;
    private JButton leaveButton;

    public NPCInteractionPanel(GameView gameView, GameManager gameManager) {
        this.gameView = gameView;
        this.gameManager = gameManager;
        this.npcActions = new NPCActions(gameManager.getPlayer(), gameManager.getGameTime());

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(220, 220, 240));

        initComponents();
        addEventListeners();

        setFocusable(true);
        System.out.println("NPCInteractionPanel: Initialized and focusable set.");
    }

    private void initComponents() {
        npcNameLabel = new JLabel("NPC Name", SwingConstants.CENTER);
        npcNameLabel.setFont(new Font("Serif", Font.BOLD, 28));
        add(npcNameLabel, BorderLayout.NORTH);

        dialogueArea = new JTextArea("Welcome! Interact with the NPC.");
        dialogueArea.setFont(new Font("SansSerif", Font.PLAIN, 16));
        dialogueArea.setEditable(false);
        dialogueArea.setLineWrap(true);
        dialogueArea.setWrapStyleWord(true);
        dialogueArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(dialogueArea);
        add(scrollPane, BorderLayout.CENTER);

        // JPanel buttonPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.setOpaque(false);

        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(120, 40); // Adjust if needed

        chatButton = new JButton("Chat");
        giftButton = new JButton("Gift");
        proposeButton = new JButton("Propose");
        marryButton = new JButton("Marry");
        leaveButton = new JButton("Leave");

        JButton[] buttons = {chatButton, giftButton, proposeButton, marryButton, leaveButton};
        String[] buttonTexts = {"Chat", "Gift", "Propose", "Marry", "Leave"};

        System.out.println("NPCInteractionPanel.initComponents: Creating buttons...");
        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setText(buttonTexts[i]);
            buttons[i].setFont(buttonFont);
            // buttons[i].setPreferredSize(buttonSize); // Can sometimes cause layout issues if too restrictive
            buttons[i].setBackground(new Color(70, 130, 180));
            buttons[i].setForeground(Color.BLACK);
            buttons[i].setFocusPainted(false);
            buttonPanel.add(buttons[i]);
            System.out.println("NPCInteractionPanel: Added button - " + buttonTexts[i]);
        }
        add(buttonPanel, BorderLayout.SOUTH);
        System.out.println("NPCInteractionPanel.initComponents: Buttons created and panel added.");
    }

    public void setupForNPC(NPC npc) {
        this.currentNpc = npc;
        System.out.println("NPCInteractionPanel.setupForNPC: Setting up for NPC - " + (npc != null ? npc.getName() : "NULL NPC"));
        if (npc != null) {
            npcNameLabel.setText(npc.getName());
            dialogueArea.setText("You are interacting with " + npc.getName() + ".\nSelect an action.");
            updateActionButtons(); // This is crucial
        } else {
            npcNameLabel.setText("Error: NPC Not Found");
            dialogueArea.setText("Could not load NPC data. Cannot interact.");
            // Disable all buttons if NPC is null
            chatButton.setEnabled(false);
            giftButton.setEnabled(false);
            proposeButton.setEnabled(false);
            marryButton.setEnabled(false);
            // leaveButton should probably still be enabled
            leaveButton.setEnabled(true);
        }
        // Request focus after the panel is likely visible and configured
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            revalidate(); // Add these
            repaint();    // Add these
        });
    }

    private void updateActionButtons() {
        Player player = gameManager.getPlayer();
        System.out.println("NPCInteractionPanel.updateActionButtons: Updating for NPC - " + (currentNpc != null ? currentNpc.getName() : "NULL NPC"));

        if (currentNpc == null || player == null) {
            System.out.println("NPCInteractionPanel.updateActionButtons: currentNpc or player is null. Disabling interaction buttons.");
            chatButton.setEnabled(false);
            giftButton.setEnabled(false);
            proposeButton.setEnabled(false);
            marryButton.setEnabled(false);
            leaveButton.setEnabled(true); // Leave should always be available
            return;
        }

        // Default states
        chatButton.setEnabled(true);
        giftButton.setEnabled(true);
        leaveButton.setEnabled(true);
        System.out.println("Chat Button Enabled: " + chatButton.isEnabled());
        System.out.println("Gift Button Enabled: " + giftButton.isEnabled());


        // Propose button logic
        boolean hasProposalRing = player.getInventory().getItemCount("Proposal Ring") > 0;
        boolean npcIsSingle = currentNpc.getRelationshipStatus() == RelationshipStatus.SINGLE;
        boolean playerCanProposeThisNpc = player.isSingle() || (player.getPartner() == currentNpc && currentNpc.getRelationshipStatus() != RelationshipStatus.MARRIED);
        boolean heartsMaxed = currentNpc.getHeartPoints() >= NPC.MAX_HEART_POINTS;

        boolean canPropose = hasProposalRing && heartsMaxed && npcIsSingle && playerCanProposeThisNpc;
        proposeButton.setEnabled(canPropose);

        System.out.println("--- Propose Check ---");
        System.out.println("Has Proposal Ring: " + hasProposalRing);
        System.out.println("NPC Hearts: " + currentNpc.getHeartPoints() + " (Need >= " + NPC.MAX_HEART_POINTS + ": " + heartsMaxed +")");
        System.out.println("NPC is Single: " + npcIsSingle + " (Status: " + currentNpc.getRelationshipStatus() + ")");
        System.out.println("Player can propose this NPC: " + playerCanProposeThisNpc + " (Player single: "+player.isSingle()+", Player partner: "+(player.getPartner() != null ? player.getPartner().getName() : "None")+")");
        System.out.println("Propose Button Enabled: " + canPropose);

        // Marry button logic
        boolean npcIsFiance = currentNpc.getRelationshipStatus() == RelationshipStatus.FIANCE;
        boolean playerEngagedToThisNpc = player.getPartner() == currentNpc && npcIsFiance;
        // Note: The "1 day after proposal" check is usually handled by NPCActions.marryNPC,
        // but you could add a basic check here if NPCActions doesn't prevent it.
        // For simplicity, we'll assume NPCActions.marryNPC handles the timing.

        boolean canMarry = hasProposalRing && playerEngagedToThisNpc;
        marryButton.setEnabled(canMarry);
        System.out.println("--- Marry Check ---");
        System.out.println("Has Proposal Ring (for marry): " + hasProposalRing);
        System.out.println("NPC is Fiance: " + npcIsFiance);
        System.out.println("Player engaged to this NPC: " + playerEngagedToThisNpc);
        System.out.println("Marry Button Enabled: " + canMarry);
    }

    private void addEventListeners() {
        chatButton.addActionListener(e -> {
            System.out.println("Chat Button Clicked");
            if (currentNpc != null) {
                String interactionResult = npcActions.chatWithNPC(currentNpc);
                dialogueArea.setText(currentNpc.getChatDialogue(gameManager.getPlayer()) + "\n\n--- Interaction Feedback ---\n" + interactionResult);
                gameManager.getTopInfoBarPanel().refreshInfo();
                updateActionButtons();
            }
        });

        giftButton.addActionListener(e -> {
            System.out.println("Gift Button Clicked");
            if (currentNpc != null) {
                String itemName = JOptionPane.showInputDialog(this,
                        "Enter item name to gift to " + currentNpc.getName() + ":",
                        "Gift Item", JOptionPane.PLAIN_MESSAGE);

                if (itemName != null && !itemName.trim().isEmpty()) {
                    Item itemToGift = gameManager.getPlayer().getInventory().findItemByName(itemName.trim());
                    if (itemToGift != null) {
                        if (gameManager.getPlayer().getInventory().getItemCount(itemToGift.getName()) > 0) {
                            String result = npcActions.giftToNPC(currentNpc, itemToGift);
                            dialogueArea.setText("--- Gift Interaction ---\n" + result);
                        } else {
                            dialogueArea.setText("You don't have any " + itemName.trim() + " to gift.");
                        }
                    } else {
                        dialogueArea.setText("Item '" + itemName.trim() + "' not found in your inventory.");
                    }
                    gameManager.getTopInfoBarPanel().refreshInfo();
                    updateActionButtons();
                }
            }
        });

        proposeButton.addActionListener(e -> {
            System.out.println("Propose Button Clicked. Enabled: " + proposeButton.isEnabled());
            if (currentNpc != null && proposeButton.isEnabled()) {
                boolean hasRing = gameManager.getPlayer().getInventory().getItemCount("Proposal Ring") > 0;
                String result = npcActions.proposeToNPC(currentNpc, hasRing);
                dialogueArea.setText("--- Proposal Attempt ---\n" + result);
                gameManager.getTopInfoBarPanel().refreshInfo();
                updateActionButtons();
            } else if (currentNpc != null) {
                 System.out.println("Propose button was clicked but was not enabled. Conditions not met.");
                 dialogueArea.setText("Cannot propose at this time. Check conditions (hearts, ring, status).");
            }
        });

        marryButton.addActionListener(e -> {
            System.out.println("Marry Button Clicked. Enabled: " + marryButton.isEnabled());
            if (currentNpc != null && marryButton.isEnabled()) {
                boolean hasRing = gameManager.getPlayer().getInventory().getItemCount("Proposal Ring") > 0;
                String result = npcActions.marryNPC(currentNpc, hasRing); // Assumes NPCActions handles day check
                dialogueArea.setText("--- Marriage Attempt ---\n" + result);
                if (result.toLowerCase().contains("selamat")) { // Check for successful marriage
                    gameManager.getGameTime().skipTo(22, 0); // Time skip as per spec
                    // Consider if player should be moved or screen changed automatically.
                    // For now, they remain on this screen until "Leave" is clicked.
                }
                gameManager.getTopInfoBarPanel().refreshInfo();
                updateActionButtons();
            } else if (currentNpc != null) {
                System.out.println("Marry button was clicked but was not enabled. Conditions not met.");
                dialogueArea.setText("Cannot marry at this time. Check conditions (fiance status, ring).");
            }
        });

        leaveButton.addActionListener(e -> {
            System.out.println("Leave Button Clicked");
            performLeaveAction();
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.out.println("Escape Key Pressed on NPCInteractionPanel");
                    performLeaveAction();
                }
            }
        });
        System.out.println("NPCInteractionPanel.addEventListeners: Listeners added.");
    }

    private void performLeaveAction() {
        System.out.println("NPCInteractionPanel.performLeaveAction: Leaving to CityScreen.");
        gameView.showScreen("CityScreen");
    }
}