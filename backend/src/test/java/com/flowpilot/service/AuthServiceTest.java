package com.flowpilot.service;

import com.flowpilot.dto.LoginRequest;
import com.flowpilot.dto.AuthResponse;
import com.flowpilot.model.Organization;
import com.flowpilot.model.Role;
import com.flowpilot.model.UserStatus;
import com.flowpilot.repository.OrganizationRepository;
import com.flowpilot.repository.UserRepository;
import com.flowpilot.security.JwtTokenProvider;
import com.flowpilot.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() {
        LoginRequest req = new LoginRequest("admin@acme.com", "Password123!");
        UserPrincipal principal = new UserPrincipal(2L, 1L, "admin@acme.com", "hash", "Eleanor", "Pemberton", Role.COMPANY_ADMIN, UserStatus.ACTIVE);

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(tokenProvider.generateToken(auth)).thenReturn("mocked.jwt.token");

        Organization org = new Organization(1L, "Acme Corporation", "acme-corp");
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(org));

        AuthResponse resp = authService.login(req);

        assertNotNull(resp);
        assertEquals("mocked.jwt.token", resp.getToken());
        assertEquals("admin@acme.com", resp.getEmail());
        assertEquals(Role.COMPANY_ADMIN, resp.getRole());
        assertEquals("Acme Corporation", resp.getOrganizationName());
    }
}
