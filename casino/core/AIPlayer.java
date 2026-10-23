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

public class AIPlayer extends Player {
    private final HandEvaluator evaluator = new HandEvaluator();

    public AIPlayer(String name, int balance) {
        super(name, balance);
    }

    // Simplest possible poker strategy: fold on nothing, raise on three of a kind or better, otherwise call
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

    // Keeps pairs or better, throws away cards whose rank appears only once (max 3)
    // NOTE: currently does not try to account for flushes and straights
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