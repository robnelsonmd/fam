package fam.puzzle.service;

import fam.puzzle.domain.Player;
import fam.puzzle.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Player getPlayer(String name) {
        Player player = playerRepository.findPlayer(name);

        return (player != null) ?  player :
                playerRepository.save(new Player(name));
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = playerRepository.findAll();
        players.sort(Comparator.comparingInt(Player::getCorrectGuessCount).reversed());
        return players;
    }

    @Override
    public Player incrementCheatCount(Player player) {
        return playerRepository.save(
                new Player(
                        player.getName(),
                        (player.getCheatCount() + 1),
                        player.getCorrectGuessCount(),
                        player.getIncorrectGuessCount(),
                        player.getShowAnswerCount()
                )
        );
    }

    @Override
    public Player incrementCorrectGuessCount(Player player) {
        return playerRepository.save(
                new Player(
                        player.getName(),
                        player.getCheatCount(),
                        (player.getCorrectGuessCount() + 1),
                        player.getIncorrectGuessCount(),
                        player.getShowAnswerCount()
                )
        );
    }

    @Override
    public Player incrementIncorrectGuessCount(Player player) {
        return playerRepository.save(
                new Player(
                        player.getName(),
                        player.getCheatCount(),
                        player.getCorrectGuessCount(),
                        (player.getIncorrectGuessCount() + 1),
                        player.getShowAnswerCount()
                )
        );
    }

    @Override
    public Player incrementShowAnswerCount(Player player) {
        return playerRepository.save(
                new Player(
                        player.getName(),
                        player.getCheatCount(),
                        player.getCorrectGuessCount(),
                        player.getIncorrectGuessCount(),
                        (player.getShowAnswerCount() + 1)
                )
        );
    }
}
