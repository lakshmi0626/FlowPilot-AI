package com.flowpilot.dto;

import com.flowpilot.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InviteRequest {

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotNull(message = "Role is required")
    private Role role = Role.EMPLOYEE;

    private Long departmentId;

    public InviteRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
}
