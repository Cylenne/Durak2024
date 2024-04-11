package Player;

import Card.*;
import Phases.AttackPhase;
import Phases.StartPhase;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addInitialAttackingCards(Player defender) {

        Set<Card> initialAttackingCards = new HashSet<>();

        Card smallestRankedCard = this.getHand().getFirst();
        initialAttackingCards.add(smallestRankedCard);


        // are there multiple cards with the same smallest rank?
        for (int i = 1; i < this.getHand().size(); i++) {
            Card additionalSmallestRankedCard = this.getHand().get(i);

            // if endgame, attacks are performed with trumps too and attacker has to be mindful of defender's hand's size
            if (AttackPhase.isDeckEmpty()) {
                if (smallestRankedCard.getRank() == additionalSmallestRankedCard.getRank() && initialAttackingCards.size() < defender.getHand().size()) {
                    initialAttackingCards.add(additionalSmallestRankedCard);
                }

            } else { // otherwise trumps are saved for later
                if (smallestRankedCard.getRank() == additionalSmallestRankedCard.getRank() && !additionalSmallestRankedCard.getSuit().equals(StartPhase.getTrumpSuit())) {
                    initialAttackingCards.add(additionalSmallestRankedCard);
                }
            }
        }

        // overwritten equals and hashcode in Card to make this work
        this.getHand().removeAll(initialAttackingCards);
        AttackPhase.getAttackScreen().updateComputerPlayersPanel();


        return initialAttackingCards;

        // should computer be aware of which trumps are still available?
        // if yes, and knows it has the highest rank and there are only two players left and it has only one card other than the currently available highest trump(s)
        // attack with the highest ranked card
        // attacks could be made more sophisticated
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(
            Set<Card> cards,
            Boolean isDefenderRightBeforeAdditionalAttacker,
            int defendersStartingHandSize,
            Set<Card> allAttackingCards) {

        List<Card> additionalAttackersHand = this.getHand();
        Set<Card> additionalAttackingCardsPerPlayer = new HashSet<>();

        for (Card additionalAttackersCard : additionalAttackersHand) {
            if (cards.stream().anyMatch(card -> card.getRank() == additionalAttackersCard.getRank())) {
                if (AttackPhase.isDeckEmpty() && (isDefenderRightBeforeAdditionalAttacker || areAllCardSame(additionalAttackersHand))) {
                    if (allAttackingCards.size() < defendersStartingHandSize) {
                        additionalAttackingCardsPerPlayer.add(additionalAttackersCard);
                        allAttackingCards.add(additionalAttackersCard);
                    }
                } else if (!additionalAttackersCard.getSuit().equals(StartPhase.getTrumpSuit()) &&
                        (allAttackingCards.size() < defendersStartingHandSize)) {
                    additionalAttackingCardsPerPlayer.add(additionalAttackersCard);
                    allAttackingCards.add(additionalAttackersCard);
                }
            }
        }

        if (!additionalAttackingCardsPerPlayer.isEmpty()) {

            String gameMessage = this.getName() + " is also attacking with " + setToString(additionalAttackingCardsPerPlayer);
            System.out.println(gameMessage);
            AttackPhase.getAttackScreen().updateAttackPhaseMessage(gameMessage);
            AttackPhase.getAttackScreen().updateAttackingCardsPanel(additionalAttackingCardsPerPlayer);

        }

        System.out.println("allAttackingCards: " + allAttackingCards.size() + ": " + allAttackingCards);
        System.out.println("defendersStartingHandSize: " + defendersStartingHandSize);

        this.getHand().removeAll(additionalAttackingCardsPerPlayer);
        AttackPhase.getAttackScreen().updateComputerPlayersPanel();

        return additionalAttackingCardsPerPlayer;
    }

    // I want this method to return multiple values, hence the RoundResult class was made
    @Override
    public RoundResult defenseState(List<Card> attackingCards) {

        List<Card> defendersHand = this.getHand();
        Set<Card> defendingCards = new HashSet<>();
        boolean currentLoopRoundDefended = true;

        attackingCards.sort(Card.sortRankReversedSuit(StartPhase.getTrumpSuit()));

        // going through the attacking cards, starting with the highest ranked
        for (int i = attackingCards.size() - 1; i >= 0; i--) {
            if (!canBeatCard(defendersHand, attackingCards.get(i), defendingCards)) {
                currentLoopRoundDefended = false;
                break; // if one attacking card can't be beaten, the round is lost already
            }
        }

        return new RoundResult(currentLoopRoundDefended, defendingCards);

        // should there be a preference to block with cards of the same rank (even trump) to avoid additional attacking cards?
    }

    private boolean canBeatCard
            (List<Card> defendersHand,
             Card attackingCard,
             Set<Card> defendingCards) {

        boolean canBeatCard = false;
        String gameMessage;

        for (Card defendersCard : defendersHand) {

            if (!isOneOfStrongestCards(defendersCard)) {
                Card.Suit trumpSuit = StartPhase.getTrumpSuit();
                if ((attackingCard.getSuit().equals(trumpSuit) && defendersCard.getSuit().equals(trumpSuit))
                        && defendersCard.getRank() > attackingCard.getRank() // if both trump & defender's rank's larger
                        || (!attackingCard.getSuit().equals(trumpSuit) && defendersCard.getSuit().equals(attackingCard.getSuit())
                        && defendersCard.getRank() > attackingCard.getRank()) // attacking card is non-trump & same suit -> first check if non-trump can beat it
                        || (!attackingCard.getSuit().equals(trumpSuit) &&
                        defendersCard.getSuit().equals(trumpSuit))) // attacking card is non-trump -> any trump beats it
                {
                    canBeatCard = true;
                    defendersHand.remove(defendersCard);
                    defendingCards.add(defendersCard);

                    gameMessage = ("Attacking card " + attackingCard + " was countered by " + defendersCard);
                    AttackPhase.getAttackScreen().updateAttackPhaseMessage(gameMessage);
                    AttackPhase.getAttackScreen().updateComputerPlayersPanel();
                    System.out.println(gameMessage);
                    AttackPhase.getAttackScreen().updateDefendingCardsPanel(defendersCard);

                    break; // once the smallest ranked defender's card was found to beat the attacking card, no need to search further
                }

            }
        }

        if (!canBeatCard) {
            gameMessage = ("Attacking card " + attackingCard + " could not be countered");

            AttackPhase.getAttackScreen().updateAttackPhaseMessage(gameMessage);
            System.out.println(gameMessage);

        }

        return canBeatCard;
    }

    // if non-endgame, defender would not defend with trump Q, K or A
    private static boolean isOneOfStrongestCards(Card defendersCard) {
        if (!AttackPhase.isDeckEmpty()) {
            int rankOfQueen = 12;
            return defendersCard.getSuit().equals(StartPhase.getTrumpSuit()) && defendersCard.getRank() >= rankOfQueen;
        }
        return false;
    }

    // does the hand contain the same rank of cards?
    private boolean areAllCardSame(List<Card> additionalAttackerHand) {
        Card firstCard = additionalAttackerHand.getFirst();

        for (Card additionalAttackersCard : additionalAttackerHand) {
            if (!firstCard.equals(additionalAttackersCard)) {
                return false;  // if any element is different, return false.
            }
        }
        return true;
    }

    private static <T> StringBuilder setToString(Set<T> set) {
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
