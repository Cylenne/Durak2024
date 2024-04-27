package Card;

import Player.*;

import java.util.List;

// singleton
public class DeckManager {
    private static DeckManager deckManagerInstance;

    private DeckManager() {
    }

    public static synchronized DeckManager getInstance() { // lazy method uses synchronized to prevent issues with multithreading
        if (deckManagerInstance == null) {
            deckManagerInstance = new DeckManager();
        }
        return deckManagerInstance;
    }

    public Card dealTrump(Deck deck) {
        List<Card> deckList = deck.getDeck();

        if (!deckList.isEmpty()) {
            Card trump = deckList.getFirst();
            deckList.removeFirst();
            return trump;
        } else {
            // Handle the case when the deck is empty, maybe throw an exception or return null
            throw new IllegalStateException("The deck is empty");
        }
    }

    public void printDeck(Deck deck) {
        System.out.print("\nThe remaining deck is: ");
        deck.printDeck();
    }

    public void dealCards(List<Player> allPlayers, Deck deck) {
        for (Player player : allPlayers) {
            player.initialDeal(deck);
        }
    }

    public void drawMissingCards(List<Player> activePlayersInRound, Deck deck) {
        // activePlayers list was created so that first attacker, then additional attackers, then defender redraws
        for (Player player : activePlayersInRound) {
            int missingCards = 6 - player.getHand().size();

            // Check if there are missing cards and remaining cards in the deck
            while (missingCards > 0 && !deck.getDeck().isEmpty()) {
                int cardsToDraw = Math.min(missingCards, deck.getDeck().size());

                // Draw cards from the deck
                for (int i = 0; i < cardsToDraw; i++) {
                    Card upcomingCardInDeck = deck.getDeck().getFirst();
                    player.getHand().add(upcomingCardInDeck);
                    deck.getDeck().remove(upcomingCardInDeck);
                }
                missingCards = 6 - player.getHand().size();
            }
        }
    }

}
