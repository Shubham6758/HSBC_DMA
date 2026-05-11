package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;

import java.util.List;

@Data
public class Step2SubmitPayloadDTO {
    // how many demands to create
    private Integer numberOfPositions;

    // --- common fields (earlier Step1 DTO fields moved here) ---
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
    private Long deliveryManagerId;
    private Long skillClusterId;
    private Long pmoId;
    private Long projectManagerId;
    private Long hbuId;
    private Long hbuSpocId;

    // experience is now VARCHAR in DB => String in Java
    private String experience;

    private String remark;

    private Boolean isSubconRR;
    private Boolean karatFlag;

    private List<Long> primarySkillsId;
    private List<Long> secondarySkillsId;
    private List<Long> demandLocationId;

    // --- per position RR + JD mapping ---
    private List<RrJdRequestDTO> rrs;


    // NEW — single JD to apply to all where RR doesn't specify one
    private Boolean useSingleJdForAll;   // default false when null

    // Choose exactly one of these two (file-based OR text-based single JD):
    private String singleJdFileName;     // must match one of 'files[i].originalFilename'
    private String singleJdText;         // use this text as JD for all

    // Optional hint used when singleJdText is used:
    private String singleFilenameHint;


}