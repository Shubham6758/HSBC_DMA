package com.coforge.hsbcdma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "notifications2",
        indexes = {
                @Index(name = "idx_notification_user", columnList = "user_id"),
                @Index(name = "idx_notification_user_read", columnList = "user_id,is_read"),
                @Index(name = "idx_notification_module_ref", columnList = "module,reference_id")
        }
)
public class Notification2 extends BaseEntity {

    /** ID of the user who receives this notification */
    @Column(name = "user_id", nullable = false)
    private String userId;

    /**
     * Source module
     * PROFILE / PROFILE_TRACKER
     */
    @Column(name = "module", nullable = false, length = 50)
    private String module;

    /**
     * Reference ID of source entity
     * → profiles.id OR profile_tracker.id
     */
    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    /** Short heading shown in UI */
    @Column(name = "title", length = 255)
    private String title;

    /** Main notification text */
    @Column(name = "message", nullable = false, length = 2000)
    private String message;

    /** Read/unread flag */
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    /** Soft delete flag */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    /** When user read the notification */
    @Column(name = "read_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime readAt;

    /** Creation timestamp */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;
}
 