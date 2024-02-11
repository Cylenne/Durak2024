import java.util.ArrayList;
import java.util.List;

public class PlayerManager {

    public static boolean isDefenderRightBeforeAdditionalAttacker(List<Player> players, Player defender, Player attacker) {

        boolean isDefenderRightBeforeAdditionalAttacker = false;
        if (players.indexOf(defender) != players.size()) {
            if (players.indexOf(defender) < players.indexOf(attacker)) {
                isDefenderRightBeforeAdditionalAttacker = true;
            }
        } else {
            if (players.indexOf(defender) > players.indexOf(attacker)) {
                isDefenderRightBeforeAdditionalAttacker = true;
            }
        }
        return isDefenderRightBeforeAdditionalAttacker;
    }

    public static void printAllPlayerDetails(List<Player> allPlayers) {
        for (Player player : allPlayers) {
            System.out.print(player.getName() + "'s hand: ");
            for (Card card : player.getHand()) {
                System.out.print(card.toString() + " ");
            }
            System.out.println();
        }
    }

    public static Player determineStartingPlayer(List<Player> players, Card.Suit trumpSuit) {
        List<Card> allPlayersHands = new ArrayList<>();

        // Collect all cards from all players' hands
        for (Player player : players) {
            allPlayersHands.addAll(player.getHand());
        }

        allPlayersHands.sort(Card.sortRankReversedSuit(trumpSuit));
//        System.out.println("All players' hands sorted from lowest to highest: " + allPlayersHands);

        Card smallestTrump = new Card();
        for (Card card : allPlayersHands) {
            if (card.getSuit() == trumpSuit) {
                smallestTrump = card;
//                System.out.println("The smallest trump card is: " + smallestTrump);
                break;
            }
        }

        // determine the player whose card comes first in the sorted list
        for (Player player : players) {
            if (player.getHand().contains(smallestTrump)) {
                return player;
            }
        }

        // default case (should not happen in a well-formed game)
        return players.getFirst();
    }

    public static Player determineAttacker(
            int roundCounter,
            Player attacker, // included due to last attacker
            Player defender,
            List<Player> players,
            Card.Suit trumpSuit,
            boolean currentRoundDefended) {
        if (roundCounter == 1) {
            attacker = PlayerManager.determineStartingPlayer(players, trumpSuit);
        } else {
            if (currentRoundDefended) {
                if (defender.getHand().isEmpty()) { // if the last defender is out of the game
                    if (!players.isEmpty()) { // if there are still players in game*
                        int lastAttackerIndex = players.indexOf(attacker);
                        if (lastAttackerIndex == players.size() - 1) { // if attacker is last in (the new) players list
                            return players.getFirst(); // go to the beginning of the list
                        }
                        return players.get(lastAttackerIndex + 1);
                    } else { // all players are out of the game*
                        return null;
                    }
                } else {
                    return defender;
                }
            } else {
                int defenderIndex = players.indexOf(defender);
                if (defenderIndex == players.size() - 1) {
                    return players.getFirst(); // if defender is last in list, go to the beginning of the list
                }
                return players.get(defenderIndex + 1); // if defender lost in previous round, the player after him/her attacks in the next round
            }
        }
        return attacker;
    }

    public static Player determineDefender(Player currentAttacker, List<Player> players) {
        int attackerIndex = players.indexOf(currentAttacker);
        if (attackerIndex == players.size() - 1) {
            return players.getFirst();
        } else {
            return players.get(attackerIndex + 1);
        }
    }

    static void sortEachPlayersHand(List<Player> players, Card.Suit trumpSuit) {
        for (Player player : players) {
            if (trumpSuit != null) {
                player.getHand().sort(Card.sortRankReversedSuit(trumpSuit));
            } else {
                System.out.println("Error: trump suit has not been determined yet!");
            }
        }
    }
}
