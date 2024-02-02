import javax.swing.Timer;
import java.util.*;

public class Gameplay {
    private List<Player> players;
    private Deck deck;
    private String gameMessage;
    private Player attacker;
    private Player defender;
    private Card.Suit trumpSuit;
    private Card trump;
    private MainScreen mainScreen;
    private AttackScreen attackScreen;
    private int roundCounter;
    private Player startingPlayer;
    private Boolean currentRoundDefended = false;
    private boolean isGameOngoing = true;
    private List<Player> winners = new ArrayList<>();
    private Timer roundTimer;
    private Timer messageTimer;

    public void startPhase() {
        // create deck
        deck = new Deck();

        // initialize StartingScreen
        StartingScreen startingScreen = new StartingScreen(this::onPlayersReady);
        startingScreen.setStandardDeck(deck);
        startingScreen.setupStartingScreen();
    }

    public void attackPhase() {
        List<Player> activePlayersInRound = new ArrayList<>();
        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        // attacker gives out attacking cards
        attacker = PlayerManager.determineAttacker(roundCounter, attacker, defender, players, trumpSuit, currentRoundDefended);
        activePlayersInRound.add(attacker);

        Set<Card> initialAttackingCards = new HashSet<>();

        System.out.println("Round: " + roundCounter);
        System.out.println("Current attacker: " + attacker);

        if (attacker instanceof ComputerPlayer) {
            initialAttackingCards = attacker.addAttackingCards(trumpSuit, deck);
        } else {
            // attacker is human -> write method for human
        }
        System.out.println("Initial attacking cards: " + initialAttackingCards);

        // determine the defender
        defender = PlayerManager.determineDefender(attacker, players);
        System.out.println("Current defender: " + defender);
        activePlayersInRound.add(defender);

        // adding additional attackers right after the attacker
        for (Player player : players) {
            if (!player.equals(attacker) && !player.equals(defender)) {
                activePlayersInRound.add(1, player);
            }
        }

        Set<Card> allDefendingCards = new HashSet<>();
        Set<Card> allAttackingCards = new HashSet<>(initialAttackingCards);

        int attackLoopCounter = 1;
        boolean roundOn = true;
        List<Card> attackingCardsPerLoop = new ArrayList<>();

        gameMessage = "";
        updateGameMessageWithinRounds("Round: " + roundCounter + "\n"
                + "Number of remaining cards in deck: " + deck.getDeck().size() + "\n"
                + attacker.getName() + " is attacking " + defender.getName());

        while (roundOn) {

            if (attackLoopCounter == 1) {
                attackingCardsPerLoop.addAll(initialAttackingCards);
                for (Player player : players) {
                    if (player instanceof ComputerPlayer) {
                        if (!player.equals(defender)) { // additional cards can only be added as long as defender has enough cards
                            attackingCardsPerLoop.addAll(player.addAdditionalAttackingCards(initialAttackingCards, deck,
                                    trumpSuit, PlayerManager.isDefenderRightBeforeAdditionalAttacker(players, defender, attacker), defender, attackingCardsPerLoop));
                        }
                    } else {
                        // add human player code
                    }
                }

            }

            System.out.println("Attacking cards per loop: " + attackingCardsPerLoop);
            updateGameMessageWithinRounds("Attacking cards: " + attackingCardsPerLoop);

            Set<Card> defendingCardsPerLoop = new HashSet<>();
            if (defender instanceof ComputerPlayer) {
                RoundResult defenseResult = defender.defenseState(attackingCardsPerLoop, trumpSuit, deck);
                defendingCardsPerLoop = defenseResult.getDefendingCards();
                allDefendingCards.addAll(defendingCardsPerLoop);
                currentRoundDefended = defenseResult.isRoundDefended();
                System.out.println("CURRENT LOOP ROUND DEFENDED: " + currentRoundDefended);
                if (!currentRoundDefended) {
                    roundOn = false;
                }
            } else {
                // human defender -> write this code
            }

            updateGameMessageWithinRounds("Defending cards: " + defendingCardsPerLoop);
            updateGameMessageWithinRounds(currentRoundDefended ? "Successful defense so far" : "Unsuccessful defense");

            allAttackingCards.addAll(attackingCardsPerLoop);
            attackingCardsPerLoop.clear();
            attackLoopCounter++;

            for (Player player : players) {
                if (player instanceof ComputerPlayer) {
                    if (!player.equals(defender)) {
                        attackingCardsPerLoop.addAll(player.addAdditionalAttackingCards(defendingCardsPerLoop, deck,
                                trumpSuit, PlayerManager.isDefenderRightBeforeAdditionalAttacker(players, defender, attacker), defender, attackingCardsPerLoop));
                    }
                } else {
                    // add human player code
                }
            }

            if (attackingCardsPerLoop.isEmpty()) {
                roundOn = false;
                updateGameMessageWithinRounds("No additional attacking cards are available");
            }

            allAttackingCards.addAll(attackingCardsPerLoop);
        }

        if (currentRoundDefended) {
            defender.getHand().removeAll(allDefendingCards);
//            System.out.println(defender.getName() + " has successfully countered the attack");
            updateGameMessageWithinRounds(defender.getName() + " has successfully countered the attack" + "\n");
            currentRoundDefended = true;
        } else {
            updateGameMessageWithinRounds(defender.getName() + " has not been able to counter the attack" + "\n");
//            System.out.println(defender.getName() + " has not been able to counter the attack");
            // defender takes all attacking and defending cards
            defender.getHand().addAll(allAttackingCards);
            defender.getHand().addAll(allDefendingCards);
        }

        roundCounter++;
        DeckManager.drawMissingCards(activePlayersInRound, deck, players);
        roundEndCheck();
//        System.out.println("Game message: " + gameMessage);
    }

    private void onPlayersReady(List<Player> allPlayers, Deck deck) {
        // save the players to the class variable
        players = allPlayers;

        DeckManager.dealCards(allPlayers, deck);

        PlayerManager.printAllPlayerDetails(allPlayers);

        getTrump(deck);
        PlayerManager.sortEachPlayersHand(players, trumpSuit);

        deck.getDeck().add(trump);

        startingPlayer = PlayerManager.determineStartingPlayer(players, trumpSuit);
        gameMessage = startingPlayer.getName() + " starts the game";

        printCurrentGameState();

        mainScreen = new MainScreen();
        mainScreen.setupStartingScreen(players, trump, gameMessage);

        roundCounter = 1;

        roundTimer = new Timer(5000, e -> {
            if (isGameOngoing) { // because of the timer, while has been changed to if (timer generates the loops)

                attackPhase();

                if (attackScreen == null) {
                    mainScreen.close();
                    attackScreen = new AttackScreen();
                    attackScreen.setUpAttackScreen(players, trump, gameMessage);
                } else {
                    attackScreen.updateAttackScreen(players, gameMessage);
                }

                roundTimer.setRepeats(true); // without this we only reach round 1

            } else {
                gameOver();
                roundTimer.stop();
            }

        });

        roundTimer.start();
    }

    private void printCurrentGameState() {
        System.out.println("The trump is: " + trump);
        DeckManager.printDeck(deck);
        System.out.println("The starting player is: " + startingPlayer);
    }

    private void getTrump(Deck deck) {
        trump = DeckManager.dealTrump(deck);
        trumpSuit = trump.getSuit();
    }

    // iterator needed to avoid ConcurrentModificationException
    public void roundEndCheck() {
        Iterator<Player> iterator = players.iterator();

        while (iterator.hasNext()) {
            Player player = iterator.next();
            if (deck.getDeck().isEmpty() && player.getHand().isEmpty()) {
                updateGameMessageWithinRounds(player.getName() + " is no longer in game");
                System.out.println(player.getName() + " is no longer in game");
                iterator.remove();  // using iterator to safely remove the player
                winners.add(player);
            }
        }

        if (players.size() == 1) {
            isGameOngoing = false;
        }
    }

    public void gameOver() {

        System.out.println("Game Over: " + winners.getFirst().getName() + " has won the game!");
        String leaderboard = "";
        for (int i = 1; i <= winners.size(); i++) {
            String suffix = switch (i) {
                case 1 -> "st";
                case 2 -> "nd";
                default -> "rd";
            };
            leaderboard = i + suffix + " place: " + winners.get(i - 1).getName();
            System.out.println(leaderboard);
        }
        System.out.println("The durak is " + players.getFirst().getName());

        gameMessage = "Round: " + roundCounter + "\n"
                + "Game Over: " + winners.getFirst().getName() + " has won the game!" + "\n"
                + leaderboard + "\n"
                + "The durak is " + players.getFirst().getName();

    }

    public void updateGameMessageWithinRounds(String newMessage) {
        gameMessage += "\n" + newMessage;
    }

}