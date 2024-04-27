package Phases;

import Card.*;
import Player.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartPhase {
    // SINGLETON
    private List<Player> players;
    private Deck deck;
    private Card trump;
    private Card.Suit trumpSuit;
    private Player startingPlayer;
    private String gameMessage;
    private ConfigPhase configPhase;
    private static StartPhase startPhaseInstance;

    public StartPhase() {
    }

    public static StartPhase getInstance() {
        if (startPhaseInstance == null) {
            startPhaseInstance = new StartPhase();
        }
        return startPhaseInstance;
    }

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1); // we are waiting for 1 event

        executor.execute(() -> ConfigPhase.ConfigPhaseBuilder.newInstance() // event we are waiting for = ConfigPhase creation
                .setPlayers(configPhase -> {
                    this.configPhase = configPhase;
                    latch.countDown();
                })
                .setDeck()
                .setTrump()
                .setTrumpSuit().build());

        try {
            latch.await(); // this thread is waiting
        } catch (InterruptedException e) { // await can throw exception
            e.printStackTrace();
        }
        // these are all the events for ConfigPhase to be created
        transferAttributes();
        initiateStartPhase();
        executor.shutdown();
    }

    private void transferAttributes() {
        if (configPhase != null) {
            players = configPhase.getPlayers();
            deck = configPhase.getDeck();
            trump = configPhase.getTrump();
            trumpSuit = configPhase.getTrumpSuit();
        } else {
            System.out.println("ConfigPhase is null.");
        }
    }

    private void initiateStartPhase() {
        // the players list is assumed to be already set with choices from the GUI
        DeckManager.dealCards(players, deck);

        PlayerManager.printAllPlayerDetails();

        PlayerManager.sortEachPlayersHand();

        deck.getDeck().add(trump);

        startingPlayer = PlayerManager.determineStartingPlayer();

        printCurrentGameState();
    }

    public void printCurrentGameState() {
        gameMessage = "The trump is: " + trump + "\n" + startingPlayer.getName() + " starts the game";
        System.out.println(gameMessage);
        DeckManager.printDeck(deck);
    }

    public  List<Player> getPlayers() {
        return players;
    }

    public  Deck getDeck() {
        return deck;
    }

    public  Card getTrump() {
        return trump;
    }

    public  Card.Suit getTrumpSuit() {
        return trumpSuit;
    }

    public  String getGameMessage() {
        return gameMessage;
    }

}
