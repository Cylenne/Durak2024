package Player;

import Card.*;
import Phases.AttackPhase;

import java.util.List;
import java.util.Set;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addInitialAttackingCards(Player defender) {
        Set<Card> selectedCards = AttackPhase.getAttackScreen().getHumanPlayerScreenManager().humanInitialAttackDialog(this, defender);
        this.getHand().removeAll(selectedCards);
        return selectedCards;
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
