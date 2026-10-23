//base logic for any game
/**
 * gets user's choice of game to play
 *
 * @author joseph
 */

package casino.core;

import java.util.Scanner;
import casino.games.Roulette;
import casino.games.Poker;
import casino.games.Blackjack;
// chose a game to run
public class Game {
    public static void main() {
    Scanner playerChoiceInt = new Scanner(System.in);

        System.out.println("\nChoose game to play:");
        System.out.println("1. roulette");
        System.out.println("2. 5-card Poker");
        System.out.println("3. blackjack");
        
        System.out.print("Enter your choice (1-6): ");
        String playerChoice = playerChoiceInt.next();
    //game runs yay
        switch (playerChoice) {
           case "1" -> {
            //play roulette

        }
           case "2" -> {
            //play 5card
        }
           case "3" -> {
                //play blackjack
        }
           default -> {
            System.out.println("eneter 1, 2, or 3 - that's what we're working with");
        }
    }
}
//play game again, different game, or save and quit
}