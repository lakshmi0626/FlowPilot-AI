package com.flowpilot.service;

import com.flowpilot.model.Department;
import com.flowpilot.model.Organization;
import com.flowpilot.repository.DepartmentRepository;
import com.flowpilot.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetOrganizationById() {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("Apex Innovations");

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));

        Organization result = organizationService.getOrganization(1L);
        assertNotNull(result);
        assertEquals("Apex Innovations", result.getName());
    }

    @Test
    void testUpdateOrganizationBranding() {
        Organization org = new Organization();
        org.setId(1L);
        org.setName("Apex Innovations");

        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));
        when(organizationRepository.save(any(Organization.class))).thenAnswer(inv -> inv.getArgument(0));

        Organization updateReq = new Organization();
        updateReq.setName("Apex Global Enterprise");
        updateReq.setPrimaryColor("#16A36A");
        updateReq.setLogoUrl("https://example.com/logo.png");

        Organization updated = organizationService.updateOrganization(1L, 2L, updateReq);

        assertNotNull(updated);
        assertEquals("Apex Global Enterprise", updated.getName());
        assertEquals("#16A36A", updated.getPrimaryColor());
        assertEquals("https://example.com/logo.png", updated.getLogoUrl());
        assertNotNull(updated.getUpdatedAt());
        verify(organizationRepository, times(1)).save(org);
        verify(auditLogService, times(1)).logAction(eq(1L), eq(2L), eq("UPDATE_SETTINGS"), eq("ORGANIZATION"), eq("1"), anyString());
    }

    @Test
    void testCreateDepartment() {
        when(departmentRepository.save(any(Department.class))).thenAnswer(inv -> {
            Department d = inv.getArgument(0);
            d.setId(101L);
            return d;
        });

        Department dept = organizationService.createDepartment(1L, 2L, "Product Operations", "Manages cross-functional product delivery");

        assertNotNull(dept);
        assertEquals(101L, dept.getId());
        assertEquals("Product Operations", dept.getName());
        assertEquals(1L, dept.getOrganizationId());
        verify(departmentRepository, times(1)).save(any(Department.class));
        verify(auditLogService, times(1)).logAction(eq(1L), eq(2L), eq("CREATE_DEPARTMENT"), eq("DEPARTMENT"), eq("101"), anyString());
    }
}
