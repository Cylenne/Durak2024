import java.util.List;
import java.util.Set;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addAttackingCards(Card.Suit trumpSuit, Deck remainingDeck) {
        // write this code
        return null;
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(Set<Card> attackingCards,
                                                 Deck remainingDeck, Card.Suit trumpSuit,
                                                 Boolean isDefenderRightBeforeAdditionalAttacker, Player currentDefender,
                                                 List<Card> attackingCardsPerLoop) {
        // write this code
        return null;
    }

    @Override
    public RoundResult defenseState(List<Card> attackingCards, Card.Suit trumpSuit, Deck remainingDeck) {
        // write this code
        return null;
    }
}
