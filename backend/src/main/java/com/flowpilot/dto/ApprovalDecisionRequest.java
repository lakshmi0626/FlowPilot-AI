package com.flowpilot.dto;

import com.flowpilot.model.ApprovalStatus;
import jakarta.validation.constraints.NotNull;

public class ApprovalDecisionRequest {

    @NotNull(message = "Decision is required")
    private ApprovalStatus decision; // APPROVED, REJECTED, CHANGES_REQUESTED

    private String decisionNotes;

    public ApprovalDecisionRequest() {}

    public ApprovalDecisionRequest(ApprovalStatus decision, String decisionNotes) {
        this.decision = decision;
        this.decisionNotes = decisionNotes;
    }

    public ApprovalStatus getDecision() { return decision; }
    public void setDecision(ApprovalStatus decision) { this.decision = decision; }

    public String getDecisionNotes() { return decisionNotes; }
    public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }
}
