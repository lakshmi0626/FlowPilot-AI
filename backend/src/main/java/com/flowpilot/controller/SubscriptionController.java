package com.flowpilot.controller;

import com.flowpilot.dto.UpgradeSubscriptionRequest;
import com.flowpilot.model.Plan;
import com.flowpilot.model.Subscription;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.SubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/plans")
    public ResponseEntity<List<Plan>> getPlans() {
        return ResponseEntity.ok(subscriptionService.getAllPlans());
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentSubscription(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionDetails(principal.getOrganizationId()));
    }

    @PostMapping("/upgrade")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')")
    public ResponseEntity<Subscription> upgradeSubscription(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody UpgradeSubscriptionRequest req) {
        return ResponseEntity.ok(subscriptionService.upgradeSubscription(principal.getOrganizationId(), principal.getId(), req));
    }
}
