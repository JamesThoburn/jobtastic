package com.jamesthoburn.jobtastic.user;

import com.jamesthoburn.jobtastic.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // --- CREATE TESTS ---

    @Test
    @DisplayName("Should successfully create a user and return the mapped response")
    void createUser_Success() {
        UserRequest userRequest = new UserRequest("Jane", "Doe", "jane@example.com", "password123");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setFirstName("Jane");
        savedUser.setLastName("Doe");
        savedUser.setEmail("jane@example.com");

        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse result = userService.createUser(userRequest);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getEmail()).isEqualTo("jane@example.com");

        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- READ TESTS ---

    @Test
    @DisplayName("Should return a list of all users")
    void getAllUsers_Success() {
        // Arrange
        User user1 = new User();
        user1.setId(1L);
        user1.setFirstName("Alice");

        User user2 = new User();
        user2.setId(2L);
        user2.setFirstName("Bob");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserResponse> result = userService.getAllUsers();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFirstName()).isEqualTo("Alice");
        assertThat(result.get(1).getFirstName()).isEqualTo("Bob");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return a specific user when searching by a valid ID")
    void getUserById_Success() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setFirstName("Jane");
        user.setEmail("jane@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFirstName()).isEqualTo("Jane");
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when searching for a non-existent ID")
    void getUserById_NotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
        verify(userRepository, times(1)).findById(99L);
    }

    // --- UPDATE TESTS ---

    @Test
    @DisplayName("Should successfully update user fields and re-encode password if provided")
    void updateUser_SuccessWithPassword() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFirstName("OldName");
        existingUser.setPassword("oldHash");

        UserRequest updateRequest = new UserRequest("NewName", "Doe", "new@example.com", "newPassword123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPassword123")).thenReturn("newHashedPassword");
        // Mock save to return the modified user entity
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.updateUser(1L, updateRequest);

        // Assert
        assertThat(result.getFirstName()).isEqualTo("NewName");
        assertThat(result.getEmail()).isEqualTo("new@example.com");
        verify(passwordEncoder, times(1)).encode("newPassword123");
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when attempting to update a non-existent user")
    void updateUser_NotFound() {
        // Arrange
        UserRequest updateRequest = new UserRequest("NewName", "Doe", "new@example.com", "password");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(99L, updateRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    // --- DELETE TESTS ---

    @Test
    @DisplayName("Should successfully delete a user if the ID exists")
    void deleteUser_Success() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when attempting to delete an ID that does not exist")
    void deleteUser_NotFound() {
        // Arrange
        when(userRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userRepository, times(1)).existsById(99L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}