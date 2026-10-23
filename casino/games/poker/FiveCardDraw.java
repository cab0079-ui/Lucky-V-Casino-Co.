//extends Game when it exists
package casino.games.poker;

import casino.cards.Card;
import casino.cards.Deck;
import casino.core.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Runs one round of five card draw: deal, draw phase, then showdown.
 * Betting is not implemented yet.
 *
 * @author Kaleb
 */
public class FiveCardDraw {

    private Deck deck;
    // Key is the player, value is the 5 cards they hold
    private Map<Player, List<Card>> playerHands;
    // Used to rank hands when deciding the winner
    private HandEvaluator handEvaluator;
    //  Builds a fresh deck and gives every player an empty hand
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
        // Give all players 5 cards
        dealInitialHands();

        // Every player gets one draw phase, in the order they were added because of LinkedHashMap
        for (Player player : playerHands.keySet()) {
            handleDrawPhase(player);
        }

        Player winner = determineWinner();
        System.out.println("Winner: " + winner.getName());
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
     * @return the winning player
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
}