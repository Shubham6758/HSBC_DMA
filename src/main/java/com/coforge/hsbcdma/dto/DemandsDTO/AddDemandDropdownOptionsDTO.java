package com.coforge.hsbcdma.dto.DemandsDTO;


import lombok.Data;

import java.util.List;

@Data
public class AddDemandDropdownOptionsDTO {
    private List<OptionDTO> lobList;
    private List<OptionDTO> subLobList;
    private List<OptionDTO> skillClusterList;
    private List<OptionDTO> primarySkillsList;
    private List<OptionDTO> secondarySkillsList;
    private List<OptionDTO> hiringManagerList;
    private List<OptionDTO> hbuList;
    private List<OptionDTO> hbuSpocList;
    private List<OptionDTO> salesSpocList;
    private List<OptionDTO> deliveryManagerList;
    private List<OptionDTO> projectManagerList;
    private List<OptionDTO> pmoList;
    private List<OptionDTO> pmoSpocList;
    private List<OptionDTO> podList;
    private List<OptionDTO> demandTypeList;
    private List<OptionDTO> statusList;
    private List<OptionDTO> demandTimelineList;
    private List<OptionDTO> externalInternalList;
    private List<OptionDTO> demandLocationList; // if you have a Location master table
    private List<OptionDTO> bandList;
    private List<OptionDTO> priorityList;
    private List<OptionDTO> onshoreLocationList;
    private List<OptionDTO> offshoreLocationList;
}