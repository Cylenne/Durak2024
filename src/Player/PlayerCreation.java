package Player;

import javax.swing.JOptionPane;

public class PlayerCreation {
    public HumanPlayer createHumanPlayer(int i) {
        // creating a brand-new window for prompting for the players' name
        String playerName = JOptionPane.showInputDialog(null,
                "Please add name for Player " + (i + 1) + ":",
                "Enter Player Name", JOptionPane.PLAIN_MESSAGE);

        // if no name is given, call it Player X (x is a number)
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "Player " + (i + 1);
        }

        return new HumanPlayer(playerName);
    }

    public ComputerPlayer createComputerPlayer(int i) {
        String playerName = "Player " + (i + 1);
        return new ComputerPlayer(playerName);
    }
}
