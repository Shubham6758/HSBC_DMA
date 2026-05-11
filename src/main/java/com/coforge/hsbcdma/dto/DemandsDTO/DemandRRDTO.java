package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandRRDTO {
    private String tempId;
    private Long rrNumber;   // numeric, null in step-1
    private String displayDemandId;
}