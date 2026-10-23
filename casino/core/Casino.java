//menu/driver, picks a game, runs core loop
/**
 * Stores each profile as a text file named after the player, inside
 * the saves/ folder.
 *
 * @author joseph
 */
package casino.core;
import java.util.Scanner;

// chose which save file to load

public class Casino{
    public static void main() {
        Scanner playerChoiceInt = new Scanner(System.in);

            System.out.println("\nChoose which Save file to call:");
            System.out.println("1. File-1");
            System.out.println("2. File-2");
            System.out.println("3. File-3");

            System.out.print("Enter your choice (1-6): ");
            String playerChoice = playerChoiceInt.next();
        //game runs yay
            switch (playerChoice) {
               case "1" -> {
                //readsvefile 1

            }
               case "2" -> {
                //readsvefile 2
            }
               case "3" -> {
                    //readsvefile 3
            }
               default -> {
                System.out.println("eneter 1, 2, or 3 - that's what we're working with");
            }
               
        }
            
}