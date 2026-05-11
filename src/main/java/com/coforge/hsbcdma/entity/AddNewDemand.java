package com.coforge.hsbcdma.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Setter
@Getter
@ToString
@Entity
@Table(name = "add_new_demands", uniqueConstraints ={@UniqueConstraint(name="demand_id_constraint",columnNames = "DEMAND_ID"),
        @UniqueConstraint(name="rr_number_constraint",columnNames = "RR_NUMBER")})

public class AddNewDemand extends BaseEntity{

    @Column(name = "DEMAND_ID", nullable = false)
    private String demandId;
    @Column(name = "RR_NUMBER", nullable = true)
    private Long rrNumber;
    @Column(name = "LOB", nullable = false)
    private String lob;
    @Column(name = "NUMBER_OF_POSITIONS", nullable = true)
    private Integer noOfPositions;
    @Column(name = "SKILL_CLUSTER", nullable = true)
    private String  skillCluter;
    @Column(name = "PRIMARY_SKILLS", nullable = true)
    private String primarySkills;
    @Column(name = "SECONDARY_SKILLS", nullable = true)
    private String secondarySkills;
    @Column(name = "DEMAND_RECEIVED_DATE", nullable = true)
    private LocalDate demandReceivedDate;
    @Column(name = "HIRING_MANAGER", nullable = true)
    private String hiringManager;
    @Column(name = "SALES_SPOC", nullable = true)
    private String salesSpoc;
    @Column(name = "DELIVERY_MANAGER", nullable = true)
    private String deliveryManager;
    @Column(name = "PMO", nullable = true)
    private String pmo;
    @Column(name = "HBU", nullable = true)
    private String hbu;
    @Column(name = "DEMAND_TYPE", nullable = true)
    private String demandType;
    @Column(name = "PROD_PROGRAM_NAME", nullable = true)
    private String prodProgramName;
    @Column(name = "EXPERIENCE", nullable = true)
    private String experience;
    @Column(name = "PRIORITY", nullable = true)
    private String priority;
    @Column(name = "DEMAND_LOCATION", nullable = true)
    private String demandLocation;
    @Column(name = "PRIORITY_COMMENT", nullable = true)
    private String priorityComment;
    @Column(name = "PM", nullable = true)
    private String pm;
    @Column(name = "BAND", nullable = true)
    private String band;
    @Column(name = "P1_AGE", nullable = true)
    private String p1Age;
    @Column(name = "CURRENT_PROFILE_SHARED", nullable = true)
    private Integer currentProfileShared;
    @Column(name = "REMARK", nullable = true)
    private String remark;
    @Column(name = "DEMAND_TIMELINE", nullable = true)
    private String demandTimeline;
    @Column(name = "EXTERNAL_INTERNAL", nullable = true)
    private String externalInternal;
    @Column(name = "STATUS", nullable = true)
    private String status;
    @Column(name = "PMO_SPOC", nullable = true)
    private String pmoSpoc;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE_CONTENT_TYPE")
    private String fileContentType;
    @Lob
    @Column(columnDefinition = "MEDIUMBLOB") //file size < 16MB
    private byte[] data;
}
