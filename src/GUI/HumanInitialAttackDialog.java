package GUI;

import Card.Card;
import Player.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class HumanInitialAttackDialog {
    private boolean selectButtonAdded = false;
    private JButton selectButton;
    private Map<JToggleButton, Card> buttonToCardMap = new HashMap<>();
    private JDialog dialog = new JDialog((Frame) null, "Select Attacking Cards", true); // true for modal;
    private DialogUtils dialogUtils;
    private JPanel dialogPanel = new JPanel();
    private JPanel humanCardsPanel = new JPanel();;
    private Set<Card> selectedCards = new HashSet<>();
    private final int[] selectedRank = {-1}; // -1 represents no selected rank,
    // and it was transformed into a final one element array so that the inner class can refer to it

    public Set<Card> execute(Player attacker, Player defender) {

        initializeHumanCardsPanel(attacker, defender);
        initializeSelectButton();

        dialogUtils = new DialogUtils(dialog, humanCardsPanel, selectButton);
        dialogUtils.createAndShowDialog();

        return selectedCards;
    }

    private void initializeHumanCardsPanel(Player attacker, Player defender) {
        dialogPanel.add(humanCardsPanel);
        humanCardsPanel.setLayout(new FlowLayout());

        // we use ButtonGroup because at first, only one card can be chosen, and all consecutively chosen cards depend on this one card
        ButtonGroup buttonGroup = new ButtonGroup();

        for (Card card : attacker.getHand()) {
            JToggleButton cardButton = createCardButton(card, defender);
            buttonGroup.add(cardButton);
            humanCardsPanel.add(cardButton);
        }
    }

    private JToggleButton createCardButton(Card card, Player defender) {

        JToggleButton cardButton = new JToggleButton(card.toImageIcon()); // button can be either "on" or "off"

        associateCardWithButton(card, cardButton);
        cardButton.setPreferredSize(new Dimension(84, 104)); // +=4 to ensure that the selection frame won't block the image's edges
        cardButton.setBorder(BorderFactory.createEmptyBorder());
        cardButton.setContentAreaFilled(false); // allows background image to show through
        cardButton.setFocusPainted(false); // not painting a focus rectangle around the button when it gains focus
        cardButton.addActionListener(createCardButtonActionListener(card, defender));
        return cardButton;
    }

    private ActionListener createCardButtonActionListener(Card card, Player defender) {
        return new ActionListener() {
            private boolean isSelected = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                isSelected = !isSelected; // toggle selected state
                Card selectedCard = getCardFromButton(selectedButton);

                if (selectedRank[0] == -1 || selectedCard.getRank() == selectedRank[0]) { // if no rank's selected or same rank's selected
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

    private void initializeSelectButton() {
        if (!selectButtonAdded) {
            selectButton = new JButton("Select Cards");
        }
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
              System.out.println("Selected cards: " + selectedCards); // REMOVE WHEN APP IS READY
                dialog.dispose(); // close the dialog and resume game flow
            }
        });
        dialogPanel.add(selectButton);
        selectButtonAdded = true;
    }

    private Card getCardFromButton(JToggleButton button) {
        return buttonToCardMap.get(button);
    }

    private void associateCardWithButton(Card card, JToggleButton button) {
        buttonToCardMap.put(button, card);
    }
}
