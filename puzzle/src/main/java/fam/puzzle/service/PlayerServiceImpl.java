package fam.puzzle.service;

import fam.puzzle.domain.Player;
import fam.puzzle.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final EmailService emailService;
    private final PlayerRepository playerRepository;

    private final Map<Integer,Player> currentLeaders = new HashMap<>();

    public PlayerServiceImpl(EmailService emailService, PlayerRepository playerRepository) {
        this.emailService = emailService;
        this.playerRepository = playerRepository;
    }

    @PostConstruct
    public void init() {
        List<Player> players = getPlayerRankings(3);
        currentLeaders.put(3, (!players.isEmpty() ? players.get(0) : null));
        players = getPlayerRankings(4);
        currentLeaders.put(4, (!players.isEmpty() ? players.get(0) : null));
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
    public List<Player> getPlayerRankings(int puzzleSize) {
        List<Player> players = playerRepository.findAll().stream()
                .filter(player -> (player.hasPlayedGame(puzzleSize)))
                .sorted(Comparator.comparingInt(player -> (player.getGuessCountRatio(puzzleSize) * -1)))
                .collect(Collectors.toList());
        return players;
    }

    @Override
    public Player incrementCheatCount(Player player) {
        player = player.playerBuilder().incrementCheatCount().build();
        return playerRepository.save(player);
    }

    @Override
    public Player incrementCorrectGuessCount(Player player, int size) {
        if (size == 3) {
            player = player.playerBuilder().incrementThreeDigitCorrectGuessCount().build();
        } else if (size == 4) {
            player = player.playerBuilder().incrementFourDigitCorrectGuessCount().build();
        }
        player = playerRepository.save(player);
        updateCurrentLeader(size);
        return player;
    }

    @Override
    public Player incrementIncorrectGuessCount(Player player, int size) {
        if (size == 3) {
            player = player.playerBuilder().incrementThreeDigitIncorrectGuessCount().build();
        } else if (size == 4) {
            player = player.playerBuilder().incrementFourDigitIncorrectGuessCount().build();
        }
        player = playerRepository.save(player);
        updateCurrentLeader(size);
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

    private void updateCurrentLeader(int size) {
        Player newLeader = getPlayerRankings(size).get(0);
        if (!newLeader.equals(currentLeaders.get(size))) {
            currentLeaders.put(size, newLeader);
            emailService.sendPuzzleLeaderChangeEmail(getPlayerRankings(size), size);
        }
    }
}
