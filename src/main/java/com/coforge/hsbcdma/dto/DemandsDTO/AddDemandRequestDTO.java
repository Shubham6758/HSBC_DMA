package com.coforge.hsbcdma.dto.DemandsDTO;

import com.coforge.hsbcdma.dto.DemandRRDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddDemandRequestDTO {

    // BASIC
    private String demandType;
    private String externalInternal;

    // SKILLS
    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private Long skillClusterId;

    // MANAGEMENT
    private Long hiringManagerId;
    private Long deliveryManagerId;
    private Long pmoId;
    private Long pmoSpocId;
    private Long salesSpocId;

    // DEMAND META
    private Float band;
    private String pod;
    private Long lobId;

    // LOCATION
    private List<Long> locationIds;

    // STATUS/REMARK
    private String remark;

    // DATES
    private LocalDate demandReceivedDate;
    private Long demandTimelineId;

    // OPTIONAL: if you want client to send flag, keep it. Otherwise remove.
    private Boolean flag;
    private Integer noOfPositions;
    private List<DemandRRDTO> demandRRDTOList;
}
