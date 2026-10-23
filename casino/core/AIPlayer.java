//no username/profile, AI doesn't persist between sessions, presumably

package casino.core;

import casino.cards.Rank;
import casino.games.poker.HandEvaluator;
import casino.games.poker.HandRank;
import casino.cards.Card;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A computer-controlled opponent using a simple poker strategy.
 * It is not saved between sessions.
 *
 * @author Kaleb
 */

public class AIPlayer extends Player {
    private final HandEvaluator evaluator = new HandEvaluator();
    /**
     * Creates an AI player.
     *
     * @param name the AI's display name
     * @param balance the starting balance
     */
    public AIPlayer(String name, int balance) {
        super(name, balance);
    }

    /**
     * Folds on a high-card hand, raises on three of a kind or better,
     * and calls otherwise.
     *
     * @param hand the AI's current cards
     * @return fold, call, or raise
     */
    @Override
    public Action decideAction(List<Card> hand) {
        HandRank rank = evaluator.handStrength(hand);
        if (rank == HandRank.HIGH_CARD) {
            return Action.FOLD;
        }
        if (rank.compareTo(HandRank.THREE_OF_A_KIND) >= 0) {
            return Action.RAISE;
        }
        return Action.CALL;
    }

    /**
     * Keeps pairs or better and discards cards whose rank appears only once,
     * up to three. Does not try to draw to flushes or straights.
     *
     * @param hand the AI's current cards
     * @return a new list of the cards to discard
     */
    @Override
    public List<Card> chooseDiscards(List<Card> hand) {
        // Count how many times each rank appears in the hand
        Map<Rank, Integer> rankCounts = new HashMap<>();
        for (Card card : hand) {
            Rank rank = card.getRank();
            rankCounts.put(rank, rankCounts.getOrDefault(rank, 0) + 1);
        }

        // A new list, so we never modify the player's actual hand here
        List<Card> discards = new ArrayList<>();
        for (Card card : hand) {
            if (discards.size() == 3) {
                break;   // Standard five card draw limit
            }
            if (rankCounts.get(card.getRank()) == 1) {
                discards.add(card);   // A lone card, not part of a pair or better
            }
        }

        return discards;
    }
}