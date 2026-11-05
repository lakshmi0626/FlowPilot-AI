package com.flowpilot.service;

import com.flowpilot.dto.CommentRequest;
import com.flowpilot.dto.TaskCreateRequest;
import com.flowpilot.dto.TaskUpdateRequest;
import com.flowpilot.model.*;
import com.flowpilot.repository.TaskCommentRepository;
import com.flowpilot.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository commentRepository;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public TaskService(TaskRepository taskRepository,
                       TaskCommentRepository commentRepository,
                       NotificationService notificationService,
                       AuditLogService auditLogService) {
        this.taskRepository = taskRepository;
        this.commentRepository = commentRepository;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    public List<Task> getTasks(Long orgId, String status, String priority, Long assigneeId) {
        List<Task> list = taskRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
        return list.stream()
                .filter(t -> status == null || status.isBlank() || t.getStatus().name().equalsIgnoreCase(status))
                .filter(t -> priority == null || priority.isBlank() || t.getPriority().name().equalsIgnoreCase(priority))
                .filter(t -> assigneeId == null || (t.getAssignedToUserId() != null && t.getAssignedToUserId().equals(assigneeId)))
                .toList();
    }

    public Task getTask(Long orgId, Long id) {
        return taskRepository.findByIdAndOrganizationId(id, orgId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + id));
    }

    @Transactional
    public Task createTask(Long orgId, Long userId, TaskCreateRequest req) {
        Task task = new Task();
        task.setOrganizationId(orgId);
        task.setCreatedByUserId(userId);
        task.setTitle(req.getTitle());
        task.setDescription(req.getDescription());
        task.setAssignedToUserId(req.getAssignedToUserId());
        task.setPriority(req.getPriority() != null ? req.getPriority() : TaskPriority.MEDIUM);
        task.setStatus(req.getStatus() != null ? req.getStatus() : TaskStatus.TODO);
        task.setDueDate(req.getDueDate());

        Task saved = taskRepository.save(task);

        auditLogService.logAction(orgId, userId, "CREATE_TASK", "TASK",
                String.valueOf(saved.getId()), "{\"title\":\"" + saved.getTitle() + "\"}");

        if (req.getAssignedToUserId() != null && !req.getAssignedToUserId().equals(userId)) {
            notificationService.sendNotification(
                    orgId,
                    req.getAssignedToUserId(),
                    "New Task Assigned",
                    "You have been assigned to: " + saved.getTitle(),
                    NotificationType.TASK_ASSIGNED,
                    "/tasks"
            );
        }

        return saved;
    }

    @Transactional
    public Task updateTask(Long orgId, Long userId, Long id, TaskUpdateRequest req) {
        Task task = getTask(orgId, id);
        if (req.getTitle() != null) task.setTitle(req.getTitle());
        if (req.getDescription() != null) task.setDescription(req.getDescription());
        if (req.getPriority() != null) task.setPriority(req.getPriority());
        if (req.getStatus() != null) task.setStatus(req.getStatus());
        if (req.getDueDate() != null) task.setDueDate(req.getDueDate());

        if (req.getAssignedToUserId() != null && !req.getAssignedToUserId().equals(task.getAssignedToUserId())) {
            task.setAssignedToUserId(req.getAssignedToUserId());
            notificationService.sendNotification(
                    orgId,
                    req.getAssignedToUserId(),
                    "Task Assigned",
                    "You have been assigned to: " + task.getTitle(),
                    NotificationType.TASK_ASSIGNED,
                    "/tasks"
            );
        }

        task.setUpdatedAt(LocalDateTime.now());
        Task saved = taskRepository.save(task);

        auditLogService.logAction(orgId, userId, "UPDATE_TASK", "TASK",
                String.valueOf(saved.getId()), "{\"status\":\"" + saved.getStatus() + "\"}");

        return saved;
    }

    @Transactional
    public void deleteTask(Long orgId, Long userId, Long id) {
        Task task = getTask(orgId, id);
        taskRepository.delete(task);
        auditLogService.logAction(orgId, userId, "DELETE_TASK", "TASK",
                String.valueOf(id), "{\"title\":\"" + task.getTitle() + "\"}");
    }

    @Transactional
    public TaskComment addComment(Long orgId, Long userId, Long taskId, CommentRequest req) {
        Task task = getTask(orgId, taskId);
        TaskComment comment = new TaskComment(task.getId(), userId, req.getCommentText());
        return commentRepository.save(comment);
    }

    public List<TaskComment> getComments(Long orgId, Long taskId) {
        getTask(orgId, taskId); // ensure belongs to tenant
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId);
    }
}
