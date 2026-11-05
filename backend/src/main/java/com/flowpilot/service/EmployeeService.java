package com.flowpilot.service;

import com.flowpilot.dto.EmployeeCreateRequest;
import com.flowpilot.dto.InviteRequest;
import com.flowpilot.dto.UserDto;
import com.flowpilot.model.*;
import com.flowpilot.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final InvitationRepository invitationRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final AuditLogService auditLogService;

    public EmployeeService(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           InvitationRepository invitationRepository,
                           PasswordEncoder passwordEncoder,
                           NotificationService notificationService,
                           AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.invitationRepository = invitationRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
        this.auditLogService = auditLogService;
    }

    public List<UserDto> getEmployees(Long orgId) {
        List<User> users = userRepository.findByOrganizationId(orgId);
        Map<Long, String> deptMap = departmentRepository.findByOrganizationId(orgId)
                .stream().collect(Collectors.toMap(Department::getId, Department::getName));
        Map<Long, String> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, User::getFullName));

        return users.stream()
                .map(u -> UserDto.from(
                        u,
                        u.getDepartmentId() != null ? deptMap.get(u.getDepartmentId()) : "General",
                        u.getManagerId() != null ? userMap.get(u.getManagerId()) : "None"
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createEmployee(Long orgId, Long adminId, EmployeeCreateRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists: " + req.getEmail());
        }

        User user = new User();
        user.setOrganizationId(orgId);
        user.setEmail(req.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword() != null ? req.getPassword() : "Password123!"));
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setTitle(req.getTitle());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole() != null ? req.getRole() : Role.EMPLOYEE);
        user.setDepartmentId(req.getDepartmentId());
        user.setManagerId(req.getManagerId());
        user.setStatus(UserStatus.ACTIVE);

        User saved = userRepository.save(user);

        auditLogService.logAction(orgId, adminId, "CREATE_EMPLOYEE", "USER",
                String.valueOf(saved.getId()), "{\"email\":\"" + saved.getEmail() + "\"}");

        notificationService.sendNotification(orgId, saved.getId(), "Welcome to FlowPilot AI",
                "Your workspace account has been created by your administrator.",
                NotificationType.SYSTEM, "/dashboard");

        String deptName = req.getDepartmentId() != null ?
                departmentRepository.findById(req.getDepartmentId()).map(Department::getName).orElse("General") : "General";
        String managerName = req.getManagerId() != null ?
                userRepository.findById(req.getManagerId()).map(User::getFullName).orElse("None") : "None";

        return UserDto.from(saved, deptName, managerName);
    }

    @Transactional
    public UserDto updateEmployee(Long orgId, Long adminId, Long userId, EmployeeCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + userId));

        if (!user.getOrganizationId().equals(orgId)) {
            throw new SecurityException("Cannot modify employee outside organization");
        }

        if (req.getFirstName() != null) user.setFirstName(req.getFirstName());
        if (req.getLastName() != null) user.setLastName(req.getLastName());
        if (req.getTitle() != null) user.setTitle(req.getTitle());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        if (req.getRole() != null) user.setRole(req.getRole());
        if (req.getDepartmentId() != null) user.setDepartmentId(req.getDepartmentId());
        if (req.getManagerId() != null) user.setManagerId(req.getManagerId());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);

        auditLogService.logAction(orgId, adminId, "UPDATE_EMPLOYEE", "USER",
                String.valueOf(saved.getId()), "{\"role\":\"" + saved.getRole() + "\"}");

        String deptName = saved.getDepartmentId() != null ?
                departmentRepository.findById(saved.getDepartmentId()).map(Department::getName).orElse("General") : "General";
        String managerName = saved.getManagerId() != null ?
                userRepository.findById(saved.getManagerId()).map(User::getFullName).orElse("None") : "None";

        return UserDto.from(saved, deptName, managerName);
    }

    @Transactional
    public void deactivateEmployee(Long orgId, Long adminId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + userId));

        if (!user.getOrganizationId().equals(orgId)) {
            throw new SecurityException("Cannot deactivate employee outside organization");
        }

        user.setStatus(UserStatus.SUSPENDED);
        userRepository.save(user);

        auditLogService.logAction(orgId, adminId, "DEACTIVATE_EMPLOYEE", "USER",
                String.valueOf(userId), "{\"status\":\"SUSPENDED\"}");
    }

    @Transactional
    public Invitation inviteEmployee(Long orgId, Long adminId, InviteRequest req) {
        Invitation invitation = new Invitation();
        invitation.setOrganizationId(orgId);
        invitation.setInvitedByUserId(adminId);
        invitation.setEmail(req.getEmail().toLowerCase().trim());
        invitation.setRole(req.getRole());
        invitation.setDepartmentId(req.getDepartmentId());
        invitation.setToken(UUID.randomUUID().toString());
        invitation.setStatus("PENDING");
        invitation.setExpiresAt(LocalDateTime.now().plusDays(7));

        Invitation saved = invitationRepository.save(invitation);

        auditLogService.logAction(orgId, adminId, "INVITE_EMPLOYEE", "INVITATION",
                String.valueOf(saved.getId()), "{\"email\":\"" + saved.getEmail() + "\"}");

        return saved;
    }
}
