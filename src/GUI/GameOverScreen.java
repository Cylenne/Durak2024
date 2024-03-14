package GUI;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

import static Card.Card.resizeImageIcon;

public class GameOverScreen {

    private JFrame frame;
    private JPanel centerPanel;
    private JPanel southPanel;

    public void setupGameOverScreen(String gameMessage) {

        addFrame();
        addGameMessage(gameMessage);
        addButton();
        addArt();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addFrame() {
        frame = new JFrame("Durak - Game Over");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 400);
        frame.setLayout(new BorderLayout());
        centerPanel = new JPanel(new GridBagLayout());
        frame.add(centerPanel);

        ImageIcon frameIcon = new ImageIcon("Images/clubs.png");
        Image iconImage = frameIcon.getImage();
        frame.setIconImage(iconImage);
    }

    private void addGameMessage(String gameMessage) {
        JTextPane gameOverTextPane = new JTextPane(); // string in JTextArea could not be centered
        gameOverTextPane.setEditable(false);
        gameOverTextPane.setOpaque(false);
        gameOverTextPane.setText(gameMessage);

        StyledDocument doc = gameOverTextPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
        messagePanel.add(gameOverTextPane, BorderLayout.CENTER);
        messagePanel.setPreferredSize(new Dimension(500, 200));

        centerPanel.add(messagePanel);
    }

    private void addButton() {
        southPanel = new JPanel(new BorderLayout());
        frame.add(southPanel, BorderLayout.SOUTH);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        southPanel.add(exitButton, BorderLayout.SOUTH);
    }

    private void addArt() {
        ImageIcon art = new ImageIcon("Images/skullAce.png");
        art = resizeImageIcon(art, 100, 100);
        JLabel artLabel = new JLabel(art);

        JPanel artPanel = new JPanel();
        artPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        artPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        artPanel.add(artLabel);

        southPanel.add(artPanel, BorderLayout.CENTER);

    }

//    public static void main(String[] args) {
//        GameOverScreen gameOverScreen = new GameOverScreen();
//        String gameMessage = "Game Over: Player 2 has won the game!\n" +
//                "Final round: 17\n\n" +
//                "1st place: Player 1\n" +
//                "2nd place: Player 2\n" +
//                "3rd place: Player 3\n\n" +
//                "The durak is Player 4";
//        gameOverScreen.setupGameOverScreen(gameMessage);
//    }


}
