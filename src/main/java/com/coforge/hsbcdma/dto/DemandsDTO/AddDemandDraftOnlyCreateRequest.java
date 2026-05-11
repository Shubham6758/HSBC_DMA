package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddDemandDraftOnlyCreateRequest {
    private Boolean flag;
    private Boolean karatFlag;
    private Integer numberOfPositions;
    private String experience;
    private String remark;
    private LocalDate demandReceivedDate;

    private Long hbuId;
    private Long hubSpocId;
    private Long bandId;
    private Long priorityId;
    private Long lobId;
    private Long demandTypeId;
    private Long demandTimelineId;
    private Long externalInternalId;
    private Long statusId;
    private Long podId;
    private Long pmoSpocId;
    private Long pmoId;
    private Long salesSpocId;
    private Long hiringManagerId;
    private Long deliveryManagerId;
    private Long skillClusterId;

    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private List<Long> locationIds;
}
