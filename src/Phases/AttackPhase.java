package Phases;

import GUI.AttackScreen;
import Player.*;
import Card.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


// this is one attack phase called multiple times from Gameplay
public class AttackPhase {

    // static variables are shared among all instances of a class
    private static List<Player> players;
    private static Deck deck;
    private static Card.Suit trumpSuit;
    private static final List<Player> winners = new ArrayList<>();
    private static AttackScreen attackScreen;

    // non-static variables are specific to that instance of a class
    private Boolean currentRoundDefended = false;
    private Player attacker;
    private Player defender;
    private String gameMessage;
    private Set<Card> initialAttackingCards = new HashSet<>();
    private Set<Card> allAttackingCards;
    private int defendersStartingHandSize;

    // getters
    public static boolean isDeckEmpty() {
        return deck.getDeck().isEmpty();
    }

    public static AttackScreen getAttackScreen() {
        return attackScreen;
    }

    public static List<Player> getWinners() {
        return winners;
    }

    // methods
    public void execute(AtomicInteger roundCounter, AtomicBoolean isGameOngoing) {

        transferAttributes();
        List<Player> activePlayersInRound = new ArrayList<>();

        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, currentRoundDefended);
        defender = PlayerManager.determineDefender(attacker);

        if (attackScreen == null) {
            attackScreen = new AttackScreen();
            attackScreen.setUpAttackScreen(StartPhase.getPlayers(), StartPhase.getTrump());

            gameMessage = StartPhase.getGameMessage();
            attackScreen.updateAttackPhaseMessage(gameMessage);
        }

        gameMessage = "Round: " + roundCounter + "\n"
                + "Number of remaining cards in deck: " + deck.getDeck().size() + "\n"
                + attacker.getName() + " is attacking " + defender.getName() + "\n";

        System.out.println(gameMessage);

        attackScreen.updateRoundMessage(gameMessage);

        activePlayersInRound.add(attacker);
        activePlayersInRound.add(defender);

        PlayerManager.sortEachPlayersHand();

        allAttackingCards = new HashSet<>();

        mainAttackersMove(attacker);
        addAdditionalAttackers(attacker, defender, activePlayersInRound);

        subAttack(roundCounter, attacker, defender, initialAttackingCards, activePlayersInRound, isGameOngoing);

        attackScreen.updateAttackPhaseMessage(gameMessage);

        DeckManager.printDeck(deck);
        PlayerManager.printAllPlayerDetails(players);

        attackScreen.clearAttackingAndDefendingCardsPanel();
    }


    private static void transferAttributes() {
        players = StartPhase.getPlayers();
        deck = StartPhase.getDeck();
        trumpSuit = StartPhase.getTrumpSuit();
    }

    // main attacker gives out attacking cards
    private void mainAttackersMove(Player attacker) {

        initialAttackingCards = attacker.addInitialAttackingCards(defender);
        allAttackingCards.addAll(initialAttackingCards);
        gameMessage = "Initial attacking cards: " + setToString(initialAttackingCards);
        attackScreen.updateAttackPhaseMessage(gameMessage);
        System.out.println(gameMessage);
        attackScreen.updateInitialAttackingCardsPanel(initialAttackingCards);
    }

    private void addAdditionalAttackers(Player attacker, Player defender, List<Player> activePlayersInRound) {
        for (Player player : players) {
            if (!player.equals(attacker) && !player.equals(defender)) {
                activePlayersInRound.add(1, player);
            }
        }
    }

    private void subAttack(
            AtomicInteger roundCounter,
            Player attacker,
            Player defender,
            Set<Card> initialAttackingCards,
            List<Player> activePlayersInRound,
            AtomicBoolean isGameOngoing) {

        Set<Card> allDefendingCards = new HashSet<>();
        AtomicInteger subAttackCounter = new AtomicInteger();
        subAttackCounter.set(1);
        AtomicBoolean roundOn = new AtomicBoolean();
        roundOn.set(true);
        List<Card> attackingCardsPerLoop = new ArrayList<>();

        defendersStartingHandSize = defender.getHand().size();

        while (roundOn.get()) {

            addAdditionalAttackingCards(attacker, defender, initialAttackingCards, subAttackCounter, attackingCardsPerLoop);

            Set<Card> defendingCardsPerLoop = new HashSet<>();

            addDefendingCards(defender, attackingCardsPerLoop, defendingCardsPerLoop, allDefendingCards, roundOn);

            checkForAdditionalAttack(attackingCardsPerLoop, subAttackCounter, defender, defendingCardsPerLoop, attacker);

            if (attackingCardsPerLoop.isEmpty()) {
                roundOn.set(false);
                gameMessage = ("No additional attacking cards, the attack has finished");
                attackScreen.updateAttackPhaseMessage(gameMessage);
                roundEndMessage(defender, allDefendingCards);
                if (!deck.getDeck().isEmpty()) {
                    gameMessage = ("Players are redrawing cards");
                    attackScreen.updateAttackPhaseMessage(gameMessage);
                    System.out.println(gameMessage);
                }
            }

        }

        roundCounter.incrementAndGet();
        DeckManager.drawMissingCards(activePlayersInRound, deck);
        attackScreen.getHumanPlayerPanelUpdater().updateHumanPanelAfterRedraw();
        roundEndCheck(isGameOngoing);
    }

    private void addAdditionalAttackingCards(
            Player attacker,
            Player defender,
            Set<Card> initialAttackingCards,
            AtomicInteger subAttackCounter,
            List<Card> attackingCardsPerLoop) {

        if (subAttackCounter.get() == 1) {
            attackingCardsPerLoop.addAll(initialAttackingCards);
            for (Player player : players) {
                if (!player.equals(defender) && !player.equals(attacker)) {
                    attackingCardsPerLoop.addAll(player.addAdditionalAttackingCards(
                            initialAttackingCards,
                            PlayerManager.isDefenderRightBeforeAdditionalAttacker(players, defender, attacker),
                            defendersStartingHandSize,
                            allAttackingCards));
                }
            }
        }
    }

    private void addDefendingCards(
            Player defender,
            List<Card> attackingCardsPerSubAttack,
            Set<Card> defendingCardsPerSubAttack,
            Set<Card> allDefendingCards,
            AtomicBoolean roundOn) {
        RoundResult defenseResult;
        defenseResult = defender.defenseState(attackingCardsPerSubAttack);

        defendingCardsPerSubAttack.addAll(defenseResult.getDefendingCards());
        allDefendingCards.addAll(defendingCardsPerSubAttack);
        currentRoundDefended = defenseResult.isRoundDefended();
        gameMessage = (currentRoundDefended ? "Successful defense so far" : "Unsuccessful defense");
        attackScreen.updateAttackPhaseMessage(gameMessage);
        System.out.println(gameMessage);

        if (!currentRoundDefended) {
            roundOn.set(false);
        }
    }

    public void checkForAdditionalAttack(
            List<Card> attackingCardsPerSubAttack,
            AtomicInteger subAttackCounter,
            Player defender,
            Set<Card> defendingCardsPerLoop,
            Player attacker) {
        attackingCardsPerSubAttack.clear();
        subAttackCounter.incrementAndGet();

        for (Player player : players) {
            if (!player.equals(defender)) {
                attackingCardsPerSubAttack.addAll(player.addAdditionalAttackingCards(
                        defendingCardsPerLoop,
                        PlayerManager.isDefenderRightBeforeAdditionalAttacker(players, defender, attacker),
                        defendersStartingHandSize,
                        allAttackingCards));
            }
        }

    }

    public void roundEndMessage(
            Player defender,
            Set<Card> allDefendingCards) {
        if (currentRoundDefended) {
            defender.getHand().removeAll(allDefendingCards);
            gameMessage = (defender.getName() + " has successfully countered the attack");
            attackScreen.updateAttackPhaseMessage(gameMessage);
            System.out.println(gameMessage);
            currentRoundDefended = true;
        } else {
            gameMessage = (defender.getName() + " has not been able to counter the attack\nThe defender takes all attacking and defending cards");
            attackScreen.updateAttackPhaseMessage(gameMessage);
            System.out.println(gameMessage);
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
                gameMessage = (player.getName() + " is no longer in game");
                attackScreen.updateAttackPhaseMessage(gameMessage);
                System.out.println(gameMessage);
                iterator.remove();  // using iterator to safely remove the player
                winners.add(player);
            }
        }

        if (players.size() <= 1) { // if the very last attack is defended, players size can be 0
            isGameOngoing.set(false);
            attackScreen.closeAttackScreen();
        }

        attackScreen.updateComputerPlayersPanel();
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

}
