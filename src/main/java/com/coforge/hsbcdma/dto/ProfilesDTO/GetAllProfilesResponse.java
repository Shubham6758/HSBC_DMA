package com.coforge.hsbcdma.dto.ProfilesDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetAllProfilesResponse {

    private Long id;

    private String candidateName;
    private String emailId;
    private String empId;
    private String sapId;
    private Long phoneNumber;
    private Boolean isActive;
    private Float experience;

    private RefDTO skillCluster;
    private RefDTO location;
    private RefDTO hbu;
    private RefDTO externalInternal;
    private RefDTO profileStatus;

    private String summary;
    private String fileName;

    // New
    @jakarta.validation.constraints.Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Invalid PAN format")
    private String panNumber;

    private List<RefDTO> primarySkills;
    private List<RefDTO> secondarySkills;

    private String createdByUserId;
    //    private String createdByName;
    private String updatedByUserId;
//    private String updatedByName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate l1InterviewDate;

    private String currentLocation;

    private String officialNP;

    private LocalDate negotiableNpLwd;

    private String recruiter;private RefDTO origin;
    private RefDTO karatStatus;
    private RefDTO source;
    private RefDTO overallStatusRdg;

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


}
