package casino.core;

import casino.cards.Card;
import java.util.List;

/**
 * Base class for anyone who sits at a table, human or AI.
 * Holds a name and a balance, and leaves decision-making to subclasses.
 *
 * @author Kaleb
 */
public abstract class Player {
    private final String name;
    private int balance;

    /**
     * Creates a player.
     *
     * @param name the player's display name
     * @param balance the starting balance
     */
    public Player(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() { return name; }
    public int getBalance() { return balance; }

    /**
     * Removes money from the balance, for example when placing a bet.
     *
     * @param amount the amount to remove
     * @throws IllegalArgumentException if the amount exceeds the balance
     */
    public void deductBalance(int amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
    }
    /**
     * Adds money to the balance, for example when winning a hand.
     *
     * @param amount the amount to add
     */
    public void addBalance(int amount) {
        balance += amount;
    }

    /**
     * Chooses what to do on this player's turn.
     * The human version asks the user; the AI version uses its own logic.
     *
     * @param hand the player's current cards
     * @return the chosen action
     */
    public abstract Action decideAction(List<Card> hand);

    /**
     * Chooses which cards to throw away during the draw phase.
     *
     * @param hand the player's current cards
     * @return a new list of the cards to discard
     */
    public abstract List<Card> chooseDiscards(List<Card> hand);
}