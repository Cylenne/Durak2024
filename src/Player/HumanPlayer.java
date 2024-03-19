package Player;

import Card.*;
import GUI.HumanAdditionalAttackDialog;
import GUI.HumanDefenseDialog;
import GUI.HumanInitialAttackDialog;
import Phases.AttackPhase;
import Phases.StartPhase;

import java.util.*;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Set<Card> addInitialAttackingCards(Player defender) {
        HumanInitialAttackDialog humanInitialAttackDialog = new HumanInitialAttackDialog();
        Set<Card> selectedCards = humanInitialAttackDialog.execute(this, defender);
        this.getHand().removeAll(selectedCards);
        return selectedCards;
    }

    @Override
    public Set<Card> addAdditionalAttackingCards(Set<Card> attackingCards,
                                                 Boolean isDefenderRightBeforeAdditionalAttacker,
                                                 Player defender,
                                                 List<Card> attackingCardsPerLoop) {

        boolean rankMatch = attackingCards.stream().anyMatch(additionalAttackCard -> additionalAttackCard.getRank() == attackingCards.stream().findFirst().get().getRank());
        Set<Card> selectedCards = Collections.emptySet();

        if (rankMatch) {
            HumanAdditionalAttackDialog humanAdditionalAttackDialog = new HumanAdditionalAttackDialog();
            selectedCards =humanAdditionalAttackDialog.execute(this, attackingCards, defender);
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

                StringBuilder gameMessage = new StringBuilder();
                for (Card attackingCard : attackingCards) {
                    for (Card defendingCard : roundResult.getDefendingCards())
                        if (defendingCard.canBeat(attackingCard)) {
                            gameMessage.append("Attacking card ").append(attackingCard).append(" was countered by ").append(defendingCard);
                        }
                }
                AttackPhase.getAttackScreen().updateAttackPhaseMessage(gameMessage.toString());
                AttackPhase.getAttackScreen().updateComputerPlayersPanel();
                System.out.println(gameMessage);
                for (Card defendingCard : roundResult.getDefendingCards()) {
                    AttackPhase.getAttackScreen().updateDefendingCardsPanel(defendingCard);
                }

                this.getHand().removeAll(roundResult.getDefendingCards());

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

}
