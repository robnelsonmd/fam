package fam.puzzle.repository;

import fam.core.repository.file.FileRepository;
import fam.puzzle.domain.Player;

public interface PlayerRepository extends FileRepository<Player> {
    Player findPlayer(String name);
}
