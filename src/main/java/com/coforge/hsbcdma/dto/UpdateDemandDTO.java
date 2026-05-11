package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.util.LocalDateFormatConverter;
import com.coforge.hsbcdma.validate.Stages;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UpdateDemandDTO {

    private Long id;
    private String demandId;
    private Long rrNumber;
    private String lob;
    private String  skillCluter;
    @NotBlank
    private String primarySkills;
    @NotBlank
    private String secondarySkills;
    @NotNull
    @JsonFormat(pattern = "dd-MMM-yyyy")
    private LocalDate demandReceivedDate;
    @NotBlank
    private String hiringManager;
    @NotBlank
    private String salesSpoc;
    @NotBlank
    private String deliveryManager;
    @NotBlank
    private String pmo;
    @NotBlank
    private String hbu;
    @NotBlank
    private String demandType;
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
    //private DropdownDataDTO dropdownDataCache;
}
