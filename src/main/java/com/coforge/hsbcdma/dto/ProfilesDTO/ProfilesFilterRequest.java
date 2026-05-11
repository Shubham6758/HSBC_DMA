package com.coforge.hsbcdma.dto.ProfilesDTO;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProfilesFilterRequest {

    // ---- Scalars ----
    private Long id;
    private Boolean isActive;
    private String candidateName;
    private String emailId;
    private String empId;
    private String sapId;
    private Long phoneNumber;

    // ---- TA upload scalars ----
    private String panNumber;
    private String recruiter;
    private String currentLocation;
    private String officialNP;

    // ---- Experience ----
    private Float minExperience;
    private Float maxExperience;
    private String experienceRange;

    // ---- TA dates ----
    private LocalDate l1InterviewDate;
    private LocalDate negotiableNpLwd;

    // ---- ManyToOne by ID ----
    private Long skillClusterId;
    private Long locationId;
    private Long hbuId;
    private Long externalInternalId;
    private Long profileStatusId;

    // ---- ManyToOne by NAME ----
    private String skillClusterName;
    private String locationName;
    private String hbuName;
    private String externalInternalName;
    private String profileStatusName;

    // ---- ManyToMany ----
    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private List<String> primarySkillNames;
    private List<String> secondarySkillNames;

    // ---- Ageing / billing (new fields) ----
    private Integer minAgeing;
    private Integer maxAgeing;
    private Double minBillingRate;
    private Double maxBillingRate;

    // ---- Other new scalar filters ----
    private String practice;
    private String band;
    private String lobShared;
    private String projectCode;

    // ---- RDG / origin fields (new masters) ----
    private Long originId;
    private Long karatStatusId;
    private Long sourceId;
    private Long overallStatusRdgId;

    private String originName;
    private String karatStatusName;
    private String sourceName;
    private String overallStatusRdgName;
}