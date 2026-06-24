package com.jamesthoburn.jobtastic.application;

import com.jamesthoburn.jobtastic.exception.ResourceNotFoundException;
import com.jamesthoburn.jobtastic.user.User;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService {
    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public Application createApplication(Application application, User user) {
        application.setUser(user);
        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByUser(User user) {
        return applicationRepository.findByUserId(user.getId());
    }

    public Application updateApplication(Long id, Map<String, Object> updates, User user) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        if (!application.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have the permission to edit this application");
        }

        updates.forEach((key, value) -> {
            switch (key) {
                case "companyName" -> application.setCompanyName((String) value);
                case "positionName" -> application.setPositionName((String) value);
                case "status" -> application.setStatus(ApplicationStatus.valueOf((String) value));
                case "notes" -> application.setNotes((String) value);
                case "dateApplied" -> {
                    application.setDateApplied(
                            value != null ? LocalDate.parse((String) value) : null
                    );
                }
                case "location" -> application.setLocation((String) value);
            }
        });

        return applicationRepository.save(application);
    }

    public void deleteApplication(Long id, User user) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

        System.out.println("DEBUG: App User ID: " + application.getUser().getId());

        if (!application.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have the permission to delete this application");
        }

        applicationRepository.delete(application);
    }
}
