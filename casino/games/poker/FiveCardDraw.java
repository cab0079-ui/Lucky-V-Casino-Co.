//extends Game when it exists
package casino.games.poker;

import casino.cards.Card;
import casino.cards.Deck;
import casino.core.Player;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class FiveCardDraw {

    private Deck deck;
    // Key is the player, value is the 5 cards they hold
    private Map<Player, List<Card>> playerHands;
    // Used to rank hands when deciding the winner
    private HandEvaluator handEvaluator;
    //  Builds a fresh deck and gives every player an empty hand
    public FiveCardDraw(List<Player> players) {
        this.deck = new Deck();
        this.deck.shuffle();   // Chase's constructor doesn't shuffle; remove if he adds it
        this.playerHands = new LinkedHashMap<>();
        this.handEvaluator = new HandEvaluator();

        for (Player player : players) {
            playerHands.put(player, new ArrayList<>());
        }
    }
    // Runs one full round: deal, draw phase for each player, then showdown
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
    // Removes the discarded cards from a player's hand, then deals the same
    // number of replacements from the deck
    public void handleDrawPhase(Player player) {
        List<Card> hand = playerHands.get(player);
        List<Card> discards = player.chooseDiscards(hand);
        hand.removeAll(discards);
        for (int i = 0; i < discards.size(); i++) {
            hand.add(deck.drawCard());
        }
    }
    // Compares every hand against the best one found so far.
    // compareHands > 0 means this hand beat the current best.
    // Ties in the same HandRank category currently go to whoever was checked first.
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
    // Lets the GUI or tests read a player's current cards
    public List<Card> getHand(Player player) {
        return playerHands.get(player);
    }
}