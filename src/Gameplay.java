import Phases.*;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Gameplay {
    private final AtomicInteger roundCounter = new AtomicInteger();
    private final AtomicBoolean isGameOngoing = new AtomicBoolean();


    private void gameFlow() {
        StartPhase.getInstance().execute();

        roundCounter.set(1);
        isGameOngoing.set(true);
        AttackPhase attackPhase = new AttackPhase();

        while (isGameOngoing.get()) {
            attackPhase.execute(roundCounter, isGameOngoing);
        }

        GameOverPhase.gameOver(roundCounter);
    }


    public static void main(String[] args) {
        Gameplay game = new Gameplay();

        game.gameFlow();
    }
}