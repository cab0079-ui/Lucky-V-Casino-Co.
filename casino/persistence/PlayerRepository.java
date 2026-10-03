//one .txt per profile
package casino.persistence;

import java.util.List;

public interface PlayerRepository {
    void save(String username, int balance);
    int load(String username);
    boolean exists(String username);
    List<String> listProfiles();
}
