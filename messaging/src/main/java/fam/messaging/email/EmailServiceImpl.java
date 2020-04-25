package fam.messaging.email;

import fam.core.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EmailServiceImpl implements EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final ExecutorService executorService;
    private final JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.executorService = Executors.newScheduledThreadPool(5);
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String subject, String text, String to) {
        sendEmail(subject, text, to, null);
    }

    @Override
    public void sendEmail(String subject, String text, String to, String[] bcc) {
        if (StringUtil.isEmptyString(subject) && StringUtil.isEmptyString(text)) {
            throw new IllegalArgumentException("Unable to send email - subject and text cannot both be empty");
        }


        if (StringUtil.isEmptyString(to) && (bcc == null)) {
            throw new IllegalArgumentException("Unable to send email - to and bcc cannot both be empty");
        }

        sendMessage(subject, text, to, bcc);
    }

    private void sendMessage(String subject, String text, String to, String[] bcc) {
        final SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(subject);
        message.setText(text);
        message.setTo(to);

        if (bcc != null) {
            message.setBcc(bcc);
        }

        executorService.submit(() -> sendMessage(message));
    }

    private void sendMessage(SimpleMailMessage message) {
        try {
            String subject = message.getSubject();
            String to = (message.getTo() == null) ? "Unknown" : Arrays.asList(message.getTo()).toString();
            String bcc = (message.getBcc() == null) ? "N/A" : Arrays.asList(message.getBcc()).toString();
            LOG.info(String.format("Sending message (%s) to (%s) BCC (%s)",subject,to,bcc));
            javaMailSender.send(message);
        } catch (MailException e) {
            LOG.error("Exception occurred while sending email", e);
        }
    }
}
