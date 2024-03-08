package Player;

import Card.*;
import GUI.AttackScreen;

import java.util.List;
import java.util.Set;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addInitialAttackingCards(Deck remainingDeck, Player currentDefender) {
        // write this code
        return null;
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(Set<Card> attackingCards,
                                                 Deck remainingDeck,
                                                 Boolean isDefenderRightBeforeAdditionalAttacker,
                                                 Player currentDefender,
                                                 List<Card> attackingCardsPerLoop,
                                                 AttackScreen attackScreen) {
        // write this code
        return null;
    }

    @Override
    public RoundResult defenseState(List<Card> attackingCards, Deck remainingDeck, AttackScreen attackScreen) {
        // write code
        return null;
    }

}
