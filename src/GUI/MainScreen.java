package GUI;

import Player.*;
import Card.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainScreen {

    private JFrame frame;

    public void setupStartingScreen(List<Player> players, Card trump, String displayMessage) {

        frame = new JFrame("Durak - Main Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

        JPanel firstHumanPlayerPanel = new JPanel();
        JPanel trumpAndMessagePanel = new JPanel();

        // add an empty border with a 10-pixel margin above
        firstHumanPlayerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        trumpAndMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // +30 adds a border below these panels
        firstHumanPlayerPanel.setPreferredSize(new Dimension(100, 100 + 30));
        trumpAndMessagePanel.setPreferredSize(new Dimension(300, 300 + 30));

        frame.add(firstHumanPlayerPanel, BorderLayout.SOUTH);
        frame.add(trumpAndMessagePanel, BorderLayout.CENTER);

        //-------------inner panels---------------------------
        trumpAndMessagePanel.setLayout(new BorderLayout());

        JLabel trumpIcon = new JLabel(trump.toImageIcon(), SwingConstants.CENTER);
        JLabel trumpText = new JLabel("Trump", SwingConstants.CENTER);
        trumpText.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel gameMessage = new JLabel(displayMessage, SwingConstants.CENTER);
        gameMessage.setFont(new Font("Arial", Font.PLAIN, 7));

        JPanel trumpPanel = new JPanel(new BorderLayout());
        trumpPanel.add(trumpIcon, BorderLayout.CENTER);
        trumpPanel.add(trumpText, BorderLayout.SOUTH);

        trumpAndMessagePanel.add(trumpPanel, BorderLayout.NORTH);
        trumpAndMessagePanel.add(gameMessage, BorderLayout.CENTER);

        //-------------first human player---------------------------

        firstHumanPlayerPanel.setLayout(new FlowLayout());

        List<Card> firstHumanPlayerHand = players.get(0).getHand();

        for (Card card : firstHumanPlayerHand) {
            firstHumanPlayerPanel.add(new JLabel(card.toImageIcon()));
        }

    }

    public void close() {
        frame.dispose();
    }

    public JFrame getFrame() {
        return frame;
    }
}
