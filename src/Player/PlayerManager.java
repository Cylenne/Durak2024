package Player;

import Card.*;
import Phases.StartPhase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager {

    public static boolean isDefenderRightBeforeAdditionalAttacker(List<Player> players, Player defender, Player attacker) {

        if (players.indexOf(defender) != players.size()) {
            return players.indexOf(defender) < players.indexOf(attacker);
        } else {
            return players.indexOf(defender) > players.indexOf(attacker);
        }
    }

    public static void printAllPlayerDetails(List<Player> allPlayers) {
        System.out.println();
        for (Player player : allPlayers) {
            System.out.print(player.getName() + "'s hand: ");
            for (Card card : player.getHand()) {
                System.out.print(card.toString() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public static Player determineStartingPlayer() {

        List<Card> allPlayersHands = new ArrayList<>();

        // Collect all cards from all players' hands
        for (Player player : StartPhase.getPlayers()) {
            allPlayersHands.addAll(player.getHand());
        }

        allPlayersHands.sort(Card.sortRankReversedSuit(StartPhase.getTrumpSuit()));

        Card smallestTrump = new Card();
        for (Card card : allPlayersHands) {
            if (card.getSuit() == StartPhase.getTrumpSuit()) {
                smallestTrump = card;
                break;
            }
        }

        // determine the player whose card comes first in the sorted list
        for (Player player : StartPhase.getPlayers()) {
            if (player.getHand().contains(smallestTrump)) {
                return player;
            }
        }

        // default case (should not happen in a well-formed game)
        return StartPhase.getPlayers().getFirst();
    }

    public static Player determineAttacker(
            AtomicInteger roundCounter,
            Player previousAttacker, // included due to last attacker
            Player defender,
            boolean currentRoundDefended) {

        List<Player> players = StartPhase.getPlayers();

        if (roundCounter.get() == 1) {
            return PlayerManager.determineStartingPlayer();
        }

        if (currentRoundDefended && (defender.getHand().isEmpty())) { // if the last defender is out of the game
            if (!players.isEmpty()) { // if there are still players in game*
                int previousAttackerIndex = players.indexOf(previousAttacker);
                if (previousAttackerIndex == players.size() - 1) { // if attacker is last in (the new) players list
                    return players.getFirst(); // go to the beginning of the list
                }
                return players.get(previousAttackerIndex + 1);
            } else { // all players are out of the game*
                return null;
            }
        } else if (currentRoundDefended) {
            return defender;
        }

        int defenderIndex = players.indexOf(defender);
        if (defenderIndex == players.size() - 1) {
            return players.getFirst(); // if defender is last in list, go to the beginning of the list
        }

        return players.get(defenderIndex + 1); // if defender lost in previous round, the player after him/her attacks in the next round
    }

    public static Player determineDefender(Player currentAttacker) {
        List <Player> players = StartPhase.getPlayers();

        int attackerIndex = players.indexOf(currentAttacker);
        if (attackerIndex == players.size() - 1) { // if attacker is last in players, he attacks the first in players
            if (players.getFirst() != null) {
                return players.getFirst();
            } else { // this occurs if there are 2 Duraks (two players exiting lastly at the same time and the game ends)
                // THIS NEEDS TO BE RESOLVED
                return null;
            }

        } else {
            return players.get(attackerIndex + 1);
        }
    }

    public static void sortEachPlayersHand() {
        for (Player player : StartPhase.getPlayers()) {
            if (StartPhase.getTrumpSuit() != null) {
                player.getHand().sort(Card.sortRankReversedSuit(StartPhase.getTrumpSuit()));
            } else {
                System.out.println("Error: trump suit has not been determined yet!");
            }
        }
    }
}
