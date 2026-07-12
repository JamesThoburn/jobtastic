package com.jamesthoburn.jobtastic.analytics;

import com.jamesthoburn.jobtastic.application.Application;
import com.jamesthoburn.jobtastic.application.ApplicationRepository;
import com.jamesthoburn.jobtastic.application.ApplicationStatus;
import com.jamesthoburn.jobtastic.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("getSummary should aggregate metrics for the authenticated user")
    void getSummary_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);

        Application applied = new Application();
        applied.setStatus(ApplicationStatus.APPLIED);

        Application interviewing = new Application();
        interviewing.setStatus(ApplicationStatus.INTERVIEWING);

        Application offer = new Application();
        offer.setStatus(ApplicationStatus.OFFER);

        Application rejected = new Application();
        rejected.setStatus(ApplicationStatus.REJECTED);

        when(applicationRepository.findByUserId(7L)).thenReturn(List.of(applied, interviewing, offer, rejected));

        AnalyticsSummaryResponse response = analyticsService.getSummary(user);

        assertEquals(4, response.getTotalApplications());
        assertEquals(2, response.getActiveApplications());
        assertEquals(0.75, response.getResponseRate());
        assertEquals(0.25, response.getOfferRate());
        assertEquals(4, response.getStatusBreakdown().size());
        verify(applicationRepository).findByUserId(7L);
    }

    @Test
    @DisplayName("getSummary should return zeroed metrics when the user has no applications")
    void getSummary_WhenUserHasNoApplications_ReturnsZeroedMetrics() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);

        when(applicationRepository.findByUserId(7L)).thenReturn(List.of());

        AnalyticsSummaryResponse response = analyticsService.getSummary(user);

        assertEquals(0, response.getTotalApplications());
        assertEquals(0, response.getActiveApplications());
        assertEquals(0.0, response.getResponseRate());
        assertEquals(0.0, response.getOfferRate());
        assertEquals(4, response.getStatusBreakdown().size());
        verify(applicationRepository).findByUserId(7L);
    }
}
