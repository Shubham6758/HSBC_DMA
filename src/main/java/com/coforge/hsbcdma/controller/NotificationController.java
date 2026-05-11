package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.entity.Notification2;
import com.coforge.hsbcdma.repository.NotificationRepositories.Notification2Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final Notification2Repository notificationRepository;

    @GetMapping("/unread-count")
    public long getUnreadCount() {
        String userId = getCurrentUserId();
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @GetMapping
    public List<Notification2> listNotifications() {
        String userId = getCurrentUserId();
        return notificationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id) {
        Notification2 n = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setIsRead(true);
        n.setReadAt(LocalDateTime.now());
        notificationRepository.save(n);
    }

    private String getCurrentUserId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return null;
        return auth.getName(); // userId
    }
}