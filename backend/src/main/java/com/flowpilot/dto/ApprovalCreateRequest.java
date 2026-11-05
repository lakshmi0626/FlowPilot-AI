package com.flowpilot.dto;

import com.flowpilot.model.ApprovalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApprovalCreateRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Request type is required")
    private ApprovalType requestType = ApprovalType.LEAVE;

    private Long approverUserId;
    private Long workflowExecutionId;
    private String detailsJson;

    public ApprovalCreateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ApprovalType getRequestType() { return requestType; }
    public void setRequestType(ApprovalType requestType) { this.requestType = requestType; }

    public Long getApproverUserId() { return approverUserId; }
    public void setApproverUserId(Long approverUserId) { this.approverUserId = approverUserId; }

    public Long getWorkflowExecutionId() { return workflowExecutionId; }
    public void setWorkflowExecutionId(Long workflowExecutionId) { this.workflowExecutionId = workflowExecutionId; }

    public String getDetailsJson() { return detailsJson; }
    public void setDetailsJson(String detailsJson) { this.detailsJson = detailsJson; }
}
