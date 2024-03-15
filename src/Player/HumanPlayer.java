package Player;

import Card.*;
import Phases.AttackPhase;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addInitialAttackingCards(Player defender) {
        Set<Card> selectedCards = AttackPhase.getAttackScreen().getHumanInitialAttackDialog().execute(this, defender);
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
        Set<Card> defendingCards = Collections.emptySet();

        ComputerPlayer testHumanDefender = new ComputerPlayer("TestHumanDefender"); // preliminary check to see if defense is even possible - IS THIS BAD DESIGN?
        if (!testHumanDefender.defenseState(attackingCards).getDefendingCards().isEmpty()) {
            return new RoundResult(false, defendingCards);
        } else { // if defense is possible, humanPlayer may select defendingCards manually
            return new RoundResult(true, AttackPhase.getAttackScreen().getHumanDefenseDialog().execute(this,attackingCards));
        }

    }

}
