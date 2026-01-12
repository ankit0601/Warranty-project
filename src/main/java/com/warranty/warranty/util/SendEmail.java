package com.warranty.warranty.util;

import com.warranty.warranty.components.MailComponent;
import jakarta.mail.internet.MimeMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendEmail {
    private static final Logger logger = LogManager.getLogger(SendEmail.class);

    private final JavaMailSender mailSender;
    private final MailComponent mailComponent;

    public SendEmail(JavaMailSender mailSender, MailComponent mailComponent) {
        this.mailSender = mailSender;
        this.mailComponent = mailComponent;
    }

    public void sendEmailOTP(String to, String body, String subject) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // HTML email
            helper.setFrom(mailComponent.getUsername());

            logger.info("Sending email to: {}", to);
            mailSender.send(message);
            logger.info("Email sent successfully.");
        } catch (Exception e) {
            logger.error("Error while sending email to {}: {}", to, e.getMessage(), e);
        }
    }
}
