import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

// this is one attack phase called multiple times from Gameplay
public class AttackPhase {

    // static variables are the same over all instances
    private static List<Player> players;
    private static Deck deck;
    private static Card.Suit trumpSuit;
    private static String gameMessage;
    private static Boolean currentRoundDefended;
    private static List<Player> winners = new ArrayList<>();

    public static List<Player> getWinners() {
        return winners;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static String getGameMessage() {
        return gameMessage;
    }

    // non-static variables = instance variables can vary in each instance
    private Set<Card> initialAttackingCards = new HashSet<>();

    public void execute(AtomicInteger roundCounter, AtomicBoolean isGameOngoing) {

        transferAttributes();
        Boolean currentRoundDefended = false;

        List<Player> activePlayersInRound = new ArrayList<>();

        Player attacker = null;
        Player defender = null;


        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, players, trumpSuit, currentRoundDefended);
        defender = PlayerManager.determineDefender(attacker, players);
        sortHandsOfActivePlayers();

        mainAttackersMove(attacker, defender, activePlayersInRound, roundCounter, currentRoundDefended);
        determineDefender(attacker, activePlayersInRound);
        addAdditionalAttackers(attacker, defender, activePlayersInRound);

        round(roundCounter, attacker, defender, initialAttackingCards, activePlayersInRound, isGameOngoing);

    }

    private static void transferAttributes() {
        players = StartPhase.getPlayers();
        deck = StartPhase.getDeck();
        trumpSuit = StartPhase.getTrumpSuit();
        gameMessage = StartPhase.getGameMessage();
    }

    private static void sortHandsOfActivePlayers() {
        PlayerManager.sortEachPlayersHand(players, trumpSuit);
    }

    // main attacker gives out attacking cards
    private void mainAttackersMove(Player attacker, Player defender, List<Player> activePlayersInRound, AtomicInteger roundCounter, Boolean currentRoundDefended) {
        // attacker gives out attacking cards
        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, players, trumpSuit, currentRoundDefended);
        activePlayersInRound.add(attacker);

        System.out.println("Round: " + roundCounter);
        System.out.println("Current attacker: " + attacker);

        if (attacker instanceof ComputerPlayer) {
            initialAttackingCards = attacker.addAttackingCards(trumpSuit, deck);
        } else {
            // attacker is human -> write method for human
        }
        System.out.println("Initial attacking cards: " + initialAttackingCards);
    }

    private void determineDefender(Player attacker, List<Player> activePlayersInRound) {
        Player defender = PlayerManager.determineDefender(attacker, players);
        System.out.println("Current defender: " + defender);
        activePlayersInRound.add(defender);
    }

    private void addAdditionalAttackers(Player attacker, Player defender, List<Player> activePlayersInRound) {
        for (Player player : players) {
            if (!player.equals(attacker) && !player.equals(defender)) {
                activePlayersInRound.add(1, player);
            }
        }
    }

    private void round(AtomicInteger roundCounter, Player attacker, Player defender, Set<Card> initialAttackingCards, List<Player> activePlayersInRound, AtomicBoolean isGameOngoing) {

        Set<Card> allDefendingCards = new HashSet<>();
        Set<Card> allAttackingCards = new HashSet<>(initialAttackingCards);

        AtomicInteger attackLoopCounter = new AtomicInteger();
        attackLoopCounter.set(1);
        AtomicBoolean roundOn = new AtomicBoolean();
        roundOn.set(true);
        List<Card> attackingCardsPerLoop = new ArrayList<>();

        gameMessage = "";
        updateGameMessageWithinRounds("Round: " + roundCounter + "\n"
                + "Number of remaining cards in deck: " + deck.getDeck().size() + "\n"
                + attacker.getName() + " is attacking " + defender.getName());

        while (roundOn.get()) {

            attackingCardsPerLoop(attacker, defender, initialAttackingCards, attackLoopCounter, attackingCardsPerLoop);

            Set<Card> defendingCardsPerLoop = new HashSet<>();

            defendingCardsPerLoop(defender, attackingCardsPerLoop, defendingCardsPerLoop, allDefendingCards, roundOn);

            checkForAdditionalAttack(allAttackingCards, attackingCardsPerLoop, attackLoopCounter, defender, defendingCardsPerLoop, attacker);

            if (attackingCardsPerLoop.isEmpty()) {
                roundOn.set(false);
                updateGameMessageWithinRounds("No additional attacking cards are available");
            }

            allAttackingCards.addAll(attackingCardsPerLoop);
        }

        roundEndMessage(defender, allDefendingCards, allAttackingCards);

        roundCounter.incrementAndGet();
        DeckManager.drawMissingCards(activePlayersInRound, deck, players);
        roundEndCheck(isGameOngoing);
    }

    private void attackingCardsPerLoop(Player attacker, Player defender, Set<Card> initialAttackingCards, AtomicInteger attackLoopCounter, List<Card> attackingCardsPerLoop) {

        if (attackLoopCounter.get() == 1) {
            attackingCardsPerLoop.addAll(initialAttackingCards);
            for (Player player : players) {
                if (player instanceof ComputerPlayer) {
                    if (!player.equals(defender)) { // additional cards can only be added as long as defender has enough cards
                        attackingCardsPerLoop.addAll(player.addAdditionalAttackingCards(initialAttackingCards, deck,
                                trumpSuit, PlayerManager.isDefenderRightBeforeAdditionalAttacker(players, defender, attacker), defender, attackingCardsPerLoop));
                    }
                } else {
                    // add human player code
                }
            }
        }

        System.out.println("Attacking cards per loop: " + attackingCardsPerLoop);
        updateGameMessageWithinRounds("Attacking cards: " + attackingCardsPerLoop);
    }

    private void defendingCardsPerLoop(Player defender, List<Card> attackingCardsPerLoop, Set<Card> defendingCardsPerLoop, Set<Card> allDefendingCards, AtomicBoolean roundOn) {
        if (defender instanceof ComputerPlayer) {
            RoundResult defenseResult = defender.defenseState(attackingCardsPerLoop, trumpSuit, deck);
            defendingCardsPerLoop = defenseResult.getDefendingCards();
            allDefendingCards.addAll(defendingCardsPerLoop);
            currentRoundDefended = defenseResult.isRoundDefended();
            System.out.println("CURRENT LOOP ROUND DEFENDED: " + currentRoundDefended);
            if (!currentRoundDefended) {
                roundOn.set(false);
            }
        } else {
            // human defender -> write this code
        }

        updateGameMessageWithinRounds("Defending cards: " + defendingCardsPerLoop);
        updateGameMessageWithinRounds(currentRoundDefended ? "Successful defense so far" : "Unsuccessful defense");
    }

    public void checkForAdditionalAttack(Set<Card> allAttackingCards, List<Card> attackingCardsPerLoop, AtomicInteger attackLoopCounter, Player defender, Set<Card> defendingCardsPerLoop, Player attacker) {
        allAttackingCards.addAll(attackingCardsPerLoop);
        attackingCardsPerLoop.clear();
        attackLoopCounter.incrementAndGet();

        for (Player player : players) {
            if (player instanceof ComputerPlayer) {
                if (!player.equals(defender)) {
                    attackingCardsPerLoop.addAll(player.addAdditionalAttackingCards(defendingCardsPerLoop, deck,
                            trumpSuit, PlayerManager.isDefenderRightBeforeAdditionalAttacker(players, defender, attacker), defender, attackingCardsPerLoop));
                }
            } else {
                // add human player code
            }
        }
    }

    public void roundEndMessage(Player defender, Set<Card> allDefendingCards, Set<Card> allAttackingCards) {
        if (currentRoundDefended) {
            defender.getHand().removeAll(allDefendingCards);
            updateGameMessageWithinRounds(defender.getName() + " has successfully countered the attack" + "\n");
            currentRoundDefended = true;
        } else {
            updateGameMessageWithinRounds(defender.getName() + " has not been able to counter the attack" + "\n");
            // defender takes all attacking and defending cards
            defender.getHand().addAll(allAttackingCards);
            defender.getHand().addAll(allDefendingCards);
        }
    }

    public void roundEndCheck(AtomicBoolean isGameOngoing) {
        Iterator<Player> iterator = players.iterator();

        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (deck.getDeck().isEmpty() && player.getHand().isEmpty()) {
                updateGameMessageWithinRounds(player.getName() + " is no longer in game");
                System.out.println(player.getName() + " is no longer in game");
                iterator.remove();  // using iterator to safely remove the player
                winners.add(player);
            }
        }

        if (players.size() == 1) {
            isGameOngoing.set(false);
        }
    }

    private void updateGameMessageWithinRounds(String newMessage) {
        gameMessage += "\n" + newMessage;
    }


}
