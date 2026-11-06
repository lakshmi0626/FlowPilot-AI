package com.flowpilot.service;

import com.flowpilot.dto.TaskCreateRequest;
import com.flowpilot.model.Task;
import com.flowpilot.model.TaskPriority;
import com.flowpilot.model.TaskStatus;
import com.flowpilot.repository.TaskCommentRepository;
import com.flowpilot.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskCommentRepository commentRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTask() {
        TaskCreateRequest req = new TaskCreateRequest();
        req.setTitle("Review Q3 Budget");
        req.setDescription("Review financial reports for Q3");
        req.setPriority(TaskPriority.HIGH);
        req.setDueDate(LocalDateTime.now().plusDays(5));
        req.setAssignedToUserId(2L);

        when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> {
            Task t = invocation.getArgument(0);
            t.setId(101L);
            return t;
        });

        Task created = taskService.createTask(1L, 1L, req);

        assertNotNull(created);
        assertEquals("Review Q3 Budget", created.getTitle());
        assertEquals(TaskPriority.HIGH, created.getPriority());
        assertEquals(TaskStatus.TODO, created.getStatus());
        assertEquals(1L, created.getOrganizationId());
        verify(taskRepository, times(1)).save(any(Task.class));
        verify(auditLogService, times(1)).logAction(eq(1L), eq(1L), eq("CREATE_TASK"), eq("TASK"), anyString(), anyString());
    }

    @Test
    void testGetTasksFiltered() {
        Task t1 = new Task();
        t1.setId(1L);
        t1.setOrganizationId(1L);
        t1.setStatus(TaskStatus.TODO);
        t1.setPriority(TaskPriority.HIGH);

        Task t2 = new Task();
        t2.setId(2L);
        t2.setOrganizationId(1L);
        t2.setStatus(TaskStatus.DONE);
        t2.setPriority(TaskPriority.LOW);

        when(taskRepository.findByOrganizationIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(t1, t2));

        List<Task> results = taskService.getTasks(1L, "TODO", "HIGH", null);
        assertEquals(1, results.size());
        assertEquals(1L, results.get(0).getId());
    }

    @Test
    void testGetTaskById() {
        Task task = new Task();
        task.setId(5L);
        task.setOrganizationId(1L);
        task.setTitle("Prepare Deployment");

        when(taskRepository.findByIdAndOrganizationId(5L, 1L)).thenReturn(Optional.of(task));

        Task result = taskService.getTask(1L, 5L);
        assertNotNull(result);
        assertEquals("Prepare Deployment", result.getTitle());
    }
}
