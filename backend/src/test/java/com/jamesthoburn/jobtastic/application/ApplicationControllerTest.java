package com.jamesthoburn.jobtastic.application;

import com.jamesthoburn.jobtastic.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.TestingAuthenticationToken;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationControllerTest {

    @Mock
    private ApplicationService applicationService;

    @InjectMocks
    private ApplicationController applicationController;

    @Test
    @DisplayName("save should create an application for the authenticated user")
    void save_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Application application = new Application();
        Authentication authentication = new TestingAuthenticationToken(user, null);

        when(applicationService.createApplication(any(Application.class), eq(user))).thenReturn(application);

        ResponseEntity<Application> response = applicationController.save(application, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertSame(application, response.getBody());
        verify(applicationService).createApplication(application, user);
    }

    @Test
    @DisplayName("getUserApplications should return the current user's applications")
    void getUserApplications_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        List<Application> applications = List.of(new Application());
        Authentication authentication = new TestingAuthenticationToken(user, null);

        when(applicationService.getApplicationsByUser(user)).thenReturn(applications);

        ResponseEntity<List<Application>> response = applicationController.getUserApplications(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertSame(applications, response.getBody());
        verify(applicationService).getApplicationsByUser(user);
    }

    @Test
    @DisplayName("updateApplication should delegate to the service")
    void updateApplication_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Application application = new Application();
        Authentication authentication = new TestingAuthenticationToken(user, null);
        Map<String, Object> updates = Map.of("companyName", "Acme");

        when(applicationService.updateApplication(1L, updates, user)).thenReturn(application);

        ResponseEntity<Application> response = applicationController.updateApplication(1L, updates, authentication);

        assertEquals(200, response.getStatusCode().value());
        assertSame(application, response.getBody());
        verify(applicationService).updateApplication(1L, updates, user);
    }

    @Test
    @DisplayName("deleteApplication should return no content")
    void deleteApplication_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Authentication authentication = new TestingAuthenticationToken(user, null);

        ResponseEntity<Application> response = applicationController.deleteApplication(1L, authentication);

        assertEquals(204, response.getStatusCode().value());
        verify(applicationService).deleteApplication(1L, user);
    }
}
