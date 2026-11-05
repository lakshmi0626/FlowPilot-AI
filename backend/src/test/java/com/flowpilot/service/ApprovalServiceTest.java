package com.flowpilot.service;

import com.flowpilot.dto.ApprovalCreateRequest;
import com.flowpilot.dto.ApprovalDecisionRequest;
import com.flowpilot.model.*;
import com.flowpilot.repository.ApprovalRepository;
import com.flowpilot.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApprovalServiceTest {

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkflowEngineService workflowEngineService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private ApprovalService approvalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateApproval() {
        ApprovalCreateRequest req = new ApprovalCreateRequest();
        req.setTitle("Software License Purchase");
        req.setRequestType(ApprovalType.PURCHASE);
        req.setDetailsJson("{\"amount\": 1200}");
        req.setApproverUserId(2L);

        when(approvalRepository.save(any(Approval.class))).thenAnswer(invocation -> {
            Approval a = invocation.getArgument(0);
            a.setId(50L);
            return a;
        });

        Approval created = approvalService.createApproval(1L, 3L, req);

        assertNotNull(created);
        assertEquals("Software License Purchase", created.getTitle());
        assertEquals(ApprovalType.PURCHASE, created.getRequestType());
        assertEquals(ApprovalStatus.PENDING, created.getStatus());
        verify(approvalRepository, times(1)).save(any(Approval.class));
        verify(notificationService, times(1)).sendNotification(eq(1L), eq(2L), anyString(), anyString(), eq(NotificationType.APPROVAL_REQUEST), anyString());
    }

    @Test
    void testDecideApprovalApprovedWithWorkflowResumption() {
        Approval approval = new Approval();
        approval.setId(10L);
        approval.setOrganizationId(1L);
        approval.setRequesterUserId(3L);
        approval.setTitle("Conference Travel Expense");
        approval.setStatus(ApprovalStatus.PENDING);
        approval.setWorkflowExecutionId(99L);

        when(approvalRepository.findByIdAndOrganizationId(10L, 1L)).thenReturn(Optional.of(approval));
        when(approvalRepository.save(any(Approval.class))).thenAnswer(inv -> inv.getArgument(0));

        ApprovalDecisionRequest decisionReq = new ApprovalDecisionRequest(ApprovalStatus.APPROVED, "Approved for travel");
        Approval decided = approvalService.decideApproval(1L, 2L, 10L, decisionReq);

        assertNotNull(decided);
        assertEquals(ApprovalStatus.APPROVED, decided.getStatus());
        assertEquals("Approved for travel", decided.getDecisionNotes());
        verify(workflowEngineService, times(1)).resumeWorkflow(eq(99L), eq(ApprovalStatus.APPROVED), eq("Approved for travel"));
        verify(notificationService, times(1)).sendNotification(eq(1L), eq(3L), anyString(), anyString(), eq(NotificationType.REQUEST_APPROVED), anyString());
    }

    @Test
    void testDecideApprovalRejected() {
        Approval approval = new Approval();
        approval.setId(11L);
        approval.setOrganizationId(1L);
        approval.setRequesterUserId(4L);
        approval.setTitle("Hardware Upgrade");
        approval.setStatus(ApprovalStatus.PENDING);

        when(approvalRepository.findByIdAndOrganizationId(11L, 1L)).thenReturn(Optional.of(approval));
        when(approvalRepository.save(any(Approval.class))).thenAnswer(inv -> inv.getArgument(0));

        ApprovalDecisionRequest decisionReq = new ApprovalDecisionRequest(ApprovalStatus.REJECTED, "Budget exceeded for Q4");
        Approval decided = approvalService.decideApproval(1L, 2L, 11L, decisionReq);

        assertNotNull(decided);
        assertEquals(ApprovalStatus.REJECTED, decided.getStatus());
        verify(workflowEngineService, never()).resumeWorkflow(anyLong(), any(), any());
        verify(notificationService, times(1)).sendNotification(eq(1L), eq(4L), anyString(), anyString(), eq(NotificationType.REQUEST_REJECTED), anyString());
    }
}
