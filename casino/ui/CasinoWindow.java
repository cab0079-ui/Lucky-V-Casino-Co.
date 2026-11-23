package casino.ui;

import casino.persistence.FilePlayerRepository;
import casino.persistence.PlayerRepository;

import javax.swing.JFrame;

/**
 * The first window: choose or create a player profile. When a profile is
 * chosen this window closes and the game window opens with that player.
 *
 * @author Kaleb
 */
@SuppressWarnings("serial")
public class CasinoWindow extends JFrame {

    /** Creates and shows the profile window. */
    public CasinoWindow() {
        super("Lucky V Casino");
        PlayerRepository repository = new FilePlayerRepository();

        // DISPOSE, not EXIT: closing this window to hand off must not end the program
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(new CasinoPanel(
                repository,
                player -> {
                    dispose();
                    new GameWindow(player, repository);
                },
                this::dispose));
        setSize(640, 560);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}