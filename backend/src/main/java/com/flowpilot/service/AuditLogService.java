package com.flowpilot.service;

import com.flowpilot.model.AuditLog;
import com.flowpilot.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logAction(Long organizationId, Long userId, String action, String entityType, String entityId, String detailsJson) {
        if (organizationId == null) return;
        try {
            AuditLog log = new AuditLog(organizationId, userId, action, entityType, entityId, "127.0.0.1", detailsJson);
            auditLogRepository.save(log);
        } catch (Exception ignored) {}
    }

    public List<AuditLog> getAuditLogs(Long organizationId) {
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(organizationId);
    }
}
