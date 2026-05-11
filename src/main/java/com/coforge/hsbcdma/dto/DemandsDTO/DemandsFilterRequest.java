package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandsFilterRequest {

    // ---------- Scalars ----------
    private Boolean flag;
    private Boolean isSubconRR;
    private Boolean karatFlag;

    private Long demandId;
    private String displayDemandId;
    private Long rrNumber;

    // experience stored as String in entity
    private String experienceRange;
//    private Integer experienceFrom;
//    private Integer experienceTo;

    private LocalDate receivedFrom;
    private LocalDate receivedTo;

    private LocalDate p1FlagDateFrom;
    private LocalDate p1FlagDateTo;

    // ---------- ManyToOne (ID‑based) ----------
    private Long hbuId;
    private Long hbuSpocId;
    private Long bandId;
    private Long priorityId;
    private Long statusId;
    private Long lobId;
    private Long subLobId;
    private Long podId;
    private Long skillClusterId;

    private Long projectManagerId;
    private Long pmoId;
    private Long pmoSpocId;

    private Long salesSpocId;
    private Long hiringManagerId;
    private Long deliveryManagerId;

    private Long demandTypeId;
    private Long externalInternalId;

    // ---------- ManyToOne (NAME‑based, MULTI‑SELECT ✅) ----------
    private List<String> hbuNames;
    private List<String> hbuSpocNames;
    private List<String> bandNames;
    private List<String> priorityNames;
    private List<String> statusNames;
    private List<String> lobNames;
    private List<String> subLobNames;
    private List<String> podNames;
    private List<String> skillClusterNames;

    private List<String> projectManagerNames;
    private List<String> pmoNames;
    private List<String> pmoSpocNames;

    private List<String> salesSpocNames;
    private List<String> hiringManagerNames;
    private List<String> deliveryManagerNames;

    private List<String> demandTypeNames;
    private List<String> demandTimelineNames;
    private List<String> externalInternalNames;

    // ---------- ManyToMany ----------
    private List<String> primarySkillNames;
    private List<Long> primarySkillIds;

    private List<String> secondarySkillNames;
    private List<Long> secondarySkillIds;

    private List<String> locationNames;
    private List<Long> locationIds;

    // ---------- Free‑text ----------
    private String keyword;
}