package com.flowpilot.service;

import com.flowpilot.model.Department;
import com.flowpilot.model.Organization;
import com.flowpilot.repository.DepartmentRepository;
import com.flowpilot.repository.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final AuditLogService auditLogService;

    public OrganizationService(OrganizationRepository organizationRepository,
                               DepartmentRepository departmentRepository,
                               AuditLogService auditLogService) {
        this.organizationRepository = organizationRepository;
        this.departmentRepository = departmentRepository;
        this.auditLogService = auditLogService;
    }

    public Organization getOrganization(Long orgId) {
        return organizationRepository.findById(orgId)
                .orElseThrow(() -> new IllegalArgumentException("Organization not found: " + orgId));
    }

    @Transactional
    public Organization updateOrganization(Long orgId, Long userId, Organization updateReq) {
        Organization org = getOrganization(orgId);
        if (updateReq.getName() != null && !updateReq.getName().isBlank()) {
            org.setName(updateReq.getName());
        }
        if (updateReq.getDomain() != null) {
            org.setDomain(updateReq.getDomain());
        }
        if (updateReq.getLogoUrl() != null) {
            org.setLogoUrl(updateReq.getLogoUrl());
        }
        if (updateReq.getPrimaryColor() != null) {
            org.setPrimaryColor(updateReq.getPrimaryColor());
        }
        if (updateReq.getSecondaryColor() != null) {
            org.setSecondaryColor(updateReq.getSecondaryColor());
        }
        org.setUpdatedAt(LocalDateTime.now());
        Organization saved = organizationRepository.save(org);

        auditLogService.logAction(orgId, userId, "UPDATE_SETTINGS", "ORGANIZATION",
                String.valueOf(orgId), "{\"name\":\"" + saved.getName() + "\"}");

        return saved;
    }

    public List<Department> getDepartments(Long orgId) {
        return departmentRepository.findByOrganizationId(orgId);
    }

    @Transactional
    public Department createDepartment(Long orgId, Long userId, String name, String description) {
        Department dept = new Department(null, orgId, name, description);
        Department saved = departmentRepository.save(dept);
        auditLogService.logAction(orgId, userId, "CREATE_DEPARTMENT", "DEPARTMENT",
                String.valueOf(saved.getId()), "{\"name\":\"" + name + "\"}");
        return saved;
    }
}
