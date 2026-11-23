package casino.ui;

import casino.core.HumanPlayer;
import casino.persistence.PlayerRepository;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * PLACEHOLDER game window. It only has to keep this shape so the profile
 * window can open it: {@code new GameWindow(HumanPlayer, PlayerRepository)}.
 * It currently offers poker only. Replace the body with the real game
 * selection and the other two games.
 *
 * @author Kaleb
 */
@SuppressWarnings("serial")
public class GameWindow extends JFrame {

    private static final Color FELT = new Color(0x0B, 0x5D, 0x2E);

    private final HumanPlayer player;
    private final PlayerRepository repository;
    private final CardLayout layout = new CardLayout();
    private final JPanel screens = new JPanel(layout);
    private final Map<String, JComponent> cards = new HashMap<>();   // lets a screen be replaced, not stacked

    /**
     * Opens the game window for a player.
     *
     * @param player the player who just logged in
     * @param repository where the player's balance is saved
     */
    public GameWindow(HumanPlayer player, PlayerRepository repository) {
        super("Lucky V Casino");
        this.player = player;
        this.repository = repository;

        // This is the last window, so closing it ends the program. Save first,
        // because the X button doesn't press any Back button.
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                repository.save(player);
            }
        });

        setContentPane(screens);
        showMenu();
        setSize(700, 640);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Shows the menu, rebuilt so it displays the current balance. */
    private void showMenu() {
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 12));
        menu.setBackground(FELT);
        menu.setBorder(BorderFactory.createEmptyBorder(80, 200, 80, 200));

        JLabel welcome = new JLabel("Welcome, " + player.getName(), SwingConstants.CENTER);
        welcome.setForeground(Color.WHITE);
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 24f));
        JLabel balance = new JLabel("Balance: $" + player.getBalance(), SwingConstants.CENTER);
        balance.setForeground(Color.WHITE);
        JButton poker = new JButton("Five Card Draw");
        poker.addActionListener(e -> showPoker());

        menu.add(welcome);
        menu.add(balance);
        menu.add(poker);
        showCard(menu, "menu");
    }

    private void showPoker() {
        showCard(new PokerPanel(player, repository, this::showMenu), "poker");
    }

    /** Puts a screen on top, removing the old one with the same name first. */
    private void showCard(JComponent panel, String name) {
        JComponent old = cards.put(name, panel);
        if (old != null) {
            screens.remove(old);
        }
        screens.add(panel, name);
        layout.show(screens, name);
        screens.revalidate();
        screens.repaint();
    }
}