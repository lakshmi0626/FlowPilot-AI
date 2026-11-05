package com.flowpilot.service;

import com.flowpilot.dto.WorkflowCreateRequest;
import com.flowpilot.model.Workflow;
import com.flowpilot.model.WorkflowStatus;
import com.flowpilot.repository.WorkflowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final AuditLogService auditLogService;

    public WorkflowService(WorkflowRepository workflowRepository, AuditLogService auditLogService) {
        this.workflowRepository = workflowRepository;
        this.auditLogService = auditLogService;
    }

    public List<Workflow> getWorkflows(Long orgId) {
        return workflowRepository.findByOrganizationId(orgId);
    }

    public Workflow getWorkflow(Long orgId, Long id) {
        return workflowRepository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new IllegalArgumentException("Workflow not found: " + id));
    }

    @Transactional
    public Workflow createWorkflow(Long orgId, Long userId, WorkflowCreateRequest req) {
        Workflow wf = new Workflow();
        wf.setOrganizationId(orgId);
        wf.setCreatedByUserId(userId);
        wf.setName(req.getName());
        wf.setDescription(req.getDescription());
        wf.setCategory(req.getCategory() != null ? req.getCategory() : "GENERAL");
        wf.setStatus(req.getStatus() != null ? req.getStatus() : WorkflowStatus.DRAFT);
        wf.setVersion(1);
        wf.setDefinitionJson(req.getDefinitionJson() != null ? req.getDefinitionJson() : "{\"nodes\":[],\"edges\":[]}");

        Workflow saved = workflowRepository.save(wf);

        auditLogService.logAction(orgId, userId, "CREATE_WORKFLOW", "WORKFLOW",
                String.valueOf(saved.getId()), "{\"name\":\"" + saved.getName() + "\"}");

        return saved;
    }

    @Transactional
    public Workflow updateWorkflow(Long orgId, Long userId, Long id, WorkflowCreateRequest req) {
        Workflow wf = getWorkflow(orgId, id);
        if (req.getName() != null) wf.setName(req.getName());
        if (req.getDescription() != null) wf.setDescription(req.getDescription());
        if (req.getCategory() != null) wf.setCategory(req.getCategory());
        if (req.getStatus() != null) wf.setStatus(req.getStatus());
        if (req.getDefinitionJson() != null) {
            wf.setDefinitionJson(req.getDefinitionJson());
            wf.setVersion(wf.getVersion() + 1);
        }
        wf.setUpdatedAt(LocalDateTime.now());

        Workflow saved = workflowRepository.save(wf);

        auditLogService.logAction(orgId, userId, "UPDATE_WORKFLOW", "WORKFLOW",
                String.valueOf(saved.getId()), "{\"version\":" + saved.getVersion() + "}");

        return saved;
    }

    @Transactional
    public Workflow duplicateWorkflow(Long orgId, Long userId, Long id) {
        Workflow source = getWorkflow(orgId, id);
        Workflow copy = new Workflow();
        copy.setOrganizationId(orgId);
        copy.setCreatedByUserId(userId);
        copy.setName(source.getName() + " (Copy)");
        copy.setDescription(source.getDescription());
        copy.setCategory(source.getCategory());
        copy.setStatus(WorkflowStatus.DRAFT);
        copy.setVersion(1);
        copy.setDefinitionJson(source.getDefinitionJson());

        Workflow saved = workflowRepository.save(copy);

        auditLogService.logAction(orgId, userId, "DUPLICATE_WORKFLOW", "WORKFLOW",
                String.valueOf(saved.getId()), "{\"sourceId\":" + source.getId() + "}");

        return saved;
    }

    @Transactional
    public void deleteWorkflow(Long orgId, Long userId, Long id) {
        Workflow wf = getWorkflow(orgId, id);
        workflowRepository.delete(wf);
        auditLogService.logAction(orgId, userId, "DELETE_WORKFLOW", "WORKFLOW",
                String.valueOf(id), "{\"name\":\"" + wf.getName() + "\"}");
    }

    @Transactional
    public Workflow toggleStatus(Long orgId, Long userId, Long id) {
        Workflow wf = getWorkflow(orgId, id);
        if (wf.getStatus() == WorkflowStatus.ACTIVE) {
            wf.setStatus(WorkflowStatus.PAUSED);
        } else {
            wf.setStatus(WorkflowStatus.ACTIVE);
        }
        wf.setUpdatedAt(LocalDateTime.now());
        return workflowRepository.save(wf);
    }
}
