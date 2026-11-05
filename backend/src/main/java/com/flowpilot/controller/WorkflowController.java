package com.flowpilot.controller;

import com.flowpilot.dto.WorkflowCreateRequest;
import com.flowpilot.model.Workflow;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.WorkflowService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/workflows")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @GetMapping
    public ResponseEntity<List<Workflow>> getWorkflows(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workflowService.getWorkflows(principal.getOrganizationId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflow(principal.getOrganizationId(), id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<Workflow> createWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody WorkflowCreateRequest req) {
        return ResponseEntity.ok(workflowService.createWorkflow(principal.getOrganizationId(), principal.getId(), req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<Workflow> updateWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody WorkflowCreateRequest req) {
        return ResponseEntity.ok(workflowService.updateWorkflow(principal.getOrganizationId(), principal.getId(), id, req));
    }

    @PostMapping("/{id}/duplicate")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<Workflow> duplicateWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(workflowService.duplicateWorkflow(principal.getOrganizationId(), principal.getId(), id));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<Workflow> toggleStatus(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(workflowService.toggleStatus(principal.getOrganizationId(), principal.getId(), id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        workflowService.deleteWorkflow(principal.getOrganizationId(), principal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Workflow deleted successfully."));
    }
}
