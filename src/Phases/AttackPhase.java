package Phases;

import Player.*;
import Card.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


// this is one attack phase called multiple times from Gameplay
public class AttackPhase {

    // Static variables are shared among all instances of a class
    private static List<Player> players;
    private static Deck deck;
    private static Card.Suit trumpSuit;
    private static final List<Player> winners = new ArrayList<>();

    // Non-static variables are specific to that instance of a class
    private Boolean currentRoundDefended = false;
    private Player attacker = null;
    private Player defender = null;
    private List<String> gameMessage = new ArrayList<>();


    public static List<Player> getWinners() {
        return winners;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public List<String> getGameMessage() {
        return gameMessage;
    }

    // non-static variables = instance variables can vary in each instance
    private Set<Card> initialAttackingCards = new HashSet<>();

    public void execute(AtomicInteger roundCounter, AtomicBoolean isGameOngoing) {

        transferAttributes();
        List<Player> activePlayersInRound = new ArrayList<>();

        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, players, trumpSuit, currentRoundDefended);
        defender = PlayerManager.determineDefender(attacker, players);
        gameMessage.add("\nRound: " + roundCounter + "\n"
                + "Number of remaining cards in deck: " + deck.getDeck().size() + "\n"
                + attacker.getName() + " is attacking " + defender.getName() + "\n");
        System.out.println(gameMessage);
        activePlayersInRound.add(attacker);
        activePlayersInRound.add(defender);

        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        mainAttackersMove(attacker);
        addAdditionalAttackers(attacker, defender, activePlayersInRound);

        round(roundCounter, attacker, defender, initialAttackingCards, activePlayersInRound, isGameOngoing);

    }

    private static void transferAttributes() {
        players = StartPhase.getPlayers();
        deck = StartPhase.getDeck();
        trumpSuit = StartPhase.getTrumpSuit();
    }

    // main attacker gives out attacking cards
    private void mainAttackersMove(Player attacker) {

        if (attacker instanceof ComputerPlayer) {
            initialAttackingCards = attacker.addAttackingCards(trumpSuit, deck);
        } else {
            // attacker is human -> write method for human
        }
        gameMessage.addFirst("Initial attacking cards: " + initialAttackingCards + "\n");
        System.out.println("Initial attacking cards: " + initialAttackingCards);
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

        AtomicInteger subAttackCounter = new AtomicInteger();
        subAttackCounter.set(1);
        AtomicBoolean roundOn = new AtomicBoolean();
        roundOn.set(true);
        List<Card> attackingCardsPerLoop = new ArrayList<>();

        while (roundOn.get()) {

            addAttackingCards(attacker, defender, initialAttackingCards, subAttackCounter, attackingCardsPerLoop);

            Set<Card> defendingCardsPerLoop = new HashSet<>();

            addDefendingCards(defender, attackingCardsPerLoop, defendingCardsPerLoop, allDefendingCards, roundOn);

            checkForAdditionalAttack(allAttackingCards, attackingCardsPerLoop, subAttackCounter, defender, defendingCardsPerLoop, attacker);

            if (attackingCardsPerLoop.isEmpty()) {
                roundOn.set(false);
                gameMessage.add("No additional attacking cards\n");
                System.out.println("No additional attacking cards");
            } else {
                System.out.println("Additional attacking cards: " + attackingCardsPerLoop);
                gameMessage.add("Additional attacking cards: " + attackingCardsPerLoop + "\n");
            }

            allAttackingCards.addAll(attackingCardsPerLoop);
        }

        roundEndMessage(defender, allDefendingCards, allAttackingCards);

        roundCounter.incrementAndGet();
        DeckManager.drawMissingCards(activePlayersInRound, deck, players);
        roundEndCheck(isGameOngoing);
    }

    private void addAttackingCards(Player attacker, Player defender, Set<Card> initialAttackingCards, AtomicInteger subAttackCounter, List<Card> attackingCardsPerLoop) {

        if (subAttackCounter.get() == 1) {
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

        System.out.println("Attacking cards per sub attack: " + attackingCardsPerLoop);
        gameMessage.add("Attacking cards per sub attack: " + attackingCardsPerLoop + "\n");
    }

    private void addDefendingCards(Player defender, List<Card> attackingCardsPerLoop, Set<Card> defendingCardsPerLoop, Set<Card> allDefendingCards, AtomicBoolean roundOn) {
        if (defender instanceof ComputerPlayer) {
            RoundResult defenseResult = defender.defenseState(attackingCardsPerLoop, trumpSuit, deck, gameMessage);
            defendingCardsPerLoop.addAll(defenseResult.getDefendingCards());
            gameMessage.add("Defending cards: " + defendingCardsPerLoop + "\n");
            System.out.println("Defending cards: " + defendingCardsPerLoop);
            allDefendingCards.addAll(defendingCardsPerLoop);
            currentRoundDefended = defenseResult.isRoundDefended();
            System.out.println(currentRoundDefended ? "Successful defense so far" : "Unsuccessful defense");
            gameMessage.add(currentRoundDefended ? "Successful defense so far" : "Unsuccessful defense" + "\n");
//            System.out.println("CURRENT LOOP ROUND DEFENDED: " + currentRoundDefended);

            if (!currentRoundDefended) {
                roundOn.set(false);
            }

        } else {
            // human defender -> write this code
        }

    }

    public void checkForAdditionalAttack(Set<Card> allAttackingCards, List<Card> attackingCardsPerLoop, AtomicInteger subAttackCounter, Player defender, Set<Card> defendingCardsPerLoop, Player attacker) {
        allAttackingCards.addAll(attackingCardsPerLoop);
        attackingCardsPerLoop.clear();
        subAttackCounter.incrementAndGet();

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
            gameMessage.add(defender.getName() + " has successfully countered the attack" + "\n");
            System.out.println(defender.getName() + " has successfully countered the attack" + "\n");
            currentRoundDefended = true;
        } else {
            gameMessage.add(defender.getName() + " has not been able to counter the attack" + "\n");
            System.out.println(defender.getName() + " has not been able to counter the attack" + "\n");
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
                gameMessage.add(player.getName() + " is no longer in game");
                System.out.println(player.getName() + " is no longer in game");
                iterator.remove();  // using iterator to safely remove the player
                winners.add(player);
            }
        }

        if (players.size() == 1) {
            isGameOngoing.set(false);
        }
    }

//    private void updateGameMessageWithinRounds(String newMessage) {
//        gameMessage += "\n" + newMessage;
//    }


}
