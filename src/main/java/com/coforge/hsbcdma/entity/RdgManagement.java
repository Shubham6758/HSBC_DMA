package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="rdg_management")
public class RdgManagement extends BaseEntity {

    @Column(name = "DEMAND_ID", nullable = false)
    private String demandId;
    @Column(name = "PRIMARY_SKILLS", nullable = false)
    private String primarySkills;
    @Column(name = "SECONDARY_SKILLS", nullable = false)
    private String secondarySkills;
    @Column(name = "EMPLOYEE_ID", nullable = false)
    private Long employeeId;
    @Column(name = "NAME_OF_CANDIDATE", nullable = false)
    private String nameOfCandidate;
    @Column(name = "YEARS_OF_EXPERIENCE", nullable = false)
    private Double yearsOfExperience;
    @Column(name = "BRIEF_SUMMARY", nullable = false)
    private String briefSummary;
    @Column(name = "LOCATION", nullable = false)
    private String location;
    @Column(name = "CV_FILE_NAME", nullable = false)
    private String cvFileName;

}
