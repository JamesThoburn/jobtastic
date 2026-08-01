package com.jamesthoburn.jobtastic.auth.token;

import com.jamesthoburn.jobtastic.auth.email.EmailService;
import com.jamesthoburn.jobtastic.exception.AuthException;
import com.jamesthoburn.jobtastic.user.User;
import com.jamesthoburn.jobtastic.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    private static final long EXPIRY_MINUTES = 30;

    PasswordResetService(UserRepository userRepository, PasswordResetTokenRepository tokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void initiateReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();

        // Remove existing tokens for this user
        tokenRepository.deleteByUserId(user.getId());

        String rawToken = generateRawToken();
        String hashedToken = hashToken(rawToken);

        PasswordResetToken entity = new PasswordResetToken();
        entity.setToken(hashedToken);
        entity.setUser(user);
        entity.setCreatedAt(Instant.now());
        entity.setExpiryDate(Instant.now().plus(EXPIRY_MINUTES, ChronoUnit.MINUTES));
        tokenRepository.save(entity);

        emailService.sendResetEmail(user.getEmail(), rawToken, EXPIRY_MINUTES);
    }

    @Transactional
    public void completeReset(String rawToken, String newPassword) {
        String hashedToken = hashToken(rawToken);
        PasswordResetToken entity = tokenRepository.findByToken(hashedToken)
                .orElseThrow(() -> new AuthException("Invalid or expired token"));

        if (entity.getExpiryDate().isBefore(Instant.now())) {
            tokenRepository.delete(entity);
            throw new AuthException("Invalid or expired token");
        }

        User user = userRepository.findById(entity.getUser().getId())
                .orElseThrow(() -> new AuthException("Invalid or expired token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(entity);
    }

    private String generateRawToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
