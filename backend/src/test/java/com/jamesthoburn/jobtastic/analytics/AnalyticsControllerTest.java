package com.jamesthoburn.jobtastic.analytics;

import com.jamesthoburn.jobtastic.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsControllerTest {

    @Mock
    private AnalyticsService analyticsService;

    @InjectMocks
    private AnalyticsController analyticsController;

    @Test
    @DisplayName("getSummary should return the analytics summary for the authenticated user")
    void getSummary_Success() {
        User user = new User("Jane", "Doe", "jane@example.com", "password");
        user.setId(7L);
        Authentication authentication = new TestingAuthenticationToken(user, null);

        AnalyticsSummaryResponse responseBody = new AnalyticsSummaryResponse(
                4L,
                2L,
                0.75f,
                0.25f,
                List.of(new StatusBreakdownEntry("APPLIED", 1L))
        );

        when(analyticsService.getSummary(user)).thenReturn(responseBody);

        ResponseEntity<AnalyticsSummaryResponse> response = analyticsController.getSummary(authentication);

        assertEquals(200, response.getStatusCode().value());
        assertSame(responseBody, response.getBody());
        verify(analyticsService).getSummary(user);
    }
}
