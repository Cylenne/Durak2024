package GUI;

import Card.Card;
import Phases.StartPhase;
import Player.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

public class HumanPlayerManager {
    private JPanel humanCardsPanel;
    private Set<Card> selectedCards;
    private JPanel humanPlayerPanel;
    private boolean selectButtonAdded = false;
    private JButton selectButton;

    public HumanPlayerManager(JPanel humanPlayerPanel, JPanel humanCardsPanel) {
        this.humanPlayerPanel = humanPlayerPanel;
        this.humanCardsPanel = humanCardsPanel;
    }

    public void updatePanelAfterRedraw() {
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

    public void updateHumanPlayerPanelForInitialAttack(Player player) {
        if (player instanceof HumanPlayer) {
            humanCardsPanel.removeAll();

            selectedCards = new HashSet<>();

            // Create a button group to ensure only one card is selected at a time
            ButtonGroup buttonGroup = new ButtonGroup();

            for (Card card : player.getHand()) {
                JToggleButton cardButton = new JToggleButton(card.toImageIcon());
                cardButton.setPreferredSize(new Dimension(84, 104)); // Adjust the size as needed
                cardButton.setBorder(BorderFactory.createEmptyBorder());
                cardButton.setContentAreaFilled(false);
                cardButton.setFocusPainted(false);
                cardButton.addActionListener(new ActionListener() {
                    private boolean isSelected = false;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JToggleButton selectedButton = (JToggleButton) e.getSource();
                        isSelected = !isSelected; // Toggle the selected state

                        if (isSelected) {
                            selectedCards.add(card);
//                            System.out.println("Button selected: " + selectedButton.getText());
                            selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true)); // Add black border with thickness 2 to indicate selection
                            // true ensures that the border is painted exactly at the edge of the component
                        } else {
                            selectedCards.remove(card);
//                            System.out.println("Button deselected: " + selectedButton.getText());
                            selectedButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border if deselected
                        }
                    }
                });
                buttonGroup.add(cardButton);
                humanCardsPanel.add(cardButton);
            }

            if (!selectButtonAdded) {
                selectButton = new JButton("Select Cards");
            }
            selectButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Close the dialog and resume game flow
                    // Here, you can store the selected cards in selectedCards list
                    System.out.println("Selected cards: " + selectedCards);
                    JDialog dialog = (JDialog) SwingUtilities.getWindowAncestor(humanPlayerPanel);
                    dialog.dispose(); // Close the dialog
                }
            });
            humanPlayerPanel.add(selectButton);
            selectButtonAdded = true; // Update the flag to indicate that the button has been added


            humanPlayerPanel.revalidate();
            humanPlayerPanel.repaint();

            // Create and show the modal dialog
            JDialog dialog = new JDialog((Frame) null, "Select Cards", true); // true for modal
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.getContentPane().add(humanPlayerPanel);
            dialog.pack();
            dialog.setLocationRelativeTo(null); // Center the dialog on the screen
            dialog.setVisible(true);
        }
    }


    private void addCardSelectionListener(Card card, JLabel cardLabel) {
        cardLabel.addMouseListener(new CardSelectionListener(card, selectedCards));
    }

    private class CardSelectionListener extends MouseAdapter {
        private Card card;
        private Set<Card> selectedCards;

        public CardSelectionListener(Card card, Set<Card> selectedCards) {
            this.card = card;
            this.selectedCards = selectedCards;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!selectedCards.contains(card)) {
                // Mark the card as selected
                selectedCards.add(card);
                // Update the visual state of the card to indicate selection
                JLabel cardLabel = (JLabel) e.getSource();
                cardLabel.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
        }
    }


}

