package com.jamesthoburn.jobtastic.user;

import com.jamesthoburn.jobtastic.auth.CookieUtils;
import com.jamesthoburn.jobtastic.auth.jwt.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final CookieUtils cookieUtils;

    public UserController(UserService userService, JwtService jwtService, CookieUtils cookieUtils) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.cookieUtils = cookieUtils;
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication,
            HttpServletResponse response
    ) {
        User user = (User) authentication.getPrincipal();
        String previousEmail = user.getEmail();

        UserResponse updatedUser = userService.updateProfile(user, request);

        if (!previousEmail.equals(request.getEmail())) {
            response.addHeader("Set-Cookie", cookieUtils.cleanAccessCookie().toString());
            response.addHeader("Set-Cookie", cookieUtils.cleanRefreshCookie().toString());
            return ResponseEntity.status(200).body(updatedUser);
        }

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        userService.changePassword(user, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser(
            Authentication authentication,
            HttpServletResponse response
    ) {
        User user = (User) authentication.getPrincipal();
        userService.deleteUser(user);

        response.addHeader("Set-Cookie", cookieUtils.cleanAccessCookie().toString());
        response.addHeader("Set-Cookie", cookieUtils.cleanRefreshCookie().toString());

        return ResponseEntity.noContent().build();
    }
}
