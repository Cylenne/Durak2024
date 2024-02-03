import java.util.List;

public class StartPhase implements GamePhase{

    private List<Player> players;
    private Deck deck;
    private Card trump;
    private Card.Suit trumpSuit;
    private Player startingPlayer;
    private String gameMessage;

    public StartPhase(List<Player> players, Deck deck, Card trump, Card.Suit trumpSuit, Player startingPlayer, String gameMessage) {
        this.players = players;
        this.deck = deck;
        this.trump = trump;
        this.trumpSuit = trumpSuit;
        this.startingPlayer = startingPlayer;
        this.gameMessage = gameMessage;
    }

    @Override
    public void execute() {
//        // use a callback to handle asynchronous onPlayersReady
//        initializeStartingScreen((players) -> {
//            // now you can safely call startPhase or any other methods
//            startGame();
//        });

    }

    // this is a callback method we'll call in execute()/main in order to ensure that only AFTER the user has selected the
    // number and type of players, will the players actually be created and can the game flow continue
    public interface OnPlayersReadyCallback {
        void onPlayersReady(List<Player> allPlayers, Deck standardDeck);
    }

    void onPlayersReady(List<Player> players) {
        this.players = players;
    }

    public void initializeStartingScreen(OnPlayersReadyCallback callback) {
        deck = new Deck();

        // this is a reference to the ActionListener in StartingScreen
        StartingScreen startingScreen = new StartingScreen((selectedPlayers, standardDeck) -> {
            onPlayersReady(selectedPlayers);
            // call the callback when selectedPlayers are ready
            callback.onPlayersReady(selectedPlayers, standardDeck);
        });

        startingScreen.setStandardDeck(deck);
        startingScreen.setupStartingScreen();
    }


    void startGame() {
        // the players list is assumed to be already set with choices from the GUI
        DeckManager.dealCards(players, deck);

        PlayerManager.printAllPlayerDetails(players);

        getTrump(deck);
        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        deck.getDeck().add(trump);

        startingPlayer = PlayerManager.determineStartingPlayer(players, trumpSuit);
        gameMessage = startingPlayer.getName() + " starts the game";

        printCurrentGameState();
    }

    private void getTrump(Deck deck) {
        trump = DeckManager.dealTrump(deck);
        trumpSuit = trump.getSuit();
    }

    private void printCurrentGameState() {
        System.out.println("The trump is: " + trump);
        DeckManager.printDeck(deck);
        System.out.println("The starting player is: " + startingPlayer);
    }
}
