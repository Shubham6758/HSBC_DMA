package com.coforge.hsbcdma.dto.ProfilesDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProfileDraftRequestDTO {

    private String candidateName;
    private String emailId;
    private String empId;
    private Long phoneNumber;
    private Float experience;

    private Long skillClusterId;
    private Long locationId;
    private Long hbuId;
    private Long externalInternalId;
    private Long countryId;
    private Long profileStatusId;

    private String summary;
    private String panNumber;

    private Set<Long> primarySkillsIds;
    private Set<Long> secondarySkillsIds;

    private Long originId;
    private Long karatStatusId;
    private Long sourceId;
    private Long overallStatusRdgId;

    private LocalDate dateOfSubmission;
    private String karatReadiness;
    private LocalDate weekOf;
    private LocalDate accountReceivedOn;
    private LocalDate statusDate;

    private String lobShared;
    private String practice;
    private String band;
    private Integer ageing;
    private String ageingRange;

    private String codes;
    private String codeType;
    private Double minBillingRate;
    private String projectCode;

    private LocalDate l1InterviewDate;
    private String currentLocation;
    private String officialNP;
    private String negotiableNpLwd;
    private String recruiter;

    private Long personaId;
    private String mentorBuddy;
    private Long prepStatusId;

    private LocalDate startDate;
    private LocalDate plannedEndDate;

    private Long coderpadResult1Id;
    private Long coderpadResult2Id;

    private Long karatReadyStatusId;
    private LocalDate karatTestDate;
    private Long karatTestResultId;
}