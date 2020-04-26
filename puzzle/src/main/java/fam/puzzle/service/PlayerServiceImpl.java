package fam.puzzle.service;

import fam.core.executor.TaskScheduler;
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
import javax.annotation.PreDestroy;
import java.time.DayOfWeek;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerServiceImpl.class);

    private final EmailService emailService;
    private final TaskScheduler taskScheduler;
    private final TextService textService;
    private final PlayerRepository playerRepository;
    private final String primaryEmailAddress;
    private final String primaryTextAddress;

    private final Map<Integer,Player> currentLeaders = new HashMap<>();

    public PlayerServiceImpl(
            EmailService emailService,
            TaskScheduler taskScheduler,
            TextService textService,
            PlayerRepository playerRepository,
            String primaryEmailAddress,
            String primaryTextAddress) {
        this.emailService = emailService;
        this.taskScheduler = taskScheduler;
        this.textService = textService;
        this.playerRepository = playerRepository;
        this.primaryEmailAddress = primaryEmailAddress;
        this.primaryTextAddress = primaryTextAddress;
    }

    @PreDestroy
    public void destroy() {
        taskScheduler.shutdown();
    }

    @PostConstruct
    public void init() {
        initializeCurrentLeaders();
        initializeWeeklyPuzzleReset();
    }

    @Override
    public Player createPlayer(String name, boolean receiveEmails,
                               String emailAddress, boolean receiveTexts,
                               CellCarrier cellCarrier, String cellNumber) {
        LOG.info(String.format("Creating player %s", name));

        Player player = new Player(name);
        player.setReceiveEmails(receiveEmails);
        player.setEmailAddress(emailAddress);
        player.setReceiveTexts(receiveTexts);
        player.setCellCarrier(cellCarrier);
        player.setCellNumber(cellNumber);
        player.setTextAddress(textService.getTextAddress(cellCarrier, cellNumber));

        sendWelcomeMessage(player);

        return playerRepository.save(player);
    }

    @Override
    public Player getPlayer(String name) {
        Player player = playerRepository.findPlayer(name);

        if (player == null) {
            emailService.sendEmail(
                    "Number Puzzle Authentication Failure",
                    String.format("Failed to authenticate user %s", name),
                    primaryEmailAddress
            );
        }

        return player;
    }

    @Override
    public List<Player> getPlayers() {
        return playerRepository.findAll().stream()
                .filter(Player::isUser)
                .sorted(Comparator.comparing(Player::getName))
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
        LOG.info(String.format("Incrementing cheat count for player %s from %d to %d", player.getName(), previousCheatCount, newCheatCount));
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
        builder.append(String.format("There is a new leader in the %s Digit Number Puzzle Game!", puzzleSizeString));
        builder.append(System.lineSeparator()).append(System.lineSeparator());

        builder.append("New Rankings:").append(System.lineSeparator());
        for (int i = 0; i < players.size(); i++) {
            builder.append(String.format("%d) %s%s", i + 1, players.get(i), System.lineSeparator()));
        }

        return builder.toString();
    }

    private String getPlayerRankingEmailSubject(String puzzleSizeString) {
        return String.format("%s Digit Number Puzzle Rankings - New Leader!", puzzleSizeString);
    }

    private String getPlayerRankingsString(List<Player> players) {
        StringBuilder builder = new StringBuilder();

        builder.append("Rankings:").append(System.lineSeparator());
        for (int i = 0; i < players.size(); i++) {
            builder.append(String.format("%d) %s%s", i + 1, players.get(i), System.lineSeparator()));
        }

        return builder.toString();
    }

    private String getPlayerRankingTextMessageBody(List<Player> players, String puzzleSizeString) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("New %s Digit Number Puzzle Rankings!", puzzleSizeString));
        builder.append(System.lineSeparator());
        builder.append(getPlayerRankingsString(players));
        return builder.toString();
    }

    private String[] getPlayerTextAddresses() {
        return getPlayers().stream()
                .filter(Player::isReceiveTexts)
                .map(Player::getTextAddress)
                .filter(PlayerServiceImpl::isValidEmailAdress)
                .toArray(String[]::new);
    }

    private String getPuzzleResetEmailMessageBody(String puzzleSizeString, String playerRankingsString) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("The %s digit number puzzle has been reset for the new week. ", puzzleSizeString.toLowerCase()));
        builder.append("Last week's final standings were:");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append(playerRankingsString);
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("Congrats to last week's winner, and good luck this week!");
        return builder.toString();
    }

    private String getPuzzleResetTextMessageBody(String puzzleSizeString, String playerRankingsString) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s Digit Number Puzzle Has Been Reset!", puzzleSizeString));
        builder.append(System.lineSeparator());
        builder.append("Last week's rankings were:").append(System.lineSeparator());
        builder.append(playerRankingsString);
        return builder.toString();
    }

    private String getWelcomeEmailMessageBody(Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Hi %s!", player.getName()));
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("Welcome to the Number Puzzle Game! You can access the game at this URL: http://34.71.172.138/");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("There are both three and four-digit puzzle games, and player rankings are tracked for each type.");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("You may optionally receive emails and/or texts whenever the rankings change for each puzzle type.");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append("Puzzle rankings will be reset each week.");
        builder.append(System.lineSeparator()).append(System.lineSeparator());
        builder.append(String.format("To log in, simply enter your name (%s).  Good luck and have fun!", player.getName()));

        return builder.toString();
    }

    private String getWelcomeTextMessageBody(Player player) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Hi %s! ", player.getName()));
        builder.append("Welcome to the Number Puzzle Game! You can access the game at this URL: http://34.71.172.138/");
        builder.append(String.format(" To log in, simply enter your name (%s).  Good luck and have fun!", player.getName()));

        return builder.toString();
    }

    private void initializeCurrentLeaders() {
        currentLeaders.put(3, getPlayerRankings(3).stream().findFirst().orElse(null));
        currentLeaders.put(4, getPlayerRankings(4).stream().findFirst().orElse(null));
    }

    private void initializeWeeklyPuzzleReset() {
        taskScheduler.scheduleWeeklyTask(this::resetPuzzleRankings, ZoneOffset.UTC, DayOfWeek.MONDAY, 4, 0, 0);
    }

    private static boolean isValidEmailAdress(String emailAddress) {
        return !StringUtil.isEmptyString(emailAddress);
    }

    private void resetPuzzleRankings() {
        resetPuzzleRankings(3);
        resetPuzzleRankings(4);
    }

    private void resetPuzzleRankings(int puzzleSize) {
        List<Player> players = getPlayerRankings(puzzleSize);

        if (players.isEmpty()) {
            return;
        }

        String playerRankingsString = getPlayerRankingsString(players);

        players.forEach(player -> {
            player.setCorrectGuessCount(puzzleSize, 0);
            player.setIncorrectGuessCount(puzzleSize, 0);
            savePlayer(player);
        });

        sendPuzzleResetNotification(puzzleSize, playerRankingsString);
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

    private void sendPuzzleResetNotification(int puzzleSize, String playerRankingsString) {
        String puzzleSizeString = PuzzleUtil.getPuzzleSizeString(puzzleSize);
        sendPuzzleResetEmail(puzzleSizeString, playerRankingsString);
        sendPuzzleResetText(puzzleSizeString, playerRankingsString);
    }

    private void sendPuzzleResetEmail(String puzzleSizeString, String playerRankingsString) {
        String subject = String.format("%s Digit Puzzle - Weekly Results", puzzleSizeString);
        String text = getPuzzleResetEmailMessageBody(puzzleSizeString, playerRankingsString);
        String[] bcc = getPlayerEmailAddresses();
        emailService.sendEmail(subject, text, primaryEmailAddress, bcc);
    }

    private void sendPuzzleResetText(String puzzleSizeString, String playerRankingsString) {
        String text = getPuzzleResetTextMessageBody(puzzleSizeString, playerRankingsString);
        String[] bcc = getPlayerTextAddresses();
        textService.sendText(null, text, primaryTextAddress, bcc);
    }

    private void sendWelcomeEmail(Player player) {
        if (!player.isReceiveEmails() || !isValidEmailAdress(player.getEmailAddress())) {
            return;
        }

        String subject = "Welcome to the Number Puzzle App!";
        String text = getWelcomeEmailMessageBody(player);
        emailService.sendEmail(subject, text, player.getEmailAddress());
    }

    private void sendWelcomeMessage(Player player) {
        sendWelcomeEmail(player);
        sendWelcomeText(player);
    }

    private void sendWelcomeText(Player player) {
        if (!player.isReceiveTexts() || !isValidEmailAdress(player.getTextAddress())) {
            return;
        }

        String text = getWelcomeTextMessageBody(player);
        emailService.sendEmail(null, text, player.getTextAddress());
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
