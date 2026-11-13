package casino.games.poker;

import casino.cards.Card;
import casino.cards.Deck;
import casino.core.AIPlayer;
import casino.core.HumanPlayer;
import casino.core.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Runs one round of five card draw: ante, deal, draw phase, then showdown.
 * Each player antes into a pot and the best hand wins it; an exact tie
 * splits the pot.
 *
 * <p>A round can be run in one call with {@link #play()}, or in steps with
 * {@link #startRound()}, {@link #discardAndDraw(Player, List)} or
 * {@link #handleDrawPhase(Player)}, and {@link #finishRound()}, which lets a
 * GUI pause for the human's choices.</p>
 *
 * @author Kaleb
 */
public class FiveCardDraw {

    private final Deck deck;
    // Key is the player, value is the 5 cards they hold
    private final Map<Player, List<Card>> playerHands;
    // Used to rank hands when deciding the winner
    private final HandEvaluator handEvaluator;
    private int pot;
    /** Amount every player antes into the pot at the start of a round. */
    public static final int ANTE = 10;
    /** Credits each AI opponent starts a hand with. */
    public static final int AI_BALANCE = 100;
    /**
     * Creates a game with a freshly shuffled deck and an empty hand for each player.
     *
     * @param players the players at the table, in turn order
     */
    public FiveCardDraw(List<Player> players) {
        this.deck = new Deck();
        this.deck.shuffle();
        this.playerHands = new LinkedHashMap<>();
        this.handEvaluator = new HandEvaluator();

        for (Player player : players) {
            playerHands.put(player, new ArrayList<>());
        }
    }
    /**
     * Plays one full round and prints the winner.
     */
    public void play() {
        startRound();
        for (Player player : new ArrayList<>(playerHands.keySet())) {
            handleDrawPhase(player);
        }
        List<Player> winners = finishRound();
        if (winners.size() == 1) {
            System.out.println("Winner: " + winners.get(0).getName());
        } else if (winners.size() > 1) {
            System.out.println("Tie, pot split between " + winners.size() + " players");
        }
    }
    /**
     * Deals five cards to every player still at the table. Only
     * {@link #startRound()} should start a deal.
     */
    private void dealInitialHands() {
        for (Player player : playerHands.keySet()) {
            List<Card> hand = playerHands.get(player);
            for (int i = 0; i < 5; i++) {
                hand.add(deck.drawCard());
            }
        }
    }
    /**
     * Takes the ante from each player through their wager and adds it to the
     * pot. Anyone who cannot afford it is removed from the round.
     */
    private void collectAntes() {
        Iterator<Player> it = playerHands.keySet().iterator();
        while (it.hasNext()) {
            Player player = it.next();
            if (player.getWager().placeBet(ANTE)) {
                pot += ANTE;
            } else {
                it.remove();   // safe: removing through the iterator, not the map
            }
        }
    }
    /**
     * Clears every player's bet and pays the pot to the winners. A tied pot is
     * divided evenly, and any leftover chips go to the earliest seat.
     *
     * @param winners the players who share the pot (empty if nobody anted)
     */
    private void payOut(List<Player> winners) {
        if (winners.isEmpty()) {
            return;   // nobody could afford the ante
        }
        for (Player player : playerHands.keySet()) {
            player.getWager().lose();   // stake already went into the pot; this clears the bet
        }
        int share = pot / winners.size();
        int oddChips = pot % winners.size();   // leftover that doesn't divide evenly
        for (Player winner : winners) {
            winner.getWager().awardPot(share);
        }
        if (oddChips > 0) {
            winners.get(0).getWager().awardPot(oddChips);   // earliest seat gets the extra
        }
        pot = 0;
    }
    /**
     * Lets a player discard cards and draws the same number of replacements.
     *
     * @param player the player whose draw phase is being run
     * @return how many cards the player replaced
     */
    public int handleDrawPhase(Player player) {
        return discardAndDraw(player, player.chooseDiscards(playerHands.get(player)));
    }
    /**
     * Finds the player or players with the strongest hand. More than one
     * player is returned only when their hands tie exactly.
     *
     * @return the winners, in seat order
     */
    private List<Player> determineWinners() {
        List<Player> winners = new ArrayList<>();
        List<Card> bestHand = null;

        for (Player player : playerHands.keySet()) {
            List<Card> hand = playerHands.get(player);
            int result = (bestHand == null) ? 1 : handEvaluator.compareHands(hand, bestHand);
            if (result > 0) {
                winners.clear();        // a new best hand knocks out earlier winners
                winners.add(player);
                bestHand = hand;
            } else if (result == 0) {
                winners.add(player);    // exact tie with the best so far
            }
        }
        return winners;
    }
    /**
     * Plays five card draw hands against AI opponents until the player quits
     * or can no longer afford the ante.
     *
     * @param human the logged-in player
     * @param input the shared Scanner (never closed here)
     */
    public static void playPoker(HumanPlayer human, Scanner input) {
        while (true) {
            if (human.getBalance() < FiveCardDraw.ANTE) {
                System.out.println("Not enough credits to ante.");
                return;
            }

            List<Player> table = new ArrayList<>();
            table.add(human);
            table.add(new AIPlayer("AI 1", AI_BALANCE));
            table.add(new AIPlayer("AI 2", AI_BALANCE));

            new FiveCardDraw(table).play();

            System.out.println("Balance: $" + human.getBalance());
            System.out.print("Play another hand? (y/n): ");
            if (!input.nextLine().trim().equalsIgnoreCase("y")) {
                return;
            }
        }
    }
    /**
     * Returns a player's current cards.
     *
     * @param player the player to look up
     * @return that player's hand
     */
    public List<Card> getHand(Player player) {
        return playerHands.get(player);
    }
    /**
     * Collects the antes and deals five cards to each player who paid.
     */
    public void startRound(){
        collectAntes();
        dealInitialHands();
    }
    /**
     * Removes the given cards from a player's hand and draws the same number
     * of replacements.
     *
     * @param player the player drawing
     * @param discards the cards to throw away (must be in the player's hand)
     * @return how many cards were replaced
     */
    public int discardAndDraw(Player player, List<Card> discards) {
        List<Card> hand = playerHands.get(player);
        hand.removeAll(discards);
        for (int i = 0; i < discards.size(); i++) {
            hand.add(deck.drawCard());
        }
        return discards.size();
    }
    /**
     * Finds the winner or winners, pays out the pot and clears it.
     *
     * @return everyone who tied for the best hand (one player unless hands tie exactly),
     *         empty if nobody could ante
     */
    public List<Player> finishRound() {
        List<Player> winners = determineWinners();
        payOut(winners);
        return winners;
    }
    /**
     * Returns the players still in the round, in turn order. Anyone who could
     * not afford the ante has been removed. The list is a copy, so changing it
     * does not affect the game.
     *
     * @return the players at the table
     */
    public List<Player> getPlayers() { return new ArrayList<>(playerHands.keySet()); }
    /**
     * Names the category of a player's current hand, such as a pair or a flush.
     *
     * @param player the player to look up
     * @return the strength category of that player's hand
     */
    public HandRank getHandRank(Player player) {
        return handEvaluator.handStrength(playerHands.get(player));
    }
    /**
     * Returns the chips currently in the pot. It is 0 before the antes are
     * collected and after the round is paid out.
     *
     * @return the pot
     */
    public int getPot() { return pot; }
}