package com.flowpilot.repository;

import com.flowpilot.model.Approval;
import com.flowpilot.model.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApprovalRepository extends JpaRepository<Approval, Long> {
    List<Approval> findByOrganizationId(Long organizationId);
    List<Approval> findByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
    List<Approval> findByOrganizationIdAndStatus(Long organizationId, ApprovalStatus status);
    List<Approval> findByOrganizationIdAndApproverUserId(Long organizationId, Long approverUserId);
    List<Approval> findByOrganizationIdAndRequesterUserId(Long organizationId, Long requesterUserId);
    Optional<Approval> findByIdAndOrganizationId(Long id, Long organizationId);
    Optional<Approval> findByWorkflowExecutionId(Long workflowExecutionId);

    long countByOrganizationId(Long organizationId);
    long countByOrganizationIdAndStatus(Long organizationId, ApprovalStatus status);
}
