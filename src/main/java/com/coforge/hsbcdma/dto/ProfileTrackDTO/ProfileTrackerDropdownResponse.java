package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProfileTrackerDropdownResponse {
    private List<RefDTO> evaluationStatuses;
    private List<RefDTO> profileTrackerStatuses;
}
