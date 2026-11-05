package com.flowpilot.service;

import com.flowpilot.dto.UpgradeSubscriptionRequest;
import com.flowpilot.model.Plan;
import com.flowpilot.model.Subscription;
import com.flowpilot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class SubscriptionService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final AuditLogService auditLogService;

    public SubscriptionService(PlanRepository planRepository,
                               SubscriptionRepository subscriptionRepository,
                               UserRepository userRepository,
                               WorkflowRepository workflowRepository,
                               WorkflowExecutionRepository executionRepository,
                               AuditLogService auditLogService) {
        this.planRepository = planRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.executionRepository = executionRepository;
        this.auditLogService = auditLogService;
    }

    public List<Plan> getAllPlans() {
        return planRepository.findAll();
    }

    public Map<String, Object> getSubscriptionDetails(Long orgId) {
        Subscription sub = subscriptionRepository.findByOrganizationId(orgId)
                .orElseGet(() -> {
                    Plan trial = planRepository.findByCode("TRIAL")
                            .orElseGet(() -> planRepository.findAll().stream().findFirst().orElse(null));
                    Subscription s = new Subscription();
                    s.setOrganizationId(orgId);
                    s.setPlanId(trial != null ? trial.getId() : 1L);
                    s.setStatus("ACTIVE");
                    return subscriptionRepository.save(s);
                });

        Plan plan = planRepository.findById(sub.getPlanId())
                .orElseGet(() -> planRepository.findAll().stream().findFirst().orElse(new Plan()));

        long userCount = userRepository.countByOrganizationId(orgId);
        long workflowCount = workflowRepository.countByOrganizationId(orgId);
        long executionCount = executionRepository.countByOrganizationId(orgId);

        Map<String, Object> result = new HashMap<>();
        result.put("subscription", sub);
        result.put("plan", plan);

        Map<String, Object> usage = new HashMap<>();
        usage.put("usersUsed", userCount);
        usage.put("usersLimit", plan.getMaxUsers() != null ? plan.getMaxUsers() : 10);
        usage.put("workflowsUsed", workflowCount);
        usage.put("workflowsLimit", plan.getMaxWorkflows() != null ? plan.getMaxWorkflows() : 5);
        usage.put("executionsUsed", executionCount);
        usage.put("executionsLimit", plan.getMaxExecutionsPerMonth() != null ? plan.getMaxExecutionsPerMonth() : 100);

        result.put("usage", usage);
        return result;
    }

    @Transactional
    public Subscription upgradeSubscription(Long orgId, Long userId, UpgradeSubscriptionRequest req) {
        Plan targetPlan = planRepository.findByCode(req.getPlanCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown plan code: " + req.getPlanCode()));

        Subscription sub = subscriptionRepository.findByOrganizationId(orgId)
                .orElseGet(() -> {
                    Subscription s = new Subscription();
                    s.setOrganizationId(orgId);
                    return s;
                });

        sub.setPlanId(targetPlan.getId());
        sub.setStatus("ACTIVE");
        sub.setBillingCycle(req.getBillingCycle() != null ? req.getBillingCycle() : "MONTHLY");
        sub.setCurrentPeriodStart(LocalDateTime.now());
        sub.setCurrentPeriodEnd(
                "YEARLY".equalsIgnoreCase(req.getBillingCycle()) ?
                        LocalDateTime.now().plusYears(1) : LocalDateTime.now().plusMonths(1)
        );
        sub.setPaymentMethod("CARD (Test Mode)");
        sub.setLast4("4242");
        sub.setUpdatedAt(LocalDateTime.now());

        Subscription saved = subscriptionRepository.save(sub);

        auditLogService.logAction(orgId, userId, "UPGRADE_SUBSCRIPTION", "SUBSCRIPTION",
                String.valueOf(saved.getId()), "{\"plan\":\"" + targetPlan.getCode() + "\"}");

        return saved;
    }
}
