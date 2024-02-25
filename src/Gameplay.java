import GUI.*;
import Phases.*;

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
                    attackScreen = new AttackScreen();
                    attackScreen.setUpAttackScreen(StartPhase.getPlayers(), StartPhase.getTrump(), StartPhase.getGameMessage());
                } else {
                    attackScreen.updateAttackScreen(AttackPhase.getPlayers(), attackPhase.getGameMessage());
                }

            Thread.sleep(3000);

        }
        GameOverPhase.gameOver(AttackPhase.getPlayers(), AttackPhase.getWinners(), gameMessage, roundCounter);
    }


    public static void main(String[] args) throws InterruptedException {
        Gameplay game = new Gameplay();

        game.gameFlow();
    }
}