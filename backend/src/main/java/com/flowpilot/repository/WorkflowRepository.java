package com.flowpilot.repository;

import com.flowpilot.model.Workflow;
import com.flowpilot.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByOrganizationId(Long organizationId);
    List<Workflow> findByOrganizationIdAndStatus(Long organizationId, WorkflowStatus status);
    Optional<Workflow> findByIdAndOrganizationId(Long id, Long organizationId);
    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndStatus(Long organizationId, WorkflowStatus status);
}
