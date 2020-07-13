package fam.puzzle.repository;

import fam.puzzle.domain.Player;
import fambam.repository.file.FileRepository;

public interface PlayerRepository extends FileRepository<Player> {
    Player findPlayer(String name);
}
