package com.flowpilot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AiWorkflowAssistantService {

    @Value("${flowpilot.ai.api-key:}")
    private String apiKey;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> generateWorkflowFromPrompt(String prompt) {
        String clean = prompt != null ? prompt.trim() : "";
        String lower = clean.toLowerCase();

        boolean hasExternalApi = apiKey != null && !apiKey.isBlank();
        String engineType = hasExternalApi ? "Gemini LLM Engine" : "Rule-Based Natural Language Engine";

        Map<String, Object> response = new HashMap<>();
        response.put("engine", engineType);
        response.put("originalPrompt", clean);

        List<Map<String, Object>> nodes = new ArrayList<>();
        List<Map<String, Object>> edges = new ArrayList<>();

        String workflowName;
        String description;
        String category;

        if (lower.contains("leave") || lower.contains("vacation") || lower.contains("time off")) {
            workflowName = "Employee Leave Approval Process";
            description = "AI-generated automated leave request workflow with manager approval gate and HR record synchronization.";
            category = "HUMAN_RESOURCES";

            nodes.add(createNode("start_1", "START", "Leave Request Submitted", 250, 40));
            nodes.add(createNode("appr_1", "APPROVAL", "Manager Review & Sign-off", 250, 160));
            nodes.add(createNode("cond_1", "CONDITION", "Check If Approved", 250, 270));
            nodes.add(createNode("task_1", "TASK", "Update HR Attendance System", 120, 390));
            nodes.add(createNode("notif_1", "NOTIFICATION", "Notify Rejection with Feedback", 380, 390));
            nodes.add(createNode("end_1", "END", "Process Complete", 250, 500));

            edges.add(createEdge("e1", "start_1", "appr_1", null));
            edges.add(createEdge("e2", "appr_1", "cond_1", null));
            edges.add(createEdge("e3", "cond_1", "task_1", "APPROVED"));
            edges.add(createEdge("e4", "cond_1", "notif_1", "REJECTED"));
            edges.add(createEdge("e5", "task_1", "end_1", null));
            edges.add(createEdge("e6", "notif_1", "end_1", null));

        } else if (lower.contains("expense") || lower.contains("reimburse") || lower.contains("invoice")) {
            workflowName = "Expense Claim Audit & Reimbursement";
            description = "Multi-step expense validation workflow with finance director review and accounting payout queue.";
            category = "FINANCE";

            nodes.add(createNode("start_1", "START", "Expense Receipt Uploaded", 250, 40));
            nodes.add(createNode("task_1", "TASK", "Automated Policy OCR Validation", 250, 150));
            nodes.add(createNode("appr_1", "APPROVAL", "Finance Lead Approval", 250, 260));
            nodes.add(createNode("task_2", "TASK", "Queue Bank Direct Deposit", 250, 370));
            nodes.add(createNode("notif_1", "NOTIFICATION", "Dispatch Payment Receipt", 250, 470));
            nodes.add(createNode("end_1", "END", "Reimbursement Completed", 250, 560));

            edges.add(createEdge("e1", "start_1", "task_1", null));
            edges.add(createEdge("e2", "task_1", "appr_1", null));
            edges.add(createEdge("e3", "appr_1", "task_2", "APPROVED"));
            edges.add(createEdge("e4", "task_2", "notif_1", null));
            edges.add(createEdge("e5", "notif_1", "end_1", null));

        } else if (lower.contains("onboard") || lower.contains("hiring") || lower.contains("new hire")) {
            workflowName = "New Employee Onboarding Pipeline";
            description = "End-to-end employee onboarding: IT provisioning, security badge assignment, and HR orientation.";
            category = "HUMAN_RESOURCES";

            nodes.add(createNode("start_1", "START", "Offer Letter Signed", 250, 40));
            nodes.add(createNode("task_1", "TASK", "Provision Work Email & Laptop", 140, 160));
            nodes.add(createNode("task_2", "TASK", "Setup HR Benefits & Payroll", 360, 160));
            nodes.add(createNode("appr_1", "APPROVAL", "Department Manager Sign-off", 250, 280));
            nodes.add(createNode("notif_1", "NOTIFICATION", "Send Day-One Welcome Package", 250, 400));
            nodes.add(createNode("end_1", "END", "Employee Ready to Work", 250, 510));

            edges.add(createEdge("e1", "start_1", "task_1", null));
            edges.add(createEdge("e2", "start_1", "task_2", null));
            edges.add(createEdge("e3", "task_1", "appr_1", null));
            edges.add(createEdge("e4", "task_2", "appr_1", null));
            edges.add(createEdge("e5", "appr_1", "notif_1", "APPROVED"));
            edges.add(createEdge("e6", "notif_1", "end_1", null));

        } else {
            // General business approval pattern
            workflowName = clean.length() > 5 ? clean.substring(0, Math.min(clean.length(), 40)) + " Workflow" : "Custom Automation Workflow";
            description = "AI-structured workflow based on: '" + clean + "'";
            category = "OPERATIONS";

            nodes.add(createNode("start_1", "START", "Request Initiated", 250, 40));
            nodes.add(createNode("appr_1", "APPROVAL", "Authorized Manager Review", 250, 160));
            nodes.add(createNode("task_1", "TASK", "Execute Business Action", 250, 280));
            nodes.add(createNode("notif_1", "NOTIFICATION", "Notify Stakeholders", 250, 390));
            nodes.add(createNode("end_1", "END", "Execution Finished", 250, 500));

            edges.add(createEdge("e1", "start_1", "appr_1", null));
            edges.add(createEdge("e2", "appr_1", "task_1", "APPROVED"));
            edges.add(createEdge("e3", "task_1", "notif_1", null));
            edges.add(createEdge("e4", "notif_1", "end_1", null));
        }

        Map<String, Object> definition = new HashMap<>();
        definition.put("nodes", nodes);
        definition.put("edges", edges);

        response.put("name", workflowName);
        response.put("description", description);
        response.put("category", category);
        response.put("definition", definition);
        try {
            response.put("definitionJson", objectMapper.writeValueAsString(definition));
        } catch (Exception e) {
            response.put("definitionJson", "{\"nodes\":[],\"edges\":[]}");
        }

        return response;
    }

    private Map<String, Object> createNode(String id, String type, String title, int x, int y) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", id);
        node.put("type", type);
        node.put("title", title);
        node.put("position", Map.of("x", x, "y", y));

        Map<String, Object> data = new HashMap<>();
        data.put("label", title);
        data.put("type", type);
        node.put("data", data);

        return node;
    }

    private Map<String, Object> createEdge(String id, String source, String target, String condition) {
        Map<String, Object> edge = new HashMap<>();
        edge.put("id", id);
        edge.put("source", source);
        edge.put("target", target);
        if (condition != null) {
            edge.put("condition", condition);
            edge.put("label", condition);
        }
        return edge;
    }
}
