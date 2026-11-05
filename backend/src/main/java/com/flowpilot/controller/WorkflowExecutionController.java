package com.flowpilot.controller;

import com.flowpilot.dto.WorkflowExecutionRequest;
import com.flowpilot.model.WorkflowExecution;
import com.flowpilot.model.WorkflowExecutionLog;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.WorkflowEngineService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executions")
public class WorkflowExecutionController {

    private final WorkflowEngineService workflowEngineService;

    public WorkflowExecutionController(WorkflowEngineService workflowEngineService) {
        this.workflowEngineService = workflowEngineService;
    }

    @GetMapping
    public ResponseEntity<List<WorkflowExecution>> getExecutions(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(workflowEngineService.getExecutions(principal.getOrganizationId()));
    }

    @GetMapping("/workflow/{workflowId}")
    public ResponseEntity<List<WorkflowExecution>> getExecutionsForWorkflow(@PathVariable Long workflowId) {
        return ResponseEntity.ok(workflowEngineService.getExecutionsForWorkflow(workflowId));
    }

    @PostMapping("/workflow/{workflowId}/execute")
    public ResponseEntity<WorkflowExecution> executeWorkflow(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long workflowId,
            @RequestBody(required = false) WorkflowExecutionRequest req) {
        String payload = req != null ? req.getPayloadJson() : "{}";
        return ResponseEntity.ok(workflowEngineService.executeWorkflow(
                principal.getOrganizationId(), workflowId, principal.getId(), payload
        ));
    }

    @GetMapping("/{id}/logs")
    public ResponseEntity<List<WorkflowExecutionLog>> getExecutionLogs(@PathVariable Long id) {
        return ResponseEntity.ok(workflowEngineService.getExecutionLogs(id));
    }
}
