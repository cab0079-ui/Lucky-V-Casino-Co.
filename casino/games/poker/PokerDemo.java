package casino.games.poker;

import casino.core.HumanPlayer;
import casino.persistence.FilePlayerRepository;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PokerDemo {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HumanPlayer player = new HumanPlayer("demo", 100);
            JFrame frame = new JFrame("Five Card Draw");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new PokerPanel(player, new FilePlayerRepository(), frame::dispose));
            frame.setSize(640, 560);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

}