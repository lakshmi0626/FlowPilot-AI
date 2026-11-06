package com.flowpilot.service;

import com.flowpilot.dto.UpgradeSubscriptionRequest;
import com.flowpilot.model.Plan;
import com.flowpilot.model.Subscription;
import com.flowpilot.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SubscriptionServiceTest {

    @Mock
    private PlanRepository planRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkflowRepository workflowRepository;

    @Mock
    private WorkflowExecutionRepository executionRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Plan createMockPlan(Long id, String name, String code, int maxUsers, int maxWorkflows, int maxExec) {
        Plan p = new Plan();
        p.setId(id);
        p.setName(name);
        p.setCode(code);
        p.setPriceMonthly(new BigDecimal("79.00"));
        p.setPriceYearly(new BigDecimal("790.00"));
        p.setMaxUsers(maxUsers);
        p.setMaxWorkflows(maxWorkflows);
        p.setMaxExecutionsPerMonth(maxExec);
        return p;
    }

    @Test
    void testGetAllPlans() {
        Plan p1 = createMockPlan(1L, "Starter", "STARTER", 10, 5, 500);
        Plan p2 = createMockPlan(2L, "Professional", "PRO", 50, 25, 5000);

        when(planRepository.findAll()).thenReturn(List.of(p1, p2));

        List<Plan> plans = subscriptionService.getAllPlans();
        assertNotNull(plans);
        assertEquals(2, plans.size());
        assertEquals("STARTER", plans.get(0).getCode());
        assertEquals("PRO", plans.get(1).getCode());
    }

    @Test
    void testGetSubscriptionDetailsWithUsage() {
        Subscription sub = new Subscription();
        sub.setId(10L);
        sub.setOrganizationId(1L);
        sub.setPlanId(2L);
        sub.setStatus("ACTIVE");

        Plan plan = createMockPlan(2L, "Professional", "PRO", 50, 25, 5000);

        when(subscriptionRepository.findByOrganizationId(1L)).thenReturn(Optional.of(sub));
        when(planRepository.findById(2L)).thenReturn(Optional.of(plan));
        when(userRepository.countByOrganizationId(1L)).thenReturn(8L);
        when(workflowRepository.countByOrganizationId(1L)).thenReturn(12L);
        when(executionRepository.countByOrganizationId(1L)).thenReturn(140L);

        Map<String, Object> details = subscriptionService.getSubscriptionDetails(1L);

        assertNotNull(details);
        assertEquals(sub, details.get("subscription"));
        assertEquals(plan, details.get("plan"));

        @SuppressWarnings("unchecked")
        Map<String, Object> usage = (Map<String, Object>) details.get("usage");
        assertNotNull(usage);
        assertEquals(8L, usage.get("usersUsed"));
        assertEquals(50, usage.get("usersLimit"));
        assertEquals(12L, usage.get("workflowsUsed"));
        assertEquals(25, usage.get("workflowsLimit"));
        assertEquals(140L, usage.get("executionsUsed"));
        assertEquals(5000, usage.get("executionsLimit"));
    }

    @Test
    void testUpgradeSubscriptionToYearlyPro() {
        Plan proPlan = createMockPlan(2L, "Professional", "PRO", 50, 25, 5000);

        when(planRepository.findByCode("PRO")).thenReturn(Optional.of(proPlan));
        when(subscriptionRepository.findByOrganizationId(1L)).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> {
            Subscription s = inv.getArgument(0);
            s.setId(25L);
            return s;
        });

        UpgradeSubscriptionRequest req = new UpgradeSubscriptionRequest();
        req.setPlanCode("PRO");
        req.setBillingCycle("YEARLY");

        Subscription upgraded = subscriptionService.upgradeSubscription(1L, 3L, req);

        assertNotNull(upgraded);
        assertEquals(25L, upgraded.getId());
        assertEquals(2L, upgraded.getPlanId());
        assertEquals("YEARLY", upgraded.getBillingCycle());
        assertEquals("ACTIVE", upgraded.getStatus());
        assertNotNull(upgraded.getCurrentPeriodEnd());
        verify(subscriptionRepository, times(1)).save(any(Subscription.class));
        verify(auditLogService, times(1)).logAction(eq(1L), eq(3L), eq("UPGRADE_SUBSCRIPTION"), eq("SUBSCRIPTION"), eq("25"), anyString());
    }
}
