package com.flowpilot.dto;

import com.flowpilot.model.Role;

public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    private Long organizationId;
    private String organizationName;
    private String organizationSlug;

    public AuthResponse() {}

    public AuthResponse(String token, Long id, String email, String firstName, String lastName,
                        Role role, Long organizationId, String organizationName, String organizationSlug) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.organizationSlug = organizationSlug;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Long getOrganizationId() { return organizationId; }
    public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getOrganizationSlug() { return organizationSlug; }
    public void setOrganizationSlug(String organizationSlug) { this.organizationSlug = organizationSlug; }
}
