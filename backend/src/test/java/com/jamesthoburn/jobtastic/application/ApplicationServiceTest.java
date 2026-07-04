package com.jamesthoburn.jobtastic.application;

import com.jamesthoburn.jobtastic.user.User;
import com.jamesthoburn.jobtastic.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    @DisplayName("createApplication should assign the current user and save it")
    void createApplication_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Application application = new Application();

        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Application saved = applicationService.createApplication(application, user);

        assertSame(application, saved);
        assertSame(user, application.getUser());
        verify(applicationRepository).save(application);
    }

    @Test
    @DisplayName("getApplicationsByUser should return applications owned by the user")
    void getApplicationsByUser_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        List<Application> expected = List.of(new Application());

        when(applicationRepository.findByUserId(7L)).thenReturn(expected);

        List<Application> result = applicationService.getApplicationsByUser(user);

        assertSame(expected, result);
        verify(applicationRepository).findByUserId(7L);
    }

    @Test
    @DisplayName("updateApplication should apply updates for the owner")
    void updateApplication_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Application application = new Application();
        application.setUser(user);
        application.setCompanyName("Old");
        application.setPositionName("Old Role");
        application.setStatus(ApplicationStatus.APPLIED);
        application.setNotes("Old notes");
        application.setDateApplied(LocalDate.of(2025, 1, 1));
        application.setLocation("Old location");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));
        when(applicationRepository.save(any(Application.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Application updated = applicationService.updateApplication(1L, Map.of(
                "companyName", "New Co",
                "positionName", "New Role",
                "status", "INTERVIEWING",
                "notes", "Updated notes",
                "dateApplied", "2025-03-01",
                "location", "New location"
        ), user);

        assertEquals("New Co", updated.getCompanyName());
        assertEquals("New Role", updated.getPositionName());
        assertEquals(ApplicationStatus.INTERVIEWING, updated.getStatus());
        assertEquals("Updated notes", updated.getNotes());
        assertEquals(LocalDate.of(2025, 3, 1), updated.getDateApplied());
        assertEquals("New location", updated.getLocation());
        verify(applicationRepository).save(application);
    }

    @Test
    @DisplayName("updateApplication should reject requests from a different user")
    void updateApplication_WhenUserIsNotOwner_ThrowsAccessDeniedException() {
        User owner = new User("Jane", "Doe", "jane@example.com", "password");
        owner.setId(7L);
        User anotherUser = new User("John", "Doe", "john@example.com", "password");
        anotherUser.setId(8L);
        Application application = new Application();
        application.setUser(owner);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(AccessDeniedException.class,
                () -> applicationService.updateApplication(1L, Map.of("companyName", "Try"), anotherUser));
    }

    @Test
    @DisplayName("deleteApplication should delete the application for the owner")
    void deleteApplication_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Application application = new Application();
        application.setUser(user);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        applicationService.deleteApplication(1L, user);

        verify(applicationRepository).delete(application);
    }

    @Test
    @DisplayName("deleteApplication should reject requests from a different user")
    void deleteApplication_WhenUserIsNotOwner_ThrowsAccessDeniedException() {
        User owner = new User("Jane", "Doe", "jane@example.com", "password");
        owner.setId(7L);
        User anotherUser = new User("John", "Doe", "john@example.com", "password");
        anotherUser.setId(8L);
        Application application = new Application();
        application.setUser(owner);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(AccessDeniedException.class, () -> applicationService.deleteApplication(1L, anotherUser));
    }

    @Test
    @DisplayName("updateApplication should throw if the application does not exist")
    void updateApplication_WhenApplicationMissing_ThrowsResourceNotFoundException() {
        when(applicationRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> applicationService.updateApplication(99L, Map.of("companyName", "Missing"), new User()));
    }
}
