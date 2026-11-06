package com.flowpilot.service;

import com.flowpilot.dto.DashboardSummaryResponse;
import com.flowpilot.model.*;
import com.flowpilot.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class AnalyticsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private WorkflowExecutionRepository executionRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboardSummaryWithMetrics() {
        when(userRepository.countByOrganizationId(1L)).thenReturn(12L);
        when(workflowRepository.countByOrganizationIdAndStatus(1L, WorkflowStatus.ACTIVE)).thenReturn(5L);
        when(workflowRepository.countByOrganizationId(1L)).thenReturn(8L);

        when(taskRepository.countByOrganizationIdAndStatus(1L, TaskStatus.TODO)).thenReturn(6L);
        when(taskRepository.countByOrganizationIdAndStatus(1L, TaskStatus.IN_PROGRESS)).thenReturn(4L);
        when(taskRepository.countByOrganizationIdAndStatus(1L, TaskStatus.REVIEW)).thenReturn(2L);
        when(taskRepository.countByOrganizationIdAndStatus(1L, TaskStatus.DONE)).thenReturn(15L);
        when(taskRepository.countByOrganizationIdAndStatusNotAndDueDateBefore(eq(1L), eq(TaskStatus.DONE), any(LocalDateTime.class))).thenReturn(1L);

        when(approvalRepository.countByOrganizationIdAndStatus(1L, ApprovalStatus.PENDING)).thenReturn(3L);

        when(executionRepository.countByOrganizationId(1L)).thenReturn(20L);
        when(executionRepository.countByOrganizationIdAndStatus(1L, ExecutionStatus.COMPLETED)).thenReturn(18L);

        DashboardSummaryResponse response = analyticsService.getDashboardSummary(1L);

        assertNotNull(response);
        assertEquals(12L, response.getTotalEmployees());
        assertEquals(5L, response.getActiveWorkflows());
        assertEquals(8L, response.getTotalWorkflows());
        assertEquals(12L, response.getPendingTasks()); // 6 + 4 + 2
        assertEquals(15L, response.getCompletedTasks());
        assertEquals(1L, response.getOverdueTasks());
        assertEquals(3L, response.getPendingApprovals());
        assertEquals(90.0, response.getWorkflowCompletionRate()); // 18 / 20 * 100
        assertNotNull(response.getTaskTrends());
        assertFalse(response.getTaskTrends().isEmpty());
    }

    @Test
    void testGetDashboardSummaryWithConfiguredDepartments() {
        Department dept1 = new Department();
        dept1.setId(10L);
        dept1.setName("Engineering");
        dept1.setOrganizationId(1L);

        Department dept2 = new Department();
        dept2.setId(20L);
        dept2.setName("Finance");
        dept2.setOrganizationId(1L);

        when(departmentRepository.findByOrganizationId(1L)).thenReturn(List.of(dept1, dept2));
        when(userRepository.findByOrganizationIdAndDepartmentId(1L, 10L)).thenReturn(List.of(new User(), new User()));
        when(userRepository.findByOrganizationIdAndDepartmentId(1L, 20L)).thenReturn(List.of(new User()));

        DashboardSummaryResponse response = analyticsService.getDashboardSummary(1L);

        assertNotNull(response);
        assertNotNull(response.getDepartmentPerformance());
        assertEquals(2, response.getDepartmentPerformance().size());
        assertEquals("Engineering", response.getDepartmentPerformance().get(0).getLabel());
        assertEquals("Finance", response.getDepartmentPerformance().get(1).getLabel());
    }
}
