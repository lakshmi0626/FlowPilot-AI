package com.flowpilot.repository;

import com.flowpilot.model.Task;
import com.flowpilot.model.TaskPriority;
import com.flowpilot.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByOrganizationId(Long organizationId);
    List<Task> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
    List<Task> findByOrganizationIdAndAssignedToUserId(Long organizationId, Long assignedToUserId);
    Optional<Task> findByIdAndOrganizationId(Long id, Long organizationId);

    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndStatus(Long organizationId, TaskStatus status);
    long countByOrganizationIdAndStatusNotAndDueDateBefore(Long organizationId, TaskStatus status, LocalDateTime now);
}
