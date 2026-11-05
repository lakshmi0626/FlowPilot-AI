package com.flowpilot.repository;

import com.flowpilot.model.ExecutionStatus;
import com.flowpilot.model.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {
    List<WorkflowExecution> findByOrganizationId(Long organizationId);
    List<WorkflowExecution> findByOrganizationIdOrderByStartedAtDesc(Long organizationId);
    List<WorkflowExecution> findByWorkflowId(Long workflowId);
    Optional<WorkflowExecution> findByIdAndOrganizationId(Long id, Long organizationId);
    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndStatus(Long organizationId, ExecutionStatus status);
}
