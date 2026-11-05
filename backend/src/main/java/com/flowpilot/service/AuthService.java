package com.flowpilot.service;

import com.flowpilot.dto.*;
import com.flowpilot.model.*;
import com.flowpilot.repository.*;
import com.flowpilot.security.JwtTokenProvider;
import com.flowpilot.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    public AuthService(UserRepository userRepository,
                       OrganizationRepository organizationRepository,
                       PlanRepository planRepository,
                       SubscriptionRepository subscriptionRepository,
                       DepartmentRepository departmentRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider tokenProvider,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        Organization org = organizationRepository.findById(principal.getOrganizationId())
                .orElse(new Organization(principal.getOrganizationId(), "Workspace", "workspace"));

        auditLogService.logAction(org.getId(), principal.getId(), "LOGIN", "USER",
                String.valueOf(principal.getId()), "{\"email\":\"" + principal.getUsername() + "\"}");

        return new AuthResponse(
                jwt,
                principal.getId(),
                principal.getUsername(),
                principal.getFirstName(),
                principal.getLastName(),
                principal.getRole(),
                org.getId(),
                org.getName(),
                org.getSlug()
        );
    }

    @Transactional
    public AuthResponse registerCompany(RegisterCompanyRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + request.getEmail());
        }

        String slug = request.getSlug().toLowerCase().trim().replaceAll("[^a-z0-9-]", "-");
        if (organizationRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 1000;
        }

        // 1. Create Organization
        Organization org = new Organization();
        org.setName(request.getCompanyName());
        org.setSlug(slug);
        org.setActive(true);
        org = organizationRepository.save(org);

        // 2. Create Default Departments
        Department engineering = departmentRepository.save(new Department(null, org.getId(), "Engineering", "Technical development & operations"));
        departmentRepository.save(new Department(null, org.getId(), "Human Resources", "People and workplace culture"));
        departmentRepository.save(new Department(null, org.getId(), "Finance & Operations", "Accounting and operational management"));

        // 3. Create Subscription (Free Trial)
        Plan trialPlan = planRepository.findByCode("TRIAL")
                .orElseGet(() -> planRepository.findAll().stream().findFirst().orElse(null));

        if (trialPlan != null) {
            Subscription sub = new Subscription();
            sub.setOrganizationId(org.getId());
            sub.setPlanId(trialPlan.getId());
            sub.setStatus("TRIALING");
            sub.setCurrentPeriodEnd(LocalDateTime.now().plusDays(14));
            subscriptionRepository.save(sub);
        }

        // 4. Create Company Admin User
        User admin = new User();
        admin.setOrganizationId(org.getId());
        admin.setDepartmentId(engineering.getId());
        admin.setEmail(request.getEmail().toLowerCase().trim());
        admin.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setTitle("Workspace Owner & Admin");
        admin.setRole(Role.COMPANY_ADMIN);
        admin.setStatus(UserStatus.ACTIVE);
        admin = userRepository.save(admin);

        auditLogService.logAction(org.getId(), admin.getId(), "REGISTER_ORGANIZATION", "ORGANIZATION",
                String.valueOf(org.getId()), "{\"companyName\":\"" + org.getName() + "\"}");

        // Authenticate & generate JWT
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        String token = tokenProvider.generateToken(auth);

        return new AuthResponse(
                token,
                admin.getId(),
                admin.getEmail(),
                admin.getFirstName(),
                admin.getLastName(),
                admin.getRole(),
                org.getId(),
                org.getName(),
                org.getSlug()
        );
    }

    @Transactional
    public void resetPassword(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("No account found with provided email"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditLogService.logAction(user.getOrganizationId(), user.getId(), "PASSWORD_RESET", "USER",
                String.valueOf(user.getId()), "{\"status\":\"SUCCESS\"}");
    }
}
