package com.coforge.hsbcdma.dto;

import lombok.Data;

@Data
public class TaManagementResponseDto {

    private Long id;
    private String demandId;
    private String primarySkills;
    private String secondarySkills;
    private String nameOfCandidate;
    private Double yearsOfExperience;
    private Long noticePeriod;
    private String briefSummary;
    private String location;
    private String cvFileName;
    private String cvFileBase64;
    private String irsFileName;
    private String irsFileBase64;

}
