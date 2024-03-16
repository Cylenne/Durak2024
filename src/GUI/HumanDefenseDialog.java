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

public class HumanDefenseDialog {
    private JPanel humanCardsPanel = new JPanel();
    private JPanel dialogPanel = new JPanel();
    private JDialog dialog = new JDialog((Frame) null, "Select Defending Cards", true);
    private JButton selectButton;
    private boolean selectButtonAdded = false;
    private Set<Card> selectedCards = new HashSet<>();
    private DialogUtils dialogUtils;

    public Set<Card> execute(Player defender, List<Card> attackingCards) {
        initializeHumanCardsPanel(defender, attackingCards);
        initializeSelectButton(attackingCards);

        dialogUtils = new DialogUtils(dialog, humanCardsPanel, selectButton);
        dialogUtils.createAndShowDialog();

        return selectedCards;
    }

    private void initializeHumanCardsPanel(Player defender, List<Card> attackingCards) {
        dialogPanel.add(humanCardsPanel);
        humanCardsPanel.setLayout(new FlowLayout());

        for (Card card : defender.getHand()) {
            JToggleButton cardButton = createCardButton(card, attackingCards);
            humanCardsPanel.add(cardButton);
        }
    }

    private JToggleButton createCardButton(Card card, List<Card> attackingCards) {
        JToggleButton cardButton = new JToggleButton(card.toImageIcon());
        cardButton.setPreferredSize(new Dimension(84, 104));
        cardButton.setBorder(BorderFactory.createEmptyBorder());
        cardButton.setContentAreaFilled(false);
        cardButton.setFocusPainted(false);
        cardButton.addActionListener(createCardButtonActionListener(card, attackingCards));
        return cardButton;
    }

    private ActionListener createCardButtonActionListener(Card defendingCard, List<Card> attackingCards) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                if (selectedButton.isSelected()) {
                    // Check if the defending card can beat any of the attacking cards
                    boolean canBeatAny = attackingCards.stream().anyMatch(attackingCard -> defendingCard.canBeat(attackingCard));
                    if (canBeatAny) {
                        selectedCards.add(defendingCard);
                        selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true));
                        dialog.setTitle("Select defending cards");
                    } else {
                        // If the defending card cannot beat any attacking card, prevent selection
                        selectedButton.setSelected(false);
                        dialog.setTitle("This card cannot beat any of the attacking cards!");
                    }
                } else {
                    selectedCards.remove(defendingCard);
                    selectedButton.setBorder(BorderFactory.createEmptyBorder());
                }
            }
        };
    }

    private void initializeSelectButton(List<Card> attackingCards) {
        selectButton = new JButton("Select Cards");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isValidSelection = (attackingCards.size() == selectedCards.size());
                if (!isValidSelection) {
                    dialog.setTitle("You need to select exactly as many defending cards as there are attacking cards!");
                } else {
                    dialog.setTitle("Select defending cards");
                    dialog.dispose();
                }
            }
        });
        dialogPanel.add(selectButton);
        selectButtonAdded = true;
    }
}
