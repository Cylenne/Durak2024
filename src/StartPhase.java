import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StartPhase {

    private List<Player> players;
    private Deck deck;

    private Card.Suit trumpSuit;
    private Player startingPlayer;
    private String gameMessage;



    public StartPhase(List<Player> players, Deck deck, Card.Suit trumpSuit, Player startingPlayer, String gameMessage) {
        this.players = players;
        this.deck = deck;

        this.trumpSuit = trumpSuit;
        this.startingPlayer = startingPlayer;
        this.gameMessage = gameMessage;
    }

    private static ConfigPhase configPhase;

    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        CountDownLatch latch = new CountDownLatch(1);

        executor.execute(() -> {
            ConfigPhase.ConfigPhaseBuilder.newInstance()
                    .setPlayers(players -> {
                        configPhase = players;
                        latch.countDown();
                    })
                    .setDeck()
                    .setTrump()
                    .setTrumpSuit().build();
        });

        try {
            latch.await(); // Wait for the latch to be released
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        print(configPhase); // Now print the configPhase
        executor.shutdown();
    }

    public static void print(ConfigPhase configPhase) {
        if (configPhase != null) {
            PlayerManager.printAllPlayerDetails(configPhase.getPlayers());
            configPhase.getDeck().printDeck();
            System.out.println(configPhase.getTrump());

        } else {
            System.out.println("ConfigPhase is null.");
        }
    }


    // Method to transfer values to DestinationClass
//    public void transferValues() {
//        Gameplay gameplay = new Gameplay(players, deck, trump, trumpSuit, startingPlayer, gameMessage);
//        // Call methods or perform operations on the DestinationClass instance
//    }

    // this is a callback method we'll call in main in order to ensure that only AFTER the user has selected the
    // number and type of players, will the players actually be created and can the game flow continue
    public interface OnPlayersReadyCallback {
        void onPlayersReady(List<Player> allPlayers);
    }

    void onPlayersReady(List<Player> players) {
        this.players = players;// config comes here
    }

    public void initializeStartingScreen(OnPlayersReadyCallback callback) {
        deck = new Deck();

        StartingScreen startingScreen = new StartingScreen((players) -> {
            onPlayersReady(players);
            // call the callback when players are ready
            callback.onPlayersReady(players);
        });

        startingScreen.setStandardDeck(deck);
        startingScreen.setupStartingScreen();
    }

    public void initiateStartPhase(Card trump) {
        // the players list is assumed to be already set with choices from the GUI
        DeckManager.dealCards(players, deck);

        PlayerManager.printAllPlayerDetails(players);

        Card localTrump = getTrump(deck);
        trump.setRank(localTrump.getRank());
        trump.setSuit(localTrump.getSuit());
        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        deck.getDeck().add(trump);

        startingPlayer = PlayerManager.determineStartingPlayer(players, trumpSuit);
        gameMessage = startingPlayer.getName() + " starts the game";

        printCurrentGameState(trump);
//        transferValues();
    }

    private Card getTrump(Deck deck) {
        Card trump = DeckManager.dealTrump(deck);
        trumpSuit = trump.getSuit();
        return trump;
    }

    private void printCurrentGameState(Card trump) {
        System.out.println("The trump is: " + trump);
        DeckManager.printDeck(deck);
        System.out.println("The starting player is: " + startingPlayer);
    }
}
