package com.coforge.hsbcdma.dto.DemandsDTO;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Update RR numbers for the generated IDs
@Getter
@Setter
public class Step2RRUpdateDTO {
    private Long draftId;
    private List<DemandRRDTO> assignments;  // demandId + rrNumber
}
