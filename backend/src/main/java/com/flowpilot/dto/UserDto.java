package com.flowpilot.dto;

import com.flowpilot.model.Role;
import com.flowpilot.model.User;
import com.flowpilot.model.UserStatus;

import java.time.LocalDateTime;

public class UserDto {

    private Long id;
    private Long organizationId;
    private Long departmentId;
    private String departmentName;
    private Long managerId;
    private String managerName;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private String title;
    private String phone;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;

    public UserDto() {}

    public static UserDto from(User user, String departmentName, String managerName) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setOrganizationId(user.getOrganizationId());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setDepartmentName(departmentName);
        dto.setManagerId(user.getManagerId());
        dto.setManagerName(managerName);
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setTitle(user.getTitle());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public Long getDepartmentId() { return departmentId; }
    public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
