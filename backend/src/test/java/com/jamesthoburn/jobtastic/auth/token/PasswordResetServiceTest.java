package com.jamesthoburn.jobtastic.auth.token;

import com.jamesthoburn.jobtastic.auth.email.EmailService;
import com.jamesthoburn.jobtastic.exception.AuthException;
import com.jamesthoburn.jobtastic.user.User;
import com.jamesthoburn.jobtastic.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    @Test
    @DisplayName("initiateReset should create a token and send a reset email when the user exists")
    void initiateReset_WhenUserExists_CreatesTokenAndSendsEmail() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(1L);

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        passwordResetService.initiateReset("jane@example.com");

        verify(tokenRepository).deleteByUserId(1L);
        verify(tokenRepository).save(any(PasswordResetToken.class));
        verify(emailService).sendResetEmail(anyString(), anyString(), eq(30L));
    }

    @Test
    @DisplayName("initiateReset should do nothing when the user does not exist")
    void initiateReset_WhenUserDoesNotExist_DoesNothing() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        passwordResetService.initiateReset("missing@example.com");

        verify(tokenRepository, never()).deleteByUserId(1L);
    }

    @Test
    @DisplayName("completeReset should update the password and delete the token for a valid token")
    void completeReset_WhenTokenIsValid_UpdatesPasswordAndDeletesToken() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(1L);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("token");
        resetToken.setUser(user);
        resetToken.setExpiryDate(Instant.now().plusSeconds(600));

        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetToken));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("new-password")).thenReturn("encoded-password");

        passwordResetService.completeReset("raw-token", "new-password");

        assertEquals("encoded-password", user.getPassword());
        verify(userRepository).save(user);
        verify(tokenRepository).delete(resetToken);
    }

    @Test
    @DisplayName("completeReset should reject an expired token")
    void completeReset_WhenTokenIsExpired_ThrowsAuthException() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(1L);
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken("token");
        resetToken.setUser(user);
        resetToken.setExpiryDate(Instant.now().minusSeconds(600));

        when(tokenRepository.findByToken(anyString())).thenReturn(Optional.of(resetToken));

        assertThrows(AuthException.class, () -> passwordResetService.completeReset("raw-token", "new-password"));
        verify(tokenRepository).delete(resetToken);
    }
}
