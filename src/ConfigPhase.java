import java.util.List;
import java.util.function.Consumer;

public class ConfigPhase {
    private final List<Player> players;
    private final Deck deck;

    private final Card trump;
    private final Card.Suit trumpSuit;

    public ConfigPhase(ConfigPhaseBuilder configPhaseBuilder) {
        this.players = configPhaseBuilder.players;
        this.deck = configPhaseBuilder.deck;
        this.trump = configPhaseBuilder.trump;
        this.trumpSuit = configPhaseBuilder.trumpSuit;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Deck getDeck() {
        return deck;
    }

    public Card getTrump() {
        return trump;
    }

    public Card.Suit getTrumpSuit() {
        return trumpSuit;
    }

    //Builder Class
    public static class ConfigPhaseBuilder{
        private List<Player> players;
        private Deck deck;

        private Card trump;
        private Card.Suit trumpSuit;

        public static ConfigPhaseBuilder newInstance()
        {
            return new ConfigPhaseBuilder();
        }

        private ConfigPhaseBuilder() {}


        public ConfigPhaseBuilder(List<Player> players, Deck deck, Card trump, Card.Suit trumpSuit) {
            this.players = players;
            this.deck = deck;
            this.trump = trump;
            this.trumpSuit = trumpSuit;
        }

        public ConfigPhaseBuilder setPlayers(Consumer<ConfigPhase> callback) {
            initializeStartingScreen(players -> {
                onPlayersReady(players);
                callback.accept(new ConfigPhase(this));
            });
            return this;
        }

        public ConfigPhaseBuilder setDeck() {
            this.deck = new Deck();
            return this;
        }

        public ConfigPhaseBuilder setTrump() {
            this.trump = new Card(8, Card.Suit.SPADE);;
            return this;
        }

        public ConfigPhaseBuilder setTrumpSuit() {
            this.trumpSuit = trumpSuit;
            return this;
        }

        public ConfigPhase build(){
            return new ConfigPhase(this);
        }

        public interface OnPlayersReadyCallback {
            void onPlayersReady(List<Player> allPlayers);
        }

        void onPlayersReady(List<Player> players) {
            this.players = players;// config comes here
        }

        public void initializeStartingScreen(StartPhase.OnPlayersReadyCallback callback) {
            setDeck();

            StartingScreen startingScreen = new StartingScreen((players) -> {
                onPlayersReady(players);
                // call the callback when players are ready
                callback.onPlayersReady(players);
            });

            startingScreen.setStandardDeck(deck);
            startingScreen.setupStartingScreen();
        }
    }
}

