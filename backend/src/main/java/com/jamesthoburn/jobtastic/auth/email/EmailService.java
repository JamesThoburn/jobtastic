package com.jamesthoburn.jobtastic.auth.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    
    @Value("${application.frontend.url}")
    private String frontendUrl;
    
    @Value("${application.backend.url}")
    private String backendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String verificationUrl = backendUrl + "/api/v1/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Verify your Jobtastic account");
        message.setText("Click the link to verify your account: " + verificationUrl);

        mailSender.send(message);
    }

    public void sendResetEmail(String toEmail, String rawToken, long expiryMinutes) {
        String resetLink = frontendUrl + "/reset-password?token=" + rawToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your Jobtastic password");
        message.setText("Click here to reset your password: " + resetLink +
                "\n\nThis link expires in " + expiryMinutes + " minutes.");
        mailSender.send(message);
    }
}
