package com.flowpilot.dto;

import com.flowpilot.model.TaskPriority;
import com.flowpilot.model.TaskStatus;

import java.time.LocalDateTime;

public class TaskUpdateRequest {

    private String title;
    private String description;
    private Long assignedToUserId;
    private TaskPriority priority;
    private TaskStatus status;
    private LocalDateTime dueDate;

    public TaskUpdateRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getAssignedToUserId() { return assignedToUserId; }
    public void setAssignedToUserId(Long assignedToUserId) { this.assignedToUserId = assignedToUserId; }

    public TaskPriority getPriority() { return priority; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}
