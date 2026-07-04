package com.jamesthoburn.jobtastic.auth.token;

import com.jamesthoburn.jobtastic.exception.AuthException;
import com.jamesthoburn.jobtastic.user.User;
import com.jamesthoburn.jobtastic.user.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("createRefreshToken should persist a refresh token for the user")
    void createRefreshToken_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        RefreshToken savedToken = new RefreshToken();
        savedToken.setToken("generated-token");
        savedToken.setUser(user);

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(savedToken);

        String token = refreshTokenService.createRefreshToken("jane@example.com");

        assertEquals("generated-token", token);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("verifyExpiration should delete and reject an expired token")
    void verifyExpiration_WhenTokenExpired_ThrowsAuthException() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(60));

        assertThrows(AuthException.class, () -> refreshTokenService.verifyExpiration(token));
        verify(refreshTokenRepository).delete(token);
    }
}
