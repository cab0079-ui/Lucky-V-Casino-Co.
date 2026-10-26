package casino.games.poker;

import casino.cards.Card;
import casino.cards.Deck;
import casino.core.AIPlayer;
import casino.core.HumanPlayer;
import casino.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Runs one round of five card draw: deal, draw phase, then showdown.
 * The round uses a Wager ante and a pot.
 *
 * @author Kaleb
 */
public class FiveCardDraw {

    private final Deck deck;
    // Key is the player, value is the 5 cards they hold
    private final Map<Player, List<Card>> playerHands;
    // Used to rank hands when deciding the winner
    private final HandEvaluator handEvaluator;
    private int pot;
    public static final int ANTE = 10;
    private static final int AI_BALANCE = 100;
    /**
     * Creates a game with a freshly shuffled deck and an empty hand for each player.
     *
     * @param players the players at the table, in turn order
     */
    public FiveCardDraw(List<Player> players) {
        this.deck = new Deck();
        this.deck.shuffle();
        this.playerHands = new LinkedHashMap<>();
        this.handEvaluator = new HandEvaluator();

        for (Player player : players) {
            playerHands.put(player, new ArrayList<>());
        }
    }
    /**
     * Plays one full round and prints the winner.
     */
    public void play() {

        collectAntes();
        // Give all players 5 cards
        dealInitialHands();

        // Every player gets one draw phase, in the order they were added because of LinkedHashMap
        for (Player player : playerHands.keySet()) {
            handleDrawPhase(player);
        }
        payOut(determineWinner());
    }
    //Deals 5 cards to every player. Private since only play() should start a deal.
    private void dealInitialHands() {
        for (Player player : playerHands.keySet()) {
            List<Card> hand = playerHands.get(player);
            for (int i = 0; i < 5; i++) {
                hand.add(deck.drawCard());
            }
        }
    }
    private void collectAntes() {
        Iterator<Player> it = playerHands.keySet().iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.getWager().placeBet(ANTE)) {
                pot += ANTE;
            } else {
                it.remove();   // safe: removing through the iterator, not the map
            }
        }
    }
    private void payOut(Player winner) {
        if (winner == null) {
            return;   // nobody could afford the ante
        }
        for (Player player : playerHands.keySet()) {
            player.getWager().lose();   // stake already went into the pot; this clears the bet
        }
        winner.getWager().awardPot(pot);
        System.out.println("Winner: " + winner.getName() + " takes " + pot);
        pot = 0;
    }
    /**
     * Lets a player discard cards and draws the same number of replacements.
     *
     * @param player the player whose draw phase is being run
     */
    private void handleDrawPhase(Player player) {
        List<Card> hand = playerHands.get(player);
        List<Card> discards = player.chooseDiscards(hand);
        hand.removeAll(discards);
        for (int i = 0; i < discards.size(); i++) {
            hand.add(deck.drawCard());
        }
    }
    /**
     * Finds the player with the strongest hand. Ties go to whoever was
     * added to the table first.
     *
     * @return the winning player
     */
    private Player determineWinner() {
        Player winner = null;
        List<Card> bestHand = null;

        for (Player player : playerHands.keySet()) {
            List<Card> hand = playerHands.get(player);
            if (bestHand == null || handEvaluator.compareHands(hand, bestHand) > 0) {
                bestHand = hand;
                winner = player;
            }
        }

        return winner;
    }
    /**
     * Plays five card draw hands against AI opponents until the player quits
     * or can no longer afford the ante.
     *
     * @param human the logged-in player
     * @param input the shared Scanner (never closed here)
     */
    public static void playPoker(HumanPlayer human, Scanner input) {
        while (true) {
            if (human.getBalance() < FiveCardDraw.ANTE) {
                System.out.println("Not enough credits to ante.");
                return;
            }

            List<Player> table = new ArrayList<>();
            table.add(human);
            table.add(new AIPlayer("AI 1", AI_BALANCE));
            table.add(new AIPlayer("AI 2", AI_BALANCE));

            new FiveCardDraw(table).play();

            System.out.println("Balance: $" + human.getBalance());
            System.out.print("Play another hand? (y/n): ");
            if (!input.nextLine().trim().equalsIgnoreCase("y")) {
                return;
            }
        }
    }
    /**
     * Returns a player's current cards.
     *
     * @param player the player to look up
     * @return that player's hand
     */
    public List<Card> getHand(Player player) {
        return playerHands.get(player);
    }
    public int getPot() { return pot; }
}