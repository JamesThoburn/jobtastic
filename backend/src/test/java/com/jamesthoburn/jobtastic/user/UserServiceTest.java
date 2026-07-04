package com.jamesthoburn.jobtastic.user;

import com.jamesthoburn.jobtastic.auth.email.EmailService;
import com.jamesthoburn.jobtastic.auth.token.VerificationTokenRepository;
import com.jamesthoburn.jobtastic.exception.AuthException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("updateProfile should update the user details and return a mapped response")
    void updateProfile_Success() {
        User existingUser = new User("Jane", "Doe", "old@example.com", "encodedPassword");
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Smith");
        request.setEmail("new@example.com");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.updateProfile(existingUser, request);

        assertEquals("Jane", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("new@example.com", response.getEmail());
        verify(userRepository).save(existingUser);
        verify(emailService).sendVerificationEmail(eq("new@example.com"), anyString());
    }

    @Test
    @DisplayName("updateProfile should reject an already-registered email")
    void updateProfile_WhenEmailAlreadyExists_ThrowsAuthException() {
        User existingUser = new User("Jane", "Doe", "old@example.com", "encodedPassword");
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("taken@example.com");

        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThrows(AuthException.class, () -> userService.updateProfile(existingUser, request));
    }

    @Test
    @DisplayName("changePassword should update the stored password when the current password is correct")
    void changePassword_Success() {
        User existingUser = new User("Jane", "Doe", "jane@example.com", "encodedPassword");
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmNewPassword("newPassword123");

        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.changePassword(existingUser, request);

        assertEquals("newEncodedPassword", existingUser.getPassword());
        verify(userRepository).save(existingUser);
    }

    @Test
    @DisplayName("changePassword should reject an incorrect current password")
    void changePassword_WhenCurrentPasswordIsIncorrect_ThrowsAuthException() {
        User existingUser = new User("Jane", "Doe", "jane@example.com", "encodedPassword");
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("wrongPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmNewPassword("newPassword123");

        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(AuthException.class, () -> userService.changePassword(existingUser, request));
    }
}