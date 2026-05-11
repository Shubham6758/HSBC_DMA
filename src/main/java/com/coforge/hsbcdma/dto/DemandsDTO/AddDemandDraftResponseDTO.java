package com.coforge.hsbcdma.dto.DemandsDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class AddDemandDraftResponseDTO {

    private Long id;
    private Boolean flag;
    private Integer numberOfPositions;
    private Boolean karatFlag;
    private String experience;
//    private String fileName;
    private String remark;
    private LocalDate demandReceivedDate;

    private String createdByName;
    private String createdByUserId;
    private String updatedByName;
    private String updatedByUserId;

    private RefDTO band;
    private RefDTO priority;
    private RefDTO lob;
    private RefDTO demandType;
    private RefDTO demandTimeline;
    private RefDTO externalInternal;
    private RefDTO status;

    private RefDTO pod;
    private RefDTO pmoSpoc;
    private RefDTO pmo;
    private RefDTO salesSpoc;

    private RefDTO hiringManager;
    private RefDTO deliveryManager;

    private RefDTO hbu;
    private RefDTO hbuSpoc;

    private RefDTO skillCluster;
    private List<RefDTO> primarySkills;
    private List<RefDTO> secondarySkills;
    private List<RefDTO> demandLocations;

    private List<AddDemandRRDraftDTO> rrDrafts;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddDemandRRDraftDTO {
        private Long id;
        private Long rrNumber;
        private String fileName;
    }

}