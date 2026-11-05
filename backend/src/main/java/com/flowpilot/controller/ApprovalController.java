package com.flowpilot.controller;

import com.flowpilot.dto.ApprovalCreateRequest;
import com.flowpilot.dto.ApprovalDecisionRequest;
import com.flowpilot.model.Approval;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;

    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @GetMapping
    public ResponseEntity<List<Approval>> getApprovals(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(approvalService.getApprovals(principal.getOrganizationId(), status, principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApproval(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(approvalService.getApproval(principal.getOrganizationId(), id));
    }

    @PostMapping
    public ResponseEntity<Approval> createApproval(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ApprovalCreateRequest req) {
        return ResponseEntity.ok(approvalService.createApproval(principal.getOrganizationId(), principal.getId(), req));
    }

    @PostMapping("/{id}/decide")
    public ResponseEntity<Approval> decideApproval(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody ApprovalDecisionRequest req) {
        return ResponseEntity.ok(approvalService.decideApproval(principal.getOrganizationId(), principal.getId(), id, req));
    }
}
