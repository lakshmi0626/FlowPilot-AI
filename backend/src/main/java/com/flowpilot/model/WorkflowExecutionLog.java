package com.flowpilot.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_execution_logs")
public class WorkflowExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_id", nullable = false)
    private Long executionId;

    @Column(name = "node_key", length = 100)
    private String nodeKey;

    @Column(name = "step_name", length = 150)
    private String stepName;

    @Column(nullable = false, length = 50)
    private String status; // STARTED, SUCCESS, WAITING_APPROVAL, FAILED, SKIPPED

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public WorkflowExecutionLog() {}

    public WorkflowExecutionLog(Long executionId, String nodeKey, String stepName, String status, String message, String detailsJson) {
        this.executionId = executionId;
        this.nodeKey = nodeKey;
        this.stepName = stepName;
        this.status = status;
        this.message = message;
        this.detailsJson = detailsJson;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getExecutionId() { return executionId; }
    public void setExecutionId(Long executionId) { this.executionId = executionId; }

    public String getNodeKey() { return nodeKey; }
    public void setNodeKey(String nodeKey) { this.nodeKey = nodeKey; }

    public String getStepName() { return stepName; }
    public void setStepName(String stepName) { this.stepName = stepName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
