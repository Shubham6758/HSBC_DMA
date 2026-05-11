package com.coforge.hsbcdma.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RdgManagementRequestDto {

    private String demandId;
    private String primarySkills;
    private String secondarySkills;
    private Long employeeId;
    private String nameOfCandidate;
    private Double yearsOfExperience;
    private String briefSummary;
    private String location;
}
