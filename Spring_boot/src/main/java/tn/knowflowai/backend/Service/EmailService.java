package tn.knowflowai.backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailService(
            JavaMailSender mailSender
    ) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(
            String recipient,
            String token
    ) {

        String resetLink =
                frontendUrl
                + "/reset-password?token="
                + token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(recipient);

        message.setSubject(
                "KnowFlow AI - Password Reset"
        );

        message.setText(
            "Hello,\n\n"
            + "We received a request to reset your password.\n\n"
            + "Click the following link to reset your password:\n\n"
            + resetLink
            + "\n\n"
            + "This link will expire in 15 minutes.\n\n"
            + "If you did not request a password reset, "
            + "you can safely ignore this email.\n\n"
            + "KnowFlow AI"
        );

        mailSender.send(message);
    }
}