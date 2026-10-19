//extends Game, uses casino.cards
package casino.games.poker;

import casino.cards.Card;
import casino.cards.Deck;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class FiveCardDraw {

    private Deck deck;
    //key is the player's name, value is the 5 cards they hold
    //swap String for Player once that class is merged in
    private Map<String, List<Card>> playerHands;
    //used to rank hands when deciding the winner
    private HandEvaluator handEvaluator;
    //Builds a fresh deck and gives every player an empty hand
    public FiveCardDraw(List<String> playerNames) {
        this.deck = new Deck();
        this.deck.shuffle();   // Chase's constructor doesn't shuffle; remove if he adds it
        this.playerHands = new HashMap<>();
        this.handEvaluator = new HandEvaluator();

        for (String name : playerNames) {
            playerHands.put(name, new ArrayList<>());
        }
    }
    //Entry point for a round. Only deals for now; draw phase and winner
    //will be called from here once the full game loop exists
    public void play() {
        dealInitialHands();
    }
    //Deals 5 cards to every player. Private since only play() should start a deal.
    private void dealInitialHands() {
        for (String playerName : playerHands.keySet()) {
            List<Card> hand = playerHands.get(playerName);
            for (int i = 0; i < 5; i++) {
                hand.add(deck.dealCard());
            }
        }
    }
    //Removes the discarded cards from a player's hand, then deals the same
    //number of replacements from the deck
    public void handleDrawPhase(String playerName, List<Card> cardsToDiscard) {
        List<Card> hand = playerHands.get(playerName);
        hand.removeAll(cardsToDiscard);
        for (int i = 0; i < cardsToDiscard.size(); i++) {
            hand.add(deck.dealCard());
        }
    }
    //Compares every hand against the best one found so far.
    //compareHands > 0 means this hand beat the current best.
    //Ties in the same HandRank category currently go to whoever was checked first.
    public String determineWinner() {
        String winner = null;
        List<Card> bestHand = null;

        for (String playerName : playerHands.keySet()) {
            List<Card> hand = playerHands.get(playerName);
            if (bestHand == null || handEvaluator.compareHands(hand, bestHand) > 0) {
                bestHand = hand;
                winner = playerName;
            }
        }

        return winner;
    }
    //Lets the GUI or tests read a player's current cards
    public List<Card> getHand(String playerName) {
        return playerHands.get(playerName);
    }
}