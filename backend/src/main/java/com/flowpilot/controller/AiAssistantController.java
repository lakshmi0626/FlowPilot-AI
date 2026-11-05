package com.flowpilot.controller;

import com.flowpilot.dto.AiWorkflowGenerateRequest;
import com.flowpilot.service.AiWorkflowAssistantService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiAssistantController {

    private final AiWorkflowAssistantService aiService;

    public AiAssistantController(AiWorkflowAssistantService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/generate-workflow")
    public ResponseEntity<Map<String, Object>> generateWorkflow(@Valid @RequestBody AiWorkflowGenerateRequest req) {
        return ResponseEntity.ok(aiService.generateWorkflowFromPrompt(req.getPrompt()));
    }
}
