//extends Game when it exists
package casino.games.poker;

import casino.cards.Card;
import casino.cards.Deck;
import casino.core.Player;
import casino.core.Wager;

import java.util.*;

/**
 * Runs one round of five card draw: ante, deal, draw phase, then showdown.
 * Every player antes into a shared pot, and the winner takes all of it.
 * Players who can't afford the ante sit out. Each object plays one round,
 * so create a new game for each hand.
 * @author Kaleb
 */
public class FiveCardDraw {

    private Deck deck;
    // Key is the player, value is the 5 cards they hold
    private Map<Player, List<Card>> playerHands;
    // Used to rank hands when deciding the winner
    private HandEvaluator handEvaluator;
    private int pot;
    /** Fixed amount every player antes into the pot. */
    private static final int ANTE = 10;
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
     * Plays one full round: ante, deal, draw phase, then showdown.
     * Prints the winner and updates balances through each player's {@link Wager}.
     */
    public void play() {
        // Take antes first; players who can't pay sit out
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
    /**
     * Takes the ante from every player and adds it to the pot.
     * Anyone who can't afford it is removed from the table.
     */
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
    /**
     * Clears every player's active bet and gives the pot to the winner.
     * Does nothing if there is no winner.
     *
     * @param winner the player who takes the pot, or null if nobody is at the table
     */
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
    public void handleDrawPhase(Player player) {
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
     * @return the winning player, or null if nobody is at the table
     */
    public Player determineWinner() {
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
     * Returns a player's current cards.
     *
     * @param player the player to look up
     * @return that player's hand
     */
    public List<Card> getHand(Player player) {
        return playerHands.get(player);
    }
    /**
     * Returns the chips currently in the pot.
     *
     * @return the pot, which is 0 before antes and after the payout
     */
    public int getPot() { return pot; }
}