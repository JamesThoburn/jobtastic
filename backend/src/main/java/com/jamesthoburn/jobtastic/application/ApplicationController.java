package com.jamesthoburn.jobtastic.application;

import com.jamesthoburn.jobtastic.exception.ResourceNotFoundException;
import com.jamesthoburn.jobtastic.user.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/applications")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<Application> save(@Valid @RequestBody Application application, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(applicationService.createApplication(application, user));
    }

    @GetMapping
    public ResponseEntity<List<Application>> getUserApplications(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return ResponseEntity.ok(applicationService.getApplicationsByUser(user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Application> updateApplication(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return ResponseEntity.ok(applicationService.updateApplication(id, updates, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Application> deleteApplication(
            @PathVariable Long id,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }
        applicationService.deleteApplication(id, user);
        return ResponseEntity.noContent().build();
    }
}
