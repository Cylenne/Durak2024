package GUI;

import Card.Card;
import Phases.StartPhase;
import Player.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

// SHOULD I WRITE THIS CLASS AS 3 IMPLEMENTING A COMMON DIALOG INTERFACE?

public class HumanPlayerScreenManager {
    private JPanel humanCardsPanel;
    private JPanel humanPlayerPanel;
    private boolean selectButtonAdded = false;
    private JButton selectButton;
    private Map<JToggleButton, Card> buttonToCardMap = new HashMap<>();
    private JDialog dialog;

    public HumanPlayerScreenManager(JPanel humanPlayerPanel, JPanel humanCardsPanel) {
        this.humanPlayerPanel = humanPlayerPanel;
        this.humanCardsPanel = humanCardsPanel;
    }

    public void updateHumanPanelAfterRedraw() {
        for (Player player : StartPhase.getPlayers())
            if (player instanceof HumanPlayer) {
                humanCardsPanel.removeAll();
                for (Card card : player.getHand()) {
                    humanCardsPanel.add(new JLabel(card.toImageIcon()));
                }
            }

        humanCardsPanel.revalidate();
        humanCardsPanel.repaint();
    }

    public void updateHumanPanelWithRemainingCards() { // rebuilding humanPlayerPanel after dialog
        humanPlayerPanel.removeAll();
        humanPlayerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        humanPlayerPanel.setLayout(new BoxLayout(humanPlayerPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Your cards:");
        humanPlayerPanel.add(label);

        humanCardsPanel = new JPanel(new FlowLayout());

        List<Card> humanPlayerHand = Collections.emptyList(); // this part could be shortened with a parameter?
        for (Player player : StartPhase.getPlayers())
            if (player instanceof HumanPlayer) {
                humanPlayerHand = player.getHand();
            }

        for (Card card : humanPlayerHand) {
            humanCardsPanel.add(new JLabel(card.toImageIcon()));
        }

        humanPlayerPanel.add(humanCardsPanel);
    }

    public Set<Card> humanInitialAttackDialog(Player attacker, Player defender) {
        Set<Card> selectedCards = new HashSet<>();
        final int[] selectedRank = {-1}; // -1 represents no selected rank,
        // and it was transformed into a final one element array so that the inner class can refer to it
        initializeHumanCardsPanel(attacker, selectedCards, defender, selectedRank);
        initializeSelectButton();
        createAndShowDialog();

        return selectedCards;
    }

    public Set<Card> humanDefenseDialog(Player defender, List<Card> attackingCards) {
        Set<Card> selectedCards = new HashSet<>();
        initializeHumanCardsPanel(defender, selectedCards, attackingCards);
        createAndShowDialog();
        initializeSelectButton(attackingCards, selectedCards, centerPanel, message);


        return selectedCards;

    }

    private void initializeHumanCardsPanel(Player attacker, Set<Card> selectedCards, Player defender, int[] selectedRank) {
        humanCardsPanel.removeAll();

        // we only use ButtonGroup to show that these buttons belong together
        ButtonGroup buttonGroup = new ButtonGroup();

        for (Card card : attacker.getHand()) {
            JToggleButton cardButton = createCardButton(card, selectedRank, selectedCards, defender);
            buttonGroup.add(cardButton);
            humanCardsPanel.add(cardButton);
        }

        humanPlayerPanel.revalidate();
        humanPlayerPanel.repaint();
    }

    // overloaded method for defense
    private void initializeHumanCardsPanel(Player defender, Set<Card> selectedCards, List<Card> attackingCards) {
        humanCardsPanel.removeAll();

        ButtonGroup buttonGroup = new ButtonGroup();

        for (Card card : defender.getHand()) {
            JToggleButton cardButton = createCardButton(card, attackingCards, selectedCards);
            buttonGroup.add(cardButton);
            humanCardsPanel.add(cardButton);
        }

        humanPlayerPanel.revalidate();
        humanPlayerPanel.repaint();
    }

    private JToggleButton createCardButton(Card card, int[] selectedRank, Set<Card> selectedCards, Player defender) {

        JToggleButton cardButton = new JToggleButton(card.toImageIcon()); // button can be either "on" or "off"

        associateCardWithButton(card, cardButton);
        cardButton.setPreferredSize(new Dimension(84, 104)); // +=4 to ensure that the selection frame won't block the image's edges
        cardButton.setBorder(BorderFactory.createEmptyBorder());
        cardButton.setContentAreaFilled(false); // allows background image to show through
        cardButton.setFocusPainted(false); // not painting a focus rectangle around the button when it gains focus
        cardButton.addActionListener(createCardButtonActionListener(card, selectedRank, selectedCards, defender));
        return cardButton;
    }

    // overloaded method for defense
    private JToggleButton createCardButton(Card card, List<Card> attackingCards, Set<Card> selectedCards) {

        JToggleButton cardButton = new JToggleButton(card.toImageIcon());

        cardButton.setPreferredSize(new Dimension(84, 104));
        cardButton.setBorder(BorderFactory.createEmptyBorder());
        cardButton.setContentAreaFilled(false);
        cardButton.setFocusPainted(false);
        cardButton.addActionListener(createCardButtonActionListener(card, attackingCards, selectedCards));
        return cardButton;
    }

    private ActionListener createCardButtonActionListener(Card card, int[] selectedRank, Set<Card> selectedCards, Player defender) {
        return new ActionListener() {
            private boolean isSelected = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                isSelected = !isSelected; // toggle selected state
                Card selectedCard = getCardFromButton(selectedButton);

                if (selectedRank[0] == -1 || selectedCard.getRank() == selectedRank[0]) { // if no rank's selected rank or same rank's selected
                    if (isSelected && selectedCards.size() < defender.getHand().size()) {
                        selectedCards.add(card);
                        selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
                        // true ensures that the border is painted exactly at the edge of the component
                        selectedRank[0] = selectedCard.getRank();
                    } else {
                        selectedCards.remove(card);
                        selectedButton.setBorder(BorderFactory.createEmptyBorder());
                        if (selectedCards.isEmpty()) {
                            selectedRank[0] = -1;
                        }
                    }
                } else {
                    isSelected = !isSelected; // Revert the toggle
                    selectedButton.setSelected(false); // deselect the button
                }
            }
        };
    }

    // overloaded method for defense
    private ActionListener createCardButtonActionListener(Card defendingCard, List<Card> attackingCards, Set<Card> selectedCards) {
        return new ActionListener() {

            private boolean isSelected = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                isSelected = !isSelected; // toggle selected state

                for (Card attackingCard : attackingCards) {
                    if (defendingCard.canBeat(attackingCard)) { // THIS IS SUCH AN ELEGANT SOLUTION, MAYBE I COULD DO MORE OF THESE??
                        selectedCards.add(defendingCard);
                        selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));

                    } else {
                        selectedCards.remove(defendingCard);
                        selectedButton.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
                isSelected = !isSelected;
                selectedButton.setSelected(false);

            }
        };
    }

    private void initializeSelectButton() {
        if (!selectButtonAdded) {
            selectButton = new JButton("Select Cards");
        }
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
//              System.out.println("Selected cards: " + selectedCards); // REMOVE WHEN APP IS READY
                dialog.dispose(); // close the dialog and resume game flow
            }
        });
        humanPlayerPanel.add(selectButton);
        selectButtonAdded = true;
    }

    // overloaded method for humanDefense
    private void initializeSelectButton(List<Card> attackingCards, Set<Card> defendingCards, JPanel centerPanel, JLabel message) {
        if (!selectButtonAdded) {
            selectButton = new JButton("Select Cards");
        }
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (attackingCards.size() != defendingCards.size()) {
                    centerPanel.remove(message);
                    centerPanel.add(new JLabel("You need to select as many defending cards as there are attacking cards:"),
                            createConstraints(0, 0, 1, 1, GridBagConstraints.CENTER)); // center the text
                    centerPanel.revalidate();
                    centerPanel.repaint();
                }
//              System.out.println("Selected cards: " + selectedCards); // REMOVE WHEN APP IS READY
                dialog.dispose(); // close the dialog and resume game flow
            }
        });
        humanPlayerPanel.add(selectButton);
        selectButtonAdded = true;
    }

    private void createAndShowDialog() {
        // modal dialog automatically stops the game flow until user action takes place
        dialog = new JDialog((Frame) null, "Select Cards", true); // true for modal
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        dialog.setIconImage(iconImage);

//            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // prevent the player from not selecting any cards by closing the dialog box
        // UNCOMMENT THE ABOVE WHEN THE APP IS ALMOST READY

        JPanel centerPanel = new JPanel(new GridBagLayout());
        JLabel message = new JLabel("Your cards:");
        centerPanel.add(message, createConstraints(0, 0, 1, 1, GridBagConstraints.CENTER)); // center the text
        centerPanel.add(humanCardsPanel, createConstraints(0, 1, 1, 1, GridBagConstraints.CENTER));
        centerPanel.add(selectButton, createConstraints(0, 2, 1, 1, GridBagConstraints.CENTER));
        dialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

        dialog.pack(); // sets the size of the dialog to be just large enough to accommodate all of its components
        dialog.setLocationRelativeTo(null); // center the dialog on the screen
        dialog.setVisible(true);
    }

    // used for centering
    private GridBagConstraints createConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.anchor = anchor;
        constraints.insets = new Insets(5, 5, 5, 5); // padding
        return constraints;
    }

    private Card getCardFromButton(JToggleButton button) {
        return buttonToCardMap.get(button);
    }

    private void associateCardWithButton(Card card, JToggleButton button) {
        buttonToCardMap.put(button, card);
    }


}

