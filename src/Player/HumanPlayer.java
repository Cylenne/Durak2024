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
    public Set<Card> addInitialAttackingCards(Player currentDefender) {
        // write this code
        return null;
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(Set<Card> attackingCards,
                                                 Boolean isDefenderRightBeforeAdditionalAttacker,
                                                 Player currentDefender,
                                                 List<Card> attackingCardsPerLoop) {
        // write this code
        return null;
    }

    @Override
    public RoundResult defenseState(List<Card> attackingCards) {
        // write code
        return null;
    }

}
