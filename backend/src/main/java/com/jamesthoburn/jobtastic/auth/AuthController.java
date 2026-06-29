package com.jamesthoburn.jobtastic.auth;

import com.jamesthoburn.jobtastic.auth.jwt.JwtService;
import com.jamesthoburn.jobtastic.auth.register.RegistrationService;
import com.jamesthoburn.jobtastic.auth.token.RefreshToken;
import com.jamesthoburn.jobtastic.auth.token.RefreshTokenService;
import com.jamesthoburn.jobtastic.auth.token.VerificationToken;
import com.jamesthoburn.jobtastic.auth.token.VerificationTokenRepository;
import com.jamesthoburn.jobtastic.exception.AuthException;
import com.jamesthoburn.jobtastic.exception.ResourceNotFoundException;
import com.jamesthoburn.jobtastic.user.User;
import com.jamesthoburn.jobtastic.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegistrationService registrationService;
    private final VerificationTokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtils cookieUtils;
    private final UserRepository userRepository;

    public AuthController(RegistrationService registrationService,
                          VerificationTokenRepository tokenRepository,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService,
                          CookieUtils cookieUtils,
                          UserRepository userRepository) {
        this.registrationService = registrationService;
        this.tokenRepository = tokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.cookieUtils = cookieUtils;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Valid @RequestBody User user) {
        registrationService.registerUser(user);
        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email to verify your account."));
    }

    @GetMapping("/verify")
    public void verify(@RequestParam String token, HttpServletResponse response) throws IOException {
        VerificationToken vToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Verification token not found"));

        if (vToken.getExpiryDate().isBefore(Instant.now())) {
            throw new AuthException("Token expired");
        }

        User user = vToken.getUser();
        user.setEnabled(true);
        tokenRepository.delete(vToken);

        response.sendRedirect("http://localhost:5173/login?verified=true");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String userEmail = auth.getName();
        String accessToken = jwtService.generateToken(userEmail);
        String refreshToken = refreshTokenService.createRefreshToken(userEmail);

        response.addHeader("Set-Cookie", cookieUtils.createAccessCookie(accessToken).toString());
        response.addHeader("Set-Cookie", cookieUtils.createRefreshCookie(refreshToken).toString());

        return ResponseEntity.ok(Map.of("message", "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = cookieUtils.extractCookieValue(request, "refresh_token");

        return refreshTokenService.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user.getEmail());
                    response.addHeader("Set-Cookie", cookieUtils.createAccessCookie(newAccessToken).toString());
                    return ResponseEntity.ok("Token refreshed");
                })
                .orElseThrow(() -> new AuthException("Refresh token not found"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        response.addHeader("Set-Cookie", cookieUtils.cleanAccessCookie().toString());
        response.addHeader("Set-Cookie", cookieUtils.cleanRefreshCookie().toString());
        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User could not be found with email: " + email));

        return ResponseEntity.ok(Map.of(
                "email", email,
                "firstName", user.getFirstName(),
                "lastName", user.getLastName(),
                "createdAt", user.getCreatedAt()
        ));
    }

}