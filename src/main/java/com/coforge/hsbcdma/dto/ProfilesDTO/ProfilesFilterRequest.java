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

    // ---- ManyToOne by NAME (MULTI‑SELECT ✅) ----
    private List<String> skillClusterNames;
    private List<String> locationNames;
    private List<String> hbuNames;
    private List<String> externalInternalNames;

    // ---- ManyToMany ----
    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private List<String> primarySkillNames;
    private List<String> secondarySkillNames;

    // ---- Ageing / billing ----
    private Integer minAgeing;
    private Integer maxAgeing;
    private Double minBillingRate;
    private Double maxBillingRate;

    // ---- RDG / origin masters ----
    private Long originId;
    private Long karatStatusId;
    private Long sourceId;
    private Long overallStatusRdgId;

    private List<String> originNames;
    private List<String> karatStatusNames;
    private List<String> sourceNames;
    private List<String> overallStatusRdgNames;


    // ---- Profile Status ----
    private Long profileStatusId;
    private List<String> profileStatusNames;


    // ================= TEXT FIELDS =================
    private String karatReadiness;
    private String lobShared;
    private String practice;
    private String band;
    private String ageingRange;
    private String codes;
    private String codeType;

}