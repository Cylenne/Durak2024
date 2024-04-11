package GUI;

import Card.Card;
import Player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HumanAdditionalAttackDialog {

    private JPanel humanCardsPanel = new JPanel();
    private JPanel dialogPanel = new JPanel();
    private JDialog dialog = new JDialog((Frame) null, "Select additional attacking cards", true);
    private JButton selectButton;
    private boolean selectButtonAdded = false;
    private Set<Card> selectedCards = new HashSet<>();
    private DialogUtils dialogUtils;

    public Set<Card> execute(Player additionalAttacker, Set<Card> attackingCards, int defendersStartingHandSize, Set<Card> allAttackingCards) {

            initializeHumanCardsPanel(additionalAttacker, attackingCards);
            initializeSelectButton(attackingCards, defendersStartingHandSize, allAttackingCards);

            dialogUtils = new DialogUtils(dialog, humanCardsPanel, selectButton);
            dialogUtils.createAndShowDialog();

            return selectedCards;

    }

    private void initializeHumanCardsPanel(Player additionalAttacker, Set<Card> attackingCards) {
        dialogPanel.add(humanCardsPanel);
        humanCardsPanel.setLayout(new FlowLayout());

        for (Card card : additionalAttacker.getHand()) {
            JToggleButton cardButton = createCardButton(card, attackingCards);
            humanCardsPanel.add(cardButton);
        }
    }

    private JToggleButton createCardButton(Card card, Set<Card> attackingCards) { // same code as in HumanDefenseDialog -> reuse that one,or put it in util?
        JToggleButton cardButton = new JToggleButton(card.toImageIcon());
        cardButton.setPreferredSize(new Dimension(84, 104));
        cardButton.setBorder(BorderFactory.createEmptyBorder());
        cardButton.setContentAreaFilled(false);
        cardButton.setFocusPainted(false);
        cardButton.addActionListener(createCardButtonActionListener(card, attackingCards));
        return cardButton;
    }

    private ActionListener createCardButtonActionListener(Card additionalAttackingCard, Set<Card> attackingCards) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                if (selectedButton.isSelected()) {
                    Set<Integer> attackingCardRanks = attackingCards.stream().map(Card::getRank).collect(Collectors.toSet());
                    boolean sameRank = attackingCardRanks.contains(additionalAttackingCard.getRank());
                    if (sameRank) {
                        selectedCards.add(additionalAttackingCard);
                        selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
                        dialog.setTitle("Select additional attacking cards");
                    } else {
                        selectedButton.setSelected(false);
                        dialog.setTitle("You can only choose cards with the same rank!");
                    }
                } else {
                    selectedCards.remove(additionalAttackingCard);
                    selectedButton.setBorder(BorderFactory.createEmptyBorder());
                }
            }
        };
    }

    private void initializeSelectButton(Set<Card> attackingCards, int defendersStartingHandSize, Set<Card> allAttackingCards) {
        selectButton = new JButton("Select Cards");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isValidSelection;
                isValidSelection =  ((allAttackingCards.size() + selectedCards.size()) <= defendersStartingHandSize);
//                if (subAttackCounter.get() == 1) { // if initial attack, initial attacking cards have to be added
//                    allAttackingCards.size() += selectedCards.size();
//                    isValidSelection = (attackingCards.size() + selectedCards.size() <= defendersStartingHandSize);
//                } else { // all subsequent additional attacks
//                    isValidSelection = selectedCards.size() <= defendersStartingHandSize;
//                }

                if (!isValidSelection) {
                    dialog.setTitle("The total number of attacking cards can't exceed the number of cards in the defender's hand! Please choose less cards.");
                } else {
                    allAttackingCards.addAll(selectedCards);
                    dialog.setTitle("Select additional attacking cards");
                    dialog.dispose();
                }
            }
        });
        dialogPanel.add(selectButton);
        selectButtonAdded = true;
    }


}
