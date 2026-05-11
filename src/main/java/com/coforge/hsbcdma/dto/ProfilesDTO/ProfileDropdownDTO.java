package com.coforge.hsbcdma.dto.ProfilesDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.Data;

import java.util.List;
@Data
public class ProfileDropdownDTO {
    private List<RefDTO> locations;
    private List<RefDTO> skillClusters;

    private List<RefDTO> hbus;
    private List<RefDTO> externalInternals;
    private List<RefDTO>profileStatusList;

    private List<RefDTO> primarySkills;
    private List<RefDTO> secondarySkills;


    private List<RefDTO> origins;
    private List<RefDTO> karatStatuses;
    private List<RefDTO> sources;
    private List<RefDTO> overallStatusRdgs;

}
