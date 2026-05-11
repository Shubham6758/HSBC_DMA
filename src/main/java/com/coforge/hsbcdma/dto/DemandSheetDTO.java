package com.coforge.hsbcdma.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DemandSheetDTO {
    private List<UpdateDemandDTO> updateDemandDTOList;
    private DropdownDataDTO dropdownDataDTO;
}
