//menu/driver, gets desired save file to use while playing
/**
 * player choses a file to play in player may chose from existing files
 * If there are any or a new file
 * @author joseph
 */
package casino.core;

import casino.persistence.PlayerRepository;
import casino.persistence.FilePlayerRepository;
import casino.core.HumanPlayer;
import casino.games.Game;
import java.util.List;
import java.util.Scanner;
// chose which save file to load

public class Casino{
public static void main(String[] args) {

        Scanner input = new Scanner(System.in);

        // Connect to the file repository
        PlayerRepository repository = new FilePlayerRepository();

        HumanPlayer player = null;

        System.out.println("Welcome to the Casino!");

        // Keep asking until a valid profile is selected
        while (player == null) {

            System.out.println("\n1. Load existing profile");
            System.out.println("2. Create new profile");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine().trim();

            switch (choice) {

                case "1":
                    // Display existing profiles
                    List<String> profiles = repository.listProfiles();

                    if (profiles.isEmpty()) {
                        System.out.println("No saved profiles found.");
                        break;
                    }

                    System.out.println("\nSaved profiles:");

                    for (int i = 0; i < profiles.size(); i++) {
                        System.out.println(
                            (i + 1) + ". " + profiles.get(i)
                        );
                    }

                    System.out.print("Enter the profile name: ");
                    String username = input.nextLine().trim();

                    if (repository.exists(username)) {
                        player = repository.load(username);
                        System.out.println(
                            "Welcome back, " + player.getName() + "!"
                        );
                        System.out.println(
                            "Current balance: $" + player.getBalance()
                        );
                    } else {
                        System.out.println("Profile not found.");
                    }

                    break;

                case "2":
                    System.out.print("Enter a username: ");
                    String newUsername = input.nextLine().trim();

                    if (newUsername.isEmpty()) {
                        System.out.println("Username cannot be empty.");
                        break;
                    }

                    if (!newUsername.matches("[a-zA-Z0-9_-]+")) {
                        System.out.println(
                            "Use only letters, numbers, underscores, "
                            + "and hyphens."
                        );
                        break;
                    }

                    if (repository.exists(newUsername)) {
                        System.out.println(
                            "That profile already exists. "
                            + "Choose another username or load it."
                        );
                        break;
                    }

                    System.out.print("Enter starting balance: ");

                    try {
                        int balance = Integer.parseInt(
                            input.nextLine().trim()
                        );

                        if (balance < 0) {
                            System.out.println(
                                "Balance cannot be negative."
                            );
                            break;
                        }

                        player = new HumanPlayer(newUsername, balance);
                        repository.save(player);

                        System.out.println(
                            "New profile created for "
                            + player.getName() + "."
                        );

                    } catch (NumberFormatException e) {
                        System.out.println(
                            "Please enter a valid whole number."
                        );
                    }

                    break;

                case "3":
                    System.out.println("Au roivior, slan go feiol, Adios!");
                    input.close();
                    return;

                default:
                    System.out.println(
                        "Invalid choice. Please select 1, 2, or 3."
                    );
            }
        }

        // The selected player is now available for the casino game.
        System.out.println("\n Have a great time in the Lucky V Casino!");

        // call game menue
        
        Game.start(player);

        input.close();
    }

}