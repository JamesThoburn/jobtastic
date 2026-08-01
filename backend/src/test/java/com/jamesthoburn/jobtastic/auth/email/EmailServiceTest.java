package com.jamesthoburn.jobtastic.auth.email;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("sendVerificationEmail should send a verification message")
    void sendVerificationEmail_Success() {
        emailService.sendVerificationEmail("jane@example.com", "abc123");

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    @DisplayName("sendResetEmail should send a reset message")
    void sendResetEmail_Success() {
        emailService.sendResetEmail("jane@example.com", "abc123", 30);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage message = messageCaptor.getValue();
        assertEquals("Reset your Jobtastic password", message.getSubject());
        assertTrue(message.getText().contains("http://localhost:5173/reset-password?token=abc123"));
        assertTrue(message.getText().contains("30 minutes"));
    }
}
