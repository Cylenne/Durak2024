import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// this is one attack phase
public class AttackPhase {
    private static List<Player> players;
    private static Deck deck;
    private static Card.Suit trumpSuit;
    private static Player startingPlayer;
    private static String gameMessage;
    private static List<Player> activePlayersInRound;
    private static Player attacker;
    private static Player defender;
    private static Set<Card> initialAttackingCards;

    public static void execute(int roundCounter) {
        transferAttributes();
        Boolean currentRoundDefended = false;

        sortHandsOfActivePlayers();
        mainAttackersMove(roundCounter, currentRoundDefended);
        determineDefender();
        addAdditionalAttackers();

        roundSetup(roundCounter);


    }

    private static void transferAttributes(){
        players = StartPhase.getPlayers();
        deck = StartPhase.getDeck();
        trumpSuit = StartPhase.getTrumpSuit();
        gameMessage = StartPhase.getGameMessage();
    }

    private static void sortHandsOfActivePlayers(){
        activePlayersInRound = new ArrayList<>();
        PlayerManager.sortEachPlayersHand(players, trumpSuit);
    }

    // main attacker gives out attacking cards
    private static void mainAttackersMove(int roundCounter, Boolean currentRoundDefended) {
        // attacker gives out attacking cards
        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, players, trumpSuit, currentRoundDefended);
        activePlayersInRound.add(attacker);

        Set<Card> initialAttackingCards = new HashSet<>();

        System.out.println("Round: " + roundCounter);
        System.out.println("Current attacker: " + attacker);

        if (attacker instanceof ComputerPlayer) {
            initialAttackingCards = attacker.addAttackingCards(trumpSuit, deck);
        } else {
            // attacker is human -> write method for human
        }
        System.out.println("Initial attacking cards: " + initialAttackingCards);
    }

    private static void determineDefender(){
        defender = PlayerManager.determineDefender(attacker, players);
        System.out.println("Current defender: " + defender);
        activePlayersInRound.add(defender);
    }

    private static void addAdditionalAttackers(){
        for (Player player : players) {
            if (!player.equals(attacker) && !player.equals(defender)) {
                activePlayersInRound.add(1, player);
            }
        }
    }

    private static void roundSetup(int roundCounter){
        Set<Card> allDefendingCards = new HashSet<>();
        Set<Card> allAttackingCards = new HashSet<>(initialAttackingCards);

        int attackLoopCounter = 1;
        boolean roundOn = true;
        List<Card> attackingCardsPerLoop = new ArrayList<>();

        gameMessage = "";
        updateGameMessageWithinRounds("Round: " + roundCounter + "\n"
                + "Number of remaining cards in deck: " + deck.getDeck().size() + "\n"
                + attacker.getName() + " is attacking " + defender.getName());
    }



    private static void updateGameMessageWithinRounds(String newMessage) {
        gameMessage += "\n" + newMessage;
    }


}
