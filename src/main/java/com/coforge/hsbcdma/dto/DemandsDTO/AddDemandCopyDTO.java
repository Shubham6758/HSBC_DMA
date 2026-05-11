package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class AddDemandCopyDTO {

    private Boolean isSubconRR;
    private Boolean karatFlag;
    private LocalDate p1FlagDate;

    private Long demandId;
    private Long rrNumber;
    private LocalDate demandReceivedDate;

    private String experience;
    private String remark;

    /* ---------- MASTER DTOs (FULL DATA) ---------- */
    private RefDTO hbu;
    private RefDTO hbuSpoc;
    private RefDTO band;
    private RefDTO priority;
    private RefDTO lob;
    private RefDTO subLob;
    private RefDTO demandType;
    private RefDTO demandTimeline;
    private RefDTO externalInternal;
    private RefDTO status;
    private RefDTO pod;
    private RefDTO pmo;
    private RefDTO pmoSpoc;
    private RefDTO salesSpoc;
    private RefDTO hiringManager;
    private RefDTO deliveryManager;
    private RefDTO skillCluster;
    private RefDTO projectManager;

    /* ---------- MANY TO MANY ---------- */
    private Set<RefDTO> primarySkills;
    private Set<RefDTO> secondarySkills;
    private Set<RefDTO> demandLocations;
}

