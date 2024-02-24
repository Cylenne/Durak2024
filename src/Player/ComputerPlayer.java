package Player;

import Card.*;
import java.util.*;

public class ComputerPlayer extends Player {

    public ComputerPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addAttackingCards(Card.Suit trumpSuit, Deck remainingDeck) {

        Set<Card> attackingCards = new HashSet<>();

        Card smallestRankedCard = this.getHand().getFirst();
        attackingCards.add(smallestRankedCard);

        // are there multiple cards with the same smallest rank?
        for (int i = 1; i < this.getHand().size(); i++) {
            Card additionalSmallestRankedCard = this.getHand().get(i);

            // if the game is close to an end, attacks are performed with trumps too
            if (remainingDeck.getDeck().isEmpty()) {
                if (smallestRankedCard.getRank() == additionalSmallestRankedCard.getRank()) {
                    attackingCards.add(additionalSmallestRankedCard);
                }

            } else { // otherwise trumps are saved for later
                if (smallestRankedCard.getRank() == additionalSmallestRankedCard.getRank() && !additionalSmallestRankedCard.getSuit().equals(trumpSuit)) {
                    attackingCards.add(additionalSmallestRankedCard);
                }
            }
        }

        // overwritten equals and hashcode in Card to make this work
        this.getHand().removeAll(attackingCards);


        return attackingCards;

        // should computer be aware of which trumps are still available?
        // if yes, and knows it has the highest rank and there are only two players left and it has only one card other than the currently available highest trump(s)
        // attack with the highest ranked card
        // attacks could be made more sophisticated
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(
            Set<Card> cards,
            Deck remainingDeck,
            Card.Suit trumpSuit,
            Boolean isDefenderRightBeforeAdditionalAttacker,
            Player currentDefender,
            List<Card> attackingCardsPerLoop
    ) {

        List<Card> additionalAttackersHand = this.getHand();
        Set<Card> additionalAttackingCardsPerPlayer = new HashSet<>();


        for (Card attackingCard : cards) {
            for (Card additionalAttackersCard : additionalAttackersHand) {
                if (attackingCard.getRank() == additionalAttackersCard.getRank()) {
                    if (remainingDeck.getDeck().isEmpty()) { // in endgame
                        if (isDefenderRightBeforeAdditionalAttacker || areAllCardSame(additionalAttackersHand)) {
                            // gives out all cards (trump included) if they are all the same rank or if additionalAttacker wants to skip being attacked
//                            System.out.println("ATTACKING CARDS PER LOOP: " + attackingCardsPerLoop.size());
//                            System.out.println("CURRENT DEFENDER'S CARDS : " + currentDefender.getHand().size());
                            if (attackingCardsPerLoop.size() < currentDefender.getHand().size()) { // attacking cards have to be less or equal than defender's available card
                                additionalAttackingCardsPerPlayer.add(additionalAttackersCard);
                            }
                        }
                    } else { // not end game
                        if (!additionalAttackersCard.getSuit().equals(trumpSuit) && (attackingCardsPerLoop.size() < currentDefender.getHand().size())) {
                            additionalAttackingCardsPerPlayer.add(additionalAttackersCard);
                        }
                    }
                }
            }
        }

        this.getHand().removeAll(additionalAttackingCardsPerPlayer);
        return additionalAttackingCardsPerPlayer;
    }

    // I want this method to return multiple values, hence the RoundResult class was made
    @Override
    public RoundResult defenseState(List<Card> attackingCards, Card.Suit trumpSuit, Deck remainingDeck, StringBuilder gameMessage) {

        List<Card> defendersHand = this.getHand();
        Set<Card> defendingCards = new HashSet<>();
        boolean currentLoopRoundDefended = true;

        attackingCards.sort(Card.sortRankReversedSuit(trumpSuit));

        // create a new list explicitly, otherwise the strongest cards will be removed from defendersHand
        List<Card> defendersTemporaryHand = new ArrayList<>(defendersHand);
        // going through the attacking cards, starting with the highest ranked
        for (int i = attackingCards.size() - 1; i >= 0; i--) {
            // if non-endgame and defender has trump Q, K and/or A
            if (!remainingDeck.getDeck().isEmpty() && !defendersStrongestCards(this, trumpSuit).isEmpty()) {
                defendersTemporaryHand.removeAll(defendersStrongestCards(this, trumpSuit));
                if (!canBeatCard(defendersTemporaryHand, attackingCards.get(i), trumpSuit, defendingCards, gameMessage)) {
                    currentLoopRoundDefended = false;
                    break; // if one attacking card can't be beaten, the round is lost already
                }
                // in every other situation
            } else if (!canBeatCard(defendersHand, attackingCards.get(i), trumpSuit, defendingCards, gameMessage)) {
                currentLoopRoundDefended = false;
                break;
            }
//            System.out.println("Current loop round defended: " + currentLoopRoundDefended);
        }

//        System.out.println("Defending cards: " + defendingCards);
        return new RoundResult(currentLoopRoundDefended, defendingCards);

        // should there be a preference to block with cards of the same rank (even trump) to avoid additional attacking cards?
    }

    public boolean canBeatCard(List<Card> defendersHand, Card attackingCard, Card.Suit trumpSuit, Set<Card> defendingCards, StringBuilder gameMessage) {
        boolean canBeatCard = false;

        for (Card defendersCard : defendersHand) {

            if ((attackingCard.getSuit().equals(trumpSuit) && defendersCard.getSuit().equals(trumpSuit)) // attacking card is trump
                    || (defendersCard.getSuit().equals(attackingCard.getSuit()) && defendersCard.getRank() > attackingCard.getRank()) // attacking card is non-trump -> first check if non-trump can beat it
                    || (defendersCard.getSuit().equals(trumpSuit))) {
                canBeatCard = true;
                defendersHand.remove(defendersCard);
                gameMessage.append("Attacking card " + attackingCard + " was countered by " + defendersCard + "\n");
                System.out.println("Attacking card " + attackingCard + " was countered by " + defendersCard);
                defendingCards.add(defendersCard);
                break; // once the smallest ranked defender's card was found to beat the attacking card, no need to search further
            }
        }

        if (!canBeatCard) {
            gameMessage.append("Attacking card " + attackingCard + " could not be countered" + "\n");
            System.out.println("Attacking card " + attackingCard + " could not be countered");
        }

        return canBeatCard;
    }

    private static List<Card> defendersStrongestCards(Player currentDefender, Card.Suit trumpSuit) {
        List<Card> defendersStrongestCards = new ArrayList<>();
        for (Card defenderCard : currentDefender.getHand()) {
            int rankOfQueen = 12;
            if (defenderCard.getSuit().equals(trumpSuit) && defenderCard.getRank() >= rankOfQueen) {
                defendersStrongestCards.add(defenderCard);
            }
        }
        return defendersStrongestCards;
    }

    // does the hand contain the same rank of cards?
    public boolean areAllCardSame(List<Card> additionalAttackerHand) {
        Card firstCard = additionalAttackerHand.get(0);

        for (Card additionalAttackersCard : additionalAttackerHand) {
            if (!firstCard.equals(additionalAttackersCard)) {
                return false;  // if any element is different, return false.
            }
        }
        return true;
    }
}
