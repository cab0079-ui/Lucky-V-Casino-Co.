package casino.games.poker;

import casino.cards.Card;
import casino.cards.Suit;
import casino.cards.Rank;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Ranks five card poker hands. A hand's category is found by counting
 * ranks and checking suits. Hands in the same category are separated by
 * comparing their ranks from most to least important, with ace high.
 *
 * @author Kaleb
 */
public class HandEvaluator {
    /**
     * Determines the category of a hand, checking from strongest to weakest.
     *
     * @param hand the five cards to evaluate
     * @return the hand's category
     */
    public HandRank handStrength(List<Card> hand) {
        // Quick checks of the hand that will be used to check for the condition below
        Map<Rank, Integer> rankCounts = countRanks(hand);
        boolean isFlush = checkFlush(hand);
        boolean isStraight = checkStraight(hand);

        // Detection order strongest to lowest, returns as soon as a match is found

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

        return HandRank.HIGH_CARD;
    }
    /**
     * Counts how many times each rank appears in a hand. A map is used because
     * the order of the ranks doesn't matter.
     *
     * @param hand the cards to count
     * @return each rank in the hand mapped to how many times it appears
     */
    private Map<Rank, Integer> countRanks(List<Card> hand) {
        Map<Rank, Integer> counts = new HashMap<>();
        for (Card card : hand) {
            Rank rank = card.getRank();
            counts.put(rank, counts.getOrDefault(rank, 0) + 1); //getOrDefault used for when there are 0 of a card recorded

        }
        return counts;
    }
    /**
     * Checks whether any rank appears exactly the given number of times. Used
     * to detect pairs, three of a kind and four of a kind.
     *
     * @param rankCounts the counts from {@link #countRanks(List)}
     * @param target the number of copies to look for
     * @return true if some rank appears exactly {@code target} times
     */
    private boolean hasCountOf(Map<Rank, Integer> rankCounts, int target) {
        return rankCounts.containsValue(target);
    }
    /**
     * Counts how many different ranks appear exactly twice. Two means two pair.
     *
     * @param rankCounts the counts from {@link #countRanks(List)}
     * @return the number of pairs in the hand
     */
    private int countOfPairs(Map<Rank, Integer> rankCounts) {
        int pairs = 0;
        for (int count : rankCounts.values()) {
            if (count == 2) pairs++;
        }
        return pairs;
    }
    /**
     * Checks whether every card in the hand has the same suit.
     *
     * @param hand the five cards to check
     * @return true if the hand is a flush
     */
    private boolean checkFlush(List<Card> hand) {
        Suit firstSuit = hand.get(0).getSuit();
        for (Card card : hand) {
            if (card.getSuit() != firstSuit) {
                return false;
            }
        }
        return true;
    }
    /**
     * Checks for five consecutive ranks. Ace is stored as 1, so A-2-3-4-5 passes
     * the ordinary check, and 10-J-Q-K-A is checked separately.
     *
     * @param hand the five cards to check
     * @return true if the hand is a straight
     */
    private boolean checkStraight(List<Card> hand) {
        // Put the hand into a list
        List<Integer> values = new ArrayList<>();
        for (Card card : hand) {
            values.add(card.getRank().getValue());
        }
        //Sort the list
        Collections.sort(values);

        // Check for duplicates since straights have no duplicates

        Set<Integer> unique = new HashSet<>(values);
        if (unique.size() != values.size()) {
            return false;   // At least two cards share a rank, so no straight
        }

        // For straights that are not ace-high, difference from first to last in the enums will be 4
        int first = values.getFirst();
        int last = values.getLast();
        if (last - first == 4) {
            return true;
        }

        // Ace-high straight check

        if (values.equals(List.of(1, 10, 11, 12, 13))) {
            return true;
        }

        return false;
    }
    /**
     * Compares two hands. Hands in different categories are decided by
     * category. Hands in the same category are decided by their tiebreak
     * values, compared from most to least important.
     *
     * @param hand1 the first hand
     * @param hand2 the second hand
     * @return negative if hand1 is weaker, positive if stronger, 0 if they tie exactly
     */
    public int compareHands(List<Card> hand1, List<Card> hand2) {
        HandRank rank1 = handStrength(hand1);
        HandRank rank2 = handStrength(hand2);
        int byCategory = rank1.compareTo(rank2);
        if (byCategory != 0) {
            return byCategory;
        }

        List<Integer> values1 = tiebreakValues(hand1, rank1);
        List<Integer> values2 = tiebreakValues(hand2, rank2);
        for (int i = 0; i < values1.size(); i++) {
            int result = Integer.compare(values1.get(i), values2.get(i));
            if (result != 0) {
                return result;
            }
        }
        return 0;   // identical ranks, a true tie
    }
    /**
     * Lists the rank values of a hand in order of importance: ranks that
     * appear most often come first, and ties in count go to the higher rank.
     * For a pair of aces with 9, 6, 3 this gives [14, 9, 6, 3].
     *
     * @param hand the five cards
     * @param category the hand's category, used to spot an ace-low straight
     * @return rank values, most important first
     */
    private List<Integer> tiebreakValues(List<Card> hand, HandRank category) {
        Map<Rank, Integer> counts = countRanks(hand);
        // A-2-3-4-5 is the lowest straight, so its ace counts as 1
        boolean aceLow = (category == HandRank.STRAIGHT || category == HandRank.STRAIGHT_FLUSH)
                && counts.containsKey(Rank.ACE) && counts.containsKey(Rank.TWO);

        List<Rank> ranks = new ArrayList<>(counts.keySet());
        ranks.sort((a, b) -> {
            int byCount = Integer.compare(counts.get(b), counts.get(a));
            if (byCount != 0) {
                return byCount;
            }
            return Integer.compare(rankValue(b, aceLow), rankValue(a, aceLow));
        });

        List<Integer> values = new ArrayList<>();
        for (Rank rank : ranks) {
            values.add(rankValue(rank, aceLow));
        }
        return values;
    }
    /**
     * Gives a rank's value for comparing hands. Ace is stored as 1 in
     * {@link Rank} but is the highest card in poker, so it counts as 14, except
     * in the lowest straight (A-2-3-4-5) where it stays 1.
     *
     * @param rank the rank to value
     * @param aceLow true if the hand is an ace-low straight
     * @return the value used for comparison
     */
    private int rankValue(Rank rank, boolean aceLow) {
        if (rank == Rank.ACE && !aceLow) {
            return 14;
        }
        return rank.getValue();
    }
}