import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class GameOverPhase {

    public static void gameOver(List<Player> players, List<Player> winners, String gameMessage, AtomicInteger roundCounter) {

        System.out.println("Game Over: " + winners.getFirst().getName() + " has won the game!");
        String leaderboard = "";
        for (int i = 1; i <= winners.size(); i++) {
            String suffix = switch (i) {
                case 1 -> "st";
                case 2 -> "nd";
                default -> "rd";
            };
            leaderboard = i + suffix + " place: " + winners.get(i - 1).getName();
            System.out.println(leaderboard);
        }
        String durak = players.getFirst().getName();
        System.out.println("The durak is " + durak);

        gameMessage = "Round: " + roundCounter + "\n"
                + "Game Over: " + winners.getFirst().getName() + " has won the game!" + "\n"
                + leaderboard + "\n"
                + "The durak is " + durak;

    }

}
