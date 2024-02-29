package Phases;

import GUI.AttackScreen;
import Player.*;
import Card.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


// this is one attack phase called multiple times from Gameplay
public class AttackPhase {

    // Static variables are shared among all instances of a class
    private static List<Player> players;
    private static Deck deck;
    private static Card.Suit trumpSuit;
    private static final List<Player> winners = new ArrayList<>();
    private static AttackScreen attackScreen;

    // Non-static variables are specific to that instance of a class
    private Boolean currentRoundDefended = false;
    private Player attacker = null;
    private Player defender = null;
    private List<String> gameMessage = new ArrayList<>();
    private Set<Card> initialAttackingCards = new HashSet<>();
    private CountDownLatch latch;

    public static List<Player> getWinners() {
        return winners;
    }

    public void execute(AtomicInteger roundCounter, AtomicBoolean isGameOngoing) {

        latch = new CountDownLatch(1);

        transferAttributes();
        List<Player> activePlayersInRound = new ArrayList<>();

        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, players, trumpSuit, currentRoundDefended);
        defender = PlayerManager.determineDefender(attacker, players);

        gameMessage.clear();

        gameMessage.add("\nRound: " + roundCounter + "\n"
                + "Number of remaining cards in deck: " + deck.getDeck().size() + "\n"
                + attacker.getName() + " is attacking " + defender.getName() + "\n");

        if (attackScreen == null) {
            attackScreen = new AttackScreen();
            attackScreen.setUpAttackScreen(StartPhase.getPlayers(), StartPhase.getTrump(), StartPhase.getGameMessage());
        }

        activePlayersInRound.add(attacker);
        activePlayersInRound.add(defender);

        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        mainAttackersMove(attacker);
        addAdditionalAttackers(attacker, defender, activePlayersInRound);

        round(roundCounter, attacker, defender, initialAttackingCards, activePlayersInRound, isGameOngoing);

        printRoundMessage(gameMessage);

        attackScreen.updateAttackScreen(players, gameMessage, latch);

        try {
            latch.await(); // This will block until latch.countDown() is called
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DeckManager.printDeck(deck);
        PlayerManager.printAllPlayerDetails(players);
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
        gameMessage.add("Initial attacking cards: " + setToString(initialAttackingCards) + "\n");
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
                gameMessage.add("No additional attacking cards. The attack has finished.\n");
                if(!deck.getDeck().isEmpty()) {
                    gameMessage.add("Players are redrawing cards.\n");
                }
            } else {
                gameMessage.add("Additional attacking cards: " + listToString(attackingCardsPerLoop) + "\n");
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

        gameMessage.add("Attacking cards per sub attack: " + listToString(attackingCardsPerLoop) + "\n");
    }

    private void addDefendingCards(Player defender, List<Card> attackingCardsPerLoop, Set<Card> defendingCardsPerLoop, Set<Card> allDefendingCards, AtomicBoolean roundOn) {
        if (defender instanceof ComputerPlayer) {
            RoundResult defenseResult = defender.defenseState(attackingCardsPerLoop, trumpSuit, deck, gameMessage);
            defendingCardsPerLoop.addAll(defenseResult.getDefendingCards());
            gameMessage.add("Defending cards: " + setToString(defendingCardsPerLoop) + "\n");
            allDefendingCards.addAll(defendingCardsPerLoop);
            currentRoundDefended = defenseResult.isRoundDefended();
            gameMessage.add(currentRoundDefended ? "Successful defense so far\n" : "Unsuccessful defense\n");

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
            currentRoundDefended = true;
        } else {
            gameMessage.add(defender.getName() + " has not been able to counter the attack. The defender takes all attacking and defending cards." + "\n");
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
                iterator.remove();  // using iterator to safely remove the player
                winners.add(player);
            }
        }

        if (players.size() <= 1) { // in case of very last attack being defended, players size can be 0
            isGameOngoing.set(false);
        }
    }

    public <T> StringBuilder listToString(List<T> list) {
        StringBuilder stringBuilder = new StringBuilder();
        for (T element : list) {
            stringBuilder.append(element);
            if (list.indexOf(element) < list.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder;
    }

    public static <T> StringBuilder setToString(Set<T> set) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        for (T element : set) {
            stringBuilder.append(element);
            if (index < set.size() - 1) {
                stringBuilder.append(", ");
            }
            index++;
        }
        return stringBuilder;
    }

    public <T> void printRoundMessage(List<T> list) {
        for (T element : list) {
            System.out.println(element);
        }
    }

}
