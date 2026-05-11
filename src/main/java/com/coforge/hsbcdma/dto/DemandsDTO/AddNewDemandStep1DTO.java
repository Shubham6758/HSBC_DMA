package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Request: what user fills in Step 1
@Getter
@Setter
public class AddNewDemandStep1DTO {
    private Long bandId;
    private Long priorityId;
    private Long lobId;
    private Long demandTypeId;
    private Long demandTimelineId;
    private Long externalInternalId;
    private Long statusId;
    private Long podId;
    private Long pmoSpocId;
    private Long salesSpocId;
    private Long hiringManagerId;
    private Long deliveryManagerId;
    private Long skillClusterId;
    private Long pmoId;
    private Long projectManagerId;
    private Long hbuId;
    private Long hbuSpocId;

    // Others
    private String experience;
//    private String fileName;     // optional at step-1 (usually step-2)
    private String remark;

    // JSON arrays of BIGINT IDs
    private List<Long> primarySkillsId;
    private List<Long> secondarySkillsId;
    private List<Long> demandLocationId;

    // Draft-only
    private Integer numberOfPositions; // required at step-1

    // Audit (from SecurityContext; server populates)
    // private String createdByUserId;
    // private String createdByName;
}
