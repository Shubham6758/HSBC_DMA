package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.validate.RrNumberWhenCurrent;
import com.coforge.hsbcdma.validate.Stages;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RrNumberWhenCurrent(groups = Stages.Stage2.class)
public class AddNewDemandDTO {

    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String lob;
    @NotNull @Digits(integer = 10, fraction = 0, groups = {Stages.Stage1.class, Stages.Stage2.class})
    private Integer noOfPositions;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String  skillCluster;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String primarySkills;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String secondarySkills;
    @NotNull(groups = {Stages.Stage1.class, Stages.Stage2.class})
    @JsonFormat(pattern = "dd-MMM-yyyy")
    @DateTimeFormat(pattern = "dd-MMM-yyyy")
    private LocalDate demandReceivedDate;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String hiringManager;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String salesSpoc;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String deliveryManager;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String pmo;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String hbu;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String demandType;
    @NotBlank(groups = {Stages.Stage1.class, Stages.Stage2.class})
    private String demandTimeline;
    private String prodProgramName;
    private String experience;
    private String priority;
    private String demandLocation;
    private String priorityComment;
    private String pm;
    private String band;
    private String p1Age;
    private Integer currentProfileShared;
    private String externalInternal;
    private String status;
    private String pmoSpoc;
    private String remark;

    @Valid
    @NotNull(groups = Stages.Stage2.class)
    private List<DemandRRDTO> demandRRDTOList;
}
