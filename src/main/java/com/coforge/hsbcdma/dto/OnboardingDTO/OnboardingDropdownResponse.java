package com.coforge.hsbcdma.dto.OnboardingDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class OnboardingDropdownResponse {

    private List<RefDTO> wbsTypes;
    private List<RefDTO> bgvStatuses;
    private List<RefDTO> onboardingStatuses;

}
