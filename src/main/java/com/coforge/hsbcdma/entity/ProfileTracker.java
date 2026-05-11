package com.coforge.hsbcdma.entity;


import com.coforge.hsbcdma.entity.dropdownEntities.Profile.EvaluationStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.ProfileTrackerStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profile_tracker",
        indexes = {
                @Index(name = "idx_pt_demand",  columnList = "demand_id"),
                @Index(name = "idx_pt_profile", columnList = "profile_id"),
        })
public class ProfileTracker extends BaseEntity {

    /** FK → add_demands.id (PK) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "demand_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_pt_add_demand")
    )
    private AddDemand demand;

    /** FK → profiles.id (PK) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "profile_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_pt_profile")
    )
    private Profile profile;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "created_by_user_id", length = 255)
    private String createdByUserId;

    @UpdateTimestamp
    @Column(name = "updated_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by_user_id", length = 255)
    private String updatedByUserId;

    @Column(name = "profile_shared_date")
    private LocalDate profileSharedDate;

    @Column(name = "interview_date")
    private LocalDate interviewDate;

    @Column(name = "attached_date")
    private LocalDate attachedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "evaluation_status_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_key_evaluation_status")
    )
    private EvaluationStatus evaluationStatus;

    @Column(name = "decision_date")
    private LocalDate decisionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "profile_tracker_status_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_pt_profile_tracker_status")
    )
    private ProfileTrackerStatus profileTrackerStatus;
}