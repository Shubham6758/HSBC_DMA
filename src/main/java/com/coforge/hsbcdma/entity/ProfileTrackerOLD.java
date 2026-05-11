package com.coforge.hsbcdma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Profile entity representing a candidate profile.
 * Created-By : pratish.b
 */
@Setter
@Getter
@ToString
@Entity
@Table(name = "add_new_demands")
public class ProfileTrackerOLD extends BaseEntity{

    @Column(name = "DEMAND_ID", nullable = false)
    private String demandId;
    @Column(name = "RR_NUMBER", nullable = true)
    private Long rrNumber;
    @Column(name = "LOB", nullable = false)
    private String lob;
    @Column(name = "HIRING_MANAGER", nullable = true)
    private String hiringManager;
    @Column(name = "SKILL_CLUSTER", nullable = true)
    private String  skillCluter;
    @Column(name = "PRIMARY_SKILLS", nullable = true)
    private String primarySkills;
    @Column(name = "SECONDARY_SKILLS", nullable = true)
    private String secondarySkills;
    @Column(name = "CURRENT_PROFILE_SHARED", nullable = true)
    private Integer currentProfileShared;
    @Column(name = "EXTERNAL_INTERNAL", nullable = true)
    private String externalInternal;
    @Column(name = "STATUS", nullable = true)
    private String status;
    @Column(name = "P1_AGE", nullable = true)
    private String p1Age;

    //@Convert(converter = LocalDateFormatConverter.class)
    @Column(name = "PROFILE_SHARED_DATE", nullable = true)
    private LocalDate profileSharedDate;
    //@Convert(converter = LocalDateFormatConverter.class)
    @Column(name = "INTERVIEW_DATE", nullable = true)
    private LocalDate interviewDate;
    //@Convert(converter = LocalDateFormatConverter.class)
    @Column(name = "DECISION_DATE", nullable = true)
    private LocalDate decisionDate;
}
