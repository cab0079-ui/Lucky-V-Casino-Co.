package casino.games.poker;


/**
 * Poker hand categories, declared from weakest to strongest so that
 * {@code compareTo} orders them correctly.
 *
 * @author Kaleb
 */
public enum HandRank {
    HIGH_CARD,
    PAIR,
    TWO_PAIR,
    THREE_OF_A_KIND,
    STRAIGHT,
    FLUSH,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    STRAIGHT_FLUSH
}