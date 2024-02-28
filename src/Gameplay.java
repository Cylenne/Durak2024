import Phases.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Gameplay {
    private AtomicInteger roundCounter = new AtomicInteger();
    private AtomicBoolean isGameOngoing = new AtomicBoolean();


    private void gameFlow() throws InterruptedException {
        StartPhase.execute();

        roundCounter.set(1);
        isGameOngoing.set(true);
        AttackPhase attackPhase = new AttackPhase();

        while (isGameOngoing.get()) {
            attackPhase.execute(roundCounter, isGameOngoing);
//            Thread.sleep(3000);

        }
        GameOverPhase.gameOver(StartPhase.getPlayers(), AttackPhase.getWinners(), roundCounter);
    }


    public static void main(String[] args) throws InterruptedException {
        Gameplay game = new Gameplay();

        game.gameFlow();
    }
}