package com.coforge.hsbcdma.dto.DemandsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EditDemandPayloadDTO {

    // Editable primitives
    private Long rrNumber;
    private String experience;
    private String remark;

    // FK references (editable)
    private Long bandId;
    private Long priorityId;
    private Long lobId;
    private Long subLobId;
    private Long demandTypeId;
    private Long demandTimelineId;
    private Long externalInternalId;
    private Long statusId;
    private Long podId;
    private Long pmoSpocId;
    private Long salesSpocId;
    private Long hiringManagerId;
    private Long projectManagerId;
    private Long deliveryManagerId;
    private Long skillClusterId;
    private Long pmoId;
    private Long hbuId;
    private Long hbuSpocId;

    private Boolean isSubconRR;
    private Boolean karatFlag;

    // Many-to-many (editable)
    // Semantics recommended:
    // - null  => do not change
    // - []    => clear
    // - [..]  => replace
    private List<Long> primarySkillsId;
    private List<Long> secondarySkillsId;
    private List<Long> demandLocationId;

    // JD update options
    // If you want to accept either jdText or fileIndex (similar to step2)
    private String jdText;
    private String fileName;
    private String filenameHint;

    // Optional: explicitly clear JD file name from DB
    private Boolean clearJd;
}
