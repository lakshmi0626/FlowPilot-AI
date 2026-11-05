package com.flowpilot.controller;

import com.flowpilot.dto.EmployeeCreateRequest;
import com.flowpilot.dto.InviteRequest;
import com.flowpilot.dto.UserDto;
import com.flowpilot.model.Invitation;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getEmployees(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(employeeService.getEmployees(principal.getOrganizationId()));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<UserDto> createEmployee(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EmployeeCreateRequest req) {
        return ResponseEntity.ok(employeeService.createEmployee(principal.getOrganizationId(), principal.getId(), req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<UserDto> updateEmployee(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody EmployeeCreateRequest req) {
        return ResponseEntity.ok(employeeService.updateEmployee(principal.getOrganizationId(), principal.getId(), id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Map<String, String>> deactivateEmployee(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        employeeService.deactivateEmployee(principal.getOrganizationId(), principal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Employee deactivated successfully."));
    }

    @PostMapping("/invite")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<Invitation> inviteEmployee(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody InviteRequest req) {
        return ResponseEntity.ok(employeeService.inviteEmployee(principal.getOrganizationId(), principal.getId(), req));
    }
}
