import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Gameplay {
    private String gameMessage;
    private AttackScreen attackScreen;
    private AtomicInteger roundCounter = new AtomicInteger();
    private AtomicBoolean isGameOngoing = new AtomicBoolean();


    private void gameFlow() throws InterruptedException {
        StartPhase.execute();

        roundCounter.set(1);
        isGameOngoing.set(true);
        AttackPhase attackPhase = new AttackPhase();

        while (isGameOngoing.get()) {
            attackPhase.execute(roundCounter, isGameOngoing);

            if (attackScreen == null) {
                    attackScreen = new AttackScreen(); // add this to AttackPhase
                    attackScreen.setUpAttackScreen(AttackPhase.getPlayers(), StartPhase.getTrump(), AttackPhase.getGameMessage()); // add this to AttackPhase
                } else {
                    attackScreen.updateAttackScreen(AttackPhase.getPlayers(), AttackPhase.getGameMessage());
                }

            Thread.sleep(2000);

        }
        gameOver(AttackPhase.getPlayers(), AttackPhase.getWinners());

    }


    private void gameOver(List<Player> players, List<Player> winners) {

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

    public static void main(String[] args) throws InterruptedException {
        Gameplay game = new Gameplay();

        game.gameFlow();
    }
}