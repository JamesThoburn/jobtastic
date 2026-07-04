package com.jamesthoburn.jobtastic.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    @DisplayName("loadUserByUsername should return the user when found")
    void loadUserByUsername_WhenUserExists_ReturnsUser() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        UserDetails result = userDetailsService.loadUserByUsername("jane@example.com");

        assertEquals("jane@example.com", result.getUsername());
    }

    @Test
    @DisplayName("loadUserByUsername should throw when the user is missing")
    void loadUserByUsername_WhenUserMissing_ThrowsUsernameNotFoundException() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing@example.com"));
    }
}
