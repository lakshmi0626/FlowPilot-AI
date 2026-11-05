package com.flowpilot.service;

import com.flowpilot.model.Notification;
import com.flowpilot.model.NotificationType;
import com.flowpilot.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendNotification() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(100L);
            return n;
        });

        Notification result = notificationService.sendNotification(
                1L, 2L, "Task Assigned", "Review Q3 Report", NotificationType.TASK_ASSIGNED, "/tasks"
        );

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("Task Assigned", result.getTitle());
        assertEquals(NotificationType.TASK_ASSIGNED, result.getType());
        assertFalse(result.getIsRead());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testGetUnreadCount() {
        when(notificationRepository.countByUserIdAndIsReadFalse(5L)).thenReturn(4L);

        long count = notificationService.getUnreadCount(5L);
        assertEquals(4L, count);
        verify(notificationRepository, times(1)).countByUserIdAndIsReadFalse(5L);
    }

    @Test
    void testMarkAsRead() {
        Notification notification = new Notification(1L, 3L, "Alert", "Test Message", NotificationType.SYSTEM, "/alerts");
        notification.setId(10L);
        notification.setIsRead(false);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        Notification updated = notificationService.markAsRead(10L, 3L);
        assertNotNull(updated);
        assertTrue(updated.getIsRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testMarkAsReadSecurityExceptionForMismatchedUser() {
        Notification notification = new Notification(1L, 3L, "Alert", "Test Message", NotificationType.SYSTEM, "/alerts");
        notification.setId(10L);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(notification));

        assertThrows(SecurityException.class, () -> notificationService.markAsRead(10L, 999L));
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testMarkAllAsRead() {
        Notification n1 = new Notification(1L, 7L, "N1", "Msg 1", NotificationType.TASK_ASSIGNED, "/tasks");
        Notification n2 = new Notification(1L, 7L, "N2", "Msg 2", NotificationType.APPROVAL_REQUEST, "/approvals");
        n1.setIsRead(false);
        n2.setIsRead(false);

        when(notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(7L)).thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead(7L);

        assertTrue(n1.getIsRead());
        assertTrue(n2.getIsRead());
        verify(notificationRepository, times(1)).saveAll(any());
    }
}
