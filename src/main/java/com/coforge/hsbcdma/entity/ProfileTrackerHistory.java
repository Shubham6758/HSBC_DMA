package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profile_tracker_history",
        indexes = {
                @Index(name = "idx_pth_tracker", columnList = "profile_tracker_id"),
                @Index(name = "idx_pth_demand", columnList = "demand_id"),
                @Index(name = "idx_pth_profile", columnList = "profile_id"),
                @Index(name = "idx_pth_add_demand_history", columnList = "add_demand_history_id")
        })
public class ProfileTrackerHistory extends BaseEntity{

    public enum Action {
        ATTACH, SHARE, UPDATE, DETACH
    }

    @Column(name = "profile_tracker_id", nullable = false)
    private Long profileTrackerId;

    @Column(name = "demand_id", nullable = false)
    private Long demandId;

    @Column(name = "profile_id", nullable = false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private Action action;

    @Column(name = "changed_by_user_id", length = 255)
    private String changedByUserId;

    @CreationTimestamp
    @Column(name = "changed_at", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime changedAt;
//
//    @Column(name = "add_demand_history_id")
//    private Long addDemandHistoryId;

    @Lob
    @Column(name = "diff", columnDefinition = "LONGTEXT")
    private String diff;
}
