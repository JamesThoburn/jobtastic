package com.jamesthoburn.jobtastic.auth.register;

import com.jamesthoburn.jobtastic.auth.email.EmailService;
import com.jamesthoburn.jobtastic.auth.token.VerificationToken;
import com.jamesthoburn.jobtastic.auth.token.VerificationTokenRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    @DisplayName("registerUser should hash the password and send a verification email")
    void registerUser_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenRepository.save(any(VerificationToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        registrationService.registerUser(user);

        assertFalse(user.isEnabled());
        assertEquals("encoded-password", user.getPassword());
        verify(userRepository).save(user);
        verify(tokenRepository).save(any(VerificationToken.class));
        verify(emailService).sendVerificationEmail(eq("jane@example.com"), anyString());
    }

    @Test
    @DisplayName("registerUser should reject an already registered email")
    void registerUser_WhenEmailAlreadyExists_ThrowsAuthException() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");

        when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

        assertThrows(AuthException.class, () -> registrationService.registerUser(user));
    }
}
