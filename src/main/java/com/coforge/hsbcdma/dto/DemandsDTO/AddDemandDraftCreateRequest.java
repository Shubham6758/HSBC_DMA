package com.coforge.hsbcdma.dto.DemandsDTO;

import com.coforge.hsbcdma.validation.NoHtml;
import com.coforge.hsbcdma.validation.ValidFileName;
import com.coforge.hsbcdma.validation.ValidRrNumber;
import com.coforge.hsbcdma.validation.ValidSummary;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddDemandDraftCreateRequest {

    private Boolean flag;

    @Min(value = 1, message = "Number of positions must be greater than 0")
    private Integer numberOfPositions;

    @NoHtml
    @Size(max = 100, message = "Experience cannot exceed 100 characters")
    private String experience;

    @ValidFileName
    private String fileName;

    @ValidSummary
    private String remark;

    private LocalDate demandReceivedDate;

    private String createdByUserId;
    private String createdByName;

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

    private List<Long> primarySkillIds;
    private List<Long> secondarySkillIds;
    private List<Long> locationIds;
    private Long subLobId;
    private Long projectManagerId;
    private Long businessFunctionId;
    private Long jobCategoryId;
    private Long contractTypeId;
    private Long ctoolId;

    @Valid
    private List<RrDraftRequest> rrDrafts;

    @Data
    public static class RrDraftRequest {

        @ValidRrNumber
        private String rrNumber;

        @ValidFileName
        private String fileName;

        private Integer fileIndex;

        private String jdText;

        @NoHtml
        @Size(max = 255, message = "Filename hint cannot exceed 255 characters")
        private String filenameHint;

        private Boolean isSubconRR;
    }
}