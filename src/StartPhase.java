import java.util.List;

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

    public static void main(String[] args) {
        ConfigPhase configPhase = ConfigPhase.ConfigPhaseBuilder.newInstance()
                .setPlayers()
                .setDeck()
                .setTrump()
                .setTrumpSuit().build();

        // shit is again asynchronous, this needs to be fixed before i call any other method
//        System.out.println(configPhase.getPlayers());
//        System.out.println(configPhase.getDeck());
//        System.out.println(configPhase.getTrump());

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
