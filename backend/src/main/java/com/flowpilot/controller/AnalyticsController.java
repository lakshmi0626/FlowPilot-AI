package com.flowpilot.controller;

import com.flowpilot.dto.DashboardSummaryResponse;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(analyticsService.getDashboardSummary(principal.getOrganizationId()));
    }
}
