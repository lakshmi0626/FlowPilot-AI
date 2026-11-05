package com.flowpilot.service;

import com.flowpilot.model.*;
import com.flowpilot.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final OrganizationRepository organizationRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final WorkflowRepository workflowRepository;
    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final ApprovalRepository approvalRepository;
    private final NotificationRepository notificationRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(OrganizationRepository organizationRepository,
                           PlanRepository planRepository,
                           SubscriptionRepository subscriptionRepository,
                           DepartmentRepository departmentRepository,
                           UserRepository userRepository,
                           WorkflowRepository workflowRepository,
                           TaskRepository taskRepository,
                           TaskCommentRepository taskCommentRepository,
                           ApprovalRepository approvalRepository,
                           NotificationRepository notificationRepository,
                           AuditLogRepository auditLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.organizationRepository = organizationRepository;
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.taskRepository = taskRepository;
        this.taskCommentRepository = taskCommentRepository;
        this.approvalRepository = approvalRepository;
        this.notificationRepository = notificationRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (organizationRepository.count() > 0) {
            return; // Already initialized
        }

        // 1. Seed Plans
        Plan trial = new Plan();
        trial.setName("Free Trial");
        trial.setCode("TRIAL");
        trial.setPriceMonthly(BigDecimal.ZERO);
        trial.setPriceYearly(BigDecimal.ZERO);
        trial.setMaxUsers(5);
        trial.setMaxWorkflows(3);
        trial.setMaxExecutionsPerMonth(100);
        trial.setFeaturesJson("{\"aiAssistant\":true,\"visualBuilder\":true,\"webhooks\":false,\"sla\":false}");
        trial = planRepository.save(trial);

        Plan starter = new Plan();
        starter.setName("Starter");
        starter.setCode("STARTER");
        starter.setPriceMonthly(new BigDecimal("49.00"));
        starter.setPriceYearly(new BigDecimal("490.00"));
        starter.setMaxUsers(15);
        starter.setMaxWorkflows(10);
        starter.setMaxExecutionsPerMonth(1000);
        starter.setFeaturesJson("{\"aiAssistant\":true,\"visualBuilder\":true,\"webhooks\":true,\"sla\":false}");
        starter = planRepository.save(starter);

        Plan pro = new Plan();
        pro.setName("Professional");
        pro.setCode("PROFESSIONAL");
        pro.setPriceMonthly(new BigDecimal("149.00"));
        pro.setPriceYearly(new BigDecimal("1490.00"));
        pro.setMaxUsers(50);
        pro.setMaxWorkflows(50);
        pro.setMaxExecutionsPerMonth(10000);
        pro.setFeaturesJson("{\"aiAssistant\":true,\"visualBuilder\":true,\"webhooks\":true,\"sla\":true,\"prioritySupport\":true}");
        pro = planRepository.save(pro);

        Plan enterprise = new Plan();
        enterprise.setName("Enterprise");
        enterprise.setCode("ENTERPRISE");
        enterprise.setPriceMonthly(new BigDecimal("399.00"));
        enterprise.setPriceYearly(new BigDecimal("3990.00"));
        enterprise.setMaxUsers(500);
        enterprise.setMaxWorkflows(500);
        enterprise.setMaxExecutionsPerMonth(100000);
        enterprise.setFeaturesJson("{\"aiAssistant\":true,\"visualBuilder\":true,\"webhooks\":true,\"sla\":true,\"customIntegrations\":true}");
        enterprise = planRepository.save(enterprise);

        // 2. Seed Organizations
        Organization acme = new Organization();
        acme.setName("Acme Corporation");
        acme.setSlug("acme-corp");
        acme.setDomain("acme.example.com");
        acme.setPrimaryColor("#16A36A");
        acme.setSecondaryColor("#174C3C");
        acme.setActive(true);
        acme = organizationRepository.save(acme);

        Organization techflow = new Organization();
        techflow.setName("TechFlow Innovations");
        techflow.setSlug("techflow");
        techflow.setDomain("techflow.example.com");
        techflow.setPrimaryColor("#16A36A");
        techflow.setSecondaryColor("#174C3C");
        techflow.setActive(true);
        techflow = organizationRepository.save(techflow);

        // 3. Subscriptions
        Subscription sub1 = new Subscription();
        sub1.setOrganizationId(acme.getId());
        sub1.setPlanId(pro.getId());
        sub1.setStatus("ACTIVE");
        sub1.setBillingCycle("MONTHLY");
        sub1.setCurrentPeriodStart(LocalDateTime.now());
        sub1.setCurrentPeriodEnd(LocalDateTime.now().plusMonths(1));
        subscriptionRepository.save(sub1);

        Subscription sub2 = new Subscription();
        sub2.setOrganizationId(techflow.getId());
        sub2.setPlanId(starter.getId());
        sub2.setStatus("ACTIVE");
        sub2.setBillingCycle("YEARLY");
        sub2.setCurrentPeriodStart(LocalDateTime.now());
        sub2.setCurrentPeriodEnd(LocalDateTime.now().plusYears(1));
        subscriptionRepository.save(sub2);

        // 4. Departments
        Department eng = departmentRepository.save(new Department(null, acme.getId(), "Engineering", "Software development & cloud infrastructure"));
        Department hr = departmentRepository.save(new Department(null, acme.getId(), "Human Resources", "People and culture"));
        Department fin = departmentRepository.save(new Department(null, acme.getId(), "Operations & Finance", "Accounting, procurement and compliance"));

        // 5. Users
        String encodedPass = passwordEncoder.encode("Password123!");

        User superAdmin = new User();
        superAdmin.setOrganizationId(acme.getId());
        superAdmin.setEmail("superadmin@flowpilot.ai");
        superAdmin.setPasswordHash(encodedPass);
        superAdmin.setFirstName("Alexander");
        superAdmin.setLastName("Vance");
        superAdmin.setTitle("Platform Super Admin");
        superAdmin.setRole(Role.SUPER_ADMIN);
        superAdmin.setStatus(UserStatus.ACTIVE);
        superAdmin = userRepository.save(superAdmin);

        User companyAdmin = new User();
        companyAdmin.setOrganizationId(acme.getId());
        companyAdmin.setDepartmentId(eng.getId());
        companyAdmin.setEmail("admin@acme.com");
        companyAdmin.setPasswordHash(encodedPass);
        companyAdmin.setFirstName("Eleanor");
        companyAdmin.setLastName("Pemberton");
        companyAdmin.setTitle("VP of Engineering / Admin");
        companyAdmin.setRole(Role.COMPANY_ADMIN);
        companyAdmin.setStatus(UserStatus.ACTIVE);
        companyAdmin = userRepository.save(companyAdmin);

        User manager = new User();
        manager.setOrganizationId(acme.getId());
        manager.setDepartmentId(eng.getId());
        manager.setManagerId(companyAdmin.getId());
        manager.setEmail("manager@acme.com");
        manager.setPasswordHash(encodedPass);
        manager.setFirstName("Marcus");
        manager.setLastName("Sterling");
        manager.setTitle("Engineering Manager");
        manager.setRole(Role.MANAGER);
        manager.setStatus(UserStatus.ACTIVE);
        manager = userRepository.save(manager);

        User employee = new User();
        employee.setOrganizationId(acme.getId());
        employee.setDepartmentId(eng.getId());
        employee.setManagerId(manager.getId());
        employee.setEmail("employee@acme.com");
        employee.setPasswordHash(encodedPass);
        employee.setFirstName("David");
        employee.setLastName("Chen");
        employee.setTitle("Senior Software Engineer");
        employee.setRole(Role.EMPLOYEE);
        employee.setStatus(UserStatus.ACTIVE);
        employee = userRepository.save(employee);

        User hrManager = new User();
        hrManager.setOrganizationId(acme.getId());
        hrManager.setDepartmentId(hr.getId());
        hrManager.setManagerId(companyAdmin.getId());
        hrManager.setEmail("sarah.hr@acme.com");
        hrManager.setPasswordHash(encodedPass);
        hrManager.setFirstName("Sarah");
        hrManager.setLastName("Jenkins");
        hrManager.setTitle("HR Operations Lead");
        hrManager.setRole(Role.MANAGER);
        hrManager.setStatus(UserStatus.ACTIVE);
        hrManager = userRepository.save(hrManager);

        // TechFlow admin
        User tfAdmin = new User();
        tfAdmin.setOrganizationId(techflow.getId());
        tfAdmin.setEmail("admin@techflow.com");
        tfAdmin.setPasswordHash(encodedPass);
        tfAdmin.setFirstName("Sophia");
        tfAdmin.setLastName("Rodriguez");
        tfAdmin.setTitle("Founder & CEO");
        tfAdmin.setRole(Role.COMPANY_ADMIN);
        tfAdmin.setStatus(UserStatus.ACTIVE);
        userRepository.save(tfAdmin);

        // 6. Seed Workflows
        Workflow wf1 = new Workflow();
        wf1.setOrganizationId(acme.getId());
        wf1.setCreatedByUserId(manager.getId());
        wf1.setName("Employee Leave Request Workflow");
        wf1.setDescription("Automated sequential review for personal and vacation leaves requiring manager approval.");
        wf1.setCategory("HUMAN_RESOURCES");
        wf1.setStatus(WorkflowStatus.ACTIVE);
        wf1.setVersion(1);
        wf1.setDefinitionJson("{\"nodes\":[{\"id\":\"start_1\",\"type\":\"START\",\"title\":\"Leave Application Submitted\",\"position\":{\"x\":250,\"y\":40},\"data\":{\"label\":\"Leave Application Submitted\",\"type\":\"START\"}},{\"id\":\"appr_1\",\"type\":\"APPROVAL\",\"title\":\"Manager Decision\",\"position\":{\"x\":250,\"y\":160},\"data\":{\"label\":\"Manager Decision\",\"type\":\"APPROVAL\"}},{\"id\":\"cond_1\",\"type\":\"CONDITION\",\"title\":\"Is Approved?\",\"position\":{\"x\":250,\"y\":280},\"data\":{\"label\":\"Is Approved?\",\"type\":\"CONDITION\"}},{\"id\":\"task_1\",\"type\":\"TASK\",\"title\":\"Update HR Records\",\"position\":{\"x\":120,\"y\":400},\"data\":{\"label\":\"Update HR Records\",\"type\":\"TASK\"}},{\"id\":\"notif_1\",\"type\":\"NOTIFICATION\",\"title\":\"Notify Rejection\",\"position\":{\"x\":380,\"y\":400},\"data\":{\"label\":\"Notify Rejection\",\"type\":\"NOTIFICATION\"}},{\"id\":\"end_1\",\"type\":\"END\",\"title\":\"Process Concluded\",\"position\":{\"x\":250,\"y\":520},\"data\":{\"label\":\"Process Concluded\",\"type\":\"END\"}}],\"edges\":[{\"id\":\"e1\",\"source\":\"start_1\",\"target\":\"appr_1\"},{\"id\":\"e2\",\"source\":\"appr_1\",\"target\":\"cond_1\"},{\"id\":\"e3\",\"source\":\"cond_1\",\"target\":\"task_1\",\"condition\":\"APPROVED\",\"label\":\"APPROVED\"},{\"id\":\"e4\",\"source\":\"cond_1\",\"target\":\"notif_1\",\"condition\":\"REJECTED\",\"label\":\"REJECTED\"},{\"id\":\"e5\",\"source\":\"task_1\",\"target\":\"end_1\"},{\"id\":\"e6\",\"source\":\"notif_1\",\"target\":\"end_1\"}]}");
        workflowRepository.save(wf1);

        Workflow wf2 = new Workflow();
        wf2.setOrganizationId(acme.getId());
        wf2.setCreatedByUserId(companyAdmin.getId());
        wf2.setName("Corporate Expense Reimbursement");
        wf2.setDescription("Multi-tier verification for travel and operational receipts exceeding $500.");
        wf2.setCategory("FINANCE");
        wf2.setStatus(WorkflowStatus.ACTIVE);
        wf2.setVersion(1);
        wf2.setDefinitionJson("{\"nodes\":[{\"id\":\"start_2\",\"type\":\"START\",\"title\":\"Expense Receipt Filed\",\"position\":{\"x\":250,\"y\":40},\"data\":{\"label\":\"Expense Receipt Filed\",\"type\":\"START\"}},{\"id\":\"appr_2\",\"type\":\"APPROVAL\",\"title\":\"Finance Director Review\",\"position\":{\"x\":250,\"y\":170},\"data\":{\"label\":\"Finance Director Review\",\"type\":\"APPROVAL\"}},{\"id\":\"task_2\",\"type\":\"TASK\",\"title\":\"Queue Direct Deposit\",\"position\":{\"x\":250,\"y\":300},\"data\":{\"label\":\"Queue Direct Deposit\",\"type\":\"TASK\"}},{\"id\":\"end_2\",\"type\":\"END\",\"title\":\"Disbursement Completed\",\"position\":{\"x\":250,\"y\":420},\"data\":{\"label\":\"Disbursement Completed\",\"type\":\"END\"}}],\"edges\":[{\"id\":\"e2_1\",\"source\":\"start_2\",\"target\":\"appr_2\"},{\"id\":\"e2_2\",\"source\":\"appr_2\",\"target\":\"task_2\"},{\"id\":\"e2_3\",\"source\":\"task_2\",\"target\":\"end_2\"}]}");
        workflowRepository.save(wf2);

        // 7. Seed Tasks
        Task t1 = new Task();
        t1.setOrganizationId(acme.getId());
        t1.setTitle("Audit Q3 Cloud Infrastructure Costs");
        t1.setDescription("Review AWS and GCP reserved instances across production Kubernetes clusters.");
        t1.setAssignedToUserId(employee.getId());
        t1.setCreatedByUserId(manager.getId());
        t1.setPriority(TaskPriority.HIGH);
        t1.setStatus(TaskStatus.IN_PROGRESS);
        t1.setDueDate(LocalDateTime.now().plusDays(3));
        t1 = taskRepository.save(t1);

        Task t2 = new Task();
        t2.setOrganizationId(acme.getId());
        t2.setTitle("Prepare Annual SOC2 Type II Evidence");
        t2.setDescription("Gather access review logs and multi-tenant database isolation verification reports.");
        t2.setAssignedToUserId(employee.getId());
        t2.setCreatedByUserId(companyAdmin.getId());
        t2.setPriority(TaskPriority.URGENT);
        t2.setStatus(TaskStatus.TODO);
        t2.setDueDate(LocalDateTime.now().plusDays(7));
        taskRepository.save(t2);

        Task t3 = new Task();
        t3.setOrganizationId(acme.getId());
        t3.setTitle("Configure Tenant Webhooks for ERP Integration");
        t3.setDescription("Set up outbound event delivery and retry exponential backoff mechanism.");
        t3.setAssignedToUserId(manager.getId());
        t3.setCreatedByUserId(companyAdmin.getId());
        t3.setPriority(TaskPriority.MEDIUM);
        t3.setStatus(TaskStatus.DONE);
        t3.setDueDate(LocalDateTime.now().minusDays(1));
        taskRepository.save(t3);

        // Comments
        taskCommentRepository.save(new TaskComment(t1.getId(), employee.getId(), "Completed initial inventory audit; 14% savings identified in compute instances."));
        taskCommentRepository.save(new TaskComment(t1.getId(), manager.getId(), "Excellent work David. Let's incorporate memory-optimized reservations next."));

        // 8. Seed Approvals (Critical Scenario)
        Approval a1 = new Approval();
        a1.setOrganizationId(acme.getId());
        a1.setRequesterUserId(employee.getId());
        a1.setApproverUserId(manager.getId());
        a1.setRequestType(ApprovalType.LEAVE);
        a1.setTitle("Annual Leave Request (3 Days)");
        a1.setDetailsJson("{\"startDate\":\"2026-11-15\",\"endDate\":\"2026-11-18\",\"reason\":\"Attending regional systems architecture summit\",\"coverageAssigned\":\"Sarah Jenkins\"}");
        a1.setStatus(ApprovalStatus.PENDING);
        approvalRepository.save(a1);

        Approval a2 = new Approval();
        a2.setOrganizationId(acme.getId());
        a2.setRequesterUserId(employee.getId());
        a2.setApproverUserId(manager.getId());
        a2.setRequestType(ApprovalType.EXPENSE);
        a2.setTitle("Conference Travel & Hotel Reimbursement");
        a2.setDetailsJson("{\"amount\":645.00,\"currency\":\"USD\",\"vendor\":\"DevOps Global\",\"category\":\"Travel\"}");
        a2.setStatus(ApprovalStatus.PENDING);
        approvalRepository.save(a2);

        Approval a3 = new Approval();
        a3.setOrganizationId(acme.getId());
        a3.setRequesterUserId(hrManager.getId());
        a3.setApproverUserId(companyAdmin.getId());
        a3.setRequestType(ApprovalType.PURCHASE);
        a3.setTitle("Ergonomic Dual-Monitor Arms Hardware");
        a3.setDetailsJson("{\"amount\":280.00,\"currency\":\"USD\",\"item\":\"Dual Monitor Gas-Spring Arms\"}");
        a3.setStatus(ApprovalStatus.APPROVED);
        a3.setDecisionNotes("Approved under the annual ergonomic wellness budget.");
        a3.setDecidedAt(LocalDateTime.now().minusDays(1));
        approvalRepository.save(a3);

        // 9. Seed Notifications
        notificationRepository.save(new Notification(
                acme.getId(), manager.getId(),
                "New Leave Approval Requested",
                "David Chen has submitted an Annual Leave Request for 3 days awaiting your approval.",
                NotificationType.APPROVAL_REQUEST, "/approvals"
        ));

        notificationRepository.save(new Notification(
                acme.getId(), employee.getId(),
                "Task Assigned: Cloud Infrastructure Audit",
                "Marcus Sterling assigned you to 'Audit Q3 Cloud Infrastructure Costs'.",
                NotificationType.TASK_ASSIGNED, "/tasks"
        ));

        notificationRepository.save(new Notification(
                acme.getId(), hrManager.getId(),
                "Hardware Purchase Approved",
                "Your purchase request for Ergonomic Dual-Monitor Arms was approved by Eleanor.",
                NotificationType.REQUEST_APPROVED, "/approvals"
        ));

        // 10. Audit Logs
        auditLogRepository.save(new AuditLog(
                acme.getId(), companyAdmin.getId(), "INITIALIZE_WORKSPACE",
                "ORGANIZATION", String.valueOf(acme.getId()), "127.0.0.1", "{\"status\":\"ACTIVE\"}"
        ));
    }
}
