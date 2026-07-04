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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private RegistrationService registrationService;

    @Mock
    private VerificationTokenRepository tokenRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private CookieUtils cookieUtils;

    @Mock
    private UserRepository userRepository;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(registrationService, tokenRepository, authenticationManager, jwtService, refreshTokenService, cookieUtils, userRepository);
    }

    @Test
    @DisplayName("signup should delegate registration and return a success response")
    void signup_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");

        ResponseEntity<Map<String, String>> response = authController.signup(user);

        verify(registrationService).registerUser(user);
        assertEquals(200, response.getStatusCode().value());
        assertEquals("Registration successful. Please check your email to verify your account.", response.getBody().get("message"));
    }

    @Test
    @DisplayName("verify should enable the user and redirect when the token is still valid")
    void verify_WhenTokenIsValid_EnablesUserAndRedirects() throws Exception {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("abc123");
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(Instant.now().plusSeconds(3600));
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(tokenRepository.findByToken("abc123")).thenReturn(Optional.of(verificationToken));

        authController.verify("abc123", response);

        assertTrue(user.isEnabled());
        verify(tokenRepository).delete(verificationToken);
        assertEquals("http://localhost:5173/login?verified=true", response.getRedirectedUrl());
    }

    @Test
    @DisplayName("verify should reject an expired token")
    void verify_WhenTokenIsExpired_ThrowsAuthException() {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken("expired");
        verificationToken.setExpiryDate(Instant.now().minusSeconds(60));
        verificationToken.setUser(new User());

        when(tokenRepository.findByToken("expired")).thenReturn(Optional.of(verificationToken));

        assertThrows(AuthException.class, () -> authController.verify("expired", new MockHttpServletResponse()));
    }

    @Test
    @DisplayName("login should issue access and refresh cookies")
    void login_Success() {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "email", "jane@example.com");
        ReflectionTestUtils.setField(request, "password", "password");
        Authentication authentication = new TestingAuthenticationToken("jane@example.com", "password", java.util.List.of());
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtService.generateToken("jane@example.com")).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken("jane@example.com")).thenReturn("refresh-token");
        when(cookieUtils.createAccessCookie("access-token")).thenReturn(ResponseCookie.from("access_token", "access-token").build());
        when(cookieUtils.createRefreshCookie("refresh-token")).thenReturn(ResponseCookie.from("refresh_token", "refresh-token").build());

        ResponseEntity<?> result = authController.login(request, response);

        assertEquals(200, result.getStatusCode().value());
        assertFalse(response.getHeaders("Set-Cookie").isEmpty());
        assertTrue(response.getHeaders("Set-Cookie").stream().anyMatch(value -> value.contains("access_token")));
        assertTrue(response.getHeaders("Set-Cookie").stream().anyMatch(value -> value.contains("refresh_token")));
    }

    @Test
    @DisplayName("refresh should mint a new access token when the refresh token is valid")
    void refresh_WhenTokenIsValid_ReturnsOk() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(1L);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);

        request.setCookies(new jakarta.servlet.http.Cookie("refresh_token", "refresh-token"));
        when(cookieUtils.extractCookieValue(any(HttpServletRequest.class), eq("refresh_token"))).thenReturn("refresh-token");
        when(refreshTokenService.findByToken("refresh-token")).thenReturn(Optional.of(refreshToken));
        when(refreshTokenService.verifyExpiration(refreshToken)).thenReturn(refreshToken);
        when(jwtService.generateToken("jane@example.com")).thenReturn("new-access-token");
        when(cookieUtils.createAccessCookie("new-access-token")).thenReturn(ResponseCookie.from("access_token", "new-access-token").build());

        ResponseEntity<?> result = authController.refresh(request, response);

        assertEquals(200, result.getStatusCode().value());
        assertTrue(response.getHeaders("Set-Cookie").stream().anyMatch(value -> value.contains("access_token")));
    }

    @Test
    @DisplayName("refresh should reject missing refresh tokens")
    void refresh_WhenTokenIsMissing_ThrowsAuthException() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        when(cookieUtils.extractCookieValue(any(HttpServletRequest.class), eq("refresh_token"))).thenReturn(null);

        assertThrows(AuthException.class, () -> authController.refresh(request, new MockHttpServletResponse()));
    }

    @Test
    @DisplayName("logout should clear the auth cookies")
    void logout_Success() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(cookieUtils.cleanAccessCookie()).thenReturn(ResponseCookie.from("access_token", "").build());
        when(cookieUtils.cleanRefreshCookie()).thenReturn(ResponseCookie.from("refresh_token", "").build());

        ResponseEntity<?> result = authController.logout(response);

        assertEquals(200, result.getStatusCode().value());
        assertTrue(response.getHeaders("Set-Cookie").stream().anyMatch(value -> value.contains("access_token")));
        assertTrue(response.getHeaders("Set-Cookie").stream().anyMatch(value -> value.contains("refresh_token")));
    }

    @Test
    @DisplayName("getCurrentUser should return the authenticated user's profile")
    void getCurrentUser_WhenAuthenticated_ReturnsProfile() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setCreatedAt(LocalDateTime.now());
        Authentication authentication = new TestingAuthenticationToken("jane@example.com", null, java.util.List.of());

        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        ResponseEntity<?> response = authController.getCurrentUser(authentication);

        assertEquals(200, response.getStatusCode().value());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("jane@example.com", body.get("email"));
        assertEquals("Jane", body.get("firstName"));
    }

    @Test
    @DisplayName("getCurrentUser should reject unauthenticated requests")
    void getCurrentUser_WhenUnauthenticated_ReturnsUnauthorized() {
        ResponseEntity<?> response = authController.getCurrentUser(null);

        assertEquals(401, response.getStatusCode().value());
        assertEquals("Not authenticated", response.getBody());
    }
}
