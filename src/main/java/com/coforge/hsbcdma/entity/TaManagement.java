package com.coforge.hsbcdma.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="ta_management")
public class TaManagement extends BaseEntity {

    @Column(name = "DEMAND_ID", nullable = false)
    private String demandId;
    @Column(name = "PRIMARY_SKILLS", nullable = false)
    private String primarySkills;
    @Column(name = "SECONDARY_SKILLS", nullable = false)
    private String secondarySkills;
    @Column(name = "NAME_OF_CANDIDATE", nullable = false)
    private String nameOfCandidate;
    @Column(name = "YEARS_OF_EXPERIENCE", nullable = false)
    private Double yearsOfExperience;
    @Column(name = "NOTICE_PERIOD", nullable = false)
    private Long noticePeriod;
    @Column(name = "BRIEF_SUMMARY", nullable = false)
    private String briefSummary;
    @Column(name = "LOCATION", nullable = false)
    private String location;
    @Column(name = "CV_FILE_NAME", nullable = false)
    private String cvFileName;
    @Column(name = "irs_FILE_NAME", nullable = false)
    private String irsFileName;

}
