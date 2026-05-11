package com.coforge.hsbcdma.dto.ProfilesDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProfileCreateRequestDTO {

    @NotBlank
    private String candidateName;

    @NotBlank
    @Email
    private String emailId;

    private String empId;
    private String sapId;
    private Long phoneNumber;
    private Float experience;

    private Long skillClusterId;
    private Long locationId;
    private Long hbuId;
    private Long externalInternalId;
    private Long countryId;
    private Long profileStatusId;
    private String summary;

    // New
    @jakarta.validation.constraints.Pattern(regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$", message = "Invalid PAN format")
    private String panNumber;


    private Set<Long> primarySkillsIds;
    private Set<Long> secondarySkillsIds;

    // Naye fields
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
}
