package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Response: includes draftId + generated demandIds + placeholders for RR numbers
@Getter
@Setter
public class Step1ResponseDTO {
//    private Long draftId;
    private List<DemandRRDTO> assignments; // demandId + rrNumber (null at this point)
}