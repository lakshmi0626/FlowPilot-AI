package com.flowpilot.controller;

import com.flowpilot.model.AuditLog;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/logs")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN', 'MANAGER')")
    public ResponseEntity<List<AuditLog>> getLogs(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(auditLogService.getAuditLogs(principal.getOrganizationId()));
    }
}
