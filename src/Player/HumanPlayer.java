package Player;

import Card.*;
import GUI.HumanAdditionalAttackDialog;
import GUI.HumanDefenseDialog;
import GUI.HumanInitialAttackDialog;
import Phases.AttackPhase;
import Phases.StartPhase;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addInitialAttackingCards(Player defender) {
        HumanInitialAttackDialog humanInitialAttackDialog = new HumanInitialAttackDialog();
        Set<Card> selectedCards = humanInitialAttackDialog.execute(this, defender);
        this.getHand().removeAll(selectedCards);
        AttackPhase.getAttackScreen().getHumanPlayerPanelUpdater().updateHumanPanelWithRemainingCards();
        return selectedCards;
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(Set<Card> cards,
                                                 Boolean isDefenderRightBeforeAdditionalAttacker,
                                                 int defendersStartingHandSize,
                                                 Set<Card> allAttackingCards) {

        Set<Integer> cardRanks = cards.stream().map(Card::getRank).collect(Collectors.toSet()); // set of the ranks of all cards in cards
        boolean rankMatch = this.getHand().stream().map(Card::getRank).anyMatch(cardRanks::contains); // checking if any ranks match in additional attacker's hand

        Set<Card> selectedCards = Collections.emptySet();

        if (rankMatch && (allAttackingCards.size() < defendersStartingHandSize)) {
            HumanAdditionalAttackDialog humanAdditionalAttackDialog = new HumanAdditionalAttackDialog();
            selectedCards = humanAdditionalAttackDialog.execute(this, cards, defendersStartingHandSize, allAttackingCards);
            this.getHand().removeAll(selectedCards);

            if (!selectedCards.isEmpty()) {
                String gameMessage = this.getName() + " is also attacking with " + setToString(selectedCards);
                AttackPhase.getAttackScreen().updateAttackPhaseMessage(gameMessage);
                System.out.println(gameMessage);
                AttackPhase.getAttackScreen().updateAttackingCardsPanel(selectedCards);
                AttackPhase.getAttackScreen().getHumanPlayerPanelUpdater().updateHumanPanelWithRemainingCards();
            }

        }
        return selectedCards;
    }

    @Override
    public RoundResult defenseState(List<Card> attackingCards) {

        RoundResult roundResult;

        if (preliminaryDefenseCheck(attackingCards).isRoundDefended()) { // if defense is possible, humanPlayer may select defendingCards manually

            HumanDefenseDialog humanDefenseDialog = new HumanDefenseDialog(); // round is only defended if enough cards were selected
            Set<Card> selectedDefendingCards = humanDefenseDialog.execute(this, attackingCards);

            if (selectedDefendingCards.size() == attackingCards.size()) {
                roundResult = new RoundResult(true, selectedDefendingCards);

                String gameMessage = "Attacking cards " + listToString(attackingCards) + " have been countered by " + setToString(roundResult.getDefendingCards());
                AttackPhase.getAttackScreen().updateAttackPhaseMessage(gameMessage);
                AttackPhase.getAttackScreen().updateComputerPlayersPanel();
                System.out.println(gameMessage);
                for (Card defendingCard : roundResult.getDefendingCards()) {
                    AttackPhase.getAttackScreen().updateDefendingCardsPanel(defendingCard);
                }

                this.getHand().removeAll(roundResult.getDefendingCards());
                AttackPhase.getAttackScreen().getHumanPlayerPanelUpdater().updateHumanPanelWithRemainingCards();

            } else { // if fewer cards are selected, the round is automatically lost
                roundResult = new RoundResult(false, selectedDefendingCards);
            }

        } else {
            roundResult = new RoundResult(false, Collections.emptySet());
        }

        return roundResult;
    }


    private RoundResult preliminaryDefenseCheck(List<Card> attackingCards) {

        List<Card> defendersHand = new ArrayList<>(this.getHand()); // make a copy of the defender's hand
        // without new ArrayList<> it would still refer to the same object
        Set<Card> defendingCards = new HashSet<>();
        boolean currentLoopRoundDefended = true;

        attackingCards.sort(Card.sortRankReversedSuit(StartPhase.getTrumpSuit()));

        for (int i = attackingCards.size() - 1; i >= 0; i--) {
            if (!canBeatCard(defendersHand, attackingCards.get(i), defendingCards)) {
                currentLoopRoundDefended = false;
                break;
            }
        }

        return new RoundResult(currentLoopRoundDefended, defendingCards);
    }

    private boolean canBeatCard // VERY SIMILAR TO COMPUTERPLAYER
    (List<Card> defendersHand,
     Card attackingCard,
     Set<Card> defendingCards) {

        for (Card defendersCard : defendersHand) {
            Card.Suit trumpSuit = StartPhase.getTrumpSuit();
            if ((attackingCard.getSuit().equals(trumpSuit) && defendersCard.getSuit().equals(trumpSuit))
                    && defendersCard.getRank() > attackingCard.getRank()
                    || (!attackingCard.getSuit().equals(trumpSuit) && defendersCard.getSuit().equals(attackingCard.getSuit())
                    && defendersCard.getRank() > attackingCard.getRank())
                    || (!attackingCard.getSuit().equals(trumpSuit) &&
                    defendersCard.getSuit().equals(trumpSuit))) {
                defendersHand.remove(defendersCard);
                defendingCards.add(defendersCard);
                return true;
            }
        }

        return false;
    }

    public static <T> StringBuilder setToString(Set<T> set) { // this also in AttackPhase, maybe move it to a Utils class?
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

}