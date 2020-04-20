package fam.puzzle.service;

import fam.puzzle.domain.Player;
import fam.puzzle.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final EmailService emailService;
    private final PlayerRepository playerRepository;

    private Player currentLeader;

    public PlayerServiceImpl(EmailService emailService, PlayerRepository playerRepository) {
        this.emailService = emailService;
        this.playerRepository = playerRepository;
    }

    @PostConstruct
    public void init() {
        List<Player> players = getPlayers();
        currentLeader = !players.isEmpty() ? players.get(0) : null;
    }

    @Override
    public Player getPlayer(String name) {
        Player player = playerRepository.findPlayer(name);

        if (player == null) {
            emailService.sendAdminEmail(
                    "Number Puzzle Authentication Failure",
                    String.format("Failed to authenticate user %s",name)
            );
        }

        return player;
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
        player = playerRepository.save(player);
        updateCurrentLeader();
        return player;
    }

    @Override
    public Player incrementIncorrectGuessCount(Player player) {
        player = player.playerBuilder().incrementIncorrectGuessCount().build();
        player = playerRepository.save(player);
        updateCurrentLeader();
        return player;
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

    private void updateCurrentLeader() {
        Player newLeader = getPlayers().get(0);
        if (!newLeader.equals(currentLeader)) {
            currentLeader = newLeader;
            emailService.sendPuzzleLeaderChangeEmail(getPlayers());
        }
    }
}
