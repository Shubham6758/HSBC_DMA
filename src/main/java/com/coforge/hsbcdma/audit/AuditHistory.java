package com.coforge.hsbcdma.audit;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "audit_history",
        indexes = {
                @Index(name = "idx_ah_entity", columnList = "entity_type, entity_id, changed_at"),
                @Index(name = "idx_ah_demand", columnList = "demand_id, changed_at"),
                @Index(name = "idx_ah_profile", columnList = "profile_id, changed_at"),
                @Index(name = "idx_ah_tracker", columnList = "profile_tracker_id, changed_at"),
        })
public class AuditHistory extends BaseEntity {

    public enum EntityType {
        ADD_DEMAND,
        ONBOARDING, PROFILE, PROFILE_TRACKER
    }

    public enum Action {
        CREATE,
        UPDATE,
        ATTACH,
        UPDATE_DEMAND,
        UPDATE_PROFILE_TRACKER,
        UPDATE_PROFILE,
        UPDATE_ONBOARDING
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false, length = 50)
    private EntityType entityType;

    /**
     * For ADD_DEMAND -> entityId = demandPkId
     * For PROFILE_TRACKER -> entityId = profileTrackerId
     */
    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private Action action;

    @Column(name = "changed_by_user_id", length = 255)
    private String changedByUserId;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime changedAt;

    // ---- Optional relational fields (for your demand<->profile history queries) ----

    @Column(name = "demand_id")
    private Long demandId;

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "profile_tracker_id")
    private Long profileTrackerId;


    @Column(name = "onboarding_id")
    private Long onboardingId;

    /**
     * Store JSON as TEXT/LONGTEXT for easiest compatibility.
     * (If you want true MySQL JSON type, set columnDefinition="JSON")
     */
    @Lob
    @Column(name = "diff", columnDefinition = "LONGTEXT")
    private String diff;
}
