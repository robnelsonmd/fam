package fam.puzzle.service;

import fam.core.util.StringUtil;
import fam.messaging.email.EmailService;
import fam.messaging.text.CellCarrier;
import fam.messaging.text.TextService;
import fam.puzzle.domain.Player;
import fam.puzzle.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    private final EmailService emailService;
    private final TextService textService;
    private final PlayerRepository playerRepository;
    private final String primaryEmailAddress;
    private final String primaryTextAddress;

    private final Map<Integer,Player> currentLeaders = new HashMap<>();

    public PlayerServiceImpl(
            EmailService emailService,
            TextService textService,
            PlayerRepository playerRepository,
            String primaryEmailAddress,
            String primaryTextAddress) {
        this.emailService = emailService;
        this.textService = textService;
        this.playerRepository = playerRepository;
        this.primaryEmailAddress = primaryEmailAddress;
        this.primaryTextAddress = primaryTextAddress;
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
            emailService.sendEmail(
                    "Number Puzzle Authentication Failure",
                    String.format("Failed to authenticate user %s",name),
                    primaryEmailAddress
            );
        }

        return player;
    }

    @Override
    public List<Player> getPlayers() {
        return playerRepository.findAll();
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
        player.setCheatCount(player.getCheatCount() + 1);
        return playerRepository.save(player);
    }

    @Override
    public Player incrementCorrectGuessCount(Player player, int size) {
        player.setCorrectGuessCount(size, (player.getCorrectGuessCount(size) + 1));
        player = playerRepository.save(player);
        updateCurrentLeader(size);
        return player;
    }

    @Override
    public Player incrementIncorrectGuessCount(Player player, int size) {
        player.setIncorrectGuessCount(size, (player.getIncorrectGuessCount(size) + 1));
        player = playerRepository.save(player);
        updateCurrentLeader(size);
        return player;
    }

    @Override
    public Player incrementShowAnswerCount(Player player) {
        player.setShowAnswerCount(player.getShowAnswerCount() + 1);
        return playerRepository.save(player);
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Player updateCellInfo(Player player, CellCarrier cellCarrier, String cellNumber) {
        player.setCellCarrier(cellCarrier);
        player.setCellNumber(cellNumber);
        player.setTextAddress(textService.getTextAddress(cellCarrier, cellNumber));
        return savePlayer(player);
    }

    private String[] getPlayerEmailAddresses() {
        return getPlayers().stream()
        .filter(Player::isReceiveEmails)
        .map(Player::getEmailAddress)
        .filter(PlayerServiceImpl::isValidEmailAdress)
        .toArray(String[]::new);
    }

    private String getPlayerRankingEmailMessageBody(List<Player> players, String puzzleSizeString) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("There is a new leader in the %s Digit Number Puzzle Game!",puzzleSizeString));
        builder.append(System.lineSeparator()).append(System.lineSeparator());

        builder.append("New Rankings:").append(System.lineSeparator());
        for (int i = 0; i < players.size(); i++) {
            builder.append(String.format("%d) %s%s",i+1,players.get(i),System.lineSeparator()));
        }

        return builder.toString();
    }

    private String getPlayerRankingEmailSubject(String puzzleSizeString) {
        return String.format("%s Digit Number Puzzle Rankings - New Leader!", puzzleSizeString);
    }

    private String getPlayerRankingTextMessageBody(List<Player> players, String puzzleSizeString) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("New %s Digit Number Puzzle Rankings!", puzzleSizeString));
        builder.append(System.lineSeparator());

        builder.append("New Rankings:").append(System.lineSeparator());
        for (int i = 0; i < players.size(); i++) {
            builder.append(String.format("%d) %s%s",i+1,players.get(i),System.lineSeparator()));
        }

        return builder.toString();
    }

    private String getPuzzleSizeString(int size) {
        switch (size) {
            case 3:
                return "Three";

            case 4:
                return "Four";

            default:
                throw new IllegalArgumentException("Invalid puzzle size: " + size);
        }
    }

    private String[] getPlayerTextAddresses() {
        return getPlayers().stream()
                .filter(Player::isReceiveTexts)
                .map(Player::getTextAddress)
                .filter(PlayerServiceImpl::isValidEmailAdress)
                .toArray(String[]::new);
    }

    private static boolean isValidEmailAdress(String emailAddress) {
        return !StringUtil.isEmptyString(emailAddress);
    }

    private void sendPuzzleLeaderChangeEmail(List<Player> players, int puzzleSize) {
        String puzzleSizeString = getPuzzleSizeString(puzzleSize);
        String subject = getPlayerRankingEmailSubject(puzzleSizeString);
        String text = getPlayerRankingEmailMessageBody(players, puzzleSizeString);
        String[] bcc = getPlayerEmailAddresses();
        emailService.sendEmail(subject, text, primaryEmailAddress, bcc);
    }

    private void sendPuzzleLeaderChangeNotification(List<Player> players, int puzzleSize) {
        sendPuzzleLeaderChangeEmail(players, puzzleSize);
        sendPuzzleLeaderChangeText(players, puzzleSize);
    }

    private void sendPuzzleLeaderChangeText(List<Player> players, int puzzleSize) {
        String puzzleSizeString = getPuzzleSizeString(puzzleSize);
        String text = getPlayerRankingTextMessageBody(players, puzzleSizeString);
        String[] bcc = getPlayerTextAddresses();
        textService.sendText(null, text, primaryTextAddress, bcc);
    }

    private void updateCurrentLeader(int size) {
        List<Player> playerRankings = getPlayerRankings(size);
        Player newLeader = !playerRankings.isEmpty() ? playerRankings.get(0) : null;
        if (!Objects.equals(currentLeaders.get(size), newLeader)) {
            currentLeaders.put(size, newLeader);
            sendPuzzleLeaderChangeNotification(getPlayerRankings(size), size);
        }
    }
}
