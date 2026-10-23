//one .txt per profile
package casino.persistence;

import casino.core.HumanPlayer;
import java.util.List;

public interface PlayerRepository {
    void save(HumanPlayer player);
    HumanPlayer load(String username);
    boolean exists(String username);
    List<String> listProfiles();
}
