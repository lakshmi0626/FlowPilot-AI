package com.flowpilot.dto;

public class WorkflowExecutionRequest {

    private String payloadJson;

    public WorkflowExecutionRequest() {}

    public WorkflowExecutionRequest(String payloadJson) {
        this.payloadJson = payloadJson;
    }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}
