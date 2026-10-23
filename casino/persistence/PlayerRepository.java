//one .txt per profile
package casino.persistence;

import casino.core.HumanPlayer;
import java.util.List;

/**
 * Contract for saving and loading player profiles, however they are stored.
 *
 * @author Kaleb
 */
public interface PlayerRepository {
    /**
     * Saves a player's profile.
     *
     * @param player the player to save
     */
    void save(HumanPlayer player);
    /**
     * Loads a saved profile.
     *
     * @param username the profile name
     * @return the loaded player
     */
    HumanPlayer load(String username);
    /**
     * Checks whether a profile has been saved.
     *
     * @param username the profile name
     * @return true if the profile exists
     */
    boolean exists(String username);
    /**
     * Lists the names of all saved profiles.
     *
     * @return the profile names, empty if none exist
     */
    List<String> listProfiles();
}
