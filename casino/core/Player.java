//name, balance, persists between games

package casino.core;

import casino.cards.Card;
import java.util.List;

public abstract class Player {
    private final String name;
    private int balance;

    public Player(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }

    public String getName() { return name; }
    public int getBalance() { return balance; }

    // Rejects an overdraw so balance can never go negative
    public void deductBalance(int amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        balance -= amount;
    }

    public void addBalance(int amount) {
        balance += amount;
    }

    // Each subclass decides differently: the human asks the user, the AI uses logic
    public abstract Action decideAction(List<Card> hand);

    // Each subclass decides which cards to throw away during the draw phase
    public abstract List<Card> chooseDiscards(List<Card> hand);
}