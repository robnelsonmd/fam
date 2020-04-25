package fam.messaging.email;

public interface EmailService {
    void sendEmail(String subject, String text, String to);
    void sendEmail(String subject, String text, String to, String[] bcc);
}
