package fam.puzzle.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {
    @Bean
    public JavaMailSender javaMailSender(
            @Value("${spring.mail.host}") String emailServer,
            @Value("${spring.mail.port}") int emailServerPort,
            @Value("${spring.mail.username}") String emailUsername,
            @Value("${spring.mail.password}") String emailPassword
    ) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(emailServer);
        mailSender.setPort(emailServerPort);

        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

    @Bean
    public String primaryEmailAddress(@Value("${spring.mail.username}") String emailUsername) {
        return emailUsername;
    }
}
