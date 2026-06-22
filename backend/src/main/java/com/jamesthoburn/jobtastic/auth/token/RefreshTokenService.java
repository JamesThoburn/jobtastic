package com.jamesthoburn.jobtastic.auth.token;

import com.jamesthoburn.jobtastic.exception.AuthException;
import com.jamesthoburn.jobtastic.user.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    public String createRefreshToken(String email) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findByEmail(email).orElseThrow());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(604800)); // 7 days
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken.getToken();
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new AuthException("Refresh token was expired. Please make a new sign-in request");
        }
        return token;
    }
}
