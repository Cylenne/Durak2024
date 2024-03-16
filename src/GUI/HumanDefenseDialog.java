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
    private JDialog dialog = new JDialog((Frame) null, "Select Cards", true);
    private JButton selectButton;
    private boolean selectButtonAdded = false;
    private JPanel centerPanel;
    private JLabel message;
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

        ButtonGroup buttonGroup = new ButtonGroup();

        for (Card card : defender.getHand()) {
            JToggleButton cardButton = createCardButton(card, attackingCards);
            buttonGroup.add(cardButton);
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

            private boolean isSelected = false;

            @Override
            public void actionPerformed(ActionEvent e) {
                JToggleButton selectedButton = (JToggleButton) e.getSource();
                isSelected = !isSelected;

                for (Card attackingCard : attackingCards) {
                    if (defendingCard.canBeat(attackingCard)) {
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

    private void initializeSelectButton(List<Card> attackingCards) {
        if (!selectButtonAdded) {
            selectButton = new JButton("Select Cards");
        }
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (attackingCards.size() != selectedCards.size()) {
                    centerPanel.remove(message);
                    centerPanel.add(new JLabel("You need to select as many defending cards as there are attacking cards:"),
                            dialogUtils.createConstraints(0, 0, 1, 1, GridBagConstraints.CENTER)); // center the text
                    centerPanel.revalidate();
                    centerPanel.repaint();
                }
              System.out.println("Selected cards: " + selectedCards); // REMOVE WHEN APP IS READY
                dialog.dispose();
            }
        });
        dialogPanel.add(selectButton);
        selectButtonAdded = true;
    }

}
