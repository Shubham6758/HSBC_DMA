package com.coforge.hsbcdma.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@NotNull
public class RdgManagementResponseDto {

    private Long id;
    private String demandId;
    private String primarySkills;
    private String secondarySkills;
    private Long employeeId;
    private String nameOfCandidate;
    private Double yearsOfExperience;
    private String briefSummary;
    private String location;
    private String cvFileName;
    private String cvFileBase64;

}
