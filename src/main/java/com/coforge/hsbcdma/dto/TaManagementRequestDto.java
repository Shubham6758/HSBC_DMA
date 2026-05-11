package com.coforge.hsbcdma.dto;

import lombok.Data;

@Data
public class TaManagementRequestDto {

    private String demandId;
    private String primarySkills;
    private String secondarySkills;
    private String nameOfCandidate;
    private Double yearsOfExperience;
    private Long noticePeriod;
    private String briefSummary;
    private String location;
}
