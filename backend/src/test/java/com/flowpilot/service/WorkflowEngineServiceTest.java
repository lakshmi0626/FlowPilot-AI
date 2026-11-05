package com.flowpilot.service;

import com.flowpilot.model.*;
import com.flowpilot.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WorkflowEngineServiceTest {

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private WorkflowExecutionRepository executionRepository;

    @Mock
    private WorkflowExecutionLogRepository logRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private ApprovalRepository approvalRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private WorkflowEngineService workflowEngineService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExecuteWorkflowPausesOnApprovalNode() {
        Workflow wf = new Workflow();
        wf.setId(10L);
        wf.setOrganizationId(1L);
        wf.setName("Leave Approval Workflow");
        wf.setDefinitionJson("{\"nodes\":[{\"id\":\"s1\",\"type\":\"START\",\"title\":\"Submit\"},{\"id\":\"a1\",\"type\":\"APPROVAL\",\"title\":\"Manager Approval\"}],\"edges\":[{\"source\":\"s1\",\"target\":\"a1\"}]}");

        when(workflowRepository.findByIdAndOrganizationId(10L, 1L)).thenReturn(Optional.of(wf));

        WorkflowExecution initialExec = new WorkflowExecution();
        initialExec.setId(100L);
        initialExec.setOrganizationId(1L);
        initialExec.setWorkflowId(10L);
        initialExec.setTriggeredByUserId(4L);
        initialExec.setStatus(ExecutionStatus.RUNNING);

        when(executionRepository.save(any(WorkflowExecution.class))).thenAnswer(i -> {
            WorkflowExecution exec = i.getArgument(0);
            if (exec.getId() == null) exec.setId(100L);
            return exec;
        });

        when(executionRepository.findById(100L)).thenReturn(Optional.of(initialExec));

        WorkflowExecution result = workflowEngineService.executeWorkflow(1L, 10L, 4L, "{\"reason\":\"Vacation\"}");

        assertNotNull(result);
        verify(approvalRepository, times(1)).save(any(Approval.class));
    }
}
