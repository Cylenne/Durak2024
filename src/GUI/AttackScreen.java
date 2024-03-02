package GUI;

import Phases.StartPhase;
import Player.*;
import Card.*;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static Card.Card.resizeImageIcon;

public class AttackScreen {
    private JFrame frame;
    private JTextArea gameMessage; // multiline text component
    private JPanel humanPlayerPanel;
    private Timer timer;
    private JPanel mainPanel;
    private JPanel centralPanel;
    private JPanel computerPlayersPanel;
    private JPanel attackingAndDefendingCardsPanel;
    private JPanel attackingCardsPanel;
    private JPanel defendingCardsPanel;

    public void setUpAttackScreen(List<Player> players, Card trump, String displayMessage) {

        createFrame();
        mainPanel = createMainPanel();
        addHumanPlayerPanel(players);
        addTrumpAndMessagePanel(trump);
        centralPanel = addCentralPanel();
        addComputerPlayersPanel(players);




        //---------------------addAttackingCardsPanel------------------------------
        attackingAndDefendingCardsPanel = new JPanel(new BorderLayout());
        centralPanel.add(attackingAndDefendingCardsPanel, BorderLayout.CENTER);


        attackingCardsPanel = new JPanel(new BorderLayout());
        attackingCardsPanel.setBackground(Color.GREEN);
        attackingAndDefendingCardsPanel.add(attackingCardsPanel, BorderLayout.NORTH);

        JLabel attackingCardsText =  new JLabel("Attacking cards:");
        attackingCardsPanel.add(attackingCardsText, BorderLayout.NORTH);

        JPanel attackingCardsDisplayed = new JPanel(new FlowLayout());
        attackingCardsPanel.add(attackingCardsDisplayed);






        //---------------------addDefendingCardsPanel------------------------------
        defendingCardsPanel = new JPanel(new BorderLayout());
        defendingCardsPanel.setBackground(Color.YELLOW);
        attackingAndDefendingCardsPanel.add(defendingCardsPanel, BorderLayout.CENTER);

        JLabel defendingCardsText =  new JLabel("Defending cards:");
        defendingCardsPanel.add(defendingCardsText, BorderLayout.NORTH);

        JPanel defendingCardsDisplayed = new JPanel(new FlowLayout());
        attackingCardsPanel.add(defendingCardsDisplayed);







        //---------------------------------------------------

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
        humanPlayerPanel.setPreferredSize(new Dimension(100, 100 + 30));
        humanPlayerPanel.setLayout(new FlowLayout());

        List<Card> humanPlayerHand = players.getFirst().getHand(); // THIS SHOULD BE CHANGED TO INSTANCE OF HUMANPLAYER IN THE FUTURE
        for (Card card : humanPlayerHand) {
            humanPlayerPanel.add(new JLabel(card.toImageIcon()));
        }

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

        gameMessage = new JTextArea();
        gameMessage.setEditable(false);
//        gameMessage.append(""); // no point in adding text here as it will disappear too fast
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

        mainPanel.add(trumpAndMessagePanel, BorderLayout.NORTH);
    }

    private JPanel addCentralPanel() {
        JPanel centralPanel = new JPanel(new BorderLayout());
//        centralPanel.setPreferredSize(new Dimension(300, 300 + 30));
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        return centralPanel;
    }

    public void addComputerPlayersPanel(List<Player> players){
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
                JLabel playerNameText = new JLabel(player.getName(), SwingConstants.CENTER);
                playerNameText.setFont(new Font("Arial", Font.BOLD, 12));
                playerNamePanel.add(playerNameText);

                computerPlayersPanel.add(playerIconPanel);
                computerPlayersPanel.add(playerNamePanel);
            }
        }
    }


    public void updateAttackScreenMessage(String message) {
        CountDownLatch latch = new CountDownLatch(1);
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    gameMessage.setText(message);
                    gameMessage.setCaretPosition(gameMessage.getDocument().getLength());
                    gameMessage.repaint();
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

}
