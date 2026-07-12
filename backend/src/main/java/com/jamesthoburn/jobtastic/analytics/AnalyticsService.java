package com.jamesthoburn.jobtastic.analytics;

import com.jamesthoburn.jobtastic.application.Application;
import com.jamesthoburn.jobtastic.application.ApplicationRepository;
import com.jamesthoburn.jobtastic.application.ApplicationStatus;
import com.jamesthoburn.jobtastic.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {
    private final ApplicationRepository applicationRepository;

    public AnalyticsService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public AnalyticsSummaryResponse getSummary (User user) {
        List<Application> applications = applicationRepository.findByUserId(user.getId());

        long totalApplications = applications.size();

        long activeApplications = applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.APPLIED
                    || app.getStatus() == ApplicationStatus.INTERVIEWING)
                .count();

        long respondedApplications = applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.INTERVIEWING
                    || app.getStatus() == ApplicationStatus.OFFER
                    || app.getStatus() == ApplicationStatus.REJECTED)
                .count();

        long offerApplications = applications.stream()
                .filter(app -> app.getStatus() == ApplicationStatus.OFFER)
                .count();

        float responseRate = totalApplications == 0 ? (float) 0.0 : (float) respondedApplications / totalApplications;
        float offerRate = totalApplications == 0 ? (float) 0.0 : (float) offerApplications / totalApplications;

        List<StatusBreakdownEntry> statusBreakdown = Arrays.stream(ApplicationStatus.values())
                .map(status -> new StatusBreakdownEntry(
                        status.name(),
                        applications.stream()
                                .filter(app -> app.getStatus() == status)
                                .count()
                ))
                .toList();

        return new AnalyticsSummaryResponse(
                totalApplications,
                activeApplications,
                responseRate,
                offerRate,
                statusBreakdown
        );
    }
}
