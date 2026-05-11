package com.coforge.hsbcdma.dto.OnboardingDTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetAllOnboardingDTO {

    // Onboarding (self)
    private Long id;
    private Long ctoolId;
    private String onboardingStatusName;
    private String bgvStatusName;
    private String wbsTypeName;

    private LocalDate offerDate;
    private LocalDate dateOfJoining;
    private LocalDate pevUploadDate;
    private LocalDate vpTagging;
    private LocalDate techSelectDate;
    private LocalDate hsbcOnboardingDate;

    // Profile
    private Long profileId;
    private String candidateName;
    private String empId;
    private String externalInternalName;

    // Demand
    private Long demandPkId;
    private Long demandNumber;
    private String lobName;
    private String bandName;
    private String hiringManagerName;
    private String hbuName;
    private String pmoSpocName;

    // Profile Tracker
    private LocalDate profileSharedDate;
}