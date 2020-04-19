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
        return playerRepository.findPlayer(name);
    }

    @Override
    public List<Player> getPlayers() {
        List<Player> players = playerRepository.findAll();
        players.sort(Comparator.comparingInt(Player::getGuessCountRatio).reversed());
        return players;
    }

    @Override
    public Player incrementCheatCount(Player player) {
        player = player.playerBuilder().incrementCheatCount().build();
        return playerRepository.save(player);
    }

    @Override
    public Player incrementCorrectGuessCount(Player player) {
        player = player.playerBuilder().incrementCorrectGuessCount().build();
        return playerRepository.save(player);
    }

    @Override
    public Player incrementIncorrectGuessCount(Player player) {
        player = player.playerBuilder().incrementIncorrectGuessCount().build();
        return playerRepository.save(player);
    }

    @Override
    public Player incrementShowAnswerCount(Player player) {
        player = player.playerBuilder().incrementShowAnswerCount().build();
        return playerRepository.save(player);
    }

    @Override
    public Player updateEmailAddress(Player player, String emailAddress) {
        player = player.playerBuilder().updateEmailAddress(emailAddress).build();
        return playerRepository.save(player);
    }

    @Override
    public Player updateReceiveEmails(Player player, boolean receiveEmails) {
        player = player.playerBuilder().updateReceiveEmails(receiveEmails).build();
        return playerRepository.save(player);
    }
}
