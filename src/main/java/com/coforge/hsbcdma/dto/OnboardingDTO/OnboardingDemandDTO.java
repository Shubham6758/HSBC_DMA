package com.coforge.hsbcdma.dto.OnboardingDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OnboardingDemandDTO {
    private Long demandId;
    private String displayDemandId;
    private String lob;
    private String band;
    private String pmoSpoc;
    private String hiringManager;
    private String hbu;
}