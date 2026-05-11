package com.coforge.hsbcdma.dto.OnboardingDTO;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EditOnboardingRequest {

    // Master references
    private Long wbsTypeId;            // optional
    private Long bgvStatusId;          // optional
    private Long onboardingStatusId;   // optional

    // Simple fields (all optional)
    private LocalDate offerDate;
    private LocalDate dateOfJoining;
    private Long ctoolId;
    private LocalDate pevUploadDate;
    private LocalDate vpTagging;
    private LocalDate techSelectDate;
    private LocalDate hsbcOnboardingDate;
}