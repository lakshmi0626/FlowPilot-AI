package com.flowpilot.repository;

import com.flowpilot.model.Role;
import com.flowpilot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByOrganizationId(Long organizationId);
    List<User> findByOrganizationIdAndRole(Long organizationId, Role role);
    List<User> findByOrganizationIdAndDepartmentId(Long organizationId, Long departmentId);
    long countByOrganizationId(Long organizationId);
}
