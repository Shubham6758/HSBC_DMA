package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditDemandResponseDTO {
    private Long id;
    private Long demandId;
    private String displayDemandId;
    private String rrNumber;
    private String fileName;
}