package com.flowpilot.dto;

import jakarta.validation.constraints.NotBlank;

public class AiWorkflowGenerateRequest {

    @NotBlank(message = "Prompt cannot be empty")
    private String prompt;

    public AiWorkflowGenerateRequest() {}

    public AiWorkflowGenerateRequest(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() { return prompt; }
    public void setPrompt(String prompt) { this.prompt = prompt; }
}
