//import java.util.List;
//
//public class StartPhase implements GamePhase{
//
//    private List<Player> players;
//    private Deck deck;
//    private Card trump;
//    @Override
//    public void execute() {
//
//    }
//
//    private void initializeStartingScreen(Gameplay gameplay) {
//        // create deck
//        deck = new Deck();
//
//        // initialize StartingScreen
//        StartingScreen startingScreen = new StartingScreen(gameplay::onPlayersReady);
//        startingScreen.setStandardDeck(deck);
//        startingScreen.setupStartingScreen();
//    }
//
//    private void startPhase() {
//        // The players list is assumed to be already set with choices from the GUI
//        DeckManager.dealCards(players, deck);
//
//        PlayerManager.printAllPlayerDetails(players);
//
//        getTrump(deck);
//        PlayerManager.sortEachPlayersHand(players, trumpSuit);
//
//        deck.getDeck().add(trump);
//
//        startingPlayer = PlayerManager.determineStartingPlayer(players, trumpSuit);
//        gameMessage = startingPlayer.getName() + " starts the game";
//
//        printCurrentGameState();
//    }
//
//    private void getTrump(Deck deck) {
//        trump = DeckManager.dealTrump(deck);
//        trumpSuit = trump.getSuit();
//    }
//}
