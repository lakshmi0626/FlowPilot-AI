package com.flowpilot.controller;

import com.flowpilot.model.Department;
import com.flowpilot.model.Organization;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/current")
    public ResponseEntity<Organization> getCurrentOrganization(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(organizationService.getOrganization(principal.getOrganizationId()));
    }

    @PutMapping("/current")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Organization> updateOrganization(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Organization updateReq) {
        return ResponseEntity.ok(organizationService.updateOrganization(principal.getOrganizationId(), principal.getId(), updateReq));
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getDepartments(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(organizationService.getDepartments(principal.getOrganizationId()));
    }

    @PostMapping("/departments")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<Department> createDepartment(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, String> body) {
        String name = body.get("name");
        String description = body.get("description");
        return ResponseEntity.ok(organizationService.createDepartment(principal.getOrganizationId(), principal.getId(), name, description));
    }
}
