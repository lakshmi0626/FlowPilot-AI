package com.flowpilot.controller;

import com.flowpilot.dto.CommentRequest;
import com.flowpilot.dto.TaskCreateRequest;
import com.flowpilot.dto.TaskUpdateRequest;
import com.flowpilot.model.Task;
import com.flowpilot.model.TaskComment;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) Long assigneeId) {
        return ResponseEntity.ok(taskService.getTasks(principal.getOrganizationId(), status, priority, assigneeId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTask(principal.getOrganizationId(), id));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody TaskCreateRequest req) {
        return ResponseEntity.ok(taskService.createTask(principal.getOrganizationId(), principal.getId(), req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @RequestBody TaskUpdateRequest req) {
        return ResponseEntity.ok(taskService.updateTask(principal.getOrganizationId(), principal.getId(), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteTask(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        taskService.deleteTask(principal.getOrganizationId(), principal.getId(), id);
        return ResponseEntity.ok(Map.of("message", "Task deleted successfully."));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TaskComment>> getComments(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getComments(principal.getOrganizationId(), id));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TaskComment> addComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest req) {
        return ResponseEntity.ok(taskService.addComment(principal.getOrganizationId(), principal.getId(), id, req));
    }
}
