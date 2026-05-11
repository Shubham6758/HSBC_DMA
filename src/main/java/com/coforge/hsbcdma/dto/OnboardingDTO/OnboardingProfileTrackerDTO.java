package com.coforge.hsbcdma.dto.OnboardingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingProfileTrackerDTO {
    private Long profileTrackerId;
    private LocalDate profileSharedDate;
}
