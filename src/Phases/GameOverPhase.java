package Phases;

import GUI.GameOverScreen;
import Player.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameOverPhase {

    public static void gameOver(List<Player> players, List<Player> winners, AtomicInteger roundCounter) {

        String leaderboard = "";
        for (int i = 1; i <= winners.size(); i++) {
            String suffix = switch (i) {
                case 1 -> "st";
                case 2 -> "nd";
                case 3 -> "rd";
                default -> "th";
            };
            leaderboard += i + suffix + " place: " + winners.get(i - 1).getName() + "\n";
        }
        String durak = players.getFirst().getName();

        String gameMessage = "Game Over: " + winners.getFirst().getName() + " has won the game!" + "\n"
                + "Final round: " + roundCounter + "\n\n"
                + leaderboard + "\n"
                + "The durak is " + durak;

        System.out.println(gameMessage);

        GameOverScreen gameOverScreen = new GameOverScreen();
        gameOverScreen.setupGameOverScreen(gameMessage);
    }

}
