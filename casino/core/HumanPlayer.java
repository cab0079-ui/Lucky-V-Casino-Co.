//username, tied to PlayerRepository for save/load


package casino.core;

import casino.cards.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * The person at the table. Their decisions are placeholders until the
 * GUI exists to collect real input.
 *
 * @author Kaleb
 */
public class HumanPlayer extends Player {
    /**
     * Creates a human player.
     *
     * @param name the player's profile name
     * @param balance the starting balance
     */
    public HumanPlayer(String name, int balance) {
        super(name, balance);
    }

    /**
     * Placeholder: always calls until the GUI can supply the player's choice.
     *
     * @param hand the player's current cards
     * @return {@link Action#CALL}
     */
    @Override
    public Action decideAction(List<Card> hand) {
        return Action.CALL;
    }

    /**
     * Placeholder: keeps every card until the GUI lets the player pick discards.
     *
     * @param hand the player's current cards
     * @return an empty list
     */
    @Override
    public List<Card> chooseDiscards(List<Card> hand) {
        return new ArrayList<>();   // discards nothing
    }
}