package com.flowpilot.dto;

import com.flowpilot.model.WorkflowStatus;
import jakarta.validation.constraints.NotBlank;

public class WorkflowCreateRequest {

    @NotBlank(message = "Workflow name is required")
    private String name;

    private String description;
    private String category = "GENERAL";
    private WorkflowStatus status = WorkflowStatus.DRAFT;
    private String definitionJson;

    public WorkflowCreateRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public WorkflowStatus getStatus() { return status; }
    public void setStatus(WorkflowStatus status) { this.status = status; }

    public String getDefinitionJson() { return definitionJson; }
    public void setDefinitionJson(String definitionJson) { this.definitionJson = definitionJson; }
}
