package casino.games.poker;

import casino.cards.Card;
import casino.core.AIPlayer;
import casino.core.HumanPlayer;
import casino.core.Player;
import casino.persistence.PlayerRepository;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Swing screen for five card draw. It only displays the game and turns
 * button clicks into calls on {@link FiveCardDraw}; the rules live there.
 *
 * <p>A hand goes: Deal (pays the ante), click the cards to discard, Draw,
 * then the showdown is revealed and the balance is saved.</p>
 *
 * @author Kaleb
 */
@SuppressWarnings("serial")
public class PokerPanel extends JPanel {

    private static final Color FELT = new Color(0x0B, 0x5D, 0x2E);        // table green
    private static final Color CARD_BACK = new Color(0x1F, 0x3A, 0x8A);   // face-down blue
    private static final Color SELECTED = new Color(0xFF, 0xE0, 0x66);    // discard highlight

    private final HumanPlayer human;
    private final PlayerRepository repository;   // may be null
    private final Runnable onExit;                // may be null
    private FiveCardDraw game;                    // the current hand
    private final JLabel header = new JLabel("", SwingConstants.CENTER);
    private final JLabel message = new JLabel("", SwingConstants.CENTER);
    private final JButton dealButton = new JButton("Deal");
    private final JButton drawButton = new JButton("Draw");
    private final JButton backButton = new JButton("Back to Menu");
    private final JPanel[] opponentRows = {new JPanel(), new JPanel()};
    private final JPanel humanRow = new JPanel();
    private final List<JToggleButton> humanCards = new ArrayList<>();

    /**
     * Builds the poker screen.
     *
     * @param human the logged-in player
     * @param repository where the balance is saved after each hand (may be null)
     * @param onExit run when the player presses Back (may be null)
     */
    public PokerPanel(HumanPlayer human, PlayerRepository repository, Runnable onExit) {
        this.human = human;
        this.repository = repository;
        this.onExit = onExit;

        setLayout(new BorderLayout());
        setBackground(FELT);
        setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // header (top)
        header.setForeground(Color.WHITE);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 18f));
        updateHeader();
        add(header, BorderLayout.NORTH);

        // seats
        styleSeat(opponentRows[0], "AI 1");
        styleSeat(opponentRows[1], "AI 2");
        styleSeat(humanRow, "You");

        // table (middle)
        JPanel table = new JPanel();
        table.setOpaque(false);
        table.setLayout(new BoxLayout(table, BoxLayout.Y_AXIS));
        table.add(opponentRows[0]);
        table.add(opponentRows[1]);
        table.add(humanRow);
        add(table, BorderLayout.CENTER);

        // buttons and message (bottom)
        message.setForeground(Color.WHITE);
        message.setFont(message.getFont().deriveFont(Font.BOLD, 15f));

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.setOpaque(false);
        buttons.add(dealButton);
        buttons.add(drawButton);
        buttons.add(backButton);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(message, BorderLayout.NORTH);
        south.add(buttons, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        // starting state: nothing to draw until a hand is dealt
        drawButton.setEnabled(false);
        message.setText("Press Deal to start a hand.");

        dealButton.addActionListener(e -> deal());
        drawButton.addActionListener(e -> draw());
        backButton.addActionListener(e -> goBack());
    }

    // ------------------------------------------------------------ actions

    /**
     * Starts a new hand: checks the player can afford the ante, seats the
     * player with two AI opponents, deals, and lets the player pick discards.
     * Deal and Back are disabled until the hand finishes.
     */
    private void deal() {
        if (human.getBalance() < FiveCardDraw.ANTE) {
            message.setText("Not enough credits to ante.");
            return;
        }

        List<Player> players = new ArrayList<>();
        players.add(human);
        players.add(new AIPlayer("AI 1", FiveCardDraw.AI_BALANCE));
        players.add(new AIPlayer("AI 2", FiveCardDraw.AI_BALANCE));

        game = new FiveCardDraw(players);
        game.startRound();
        if (!game.getPlayers().contains(human)) {
            message.setText("Could not ante.");
            return;
        }

        fillFaceDown(opponentRows[0], 5);
        fillFaceDown(opponentRows[1], 5);
        fillHumanRow(game.getHand(human));
        setSeatTitle(opponentRows[0], "AI 1");
        setSeatTitle(opponentRows[1], "AI 2");
        setSeatTitle(humanRow, "You - " + describe(game.getHandRank(human)));

        message.setText("Pot: $" + game.getPot()
                + ".  Click cards to discard, then press Draw.");
        updateHeader();

        dealButton.setEnabled(false);
        drawButton.setEnabled(true);
        backButton.setEnabled(false);
    }

    /**
     * Throws away the cards the player selected, lets the AI players draw,
     * pays out the pot, reveals every hand and saves the profile. Deal and
     * Back are re-enabled afterward.
     */
    private void draw() {
        List<Card> hand = game.getHand(human);
        List<Card> discards = new ArrayList<>();
        for (int i = 0; i < humanCards.size(); i++) {
            if (humanCards.get(i).isSelected()) {
                discards.add(hand.get(i));
            }
        }

        Map<Player, Integer> drawn = new HashMap<>();
        drawn.put(human, game.discardAndDraw(human, discards));

        List<Player> seated = game.getPlayers();
        for (Player p : seated) {
            if (p != human) {
                drawn.put(p, game.handleDrawPhase(p));
            }
        }

        int pot = game.getPot();                 // read before finishRound() resets it
        List<Player> winners = game.finishRound();

        showHand(opponentRows[0], seated.get(1), winners, drawn.get(seated.get(1)));
        showHand(opponentRows[1], seated.get(2), winners, drawn.get(seated.get(2)));
        showHand(humanRow, human, winners, drawn.get(human));

        message.setText(resultMessage(winners, pot));

        updateHeader();
        if (repository != null) {
            repository.save(human);
        }

        dealButton.setEnabled(human.getBalance() >= FiveCardDraw.ANTE);
        drawButton.setEnabled(false);
        backButton.setEnabled(true);
        if (human.getBalance() < FiveCardDraw.ANTE) {
            message.setText(message.getText() + "  Not enough credits for another hand.");
        }
    }

    /**
     * Saves the profile and runs the exit action given to the constructor.
     */
    private void goBack() {
        if (repository != null) {
            repository.save(human);
        }
        if (onExit != null) {
            onExit.run();
        }
    }

    // ------------------------------------------------------------ display
    /**
     * Refreshes the header with the player's name and current balance.
     */
    private void updateHeader() {
        header.setText(human.getName() + "    Balance: $" + human.getBalance());
    }
    /**
     * Builds the line shown after a hand: who won and what it paid, or who
     * split the pot on an exact tie.
     *
     * @param winners everyone who tied for the best hand
     * @param pot the pot before it was paid out
     * @return the message to display
     */
    private String resultMessage(List<Player> winners, int pot) {
        if (winners.size() == 1) {
            Player winner = winners.get(0);
            if (winner == human) {
                return "You win $" + pot + "!";
            }
            return winner.getName() + " wins $" + pot + ".  You lost $" + FiveCardDraw.ANTE + ".";
        }

        int share = pot / winners.size();
        if (winners.contains(human)) {
            return "Tie! You split the pot and get $" + share + ".";
        }
        List<String> names = new ArrayList<>();
        for (Player winner : winners) {
            names.add(winner.getName());
        }
        return "Tie between " + String.join(" and ", names)
                + ".  You lost $" + FiveCardDraw.ANTE + ".";
    }
    /**
     * Reveals one player's cards and titles their seat with the hand's
     * category, how many cards they drew, and a WINNER or TIE tag if they won.
     *
     * @param row the seat's panel
     * @param player the player in that seat
     * @param winners everyone who won the hand
     * @param drew how many cards the player replaced
     */
    private void showHand(JPanel row, Player player, List<Player> winners, int drew) {
        fillFaces(row, game.getHand(player));
        String name = (player == human) ? "You" : player.getName();
        String tag = "";
        if (winners.contains(player)) {
            tag = winners.size() > 1 ? "  (TIE)" : "  (WINNER)";
        }
        setSeatTitle(row, name + " - " + describe(game.getHandRank(player))
                + " (" + drewText(drew) + ")" + tag);
    }
    /**
     * Gives a seat the table look: transparent background and a white outline
     * with a bold white title.
     *
     * @param row the seat's panel
     * @param title the starting title
     */
    private void styleSeat(JPanel row, String title) {
        row.setOpaque(false);
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), title);
        border.setTitleColor(Color.WHITE);
        border.setTitleFont(border.getTitleFont().deriveFont(Font.BOLD, 14f));
        row.setBorder(border);
    }
    /**
     * Changes the title shown on a seat's outline.
     *
     * @param row the seat's panel (its border must be a titled border)
     * @param title the new title
     */
    private void setSeatTitle(JPanel row, String title) {
        ((TitledBorder) row.getBorder()).setTitle(title);
        row.repaint();
    }
    /**
     * Shows cards face up in a seat. The cards are plain labels, so they
     * cannot be clicked.
     *
     * @param row the seat's panel
     * @param cards the cards to show
     */
    private void fillFaces(JPanel row, List<Card> cards) {
        row.removeAll();
        for (Card card : cards) {
            JLabel face = new JLabel(cardText(card), SwingConstants.CENTER);
            face.setOpaque(true);                       // without this, the background color is ignored
            face.setBackground(Color.WHITE);
            face.setForeground(cardColor(card));
            face.setFont(new Font("Dialog", Font.BOLD, 26));
            face.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            face.setPreferredSize(new Dimension(74, 104));
            row.add(face);
        }
        row.revalidate();
        row.repaint();
    }
    /**
     * Shows face-down cards in a seat, used for the opponents until the showdown.
     *
     * @param row the seat's panel
     * @param count how many cards to show
     */
    private void fillFaceDown(JPanel row, int count) {
        row.removeAll();
        for (int i = 0; i < count; i++) {
            JLabel back = new JLabel("?", SwingConstants.CENTER);
            back.setOpaque(true);
            back.setBackground(CARD_BACK);
            back.setForeground(Color.WHITE);
            back.setFont(new Font("Dialog", Font.BOLD, 26));
            back.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            back.setPreferredSize(new Dimension(74, 104));
            row.add(back);
        }
        row.revalidate();
        row.repaint();
    }
    /**
     * Shows the player's cards as toggle buttons. A pressed button means the
     * card will be discarded. The buttons are kept in hand order so a button's
     * position matches its card's position in the hand.
     *
     * @param cards the player's hand
     */
    private void fillHumanRow(List<Card> cards) {
        humanRow.removeAll();
        humanCards.clear();
        for (Card card : cards) {
            JToggleButton button = new JToggleButton(cardText(card));
            button.setFont(new Font("Dialog", Font.BOLD, 26));
            button.setForeground(cardColor(card));
            button.setBackground(Color.WHITE);
            button.setOpaque(true);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 2));
            button.setPreferredSize(new Dimension(74, 104));
            button.addItemListener(e -> markSelected(button));
            humanCards.add(button);
            humanRow.add(button);
        }
        humanRow.revalidate();
        humanRow.repaint();
    }
    /**
     * Highlights a card button while it is selected for discard and restores
     * its normal look when it is deselected.
     *
     * @param button the card button that was toggled
     */
    private void markSelected(JToggleButton button) {
        boolean out = button.isSelected();
        button.setBackground(out ? SELECTED : Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(
                out ? Color.RED : Color.DARK_GRAY, out ? 4 : 2));
    }

    // ------------------------------------------------------------ helpers
    /**
     * Turns a card into display text such as "A♠" or "10♥".
     *
     * @param card the card
     * @return the rank followed by the suit symbol
     */
    private static String cardText(Card card) {
        return rankText(card.getRank().getValue()) + suitText(card);
    }
    /**
     * Converts a rank value to its label. Ace is stored as 1 and face cards as 11 to 13.
     *
     * @param value the rank's numeric value
     * @return "A", "J", "Q", "K", or the number
     */
    private static String rankText(int value) {
        return switch (value) {
            case 1 -> "A";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(value);
        };
    }
    /**
     * Returns the Unicode symbol for a card's suit.
     *
     * @param card the card
     * @return the spade, heart, diamond or club symbol
     */
    private static String suitText(Card card) {
        return switch (card.getSuit()) {
            case SPADES -> "\u2660";
            case HEARTS -> "\u2665";
            case DIAMONDS -> "\u2666";
            case CLUBS -> "\u2663";
        };
    }
    /**
     * Picks the text color for a card.
     *
     * @param card the card
     * @return red for hearts and diamonds, black for spades and clubs
     */
    private static Color cardColor(Card card) {
        return switch (card.getSuit()) {
            case HEARTS, DIAMONDS -> Color.RED;
            case SPADES, CLUBS -> Color.BLACK;
        };
    }
    /**
     * Turns a hand category into readable text, for example
     * THREE_OF_A_KIND becomes "Three Of A Kind".
     *
     * @param rank the hand category
     * @return the category's display name
     */
    private static String describe(HandRank rank) {
        StringBuilder text = new StringBuilder();
        for (String word : rank.name().split("_")) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        return text.toString();
    }
    /**
     * Describes how many cards a player replaced.
     *
     * @param count the number of cards drawn
     * @return "stood pat" for none, otherwise "drew" followed by the count
     */
    private static String drewText(int count) {
        return count == 0 ? "stood pat" : "drew " + count;
    }
}