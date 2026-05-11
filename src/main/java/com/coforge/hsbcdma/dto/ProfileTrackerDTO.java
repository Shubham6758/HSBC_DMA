package com.coforge.hsbcdma.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


/**
 * ProfileDTO used to transfer Profile tracker data.
 * pratish.b
 */

@Getter
@Setter
public class ProfileTrackerDTO {

    private Long id; //Primary key for AddNewDemand table
    @NotBlank
    private String demandId;
    @NotBlank
    private Long rrNumber;
    @NotBlank
    private String lob;
    private String hiringManager;
    private String  skillCluter;
    private String primarySkills;
    private String secondarySkills;
    private Integer currentProfileShared;
    @JsonFormat(pattern = "dd-MMM-yyyy")
    private LocalDate profileSharedDate;
    private String externalInternal;
    @JsonFormat(pattern = "dd-MMM-yyyy")
    private LocalDate interviewDate;
    private String status;
    @JsonFormat(pattern = "dd-MMM-yyyy")
    private LocalDate decisionDate;
    private String p1Age;
}
