//no username/profile, AI doesn't persist between sessions, presumably

package casino.core;

import casino.games.poker.HandEvaluator;
import casino.games.poker.HandRank;
import casino.cards.Card;
import java.util.List;

public class AIPlayer extends Player {
    private final HandEvaluator evaluator = new HandEvaluator();

    public AIPlayer(String name, int balance) {
        super(name, balance);
    }

    //Simplest possible poker strategy: fold on nothing, raise on three of a kind or better, otherwise call
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
}