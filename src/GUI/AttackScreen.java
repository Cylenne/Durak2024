package GUI;

import Phases.AttackPhase;
import Player.*;
import Card.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AttackScreen {
    private JFrame frame;
    private JTextArea gameMessage; // multiline text component
    private JPanel firstHumanPlayerPanel;
    private Timer timer;

    public void setUpAttackScreen(List<Player> players, Card trump, String displayMessage) {

        frame = new JFrame("Durak - Attack Phase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        frame.setIconImage(iconImage);

        firstHumanPlayerPanel = new JPanel();
        JPanel trumpAndMessagePanel = new JPanel();
        JPanel attackPanel = new JPanel();

        firstHumanPlayerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        trumpAndMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        firstHumanPlayerPanel.setPreferredSize(new Dimension(100, 100 + 30));
        trumpAndMessagePanel.setPreferredSize(new Dimension(100, 110 + 30));
        attackPanel.setPreferredSize(new Dimension(300, 300 + 30));

        frame.add(firstHumanPlayerPanel, BorderLayout.SOUTH);
        frame.add(trumpAndMessagePanel, BorderLayout.NORTH);
        frame.add(attackPanel, BorderLayout.CENTER);

        //-------------inner panels---------------------------
        trumpAndMessagePanel.setLayout(new BorderLayout());

        JLabel trumpIcon = new JLabel(trump.toImageIcon(), SwingConstants.CENTER);
        JLabel trumpText = new JLabel("Trump", SwingConstants.CENTER);
        trumpText.setFont(new Font("Arial", Font.BOLD, 14));

        // JLabel uses HTML-like syntax for text rendering, so in order to display line breaks,we need HTML formatting
        gameMessage = new JTextArea();
        gameMessage.setEditable(false);
        gameMessage.append(displayMessage);
        gameMessage.setFont(new Font("BlackJack", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(gameMessage);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel trumpPanel = new JPanel(new BorderLayout());
        trumpPanel.add(trumpIcon, BorderLayout.NORTH);
        trumpPanel.add(trumpText, BorderLayout.SOUTH);

        trumpAndMessagePanel.add(trumpPanel, BorderLayout.WEST);
        trumpAndMessagePanel.add(scrollPane, BorderLayout.CENTER);

        //-------------first human player---------------------------

        firstHumanPlayerPanel.setLayout(new FlowLayout());

        List<Card> firstHumanPlayerHand = players.getFirst().getHand();

        for (Card card : firstHumanPlayerHand) {
            firstHumanPlayerPanel.add(new JLabel(card.toImageIcon()));
        }

    }

    public void updateAttackScreen(List<Player> players, List<String> displayMessage, CountDownLatch latch) {

        final int[] step = {0}; // because of the inner class and action listener this needs to be a final one-element array

        gameMessage.setText("");

        // Timer to update the message every 3 seconds
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (step[0] < displayMessage.size()) {
                    gameMessage.append(displayMessage.get(step[0]));
                    step[0]++;
                } else {
                    timer.stop();
                    latch.countDown();
                }
            }
        });
        timer.start();

    }

}
