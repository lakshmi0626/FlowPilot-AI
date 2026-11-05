package com.flowpilot.controller;

import com.flowpilot.dto.*;
import com.flowpilot.model.Organization;
import com.flowpilot.model.User;
import com.flowpilot.repository.OrganizationRepository;
import com.flowpilot.repository.UserRepository;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public AuthController(AuthService authService,
                          UserRepository userRepository,
                          OrganizationRepository organizationRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register-company")
    public ResponseEntity<AuthResponse> registerCompany(@Valid @RequestBody RegisterCompanyRequest request) {
        return ResponseEntity.ok(authService.registerCompany(request));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(Map.of("message", "Password has been successfully updated."));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        User user = userRepository.findById(principal.getId()).orElseThrow();
        Organization org = organizationRepository.findById(user.getOrganizationId()).orElse(null);

        return ResponseEntity.ok(new AuthResponse(
                null,
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getOrganizationId(),
                org != null ? org.getName() : "Workspace",
                org != null ? org.getSlug() : "workspace"
        ));
    }
}
