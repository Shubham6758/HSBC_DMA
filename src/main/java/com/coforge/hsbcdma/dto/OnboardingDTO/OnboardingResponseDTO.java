package com.coforge.hsbcdma.dto.OnboardingDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingResponseDTO {

    private Long onboardingId;

    private LocalDate offerDate;
    private LocalDate dateOfJoining;

    private Long ctoolId;

    private LocalDate pevUploadDate;
    private LocalDate vpTagging;
    private LocalDate techSelectDate;
    private LocalDate hsbcOnboardingDate;

    private RefDTO bgvStatus;
    private RefDTO onboardingStatus;
    private RefDTO wbsType;

    private OnboardingDemandDTO demand;
    private OnboardingProfileDTO profile;
    private OnboardingProfileTrackerDTO profileTracker;
}
