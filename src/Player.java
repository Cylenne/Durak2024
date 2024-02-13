import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class Player {
    private String name;
    private ArrayList<Card> hand;

    public Player(String name) {
        this.name = name;
        hand = new ArrayList<>();
    }

    public void initialDeal(Deck deck) {
        while (hand.size() < 6) {
            hand.add(deck.getDeck().getFirst());
            deck.getDeck().removeFirst();
        }
    }

    public abstract Set<Card> addAttackingCards(Card.Suit trumpSuit, Deck remainingDeck);

    public abstract Set<Card> addAdditionalAttackingCards(Set<Card> attackingCards,
                                                          Deck remainingDeck, Card.Suit trumpSuit,
                                                          Boolean isDefenderRightBeforeAdditionalAttacker, Player currentDefender,
                                                          List<Card> attackingCardsPerLoop);
    public abstract RoundResult defenseState(List<Card> attackingCards, Card.Suit trumpSuit, Deck remainingDeck);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ArrayList<Card> getHand() {
        return hand;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    @Override
    public String toString() {
        return name + hand;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Player otherPlayer = (Player) obj;
        return Objects.equals(name, otherPlayer.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}