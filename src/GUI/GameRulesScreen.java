package GUI;

import javax.swing.*;

public class GameRulesScreen extends JDialog {

    public GameRulesScreen(JFrame parentFrame) {
        super(parentFrame, "Game Rules", true); // true for modal dialog
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(400, 350);
        setLocationRelativeTo(parentFrame); // Center dialog relative to parent frame

        JTextArea rulesTextArea = new JTextArea();
        rulesTextArea.setEditable(false);
        rulesTextArea.setLineWrap(true);
        rulesTextArea.setWrapStyleWord(true);

        // Add game rules text to the text area
        String gameRulesText = "Game Rules:\n\n" +
                "1. Durak is a Russian card game played with a deck of 36 cards.\n" +
                "2. The game can have 2 to 4 players.\n" +
                "3. The goal of the game is to get rid of all your cards.\n" +
                "4. The player with the lowest trump card starts the game.\n" +
                "5. Players take turns attacking each other by playing cards of the same rank.\n" +
                "6. Additional attackers may join the attack if they have the same rank of cards as the initial attacker's.\n" +
                "7. The defender must beat all attacking cards with higher cards of the same suit or any trump card.\n" +
                "8. If the defender cannot beat the attacking cards, they must pick up all the cards on the table.\n" +
                "9. The game continues until all cards are played or only one player has cards left.\n\n" +
                "Enjoy playing Durak!";
        rulesTextArea.setText(gameRulesText);

        // Add an empty border to create padding
        rulesTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(rulesTextArea);

        getContentPane().add(scrollPane);
    }

    public static void showGameRules(JFrame parentFrame) {
        GameRulesScreen dialog = new GameRulesScreen(parentFrame);
        dialog.setVisible(true);
    }
}

