package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.entity.dropdownEntities.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DropdownDataDTO {
    List<Lob> lobList;
    List<SkillCluster> skillClusterList;
    List<PrimarySkills> primarySkillsList;
    List<SecondarySkills> secondarySkillsList;
    List<HiringManager> hiringManagerList;
    List<SalesSpoc> salesSpocList;
    List<DeliveryManager> deliveryManagerList;
    List<Pmo> pmoList;
    List<Hbu> hbuList;
    List<DemandType> demandTypeList;
    List<Status> statusList;
    List<DemandTimeLine> demandTimeLineList;
    List<ExternalInternal>  externalInternalList;
    List<PmoSpoc> pmoSpocList;
}
