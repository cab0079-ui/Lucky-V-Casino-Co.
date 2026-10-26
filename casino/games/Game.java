//base logic for any game
/**
 * gets user's choice of game to play
 *
 * @author joseph
 */

package casino.games;

import java.util.Scanner;
import casino.core.HumanPlayer;
import casino.core.player;
import casino.games.roulette.Roulette;
import casino.games.poker.FiveCardDraw;
import casino.games.blackjack.Blackjack;

// chose a game to run
public class Game {
    public static void main() {
    Scanner input = new Scanner(System.in);
        System.out.println("Player: " + player.getName());
        System.out.println("Balance: $" + player.getBalance());
        System.out.println("\nChoose game to play:");
        System.out.println("1. roulette");
        System.out.println("2. 5-card Poker");
        System.out.println("3. blackjack");
        System.out.println("4. Return to main menu");
        
        System.out.print("Enter your choice (1-6): ");
        String playerChoice = input.nextLine().trim();
    //game runs yay
        switch (playerChoice) {
           case "1" -> {
            System.out.println("\nStarting Game");
            //play roulette
            Roulette roulette = new Roulette();
            roulette.play(player);

        }
           case "2" -> {
            System.out.println("\nStarting Game");
            //play 5card
            FiveCardDraw fivecarddraw = new FiveCardDraw();
            fivecarddraw.play(player);
        }
           case "3" -> {
               System.out.println("\nStarting Game");
                //play blackjack
                Blackjack blackjack = new Blackjack();
                blackjack.play(player);
        }
           case "4" -> {
               System.out.println("\nreturning to main menu");
                //retuirn to main menu
                input.close();
                return;

        }
           default -> {
            System.out.println("eneter 1, 2, or 3. I guess you can do 4 if you want, but notining else");
            
        }
    }
}
//play game again, different game, or save and quit
}