//save, load, persists, list profiles
package casino.persistence;


import java.io.File; //for saves/
import java.io.FileWriter; //for save(), writes text to file
import java.io.FileReader; //for load(), opens a file for reading
import java.io.BufferedReader; //for load(), reads lines at a time, as opposed to characters
import java.io.IOException; //exceptions for file operations
import java.util.List; //for list return type
import java.util.ArrayList; //list implementation

public class FilePlayerRepository implements PlayerRepository {

    private static final String SAVE_DIR = "saves/";

    @Override
    public void save(String username, int balance) {
        File dir = new File(SAVE_DIR);
        //existence check
        if (!dir.exists()) {
            dir.mkdirs(); //create the saves/ folder if it doesn't exist yet
        }
        //creates and opens a file with username.txt and writes the username and balance to it
        try (FileWriter writer = new FileWriter(SAVE_DIR + username + ".txt")) {
            writer.write("username=" + username + "\n");
            writer.write("balance=" + balance + "\n");
        }
        //if something goes wrong, catch it
        catch (IOException e) {
            System.out.println("Failed to save profile: " + e.getMessage());
        }
    }

    @Override
    public int load(String username) {
        File file = new File(SAVE_DIR + username + ".txt");
        int balance = 0;
        //reads a line if it starts with "balance=", it strips that prefix and only reads the number
        //then saves that int value to balance
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("balance=")) {
                    balance = Integer.parseInt(line.substring("balance=".length()));
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to load profile: " + e.getMessage());
        }

        return balance;
    }
    //existence check
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
        //Gets all files inside File object, for each file found it strips the .txt
        //to add the username to the list
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