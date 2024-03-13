package GUI;

import Card.Card;
import Phases.StartPhase;
import Player.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HumanPlayerGUIManager {
    private JPanel humanCardsPanel;
    private Set<Card> selectedCards;
    private JPanel humanPlayerPanel;
    private boolean selectButtonAdded = false;
    private JButton selectButton;
    private Map<JToggleButton, Card> buttonToCardMap = new HashMap<>();
    private JDialog dialog;

    public HumanPlayerGUIManager(JPanel humanPlayerPanel, JPanel humanCardsPanel) {
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
            final int[] selectedRank = {-1}; // -1 represents no selected rank,
            // and it was transformed into a final one element array so that the inner class can refer to it

            // we only use ButtonGroup to show that these buttons belong together
            ButtonGroup buttonGroup = new ButtonGroup();

            for (Card card : player.getHand()) {
                JToggleButton cardButton = new JToggleButton(card.toImageIcon()); // button that can be in either an "on" or "off" state
                associateCardWithButton(card, cardButton);
                cardButton.setPreferredSize(new Dimension(84, 104)); // added +4 to ensure that the selection frame won't block the image's edges
                cardButton.setBorder(BorderFactory.createEmptyBorder());
                cardButton.setContentAreaFilled(false); // allowing the background color or image of the parent container to show through
                cardButton.setFocusPainted(false); // not painting a focus rectangle around the button when it gains focus
                cardButton.addActionListener(new ActionListener() {
                    private boolean isSelected = false;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JToggleButton selectedButton = (JToggleButton) e.getSource();
                        isSelected = !isSelected; // Toggle the selected state
                        Card selectedCard = getCardFromButton(selectedButton);
                        // If isSelected was true before this line, it becomes false after the line executes.
                        // If isSelected was false before, it becomes true after the line executes.

                        if (selectedRank[0] == -1 || selectedCard.getRank() == selectedRank[0]) {
                            if (isSelected) {
                                selectedCards.add(card);
                                selectedButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3, true)); // add black border with thickness 2 to indicate selection
                                // true ensures that the border is painted exactly at the edge of the component
                                selectedRank[0] = selectedCard.getRank();
                            } else {
                                selectedCards.remove(card);
                                selectedButton.setBorder(BorderFactory.createEmptyBorder());
                                selectedRank[0] = -1;
                            }
                        } else {
                            isSelected = !isSelected; // Revert the toggle
                            selectedButton.setSelected(false); // deselect the button
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
                    System.out.println("Selected cards: " + selectedCards);
                    dialog.dispose();
                }
            });
            humanPlayerPanel.add(selectButton);
            selectButtonAdded = true;

            humanPlayerPanel.revalidate();
            humanPlayerPanel.repaint();

            // modal dialog automatically stops the game flow until user action takes place
            dialog = new JDialog((Frame) null, "Select Cards", true); // true for modal
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel centerPanel = new JPanel(new GridBagLayout());
            centerPanel.add(new JLabel("Your Cards"), createConstraints(0, 0, 1, 1, GridBagConstraints.CENTER)); // center the text
            centerPanel.add(humanCardsPanel, createConstraints(0, 1, 1, 1, GridBagConstraints.CENTER));
            centerPanel.add(selectButton, createConstraints(0, 2, 1, 1, GridBagConstraints.CENTER));
            dialog.getContentPane().add(centerPanel, BorderLayout.CENTER);

            dialog.pack(); // sets the size of the dialog to be just large enough to accommodate all of its components
            dialog.setLocationRelativeTo(null); // center the dialog on the screen
            dialog.setVisible(true);
        }
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

    public Card getCardFromButton(JToggleButton button) {
        return buttonToCardMap.get(button);
    }
    public void associateCardWithButton(Card card, JToggleButton button) {
        buttonToCardMap.put(button, card);
    }


}

