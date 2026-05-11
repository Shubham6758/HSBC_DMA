package com.coforge.hsbcdma.dto.DemandsDTO;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class DemandDraftEditRequest {

    // -------------------------
    // Draft fields (same as create)
    // -------------------------
    private Boolean flag;
    private Boolean karatFlag;
    private Integer numberOfPositions;
    private String experience;
    private String fileName;        // generally not used for draft header if you store JD per RR row
    private String remark;
    private LocalDate demandReceivedDate;

    private String createdByUserId;
    private String createdByName;

    // Dropdown references as IDs
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

    // Many-to-many ids
    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private List<Long> locationIds;

    // RR Draft rows (edit)
    private List<RrDraftEditRequest> rrDrafts;

    @Data
    public static class RrDraftEditRequest {
        private Long rrDraftId;       // best if you store rr row id
        private Integer positionIndex; // fallback: 1..N in UI order

        private Long rrNumber;

        // keep your earlier field
        private String fileName; // server overwrites with stored filename

        // file/text inputs
        private Integer fileIndex;     // index from multipart files list
        private String jdText;         // optional text
        private String filenameHint;   // optional hint for text

        // allow clearing existing file mapping
        private Boolean clearFile;


        // NEW (partial update)
        private Boolean isSubconRR;

    }
}
