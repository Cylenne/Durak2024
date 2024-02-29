package GUI;

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
    private JPanel humanPlayerPanel;
    private Timer timer;

    public void setUpAttackScreen(List<Player> players, Card trump, String displayMessage) {

        //-------------frame---------------------------

        frame = new JFrame("Durak - Attack Phase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
        frame.setLayout(new BorderLayout());

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        frame.setIconImage(iconImage);

        //----------------humanPlayerPanel------------------------

        humanPlayerPanel = new JPanel();
        JPanel trumpAndMessagePanel = new JPanel();

        humanPlayerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        trumpAndMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        humanPlayerPanel.setPreferredSize(new Dimension(100, 100 + 30));
        trumpAndMessagePanel.setPreferredSize(new Dimension(100, 110 + 30));

        humanPlayerPanel.setLayout(new FlowLayout());

        List<Card> firstHumanPlayerHand = players.getFirst().getHand();

        for (Card card : firstHumanPlayerHand) {
            humanPlayerPanel.add(new JLabel(card.toImageIcon()));
        }

        //-------------trumpAndMessagePanel---------------------------

        trumpAndMessagePanel.setLayout(new BorderLayout());

        JLabel trumpIcon = new JLabel(trump.toImageIcon(), SwingConstants.CENTER);
        JLabel trumpText = new JLabel("Trump", SwingConstants.CENTER);
        trumpText.setFont(new Font("Arial", Font.BOLD, 14));

        gameMessage = new JTextArea();
        gameMessage.setEditable(false);
        gameMessage.append(displayMessage);
        gameMessage.setFont(new Font("BlackJack", Font.PLAIN, 12));
        gameMessage.setFocusable(false); // removes the cursor
        gameMessage.setBackground(frame.getContentPane().getBackground()); // setting the field's background color to that of the frame's

        JScrollPane scrollPane = new JScrollPane(gameMessage);
        scrollPane.setPreferredSize(new Dimension(500, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel trumpPanel = new JPanel(new BorderLayout());
        trumpPanel.add(trumpIcon, BorderLayout.NORTH);
        trumpPanel.add(trumpText, BorderLayout.SOUTH);

        trumpAndMessagePanel.add(trumpPanel, BorderLayout.WEST);
        trumpAndMessagePanel.add(scrollPane, BorderLayout.CENTER);

        //-------------centralPanel---------------------------

        JPanel centralPanel = new JPanel();
//        centralPanel.setPreferredSize(new Dimension(300, 300 + 30));



        frame.add(humanPlayerPanel, BorderLayout.SOUTH);
        frame.add(trumpAndMessagePanel, BorderLayout.NORTH);
        frame.add(centralPanel, BorderLayout.CENTER);

        frame.setVisible(true); // this needs to be set after the components have been added, otherwise the screen remains blank

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
                    gameMessage.setCaretPosition(gameMessage.getDocument().getLength()); // this jumps to the bottom of the field with each update
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
