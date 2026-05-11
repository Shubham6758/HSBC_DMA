package com.coforge.hsbcdma.entity;

import com.coforge.hsbcdma.entity.dropdownEntities.*;
import com.coforge.hsbcdma.entity.dropdownEntities.Profile.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_profiles_phone", columnNames = {"phone_number"}),
                @UniqueConstraint(name = "uq_profiles_pan", columnNames = {"pan_number"})
        }
)
@Getter
@Setter
public class Profile extends BaseEntity {

    // ================= BASIC =================

    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Column(name = "email_id", unique = true)
    private String emailId;

    @Column(name = "emp_id", unique = true)
    private String empId;

    @Column(name = "sap_id", unique = true)
    private String sapId;

    @Column(name = "phone_number", unique = true)
    private Long phoneNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "experience")
    private Float experience;

    // ================= MASTER RELATIONS =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skill_cluster_id")
    private SkillCluster skillCluster;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hbu_id")
    private Hbu hbu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_internal_id")
    private ExternalInternal externalInternal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id", nullable = false)
    private CountryCode country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_status_id")
    private ProfileStatus profileStatus;

    // ================= NEW MASTER FIELDS =================

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id")
    private Origin origin;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "karat_status_id")
    private KaratStatusMaster karatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id")
    private SourceMaster source;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "overall_status_id")
    private OverallStatusMaster overallStatusRdg;

    // ================= NORMAL FIELDS =================

    @Column(name = "summary", length = 2000)
    private String summary;

    @Column(name = "file_name")
    private String fileName;

    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$",
            message = "Invalid PAN format"
    )
    @Column(name = "pan_number", length = 10)
    private String panNumber;

    @Column(name = "date_of_submission")
    private LocalDate dateOfSubmission;

    @Column(name = "karat_readiness")
    private String karatReadiness;

    @Column(name = "week_of")
    private LocalDate weekOf;

    @Column(name = "account_received_on")
    private LocalDate accountReceivedOn;

    @Column(name = "status_date")
    private LocalDate statusDate;

    @Column(name = "lob_shared")
    private String lobShared;


    @Column(name = "practice")
    private String practice;

    @Column(name = "band")
    private String band;

    @Column(name = "ageing")
    private Integer ageing;

    @Column(name = "ageing_range")
    private String ageingRange;

    @Column(name = "codes")
    private String codes;

    @Column(name = "code_type")
    private String codeType;

    @Column(name = "min_billing_rate")
    private Double minBillingRate;


    @Column(name = "project_code")
    private String projectCode;


    // ================= EXTERNAL =================

    @Column(name = "l1_interview_date")
    private LocalDate l1InterviewDate;

    @Column(name = "current_location", length = 255)
    private String currentLocation;

    @Column(name = "official_np", length = 50)
    private String officialNP;

    @Column(name = "negotiable_np_lwd")
    private LocalDate negotiableNpLwd;

    @Column(name = "recruiter", length = 255)
    private String recruiter;

    // ================= SKILLS =================

    @ManyToMany
    @JoinTable(
            name = "profile_primary_skills",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "primary_skill_id")
    )
    private Set<PrimarySkills> primarySkills = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "profile_secondary_skills",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "secondary_skill_id")
    )
    private Set<SecondarySkills> secondarySkills = new HashSet<>();

    // ================= AUDIT =================

    @Column(name = "created_by_user_id")
    private String createdByUserId;

    @Column(name = "updated_by_user_id")
    private String updatedByUserId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ================= RELATION =================

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfileTracker> demandTrackers = new HashSet<>();

    // ================= PRE-PROCESS =================

    @PrePersist
    @PreUpdate
    private void normalizePan() {
        if (this.panNumber != null) {
            this.panNumber = this.panNumber.trim().toUpperCase();
        }
    }
}