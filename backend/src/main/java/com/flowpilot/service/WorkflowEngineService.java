package com.flowpilot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flowpilot.model.*;
import com.flowpilot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WorkflowEngineService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowExecutionLogRepository logRepository;
    private final TaskRepository taskRepository;
    private final ApprovalRepository approvalRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WorkflowEngineService(WorkflowRepository workflowRepository,
                                 WorkflowExecutionRepository executionRepository,
                                 WorkflowExecutionLogRepository logRepository,
                                 TaskRepository taskRepository,
                                 ApprovalRepository approvalRepository,
                                 NotificationService notificationService,
                                 AuditLogService auditLogService) {
        this.workflowRepository = workflowRepository;
        this.executionRepository = executionRepository;
        this.logRepository = logRepository;
        this.taskRepository = taskRepository;
        this.approvalRepository = approvalRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    public List<WorkflowExecution> getExecutions(Long orgId) {
        return executionRepository.findByOrganizationIdOrderByStartedAtDesc(orgId);
    }

    public List<WorkflowExecution> getExecutionsForWorkflow(Long workflowId) {
        return executionRepository.findByWorkflowId(workflowId);
    }

    public List<WorkflowExecutionLog> getExecutionLogs(Long executionId) {
        return logRepository.findByExecutionIdOrderByCreatedAtAsc(executionId);
    }

    @Transactional
    public WorkflowExecution executeWorkflow(Long orgId, Long workflowId, Long userId, String payloadJson) {
        Workflow workflow = workflowRepository.findByIdAndOrganizationId(workflowId, orgId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + workflowId));

        WorkflowExecution execution = new WorkflowExecution();
        execution.setOrganizationId(orgId);
        execution.setWorkflowId(workflowId);
        execution.setTriggeredByUserId(userId);
        execution.setStatus(ExecutionStatus.RUNNING);
        execution.setPayloadJson(payloadJson);
        execution.setStartedAt(LocalDateTime.now());

        execution = executionRepository.save(execution);

        logRepository.save(new WorkflowExecutionLog(
                execution.getId(), "start", "Execution Initialized", "STARTED",
                "Workflow '" + workflow.getName() + "' initiated by User #" + userId,
                payloadJson
        ));

        auditLogService.logAction(orgId, userId, "EXECUTE_WORKFLOW", "WORKFLOW_EXECUTION",
                String.valueOf(execution.getId()), "{\"workflowId\":" + workflowId + "}");

        // Parse and process definition
        processWorkflow(execution, workflow, "start", null);

        return executionRepository.findById(execution.getId()).orElse(execution);
    }

    private void processWorkflow(WorkflowExecution execution, Workflow workflow, String fromNodeKey, String previousDecision) {
        try {
            String defJson = workflow.getDefinitionJson();
            if (defJson == null || defJson.isBlank()) {
                completeExecution(execution, "Workflow completed (empty definition)");
                return;
            }

            JsonNode root = objectMapper.readTree(defJson);
            JsonNode nodesNode = root.has("nodes") ? root.get("nodes") : null;
            JsonNode edgesNode = root.has("edges") ? root.get("edges") : null;

            if (nodesNode == null || !nodesNode.isArray()) {
                completeExecution(execution, "Workflow completed successfully");
                return;
            }

            // Find start or next node
            JsonNode currentNode = null;
            if ("start".equals(fromNodeKey)) {
                for (JsonNode n : nodesNode) {
                    String type = n.has("type") ? n.get("type").asText().toUpperCase() : "";
                    if ("START".equals(type) || "INPUT".equals(type)) {
                        currentNode = n;
                        break;
                    }
                }
                if (currentNode == null && nodesNode.size() > 0) {
                    currentNode = nodesNode.get(0);
                }
            } else if (fromNodeKey != null) {
                // Resume from next node following fromNodeKey
                currentNode = findNextNode(fromNodeKey, edgesNode, nodesNode, previousDecision);
                if (currentNode == null) {
                    // Fallback to any edge without condition
                    currentNode = findNextNode(fromNodeKey, edgesNode, nodesNode, null);
                }
            }

            // If we have an active node, advance through nodes
            while (currentNode != null) {
                String nodeKey = currentNode.has("id") ? currentNode.get("id").asText() : "node";
                String nodeType = currentNode.has("type") ? currentNode.get("type").asText().toUpperCase() : "TASK";
                String title = currentNode.has("title") ? currentNode.get("title").asText() :
                               currentNode.has("data") && currentNode.get("data").has("label") ?
                                       currentNode.get("data").get("label").asText() : nodeType;

                execution.setCurrentNodeKey(nodeKey);
                executionRepository.save(execution);

                if ("START".equals(nodeType) || "INPUT".equals(nodeType)) {
                    logRepository.save(new WorkflowExecutionLog(
                            execution.getId(), nodeKey, title, "SUCCESS",
                            "Start trigger validated", null
                    ));
                } else if ("TASK".equals(nodeType)) {
                    // Create an automated task
                    Task task = new Task();
                    task.setOrganizationId(execution.getOrganizationId());
                    task.setWorkflowExecutionId(execution.getId());
                    task.setTitle(title);
                    task.setDescription("Automated workflow task generated from '" + workflow.getName() + "'");
                    task.setAssignedToUserId(execution.getTriggeredByUserId());
                    task.setCreatedByUserId(execution.getTriggeredByUserId());
                    task.setStatus(TaskStatus.IN_PROGRESS);
                    task.setPriority(TaskPriority.MEDIUM);
                    taskRepository.save(task);

                    logRepository.save(new WorkflowExecutionLog(
                            execution.getId(), nodeKey, title, "SUCCESS",
                            "Automated task created & dispatched: " + title, null
                    ));
                } else if ("NOTIFICATION".equals(nodeType)) {
                    notificationService.sendNotification(
                            execution.getOrganizationId(),
                            execution.getTriggeredByUserId(),
                            title,
                            "Workflow notification: " + title + " processed.",
                            NotificationType.WORKFLOW_COMPLETED,
                            "/workflows"
                    );

                    logRepository.save(new WorkflowExecutionLog(
                            execution.getId(), nodeKey, title, "SUCCESS",
                            "Notification dispatched to user", null
                    ));
                } else if ("APPROVAL".equals(nodeType)) {
                    // PAUSE EXECUTION FOR APPROVAL
                    execution.setStatus(ExecutionStatus.PAUSED_APPROVAL);
                    executionRepository.save(execution);

                    Approval approval = new Approval();
                    approval.setOrganizationId(execution.getOrganizationId());
                    approval.setWorkflowExecutionId(execution.getId());
                    approval.setRequesterUserId(execution.getTriggeredByUserId());
                    // Find an approver (manager or admin)
                    approval.setRequestType(ApprovalType.LEAVE);
                    approval.setTitle(title + " (" + workflow.getName() + ")");
                    approval.setDetailsJson(execution.getPayloadJson());
                    approval.setStatus(ApprovalStatus.PENDING);
                    approvalRepository.save(approval);

                    logRepository.save(new WorkflowExecutionLog(
                            execution.getId(), nodeKey, title, "WAITING_APPROVAL",
                            "Execution paused awaiting approval decision", null
                    ));

                    return; // PAUSED!
                } else if ("END".equals(nodeType) || "OUTPUT".equals(nodeType)) {
                    completeExecution(execution, "All workflow nodes completed successfully");
                    return;
                }

                // Find next node via edges
                currentNode = findNextNode(nodeKey, edgesNode, nodesNode, null);
            }

            completeExecution(execution, "Workflow completed successfully");

        } catch (Exception ex) {
            execution.setStatus(ExecutionStatus.FAILED);
            execution.setErrorMessage("Execution error: " + ex.getMessage());
            execution.setCompletedAt(LocalDateTime.now());
            executionRepository.save(execution);

            logRepository.save(new WorkflowExecutionLog(
                    execution.getId(), "error", "Execution Failed", "FAILED",
                    ex.getMessage(), null
            ));
        }
    }

    private JsonNode findNextNode(String currentKey, JsonNode edgesNode, JsonNode nodesNode, String condition) {
        if (edgesNode == null || !edgesNode.isArray()) return null;

        for (JsonNode edge : edgesNode) {
            String source = edge.has("source") ? edge.get("source").asText() : "";
            if (currentKey.equals(source)) {
                if (condition != null && edge.has("condition")) {
                    String edgeCond = edge.get("condition").asText();
                    if (!condition.equalsIgnoreCase(edgeCond)) {
                        continue;
                    }
                }
                String target = edge.has("target") ? edge.get("target").asText() : "";
                for (JsonNode n : nodesNode) {
                    if (target.equals(n.has("id") ? n.get("id").asText() : "")) {
                        return n;
                    }
                }
            }
        }
        return null;
    }

    @Transactional
    public WorkflowExecution resumeWorkflow(Long executionId, ApprovalStatus decision, String notes) {
        WorkflowExecution execution = executionRepository.findById(executionId)
                .orElseThrow(() -> new IllegalArgumentException("Execution not found: " + executionId));

        Workflow workflow = workflowRepository.findById(execution.getWorkflowId())
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found"));

        if (decision == ApprovalStatus.APPROVED) {
            logRepository.save(new WorkflowExecutionLog(
                    execution.getId(), execution.getCurrentNodeKey(), "Approval Decision: APPROVED", "SUCCESS",
                    "Approval granted by manager. Notes: " + (notes != null ? notes : "Approved"), null
            ));

            notificationService.sendNotification(
                    execution.getOrganizationId(),
                    execution.getTriggeredByUserId(),
                    "Request Approved!",
                    "Your request in '" + workflow.getName() + "' was approved.",
                    NotificationType.REQUEST_APPROVED,
                    "/approvals"
            );

            // Resume and process remaining nodes
            execution.setStatus(ExecutionStatus.RUNNING);
            executionRepository.save(execution);
            processWorkflow(execution, workflow, execution.getCurrentNodeKey(), "APPROVED");

        } else if (decision == ApprovalStatus.REJECTED) {
            execution.setStatus(ExecutionStatus.REJECTED);
            execution.setCompletedAt(LocalDateTime.now());
            executionRepository.save(execution);

            logRepository.save(new WorkflowExecutionLog(
                    execution.getId(), execution.getCurrentNodeKey(), "Approval Decision: REJECTED", "FAILED",
                    "Request rejected. Notes: " + (notes != null ? notes : "No notes"), null
            ));

            notificationService.sendNotification(
                    execution.getOrganizationId(),
                    execution.getTriggeredByUserId(),
                    "Request Rejected",
                    "Your request in '" + workflow.getName() + "' was rejected. Notes: " + notes,
                    NotificationType.REQUEST_REJECTED,
                    "/approvals"
            );
        } else {
            logRepository.save(new WorkflowExecutionLog(
                    execution.getId(), execution.getCurrentNodeKey(), "Decision: " + decision, "WAITING_APPROVAL",
                    notes, null
            ));
        }

        return execution;
    }

    private void completeExecution(WorkflowExecution execution, String message) {
        execution.setStatus(ExecutionStatus.COMPLETED);
        execution.setCompletedAt(LocalDateTime.now());
        executionRepository.save(execution);

        logRepository.save(new WorkflowExecutionLog(
                execution.getId(), "end", "Execution Completed", "SUCCESS",
                message, null
        ));

        notificationService.sendNotification(
                execution.getOrganizationId(),
                execution.getTriggeredByUserId(),
                "Workflow Completed",
                message,
                NotificationType.WORKFLOW_COMPLETED,
                "/workflows"
        );
    }
}
