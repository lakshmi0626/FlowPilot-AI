package com.flowpilot.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AiWorkflowAssistantServiceTest {

    private AiWorkflowAssistantService aiAssistantService;

    @BeforeEach
    void setUp() {
        aiAssistantService = new AiWorkflowAssistantService();
    }

    @Test
    void testGenerateLeaveWorkflowFromPrompt() {
        Map<String, Object> result = aiAssistantService.generateWorkflowFromPrompt("Automate employee vacation and leave approval");

        assertNotNull(result);
        assertEquals("Employee Leave Approval Process", result.get("name"));
        assertEquals("HUMAN_RESOURCES", result.get("category"));

        @SuppressWarnings("unchecked")
        Map<String, Object> def = (Map<String, Object>) result.get("definition");
        assertNotNull(def);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) def.get("nodes");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> edges = (List<Map<String, Object>>) def.get("edges");

        assertEquals(6, nodes.size());
        assertEquals(6, edges.size());

        // Verify Start and End nodes exist
        assertTrue(nodes.stream().anyMatch(n -> "START".equals(n.get("type"))));
        assertTrue(nodes.stream().anyMatch(n -> "END".equals(n.get("type"))));
        assertTrue(nodes.stream().anyMatch(n -> "APPROVAL".equals(n.get("type"))));
        assertTrue(nodes.stream().anyMatch(n -> "CONDITION".equals(n.get("type"))));
    }

    @Test
    void testGenerateExpenseWorkflowFromPrompt() {
        Map<String, Object> result = aiAssistantService.generateWorkflowFromPrompt("Process travel expense reimbursements and invoices");

        assertNotNull(result);
        assertEquals("Expense Claim Audit & Reimbursement", result.get("name"));
        assertEquals("FINANCE", result.get("category"));

        String defJson = (String) result.get("definitionJson");
        assertNotNull(defJson);
        assertTrue(defJson.contains("nodes"));
        assertTrue(defJson.contains("edges"));
    }

    @Test
    void testGenerateGenericFallbackWorkflow() {
        Map<String, Object> result = aiAssistantService.generateWorkflowFromPrompt("Run periodic server log maintenance");

        assertNotNull(result);
        assertNotNull(result.get("name"));
        assertNotNull(result.get("category"));

        @SuppressWarnings("unchecked")
        Map<String, Object> def = (Map<String, Object>) result.get("definition");
        assertNotNull(def);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) def.get("nodes");
        assertFalse(nodes.isEmpty());
    }
}
