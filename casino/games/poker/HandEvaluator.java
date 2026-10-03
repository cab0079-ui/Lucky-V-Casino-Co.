//ranks hands
package casino.games.poker;

import casino.cards.Card;
import casino.cards.Suit;
import casino.cards.Rank;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HandEvaluator {

    public HandRank handStrength(List<Card> hand) {
        //Quick checks of the hand that will be used to check for the condtion below
        Map<Rank, Integer> rankCounts = countRanks(hand);
        boolean isFlush = checkFlush(hand);
        boolean isStraight = checkStraight(hand);

        //detection order strongest to lowest, returns as soon as a match is found

        if (isStraight && isFlush) {
            return HandRank.STRAIGHT_FLUSH;
        }
        if (hasCountOf(rankCounts, 4)) {
            return HandRank.FOUR_OF_A_KIND;
        }
        if (hasCountOf(rankCounts, 3) && hasCountOf(rankCounts, 2)) {
            return HandRank.FULL_HOUSE;
        }
        if (isFlush) {
            return HandRank.FLUSH;
        }
        if (isStraight) {
            return HandRank.STRAIGHT;
        }
        if (hasCountOf(rankCounts, 3)) {
            return HandRank.THREE_OF_A_KIND;
        }
        if (countOfPairs(rankCounts) == 2) {
            return HandRank.TWO_PAIR;
        }
        if (hasCountOf(rankCounts, 2)) {
            return HandRank.PAIR;
        }

        return HandRank.HIGH_CARD
    }
    //This method counts the ranks in a hand using Map and HashMap since order doesn't matter.
    //The key passed is the Card enum, in the hand, and the value is the number of that card in the hand.
    private Map<Rank, Integer> countRanks(List<Card> hand) {
        Map<Rank, Integer> counts = new HashMap<>();
        for (Card card : hand) {
            Rank rank = card.getRank();
            counts.put(rank, counts.getOrDefault(rank, 0) + 1); //getOrDefault used for when there are 0 of a card recorded

        }
        return counts;
    }
    //Used to check for three of a kind and four of a kind
    private boolean hasCountOf(Map<Rank, Integer> rankCounts, int target) {
        return rankCounts.containsValue(target);
    }
    //Used to see how many different ranks appear exactly twice, how many pairs
    private int countOfPairs(Map<Rank, Integer> rankCounts) {
        int pairs = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) pairs++;
        }
        return pairs;
    }

    private boolean checkFlush(List<Card> hand) {
        //currently assuming some kind of suit accesor
        Suit firstSuit = hand.get(0).getSuit();
        for (Card card : hand) {
            if (card.getSuit() != firstSuit) {
                return false;
            }
        }
        return true;
    }

    private boolean checkStraight(List<Card> hand) {
        //left as a stub for now
        //
        return false;
    }

    public int compareHands(List<Card> hand1, List<Card> hand2) {
        HandRank rank1 = handStrength(hand1);
        HandRank rank2 = handStrength(hand2);
        return rank1.compareTo(rank2);
    }
}