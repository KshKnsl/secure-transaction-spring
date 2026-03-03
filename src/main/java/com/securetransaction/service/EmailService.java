package com.securetransaction.service;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

// All methods are @Async so they run in a background thread
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendRegistrationEmail(String toEmail, String name) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("Backend Ledger <" + fromEmail + ">");
            msg.setTo(toEmail);
            msg.setSubject("Welcome to Backend Ledger!");
            msg.setText("""
                    Hello %s,

                    Thank you for registering at Backend Ledger. We're excited to have you on board!

                    Best regards,
                    The Backend Ledger Team
                    """.formatted(name));
            mailSender.send(msg);
            System.out.println("Registration email sent to " + toEmail);
        } catch (MailException e) {
            System.err.println("Failed to send registration email to " + toEmail + ": " + e.getMessage());
        }
    }

    @Async
    public void sendTransactionEmail(String toEmail, String name, String amount, String toAccount) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("Backend Ledger <" + fromEmail + ">");
            msg.setTo(toEmail);
            msg.setSubject("Transaction Successful!");
            msg.setText("""
                    Hello %s,

                    Your transaction of $%s to account %s was successful.

                    Best regards,
                    The Backend Ledger Team
                    """.formatted(name, amount, toAccount));
            mailSender.send(msg);
            System.out.println("Transaction email sent to " + toEmail);
        } catch (MailException e) {
            System.err.println("Failed to send transaction email to " + toEmail + ": " + e.getMessage());
        }
    }

    @Async
    public void sendTransactionFailureEmail(String toEmail, String name, String amount, String toAccount) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom("Backend Ledger <" + fromEmail + ">");
            msg.setTo(toEmail);
            msg.setSubject("Transaction Failed");
            msg.setText("""
                    Hello %s,

                    We regret to inform you that your transaction of $%s to account %s has failed.
                    Please try again later.

                    Best regards,
                    The Backend Ledger Team
                    """.formatted(name, amount, toAccount));
            mailSender.send(msg);
        } catch (MailException e) {
            System.err.println("Failed to send failure email to " + toEmail + ": " + e.getMessage());
        }
    }
}
