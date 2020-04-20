package fam.puzzle.service;


import fam.puzzle.domain.Player;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {
    private final ExecutorService executorService;
    private final JavaMailSender javaMailSender;
    private final String primaryEmailAddress;

    public EmailServiceImpl(JavaMailSender javaMailSender,
                            String primaryEmailAddress
    ) {
        this.executorService = Executors.newSingleThreadExecutor();
        this.javaMailSender = javaMailSender;
        this.primaryEmailAddress = primaryEmailAddress;
    }

    @Override
    public void sendPuzzleLeaderChangeEmail(List<Player> players) {
        SimpleMailMessage message = getPlayerRankingEmailMessage(players);
        executorService.submit(() -> javaMailSender.send(message));
    }

    private SimpleMailMessage getPlayerRankingEmailMessage(List<Player> players) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(primaryEmailAddress);
        message.setSubject("Number Puzzle Rankings - New Leader!");
        message.setBcc(getPlayerRankingEmailMessageBccAddresses(players));
        message.setText(getPlayerRankingEmailMessageBody(players));

        return message;
    }

    private String[] getPlayerRankingEmailMessageBccAddresses(List<Player> players) {
        String[] bccAddresses = getPlayersWithEmailEnabled(players).stream()
                .map(Player::getEmailAddress)
                .toArray(String[]::new);

        return bccAddresses;
    }

    private String getPlayerRankingEmailMessageBody(List<Player> players) {
        StringBuilder builder = new StringBuilder();
        builder.append("There is a new leader in the Number Puzzle Game!");
        builder.append(System.lineSeparator()).append(System.lineSeparator());

        builder.append("New Rankings:").append(System.lineSeparator());
        for (int i = 0; i < players.size(); i++) {
            builder.append(String.format("%d) %s%s",i+1,players.get(i),System.lineSeparator()));
        }

        return builder.toString();
    }

    private List<Player> getPlayersWithEmailEnabled(List<Player> players) {
        return players.stream()
                .filter(Player::isReceiveEmails)
                .collect(Collectors.toList());
    }
}


