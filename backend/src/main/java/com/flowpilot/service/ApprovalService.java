package com.flowpilot.service;

import com.flowpilot.dto.ApprovalCreateRequest;
import com.flowpilot.dto.ApprovalDecisionRequest;
import com.flowpilot.model.*;
import com.flowpilot.repository.ApprovalRepository;
import com.flowpilot.repository.UserRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalRepository approvalRepository;
    private final UserRepository userRepository;
    private final WorkflowEngineService workflowEngineService;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public ApprovalService(ApprovalRepository approvalRepository,
                           UserRepository userRepository,
                           @Lazy WorkflowEngineService workflowEngineService,
                           NotificationService notificationService,
                           AuditLogService auditLogService) {
        this.approvalRepository = approvalRepository;
        this.userRepository = userRepository;
        this.workflowEngineService = workflowEngineService;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    public List<Approval> getApprovals(Long orgId, String status, Long userId) {
        List<Approval> list = approvalRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
        return list.stream()
                .filter(a -> status == null || status.isBlank() || a.getStatus().name().equalsIgnoreCase(status))
                .toList();
    }

    public Approval getApproval(Long orgId, Long id) {
        return approvalRepository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new IllegalArgumentException("Approval not found: " + id));
    }

    @Transactional
    public Approval createApproval(Long orgId, Long requesterId, ApprovalCreateRequest req) {
        Approval approval = new Approval();
        approval.setOrganizationId(orgId);
        approval.setRequesterUserId(requesterId);
        approval.setWorkflowExecutionId(req.getWorkflowExecutionId());
        approval.setRequestType(req.getRequestType());
        approval.setTitle(req.getTitle());
        approval.setDetailsJson(req.getDetailsJson());
        approval.setStatus(ApprovalStatus.PENDING);

        // Assign approver: either requested or the user's manager
        Long approverId = req.getApproverUserId();
        if (approverId == null) {
            User requester = userRepository.findById(requesterId).orElse(null);
            if (requester != null && requester.getManagerId() != null) {
                approverId = requester.getManagerId();
            } else {
                // Find first company admin or manager
                List<User> managers = userRepository.findByOrganizationIdAndRole(orgId, Role.MANAGER);
                if (!managers.isEmpty()) {
                    approverId = managers.get(0).getId();
                } else {
                    List<User> admins = userRepository.findByOrganizationIdAndRole(orgId, Role.COMPANY_ADMIN);
                    if (!admins.isEmpty()) approverId = admins.get(0).getId();
                }
            }
        }
        approval.setApproverUserId(approverId);

        Approval saved = approvalRepository.save(approval);

        auditLogService.logAction(orgId, requesterId, "SUBMIT_APPROVAL", "APPROVAL",
                String.valueOf(saved.getId()), "{\"type\":\"" + saved.getRequestType() + "\"}");

        if (approverId != null) {
            notificationService.sendNotification(
                    orgId,
                    approverId,
                    "New " + saved.getRequestType() + " Request",
                    "A new request requires your review: " + saved.getTitle(),
                    NotificationType.APPROVAL_REQUEST,
                    "/approvals"
            );
        }

        return saved;
    }

    @Transactional
    public Approval decideApproval(Long orgId, Long approverUserId, Long id, ApprovalDecisionRequest req) {
        Approval approval = getApproval(orgId, id);

        approval.setStatus(req.getDecision());
        approval.setDecisionNotes(req.getDecisionNotes());
        approval.setDecidedAt(LocalDateTime.now());
        approval.setApproverUserId(approverUserId);

        Approval saved = approvalRepository.save(approval);

        auditLogService.logAction(orgId, approverUserId, "DECIDE_APPROVAL", "APPROVAL",
                String.valueOf(saved.getId()), "{\"decision\":\"" + req.getDecision() + "\"}");

        NotificationType notifType = req.getDecision() == ApprovalStatus.APPROVED ?
                NotificationType.REQUEST_APPROVED : NotificationType.REQUEST_REJECTED;

        notificationService.sendNotification(
                orgId,
                saved.getRequesterUserId(),
                "Request " + req.getDecision().name().replace('_', ' '),
                "Your request '" + saved.getTitle() + "' was " + req.getDecision().name().toLowerCase() +
                        (req.getDecisionNotes() != null ? ": " + req.getDecisionNotes() : "."),
                notifType,
                "/approvals"
        );

        // Resume workflow execution if linked
        if (saved.getWorkflowExecutionId() != null) {
            workflowEngineService.resumeWorkflow(
                    saved.getWorkflowExecutionId(),
                    req.getDecision(),
                    req.getDecisionNotes()
            );
        }

        return saved;
    }
}
