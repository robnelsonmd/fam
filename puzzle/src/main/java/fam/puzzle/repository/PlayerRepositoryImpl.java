package fam.puzzle.repository;

import fam.core.repository.file.FileRepositoryImpl;
import fam.puzzle.domain.Player;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public class PlayerRepositoryImpl extends FileRepositoryImpl<Player> implements PlayerRepository {
    public PlayerRepositoryImpl(File dataDirectory) {
        super(dataDirectory);
    }

    @Override
    public Player findPlayer(String name) {
        return findAll().stream()
                .filter(player -> player.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }
}
