package casino.ui;

import casino.core.HumanPlayer;
import casino.persistence.PlayerRepository;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.function.Consumer;

/**
 * Profile screen: the player types a username and either loads a saved
 * profile or creates a new one. It does not decide what happens next; once
 * a profile is chosen it hands the player to the {@code onLogin} callback.
 *
 * @author Kaleb
 */
@SuppressWarnings("serial")
public class CasinoPanel extends JPanel {

    private static final Color FELT = new Color(0x0B, 0x5D, 0x2E);
    /** Smallest starting balance a new profile may have; also the value suggested in the box. */
    public static final int MIN_BALANCE = 100;

    private final PlayerRepository repository;
    private final Consumer<HumanPlayer> onLogin;
    private final Runnable onExit;

    private final JTextField usernameField = new JTextField(16);
    private final JTextField balanceField = new JTextField(String.valueOf(MIN_BALANCE), 16);
    private final JButton loadButton = new JButton("Load Profile");
    private final JButton newButton = new JButton("New Profile");
    private final JButton exitButton = new JButton("Exit");
    private final JLabel message = new JLabel(" ", SwingConstants.CENTER);

    /**
     * Builds the profile screen.
     *
     * @param repository where profiles are saved and loaded
     * @param onLogin called with the player once a profile is chosen
     * @param onExit called when the player presses Exit
     */
    public CasinoPanel(PlayerRepository repository, Consumer<HumanPlayer> onLogin, Runnable onExit) {
        this.repository = repository;
        this.onLogin = onLogin;
        this.onExit = onExit;

        setLayout(new GridBagLayout());          // centers the column below
        setBackground(FELT);

        JLabel title = new JLabel("Lucky V Casino", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 30f));
        title.setForeground(Color.WHITE);

        JLabel prompt = new JLabel("Username", SwingConstants.CENTER);
        prompt.setForeground(Color.WHITE);

        JLabel balancePrompt = new JLabel("Starting balance (new profiles only)", SwingConstants.CENTER);
        balancePrompt.setForeground(Color.WHITE);

        message.setForeground(new Color(0xFF, 0xE0, 0x66));   // yellow so errors stand out on green
        message.setFont(message.getFont().deriveFont(Font.BOLD, 13f));

        JPanel column = new JPanel(new GridLayout(0, 1, 0, 10));
        column.setOpaque(false);
        column.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        column.setPreferredSize(new Dimension(340, 400));
        column.add(title);
        column.add(prompt);
        column.add(usernameField);
        column.add(balancePrompt);
        column.add(balanceField);
        column.add(loadButton);
        column.add(newButton);
        column.add(exitButton);
        column.add(message);
        add(column);

        loadButton.addActionListener(e -> loadProfile());
        newButton.addActionListener(e -> createProfile());
        exitButton.addActionListener(e -> {
            if (onExit != null) {
                onExit.run();
            }
        });
    }

    /**
     * Loads the profile named in the username field. The saved balance is
     * used as it is; the balance box is ignored.
     */
    private void loadProfile() {
        String name = usernameField.getText().trim();
        if (name.isEmpty()) {
            message.setText("Enter the name of a saved profile.");
            return;
        }
        if (!repository.exists(name)) {
            message.setText("Profile not found.");
            return;
        }
        HumanPlayer player = repository.load(name);
        message.setText(" ");
        onLogin.accept(player);
    }

    /**
     * Creates and saves a new profile using the username and starting
     * balance fields. Shows a message and stops at the first problem.
     */
    private void createProfile() {
        String name = usernameField.getText().trim();
        if (name.isEmpty()) {
            message.setText("Username cannot be empty.");
            return;
        }
        if (!name.matches("[a-zA-Z0-9_-]+")) {
            message.setText("Use only letters, numbers, underscores and hyphens.");
            return;
        }
        if (repository.exists(name)) {
            message.setText("That profile already exists. Pick another name or load it.");
            return;
        }

        int balance;
        try {
            balance = Integer.parseInt(balanceField.getText().trim());
        } catch (NumberFormatException e) {
            message.setText("Starting balance must be a whole number.");
            return;
        }
        if (balance < MIN_BALANCE) {
            message.setText("Starting balance must be at least $" + MIN_BALANCE + ".");
            return;
        }

        HumanPlayer player = new HumanPlayer(name, balance);
        repository.save(player);
        message.setText(" ");
        onLogin.accept(player);
    }
}