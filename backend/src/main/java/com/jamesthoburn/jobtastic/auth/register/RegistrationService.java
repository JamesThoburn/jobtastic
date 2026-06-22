package com.jamesthoburn.jobtastic.auth.register;

import com.jamesthoburn.jobtastic.auth.email.EmailService;
import com.jamesthoburn.jobtastic.auth.token.VerificationToken;
import com.jamesthoburn.jobtastic.auth.token.VerificationTokenRepository;
import com.jamesthoburn.jobtastic.exception.AuthException;
import com.jamesthoburn.jobtastic.user.User;
import com.jamesthoburn.jobtastic.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository userRepository, VerificationTokenRepository tokenRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new AuthException("Email is already registered");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        userRepository.save(user);

        String tokenString = UUID.randomUUID().toString();
        VerificationToken token = new VerificationToken();
        token.setToken(tokenString);
        token.setUser(user);
        token.setExpiryDate(Instant.now().plus(24, ChronoUnit.HOURS));

        tokenRepository.save(token);

        emailService.sendVerificationEmail(user.getEmail(), tokenString);
    }

}
