package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ProfileTrackerFilterRequest {

    // ===== Keys =====
    private Long demandPkId;
    private Long demandNumber;
    private Long profileId;

    // ===== Demand-side (MULTI‑SELECT ✅) =====
    private List<String> priorityNames;
    private List<String> skillClusterNames;
    private List<String> lobNames;
    private List<String> hbuNames;
    private List<String> hiringManagerNames;

    // External/Internal comes from PROFILE
    private List<String> externalInternalNames;

    // ===== Profile identity =====
    private String candidateName;
    private String empId;
    private String emailId;

    // ===== Tracker-side masters (MULTI‑SELECT ✅) =====
    private List<String> evaluationStatusNames;
    private List<String> profileTrackerStatusNames;

    // ===== Tracker dates =====
    private LocalDate profileSharedDateFrom;
    private LocalDate profileSharedDateTo;

    private LocalDate interviewDateFrom;
    private LocalDate interviewDateTo;

    private LocalDate attachedDateFrom;
    private LocalDate attachedDateTo;

    private LocalDate decisionDateFrom;
    private LocalDate decisionDateTo;

    // ===== Demand M:N =====
    private List<String> demandPrimarySkillNames;
    private List<String> demandSecondarySkillNames;
    private List<String> demandLocationNames;
    private List<Long> demandLocationIds;
}