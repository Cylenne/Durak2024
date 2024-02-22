package Phases;

import Card.*;
import Player.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartPhase {

    // SHOULD THIS BE A SINGLETON??

    private static List<Player> players;
    private static Deck deck;
    private static Card trump;
    private static Card.Suit trumpSuit;
    private static Player startingPlayer;
    private static String gameMessage;
    private static ConfigPhase configPhase;

    public static void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1); // we are waiting for 1 event

        executor.execute(() -> ConfigPhase.ConfigPhaseBuilder.newInstance() // event we are waiting for = ConfigPhase creation
                .setPlayers(players -> {
                    configPhase = players;
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

    private static void transferAttributes(){
        if (configPhase != null) {
            players = configPhase.getPlayers();
            deck = configPhase.getDeck();
            trump = configPhase.getTrump();
            trumpSuit = configPhase.getTrumpSuit();
        } else {
            System.out.println("ConfigPhase is null.");
        }
    }

    private static void initiateStartPhase() {
        // the players list is assumed to be already set with choices from the GUI
        DeckManager.dealCards(players, deck);

        PlayerManager.printAllPlayerDetails(players);

        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        deck.getDeck().add(trump);

        startingPlayer = PlayerManager.determineStartingPlayer(players, trumpSuit);
        gameMessage = startingPlayer.getName() + " starts the game";

        printCurrentGameState();
    }

    public static void printCurrentGameState() {
        System.out.println("The trump is: " + trump);
        DeckManager.printDeck(deck);
        System.out.println("The starting player is: " + startingPlayer);
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static Deck getDeck() {
        return deck;
    }

    public static Card getTrump() {
        return trump;
    }

    public static Card.Suit getTrumpSuit() {
        return trumpSuit;
    }

    public static Player getStartingPlayer() {
        return startingPlayer;
    }

    public static String getGameMessage() {
        return gameMessage;
    }

    public static ConfigPhase getConfigPhase() {
        return configPhase;
    }
}
