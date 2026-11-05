package com.flowpilot.controller;

import com.flowpilot.model.Notification;
import com.flowpilot.security.UserPrincipal;
import com.flowpilot.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean unreadOnly) {
        return ResponseEntity.ok(notificationService.getUserNotifications(
                principal.getOrganizationId(), principal.getId(), unreadOnly
        ));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(Map.of("unreadCount", notificationService.getUnreadCount(principal.getId())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markAsRead(id, principal.getId()));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<Map<String, String>> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getId());
        return ResponseEntity.ok(Map.of("message", "All notifications marked as read."));
    }
}
