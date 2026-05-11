package com.coforge.hsbcdma.entity;

import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.BgvStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.OnboardingStatus;
import com.coforge.hsbcdma.entity.dropdownEntities.Onboarding.WbsType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(
        name = "onboarding",
        indexes = {
                @Index(name = "idx_onboarding_profile_tracker", columnList = "profile_tracker_id"),
                @Index(name = "idx_onboarding_onboarding_status", columnList = "onboarding_status_id"),
                @Index(name = "idx_onboarding_demand", columnList = "demand_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Onboarding extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "profile_tracker_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_onboarding_profile_tracker")
    )
    private ProfileTracker profileTracker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wbs_type_id", foreignKey = @ForeignKey(name = "fk_onboarding_wbs_type"))
    private WbsType wbsType;

    @Column(name = "offer_date")
    private LocalDate offerDate;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "ctool_id", length = 50)
    private Long ctoolId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bgv_status_id",
            foreignKey = @ForeignKey(name = "fk_onboarding_bgv_status"))
    private BgvStatus bgvStatus;

    @Column(name = "pev_upload_date")
    private LocalDate pevUploadDate;

    @Column(name = "vp_tagging", length = 100)
    private LocalDate vpTagging;

    @Column(name = "tech_select_date")
    private LocalDate techSelectDate;

    @Column(name = "hsbc_onboarding_date")
    private LocalDate hsbcOnboardingDate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "onboarding_status_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_onboarding_status"))
    private OnboardingStatus onboardingStatus;


    // NEW: demand reference (FK to add_demands.id) & profile reference (FK to profile.id)
    @ManyToOne(fetch = FetchType.LAZY, optional = false) // set to true if you want it nullable
    @JoinColumn(
            name = "demand_id",
            nullable = false, // set to true if you want to allow nulls
            foreignKey = @ForeignKey(name = "fk_onboarding_demand")
    )
    private AddDemand demand;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // set to true if you want it nullable
    @JoinColumn(
            name = "profile_id",
            nullable = false, // set to true if you want to allow nulls
            foreignKey = @ForeignKey(name = "fk_onboarding_profile")
    )
    private Profile profile;
}
