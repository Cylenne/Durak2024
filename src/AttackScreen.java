import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AttackScreen{
    private JFrame frame;
    private JLabel gameMessage;
    private JPanel firstHumanPlayerPanel;

    public void setUpAttackScreen(List<Player> players, Card trump, String displayMessage) {

        frame = new JFrame("Durak - Attack Phase");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 700);
        frame.setVisible(true);
        frame.setLayout(new BorderLayout());

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
        gameMessage = new JLabel("<html>" + displayMessage.replaceAll("\n", "<br/>") + "</html>", SwingConstants.CENTER);
        gameMessage.setFont(new Font("BlackJack", Font.PLAIN, 9));

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

    public void updateAttackScreen(List<Player> players, String displayMessage) {
        gameMessage.setText("<html>" + displayMessage.replaceAll("\n", "<br/>") + "</html>");
    }

}
