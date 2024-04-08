package Card;
import Phases.StartPhase;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Comparator;
import java.util.Objects;

public class Card {

    public enum Suit {
        CLUB(9827), DIAMOND(9830), HEART(9829), SPADE(9824);
        final int imageReference;

        Suit(int imageReference) {
            this.imageReference = imageReference;
        }

        public int getImage() {
            return imageReference;
        }
    }

    private int rank;
    private Suit suit;

    public Card(int rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Card() {
    }

    @Override
    public String toString() {
        String finalRank;
        if (6 <= rank && rank <= 10) {
            finalRank = String.valueOf(rank);
        } else {
            finalRank = switch (rank) {
                case 11 -> "Jack";
                case 12 -> "Queen";
                case 13 -> "King";
                case 14 -> "Ace";
                default -> throw new IllegalStateException("Unexpected value: " + rank);
            };
        }
        return "%s%c".formatted(finalRank, suit.getImage());
    }

// sort from lowest to highest rank, trumps separately at the end
public static Comparator<Card> sortRankReversedSuit(Card.Suit trumpSuit) {
    return Comparator.<Card, Boolean>comparing(card -> card.getSuit() == trumpSuit)
            .thenComparing(Card::getRank);
}

    public int getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public ImageIcon toImageIcon() {

        // going through each card in the Cards folder
        File cardsFolder = new File("Cards");
        File[] cardImageFiles = cardsFolder.listFiles();

        // converting Card object attributes to string in order to find these strings in the file name
        String suitString = suit.toString().toLowerCase() + "s";
        String rankString = "";
        if (6 <= rank && rank <= 10) {
            rankString = String.valueOf(rank);
        } else {
            rankString = switch (rank) {
                case 11 -> "jack";
                case 12 -> "queen";
                case 13 -> "king";
                case 14 -> "ace";
                default -> throw new IllegalStateException("Unexpected value: " + rank);
            };
        }

        // if suit and rank are both found, image icon is created and resized
        ImageIcon cardAsImage = null;
        for (File file : cardImageFiles) {
            if (file.getName().contains(suitString) && file.getName().contains(rankString)) {
                cardAsImage = new ImageIcon("Cards\\" + rankString + "_of_" + suitString + ".png");
                cardAsImage = resizeImageIcon(cardAsImage, 80, 100);
            }
        }

        return cardAsImage;
    }

    public static ImageIcon resizeImageIcon(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public Boolean canBeat(Card attackingCard) {
        Card.Suit trumpSuit = StartPhase.getTrumpSuit();
        return ( bothTrumpAndDefendersRankLarger(attackingCard, trumpSuit)
                || nonTrumpAttackingSameSuitAndDefendersRankLarger(attackingCard, trumpSuit)
                || nonTrumpAttackingAndDefendingCardIsTrump(attackingCard, trumpSuit));
    }

    private boolean bothTrumpAndDefendersRankLarger(Card attackingCard, Card.Suit trumpSuit) {
        return attackingCard.getSuit().equals(trumpSuit) && this.getSuit().equals(trumpSuit)
                && this.getRank() > attackingCard.getRank();
    }

    private boolean nonTrumpAttackingSameSuitAndDefendersRankLarger(Card attackingCard, Card.Suit trumpSuit) { // first check if non-trump can beat it
        return (!attackingCard.getSuit().equals(trumpSuit) && this.getSuit().equals(attackingCard.getSuit())
                && this.getRank() > attackingCard.getRank());
    }

    private boolean nonTrumpAttackingAndDefendingCardIsTrump(Card attackingCard, Card.Suit trumpSuit){
        return (!attackingCard.getSuit().equals(trumpSuit) &&
                this.getSuit().equals(trumpSuit));
    }

    @Override // overridden to ensure that cards with the same rank and suit produce the same hash code
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    @Override // comparing rank and suit attributes of two cards
    public boolean equals(Object obj) {
        // checks if the object being compared (obj) is the same instance as the current object -> true
        if (this == obj) return true;
        // if the object compared is null or belongs to a different class -> false
        if (obj == null || getClass() != obj.getClass()) return false;

        Card otherCard = (Card) obj;
        // compares the rank and suit attributes of the current card (this) and the other card
        // if both rank and suit are equal, the cards are equal
        return rank == otherCard.rank && suit == otherCard.suit;
    }
}
