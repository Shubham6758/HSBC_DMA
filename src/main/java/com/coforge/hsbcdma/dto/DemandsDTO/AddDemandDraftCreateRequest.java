package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddDemandDraftCreateRequest {

    // Draft fields
    private Boolean flag;
    private Integer numberOfPositions;
    private String experience;
    private String fileName;
    private String remark;

    private LocalDate demandReceivedDate;

    private String createdByUserId;
    private String createdByName;


    // Dropdown references as IDs (recommended for POST payload)
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
    private Boolean karatFlag;

    // Many-to-many as ids (optional)
    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private List<Long> locationIds;

    // RR Draft rows
    private List<RrDraftRequest> rrDrafts;

    @Data
    public static class RrDraftRequest {
        private Long rrNumber;

        // keep your earlier field
        private String fileName; // server will overwrite with stored RR JD filename

        // ✅ ADD these 3 fields inside RR as you asked
        private Integer fileIndex;      // RR JD file position in files[]
        private String jdText;          // optional RR JD text
        private String filenameHint;    // optional filename hint for RR text


        // NEW
        private Boolean isSubconRR;  // true => Subcon, false => RR, null => unspecified in draft


    }
}

