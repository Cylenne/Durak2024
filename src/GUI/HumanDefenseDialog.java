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
    private JPanel humanCardsPanel;
    private JPanel humanPlayerPanel;
    private JDialog dialog = new JDialog();;
    private JButton selectButton;
    private boolean selectButtonAdded = false;
    private JPanel centerPanel;
    private JLabel message;
    private DialogUtils dialogUtils;

    public HumanDefenseDialog(JPanel humanCardsPanel, JPanel humanPlayerPanel) {
        this.humanCardsPanel = humanCardsPanel;
        this.humanPlayerPanel = humanPlayerPanel;
    }

    public Set<Card> execute(Player defender, List<Card> attackingCards) {
        Set<Card> selectedCards = new HashSet<>();
        initializeHumanCardsPanel(defender, selectedCards, attackingCards);
        initializeSelectButton(attackingCards, selectedCards, centerPanel, message);
        dialogUtils = new DialogUtils(dialog, humanCardsPanel, selectButton);
        dialogUtils.createAndShowDialog();

        return selectedCards;
    }

    private void initializeHumanCardsPanel(Player defender, Set<Card> selectedCards, List<Card> attackingCards) {
        humanCardsPanel.removeAll();

        humanCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        ButtonGroup buttonGroup = new ButtonGroup();

        for (Card card : defender.getHand()) {
            JToggleButton cardButton = createCardButton(card, attackingCards, selectedCards);
            buttonGroup.add(cardButton);
            humanCardsPanel.add(cardButton);
        }

        humanPlayerPanel.revalidate();
        humanPlayerPanel.repaint();
    }

    private JToggleButton createCardButton(Card card, List<Card> attackingCards, Set<Card> selectedCards) {

        JToggleButton cardButton = new JToggleButton(card.toImageIcon());

        cardButton.setPreferredSize(new Dimension(84, 104));
        cardButton.setBorder(BorderFactory.createEmptyBorder());
        cardButton.setContentAreaFilled(false);
        cardButton.setFocusPainted(false);
        cardButton.addActionListener(createCardButtonActionListener(card, attackingCards, selectedCards));
        return cardButton;
    }

    private ActionListener createCardButtonActionListener(Card defendingCard, List<Card> attackingCards, Set<Card> selectedCards) {
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
                            dialogUtils.createConstraints(0, 0, 1, 1, GridBagConstraints.CENTER)); // center the text
                    centerPanel.revalidate();
                    centerPanel.repaint();
                }
//              System.out.println("Selected cards: " + selectedCards); // REMOVE WHEN APP IS READY
                dialog.dispose();
            }
        });
        humanPlayerPanel.add(selectButton);
        selectButtonAdded = true;
    }

}
