package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;

import java.util.List;

@Data
public class Step1NextResponseDTO {
    private AddNewDemandStep1DTO step1;
    private List<DemandRRDTO> assignments;
}
