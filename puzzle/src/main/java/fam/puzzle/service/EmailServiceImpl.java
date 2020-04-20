package fam.puzzle.service;


import fam.puzzle.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

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
    public void sendAdminEmail(String subject, String text) {
        sendMessage(subject, text, primaryEmailAddress);
    }

    @Override
    public void sendPuzzleLeaderChangeEmail(List<Player> players) {        SimpleMailMessage message = new SimpleMailMessage();
        String subject = "Number Puzzle Rankings - New Leader!";
        String text = getPlayerRankingEmailMessageBody(players);
        String to = primaryEmailAddress;
        String[] bcc = getPlayerRankingEmailMessageBccAddresses(players);

        executorService.submit(() -> sendMessage(subject,text,to,bcc));
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

    private void sendMessage(String subject, String text, String to) {
        sendMessage(subject, text, to, null);
    }

    private void sendMessage(String subject, String text, String to, String[] bcc) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(text);
        message.setTo(to);

        if (bcc != null) {
            message.setBcc(bcc);
        }

        sendMessage(message);
    }

    private void sendMessage(SimpleMailMessage message) {
        try {
            String subject = message.getSubject();
            String to = (message.getTo() == null) ? "Unknown" : Arrays.asList(message.getTo()).toString();
            String bcc = (message.getBcc() == null) ? "N/A" : Arrays.asList(message.getBcc()).toString();
            LOG.info(String.format("Sending message (%s) to (%s) BCC (%s)",subject,to,bcc));
            javaMailSender.send(message);
        } catch (MailException e) {
            LOG.error("Exception occurred while sending email",e);
        }
    }
}


