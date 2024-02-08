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

    public void execute() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        executor.execute(() -> ConfigPhase.ConfigPhaseBuilder.newInstance()
                .setPlayers(players -> {
                    configPhase = players;
                    latch.countDown();
                })
                .setDeck()
                .setTrump()
                .setTrumpSuit().build());

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //
        transferAttributes();
        initiateStartPhase();
        executor.shutdown();
    }

    public static void transferAttributes(){
        if (configPhase != null) {
            players = configPhase.getPlayers();
            deck = configPhase.getDeck();
            trump = configPhase.getTrump();
            trumpSuit = configPhase.getTrumpSuit();
        } else {
            System.out.println("ConfigPhase is null.");
        }
    }

    public static void initiateStartPhase() {
        // the players list is assumed to be already set with choices from the GUI
        DeckManager.dealCards(players, deck);

        PlayerManager.printAllPlayerDetails(players);

        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        deck.getDeck().add(trump);

        startingPlayer = PlayerManager.determineStartingPlayer(players, trumpSuit);
        gameMessage = startingPlayer.getName() + " starts the game";

        printCurrentGameState(trump);
    }

    private static void printCurrentGameState(Card trump) {
        System.out.println("The trump is: " + trump);
        DeckManager.printDeck(deck);
        System.out.println("The starting player is: " + startingPlayer);
    }
}
