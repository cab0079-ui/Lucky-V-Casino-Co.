//save, load, persists, list profiles
package casino.persistence;


import casino.core.HumanPlayer;

import java.io.File; //for saves/
import java.io.FileWriter; //for save(), writes text to file
import java.io.FileReader; //for load(), opens a file for reading
import java.io.BufferedReader; //for load(), reads lines at a time, as opposed to characters
import java.io.IOException; //exceptions for file operations
import java.util.List; //for list return type
import java.util.ArrayList; //list implementation

/**
 * Stores each profile as a text file named after the player, inside
 * the saves/ folder.
 *
 * @author Kaleb
 */
public class FilePlayerRepository implements PlayerRepository {

    private static final String SAVE_DIR = "saves/";

    /** {@inheritDoc} Writes the profile to saves/&lt;name&gt;.txt. */
    @Override
    public void save(HumanPlayer player) {
        // Make the saves/ folder the first time anything is saved
        File dir = new File(SAVE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // try-with-resources closes the writer automatically
        try (FileWriter writer = new FileWriter(SAVE_DIR + player.getName() + ".txt")) {
            writer.write("username=" + player.getName() + "\n");
            writer.write("balance=" + player.getBalance() + "\n");
        }
        catch (IOException e) {
            System.out.println("Failed to save profile: " + e.getMessage());
        }
    }
    /**
     * {@inheritDoc}
     * If the file is missing or unreadable, the player is returned with a
     * balance of 0.
     */
    @Override
    public HumanPlayer load(String username) {
        File file = new File(SAVE_DIR + username + ".txt");
        int balance = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("balance=")) {
                    balance = Integer.parseInt(line.substring("balance=".length()));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Failed to load profile: " + e.getMessage());
        }

        return new HumanPlayer(username, balance);
    }
    // Existence check
    @Override
    public boolean exists(String username) {
        File file = new File(SAVE_DIR + username + ".txt");
        return file.exists();
    }

    @Override
    public List<String> listProfiles() {
        List<String> profiles = new ArrayList<>();
        File dir = new File(SAVE_DIR);

        if (!dir.exists()) {
            return profiles; //empty list, no profiles saved yet
        }
        // Gets all files inside File object, for each file found it strips the .txt
        // to add the username to the list
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                if (name.endsWith(".txt")) {
                    profiles.add(name.substring(0, name.length() - 4)); //strip ".txt"
                }
            }
        }

        return profiles;
    }
}