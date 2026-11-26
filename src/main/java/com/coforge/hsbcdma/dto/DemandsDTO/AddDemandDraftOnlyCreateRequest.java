package com.coforge.hsbcdma.dto.DemandsDTO;

import com.coforge.hsbcdma.validation.NoHtml;
import com.coforge.hsbcdma.validation.ValidSummary;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AddDemandDraftOnlyCreateRequest {

    private Boolean flag;

    private Boolean karatFlag;

    @NotNull(message = "Number of positions is required")
    @Min(value = 1, message = "Number of positions must be greater than 0")
    private Integer numberOfPositions;

    @NoHtml
    @Size(max = 100, message = "Experience cannot exceed 100 characters")
    private String experience;

    @ValidSummary
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
    private Long subLobId;
    private Long projectManagerId;
}
