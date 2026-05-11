package com.coforge.hsbcdma.dto.OnboardingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingProfileDTO {
    private Long profileId;
    private String empId;
    private String candidateName;
    private String externalInternal;
}
