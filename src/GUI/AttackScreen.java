package GUI;

import Player.*;
import Card.*;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import static Card.Card.resizeImageIcon;

public class AttackScreen {
    private JFrame frame;
    private JTextArea roundMessage; // multiline text component
    private JTextPane attackPhaseMessage;
    private JPanel humanPlayerPanel;
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JPanel computerPlayersPanel;
    private JPanel attackingCardsPanel;
    private JPanel defendingCardsPanel;
    private JPanel attackingCardsDisplayed;
    private JPanel defendingCardsDisplayed;
    private JLabel attackingCardsText;
    private JLabel defendingCardsText;
    private final int delay = 3000;

    public void setUpAttackScreen(List<Player> players, Card trump) {

        createFrame();
        mainPanel = createMainPanel();
        addHumanPlayerPanel(players);
        addTrumpAndMessagePanel(trump);
        centralPanel = addCentralPanel();
        addComputerPlayersPanel(players);
        addAttackPhaseMessage();
        addAttackingAndDefendingCardsPanel();
        frame.setVisible(true); // this needs to be set after the components have been added, otherwise the screen remains blank

    }

    private void createFrame() {
        frame = new JFrame("Durak - Attack Phase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // full screen
        frame.setLayout(new BorderLayout());

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        frame.setIconImage(iconImage);

    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        frame.add(mainPanel);
        return mainPanel;
    }

    private void addHumanPlayerPanel(List<Player> players) {
        humanPlayerPanel = new JPanel();
        humanPlayerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        humanPlayerPanel.setLayout(new BoxLayout(humanPlayerPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Your cards:");
        humanPlayerPanel.add(label);

        JPanel cardsPanel = new JPanel(new FlowLayout());

        List<Card> humanPlayerHand = players.getFirst().getHand(); // THIS SHOULD BE CHANGED TO INSTANCE OF HUMANPLAYER IN THE FUTURE
        for (Card card : humanPlayerHand) {
            cardsPanel.add(new JLabel(card.toImageIcon()));
        }

        humanPlayerPanel.add(cardsPanel);

        mainPanel.add(humanPlayerPanel, BorderLayout.SOUTH);
    }

    private void addTrumpAndMessagePanel(Card trump) {
        JPanel trumpAndMessagePanel = new JPanel();
        trumpAndMessagePanel.setLayout(new BorderLayout());
        trumpAndMessagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        trumpAndMessagePanel.setPreferredSize(new Dimension(100, 110 + 30));

        JLabel trumpIcon = new JLabel(trump.toImageIcon(), SwingConstants.CENTER);
        JLabel trumpText = new JLabel("Trump", SwingConstants.CENTER);
        trumpText.setFont(new Font("Arial", Font.BOLD, 14));

        roundMessage = new JTextArea();
        roundMessage.setEditable(false);
//        gameMessage.append(""); // no point in adding text here as it will disappear too fast
        roundMessage.setFont(new Font("BlackJack", Font.PLAIN, 12));
        roundMessage.setFocusable(false); // removes the cursor
        roundMessage.setBackground(frame.getContentPane().getBackground()); // setting the field's background color to that of the frame's

        JScrollPane scrollPaneRoundMessage = new JScrollPane(roundMessage);
        scrollPaneRoundMessage.setPreferredSize(new Dimension(300, 200));
        scrollPaneRoundMessage.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 10));

        JPanel trumpPanel = new JPanel(new BorderLayout());
        trumpPanel.add(trumpIcon, BorderLayout.NORTH);
        trumpPanel.add(trumpText, BorderLayout.SOUTH);


        trumpAndMessagePanel.add(trumpPanel, BorderLayout.WEST);
        trumpAndMessagePanel.add(scrollPaneRoundMessage, BorderLayout.CENTER);

        mainPanel.add(trumpAndMessagePanel, BorderLayout.NORTH);
    }

    private JPanel addCentralPanel() {
        JPanel centralPanel = new JPanel(new BorderLayout());
//        centralPanel.setPreferredSize(new Dimension(300, 300 + 30));
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        return centralPanel;
    }

    public void addComputerPlayersPanel(List<Player> players) {
        computerPlayersPanel = new JPanel();
        computerPlayersPanel.setPreferredSize(new Dimension(80, 150));
        computerPlayersPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centralPanel.add(computerPlayersPanel, BorderLayout.WEST);

        // creating as many player icons as there are players in game
        for (Player player : players) {

            if (player instanceof ComputerPlayer) {

                JPanel playerIconPanel = new JPanel();
                ImageIcon playerIcon = new ImageIcon("Images/3cardsFromBack.png");
                playerIcon = resizeImageIcon(playerIcon, 100, 90);
                JLabel playerIconLabel = new JLabel(playerIcon);
                playerIconPanel.add(playerIconLabel);

                JPanel playerNamePanel = new JPanel();
                JLabel playerNameText = new JLabel(player.getName() + " (" + player.getHand().size() + ")", SwingConstants.CENTER);
                playerNameText.setFont(new Font("Arial", Font.BOLD, 12));
                playerNamePanel.add(playerNameText);

                computerPlayersPanel.add(playerIconPanel);
                computerPlayersPanel.add(playerNamePanel);
            }
        }
    }

    public void addAttackPhaseMessage() {
        JPanel attackPhaseMessagePanel = new JPanel();

        attackPhaseMessage = new JTextPane(); // Change to JTextPane
        attackPhaseMessage.setEditable(false);
        attackPhaseMessage.setFont(new Font("BlackJack", Font.PLAIN, 12));
        attackPhaseMessage.setFocusable(false);
        attackPhaseMessage.setBackground(frame.getContentPane().getBackground());

        JScrollPane scrollPaneAttackPhaseMessage = new JScrollPane(attackPhaseMessage);
        scrollPaneAttackPhaseMessage.setPreferredSize(new Dimension(500, 100));
        scrollPaneAttackPhaseMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        attackPhaseMessagePanel.setLayout(new BorderLayout());
        attackPhaseMessagePanel.add(scrollPaneAttackPhaseMessage, BorderLayout.CENTER);

        StyledDocument doc = attackPhaseMessage.getStyledDocument(); // this whole block is added to center the text within attackPhaseMessage
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false); // Center align text

        centralPanel.add(attackPhaseMessagePanel, BorderLayout.NORTH);
    }


    public void addAttackingAndDefendingCardsPanel() {
        JPanel attackingAndDefendingCardsPanel = new JPanel(new BorderLayout());
        centralPanel.add(attackingAndDefendingCardsPanel, BorderLayout.CENTER);

        attackingCardsPanel = new JPanel(new BorderLayout());
//        attackingCardsPanel.setBackground(Color.GREEN);
        attackingCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 70, 10, 10));
        attackingCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        attackingAndDefendingCardsPanel.add(attackingCardsPanel, BorderLayout.NORTH);

        attackingCardsText = new JLabel("Attacking cards:");
        attackingCardsPanel.add(attackingCardsText);
        attackingCardsText.setVisible(false);

        attackingCardsDisplayed = new JPanel(new FlowLayout());
        attackingCardsPanel.add(attackingCardsDisplayed);


        defendingCardsPanel = new JPanel(new BorderLayout());
//        defendingCardsPanel.setBackground(Color.YELLOW);
        defendingCardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 70, 10, 10));
        defendingCardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        attackingAndDefendingCardsPanel.add(defendingCardsPanel, BorderLayout.CENTER);

        defendingCardsText = new JLabel("Defending cards:");
        defendingCardsPanel.add(defendingCardsText);
        defendingCardsText.setVisible(false);

        defendingCardsDisplayed = new JPanel(new FlowLayout());
        defendingCardsPanel.add(defendingCardsDisplayed);


    }


    public void updateRoundMessage(String message) {
        CountDownLatch latch = new CountDownLatch(1);
        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    roundMessage.setText(message);
                    roundMessage.setCaretPosition(roundMessage.getDocument().getLength());
                    roundMessage.repaint();
                    latch.countDown(); // callback to countdown latch
                });
            }
        });
        timer.setRepeats(false); // execute the action only once
        timer.start();

        try {
            latch.await(); // wait for the latch to countdown
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void updateComputerPlayersPanel(List<Player> players) {
        computerPlayersPanel.removeAll(); // remove all existing computer player panels
        addComputerPlayersPanel(players);

        computerPlayersPanel.revalidate(); // recalculate the layout (when you add/remove components dynamically)
        computerPlayersPanel.repaint(); // visually renders updated changes
    }

    public void updateHumanPlayerPanel(List<Player> players) {
        humanPlayerPanel.removeAll();
        addHumanPlayerPanel(players);
        computerPlayersPanel.revalidate();
        computerPlayersPanel.repaint();
    }

    public void updateAttackPhaseMessage(String message) {
        CountDownLatch latch = new CountDownLatch(1);
        Timer timer = new Timer(delay, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    attackPhaseMessage.setText(message);
                    attackPhaseMessage.setCaretPosition(attackPhaseMessage.getDocument().getLength());
                    attackPhaseMessage.repaint();
                    latch.countDown(); // callback to countdown latch
                });
            }
        });
        timer.setRepeats(false); // execute the action only once
        timer.start();

        try {
            latch.await(); // wait for the latch to countdown
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void updateInitialAttackingCardsPanel(Set<Card> attackingCards) {
        attackingCardsText.setVisible(true);

        for (Card card : attackingCards) {
            attackingCardsDisplayed.add(new JLabel(card.toImageIcon()));
        }

        attackingCardsPanel.revalidate();
        attackingCardsPanel.repaint();
    }

    public void updateAttackingCardsPanel(Set<Card> attackingCards) {

        for (Card card : attackingCards) {
            attackingCardsDisplayed.add(new JLabel(card.toImageIcon()));
        }

        attackingCardsPanel.revalidate();
        attackingCardsPanel.repaint();
    }

    public void updateDefendingCardsPanel(Card defendingCard) {
        defendingCardsText.setVisible(true);
        defendingCardsDisplayed.add(new JLabel(defendingCard.toImageIcon()));
        defendingCardsPanel.revalidate();
        defendingCardsPanel.repaint();
    }

    public void clearAttackingAndDefendingCardsPanel() {
        attackingCardsText.setVisible(false);
        defendingCardsText.setVisible(false);
        attackingCardsDisplayed.removeAll();
        defendingCardsDisplayed.removeAll();
    }

    public void closeAttackScreen() {
        frame.dispose();
    }

}
