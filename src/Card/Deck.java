package Card;

import java.util.ArrayList;
import java.util.Collections;

public class Deck extends Card {

    private ArrayList<Card> deck;

    public Deck() {
        // create all cards from 6 until ace, in every suit
        deck = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values()) {
            for (int rank = 6; rank <= 14; rank++) {
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);
    }

    public void printDeck() {
        deck.forEach(c -> System.out.print(c + " "));
        System.out.println();
    }
    public ArrayList<Card> getDeck() {
        return deck;
    }
}
