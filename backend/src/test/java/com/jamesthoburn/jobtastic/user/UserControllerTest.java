package com.jamesthoburn.jobtastic.user;

import com.jamesthoburn.jobtastic.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    // --- POST /api/users (CREATE) ---

    @Test
    @DisplayName("POST /api/users should return 201 Created when request is valid")
    void createUser_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        UserRequest request = new UserRequest("Jane", "Doe", "jane@example.com", "securePassword123");
        UserResponse response = new UserResponse(1L, "Jane", "Doe", "jane@example.com");

        when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"));
    }

    @Test
    @DisplayName("POST /api/users should return 400 Bad Request when validation constraints fail")
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange - Blank name, invalid email, short password
        UserRequest invalidRequest = new UserRequest("", "Doe", "not-an-email", "short");

        // Act & Assert
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());

        // Verify the request was blocked at the controller boundary and never hit the service layer
        verifyNoInteractions(userService);
    }

    // --- GET /api/users (READ ALL) ---

    @Test
    @DisplayName("GET /api/users should return 200 OK along with a list of users")
    void getAllUsers_ReturnsList() throws Exception {
        // Arrange
        UserResponse user1 = new UserResponse(1L, "Alice", "Smith", "alice@example.com");
        UserResponse user2 = new UserResponse(2L, "Bob", "Jones", "bob@example.com");
        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"))
                .andExpect(jsonPath("$[1].firstName").value("Bob"));
    }

    // --- GET /api/users/{id} (READ BY ID) ---

    @Test
    @DisplayName("GET /api/users/{id} should return 200 OK when target user ID exists")
    void getUserById_Found_ReturnsUser() throws Exception {
        // Arrange
        UserResponse response = new UserResponse(1L, "Jane", "Doe", "jane@example.com");
        when(userService.getUserById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.firstName").value("Jane"));
    }

    @Test
    @DisplayName("GET /api/users/{id} should return 404 Not Found when UserNotFoundException is thrown globally")
    void getUserById_NotFound_Returns404() throws Exception {
        // Arrange
        when(userService.getUserById(99L)).thenThrow(new UserNotFoundException("User not found with id: 99"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with id: 99"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    // --- PUT /api/users/{id} (UPDATE) ---

    @Test
    @DisplayName("PUT /api/users/{id} should return 200 OK when update payload matches criteria")
    void updateUser_ValidRequest_ReturnsUpdatedUser() throws Exception {
        // Arrange
        UserRequest updateRequest = new UserRequest("Jane", "Changed", "jane@example.com", "newPassword123");
        UserResponse updatedResponse = new UserResponse(1L, "Jane", "Changed", "jane@example.com");

        when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(updatedResponse);

        // Act & Assert
        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Changed"));
    }

    // --- DELETE /api/users/{id} (DELETE) ---

    @Test
    @DisplayName("DELETE /api/users/{id} should return 24 No Content when delete is successful")
    void deleteUser_Success_Returns204() throws Exception {
        // Arrange
        doNothing().when(userService).deleteUser(1L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(1L);
    }
}