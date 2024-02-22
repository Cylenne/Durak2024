package Card;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Comparator;
import java.util.Objects;

public class Card {

    public enum Suit {
        CLUB, DIAMOND, HEART, SPADE;

        public char getImage() {
            return (new char[]{9827, 9830, 9829, 9824}[this.ordinal()]);
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

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public void setSuit(Suit suit) {
        this.suit = suit;
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
        ImageIcon CardAsImage = null;
        for (File file : cardImageFiles) {
            if (file.getName().contains(suitString) && file.getName().contains(rankString)) {
                CardAsImage = new ImageIcon("Cards\\" + rankString + "_of_" + suitString + ".png");
                CardAsImage = resizeImageIcon(CardAsImage, 80, 100);
            }
        }

        return CardAsImage;
    }

    private static ImageIcon resizeImageIcon(ImageIcon originalIcon, int width, int height) {
        Image image = originalIcon.getImage();
        Image resizedImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
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
