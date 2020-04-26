package fam.puzzle.service;

import fam.core.util.StringUtil;
import fam.messaging.email.EmailService;
import fam.messaging.text.CellCarrier;
import fam.messaging.text.TextService;
import fam.puzzle.domain.Player;
import fam.puzzle.repository.PlayerRepository;
import fam.puzzle.util.PuzzleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerServiceImpl.class);

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
        return playerRepository.findAll().stream()
                .filter(Player::isUser)
                .collect(Collectors.toList());
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
        int previousCheatCount = player.getCheatCount();
        int newCheatCount = previousCheatCount + 1;
        LOG.info(String.format("Incrementing cheat count for player %s from %d to %d",player.getName(),previousCheatCount,newCheatCount));
        player.setCheatCount(newCheatCount);
        return playerRepository.save(player);
    }

    @Override
    public Player incrementCorrectGuessCount(Player player, int size) {
        String puzzleSizeString = PuzzleUtil.getPuzzleSizeString(size).toLowerCase();
        int previousCorrectGuessCount = player.getCorrectGuessCount(size);
        int newCorrectGuessCount = previousCorrectGuessCount + 1;
        LOG.info(String.format("Incrementing %s digit puzzle correct guess count for player %s from %d to %d",
                puzzleSizeString, player.getName(), previousCorrectGuessCount, newCorrectGuessCount));
        player.setCorrectGuessCount(size, newCorrectGuessCount);
        player = playerRepository.save(player);
        updateCurrentLeader(size);
        return player;
    }

    @Override
    public Player incrementIncorrectGuessCount(Player player, int size) {
        String puzzleSizeString = PuzzleUtil.getPuzzleSizeString(size).toLowerCase();
        int previousIncorrectGuessCount = player.getIncorrectGuessCount(size);
        int newIncorrectGuessCount = previousIncorrectGuessCount + 1;
        LOG.info(String.format("Incrementing %s digit puzzle incorrect guess count for player %s from %d to %d",
                puzzleSizeString, player.getName(), previousIncorrectGuessCount, newIncorrectGuessCount));
        player.setIncorrectGuessCount(size, newIncorrectGuessCount);
        player = playerRepository.save(player);
        updateCurrentLeader(size);
        return player;
    }

    @Override
    public Player incrementShowAnswerCount(Player player) {
        int previousShowAnswerCount = player.getShowAnswerCount();
        int newShowAnswerCount = previousShowAnswerCount + 1;
        LOG.info(String.format("Incrementing show answer count for player %s from %d to %d",
                player.getName(), previousShowAnswerCount, newShowAnswerCount));
        player.setShowAnswerCount(player.getShowAnswerCount() + 1);
        return playerRepository.save(player);
    }

    @Override
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    @Override
    public Player updateEmailSettings(Player player, boolean receiveEmails, String emailAddress) {
        LOG.info(String.format("Updating email settings for player %s to %s/%s",
                player.getName(), receiveEmails, emailAddress));
        player.setReceiveEmails(receiveEmails);
        player.setEmailAddress(emailAddress);
        return savePlayer(player);
    }

    @Override
    public Player updateTextSettings(Player player, boolean receiveTexts, CellCarrier cellCarrier, String cellNumber) {
        String textAddress = textService.getTextAddress(cellCarrier, cellNumber);
        LOG.info(String.format("Updating text settings for player %s to %s/%s/%s/%s",
                player.getName(), receiveTexts, cellCarrier, cellNumber, textAddress));
        player.setReceiveTexts(receiveTexts);
        player.setCellCarrier(cellCarrier);
        player.setCellNumber(cellNumber);
        player.setTextAddress(textAddress);
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
        String puzzleSizeString = PuzzleUtil.getPuzzleSizeString(puzzleSize);
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
        String puzzleSizeString = PuzzleUtil.getPuzzleSizeString(puzzleSize);
        String text = getPlayerRankingTextMessageBody(players, puzzleSizeString);
        String[] bcc = getPlayerTextAddresses();
        textService.sendText(null, text, primaryTextAddress, bcc);
    }

    private void updateCurrentLeader(int size) {
        List<Player> playerRankings = getPlayerRankings(size);
        Player newLeader = !playerRankings.isEmpty() ? playerRankings.get(0) : null;
        Player currentLeader = currentLeaders.get(size);
        if (!Objects.equals(currentLeader, newLeader)) {
            currentLeaders.put(size, newLeader);
            sendPuzzleLeaderChangeNotification(getPlayerRankings(size), size);
        }
    }
}
