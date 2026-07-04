package com.jamesthoburn.jobtastic.user;

import com.jamesthoburn.jobtastic.auth.email.EmailService;
import com.jamesthoburn.jobtastic.auth.token.VerificationToken;
import com.jamesthoburn.jobtastic.auth.token.VerificationTokenRepository;
import com.jamesthoburn.jobtastic.exception.AuthException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public UserResponse updateProfile(User user, UpdateProfileRequest request) {
        if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
            throw new AuthException("Email is already registered");
        }

        boolean emailChanged = !user.getEmail().equals(request.getEmail());

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        if (emailChanged) {
            user.setEnabled(false);
            String tokenString = UUID.randomUUID().toString();
            VerificationToken token = new VerificationToken();
            token.setToken(tokenString);
            token.setUser(user);
            token.setExpiryDate(Instant.now().plus(24, ChronoUnit.HOURS));
            verificationTokenRepository.save(token);
            emailService.sendVerificationEmail(user.getEmail(), tokenString);
        }

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Transactional
    public void changePassword(User user, UpdatePasswordRequest request) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AuthException("Current password is incorrect");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AuthException("New passwords do not match");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );
    }
}
