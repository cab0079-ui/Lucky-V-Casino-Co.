//username, tied to PlayerRepository for save/load


package casino.core;

import casino.cards.Card;
import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer(String name, int balance) {
        super(name, balance);
    }

    //TEMP: placeholder until the GUI exists and can supply the player's click
    @Override
    public Action decideAction(List<Card> hand) {
        return Action.CALL;
    }
}