import java.util.Set;

public class RoundResult {
    private boolean roundDefended;
    private Set<Card> defendingCards;

    public RoundResult(boolean roundDefended, Set<Card> defendingCards) {
        this.roundDefended = roundDefended;
        this.defendingCards = defendingCards;
    }

    public boolean isRoundDefended() {
        return roundDefended;
    }

    public Set<Card> getDefendingCards() {
        return defendingCards;
    }
}
