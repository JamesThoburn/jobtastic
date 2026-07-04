package com.jamesthoburn.jobtastic.user;

import com.jamesthoburn.jobtastic.auth.CookieUtils;
import com.jamesthoburn.jobtastic.auth.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private CookieUtils cookieUtils;

    private UserController userController;

    @BeforeEach
    void setup() {
        userController = new UserController(userService, jwtService, cookieUtils);
    }

    @Test
    @DisplayName("updateProfile should return the updated profile and refresh the access cookie when email changes")
    void updateProfile_WhenEmailChanges_ReturnsUpdatedProfileAndSetsCookie() {
        User user = new User("Jane", "Doe", "old@example.com", "encodedPassword");
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setFirstName("Jane");
        request.setLastName("Doe");
        request.setEmail("new@example.com");

        UserResponse expectedResponse = new UserResponse(1L, "Jane", "Doe", "new@example.com");
        Authentication authentication = new TestingAuthenticationToken(user, null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(userService.updateProfile(eq(user), any(UpdateProfileRequest.class))).thenReturn(expectedResponse);
        when(cookieUtils.cleanAccessCookie()).thenReturn(ResponseCookie.from("access_token", "").build());
        when(cookieUtils.cleanRefreshCookie()).thenReturn(ResponseCookie.from("refresh_token", "").build());

        ResponseEntity<UserResponse> result = userController.updateProfile(request, authentication, response);

        assertEquals(200, result.getStatusCode().value());
        assertEquals(expectedResponse, result.getBody());
        assertTrue(response.getHeader("Set-Cookie").contains("access_token"));
    }

    @Test
    @DisplayName("updatePassword should delegate to the service and return no content")
    void updatePassword_WhenValidRequest_ReturnsNoContent() {
        User user = new User("Jane", "Doe", "jane@example.com", "encodedPassword");
        UpdatePasswordRequest request = new UpdatePasswordRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword123");
        request.setConfirmNewPassword("newPassword123");

        Authentication authentication = new TestingAuthenticationToken(user, null);

        ResponseEntity<Void> result = userController.updatePassword(request, authentication);

        verify(userService).changePassword(eq(user), eq(request));
        assertEquals(204, result.getStatusCode().value());
    }

    @Test
    @DisplayName("deleteCurrentUser should delegate to the service and clear auth cookies")
    void deleteCurrentUser_WhenCalled_DeletesUserAndClearsCookies() {
        User user = new User("Jane", "Doe", "jane@example.com", "encodedPassword");
        Authentication authentication = new TestingAuthenticationToken(user, null);
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(cookieUtils.cleanAccessCookie()).thenReturn(ResponseCookie.from("access_token", "").build());
        when(cookieUtils.cleanRefreshCookie()).thenReturn(ResponseCookie.from("refresh_token", "").build());

        ResponseEntity<Void> result = userController.deleteCurrentUser(authentication, response);

        verify(userService).deleteUser(eq(user));
        assertEquals(204, result.getStatusCode().value());
        assertTrue(response.getHeader("Set-Cookie").contains("access_token"));
    }
}
