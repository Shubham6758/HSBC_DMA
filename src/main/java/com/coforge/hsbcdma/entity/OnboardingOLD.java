package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Onboarding entity created when a profile is selected for a demand.
 */
@Entity
@Table(name = "onboardingOLD")
@Getter
@Setter
public class OnboardingOLD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "demand_id", nullable = false)
    private Demand demand;

    @OneToOne
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileTrackerOLD profile;

    private LocalDateTime selectedAt = LocalDateTime.now();
    private String notes;

  }
