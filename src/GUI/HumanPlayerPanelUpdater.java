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

public class HumanPlayerPanelUpdater  {
    private JPanel humanCardsPanel;
    private JPanel humanPlayerPanel;

    public HumanPlayerPanelUpdater (JPanel humanPlayerPanel, JPanel humanCardsPanel) {
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

        humanPlayerPanel.revalidate();
        humanPlayerPanel.repaint();
    }

}

