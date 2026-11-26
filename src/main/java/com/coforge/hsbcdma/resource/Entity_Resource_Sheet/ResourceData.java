package com.coforge.hsbcdma.resource.Entity_Resource_Sheet;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "resource_data")
@Getter
@Setter
public class ResourceData {

    // ================= BASIC =================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "psid")
    private String psid;

    @Column(name = "resource_name")
    private String resourceName;

    @Column(name = "lob")
    private String lob;

    @Column(name = "new_lob")
    private String newLob;

    @Column(name = "new_lob_for_others")
    private String newLobForOthers;

    @Column(name = "business_function")
    private String businessFunction;

    @Column(name = "sub_business_function")
    private String subBusinessFunction;

    @Column(name = "department")
    private String department;

    @Column(name = "functional_mgr_hsbc")
    private String functionalMgrHsbc;

    @Column(name = "primary_skill")
    private String primarySkill;

    // ================= FINANCIAL / EXPERIENCE =================

    @Column(name = "experience")
    private BigDecimal experience;

    @Column(name = "ctool")
    private String ctool;

    @Column(name = "ctool_location")
    private String ctoolLocation;

    @Column(name = "pricing_model_hsbc")
    private String pricingModelHsbc;

    @Column(name = "job_category")
    private String jobCategory;

    @Column(name = "gcb_level")
    private String gcbLevel;

    @Column(name = "currency")
    private String currency;

    @Column(name = "rate")
    private BigDecimal rate;

    @Column(name = "effective_rate")
    private BigDecimal effectiveRate;

    @Column(name = "fixed_capacity_rate")
    private BigDecimal fixedCapacityRate;

    // ================= DATE FIELDS =================

    @Column(name = "effective_start_date")
    private LocalDate effectiveStartDate;

    @Column(name = "effective_end_date")
    private LocalDate effectiveEndDate;

    @Column(name = "tentative_onboarding_date")
    private LocalDate tentativeOnboardingDate;

    @Column(name = "date_of_exit")
    private LocalDate dateOfExit;

    @Column(name = "ipms_allocation_start_date")
    private LocalDate ipmsAllocationStartDate;

    @Column(name = "ipms_allocation_end_date")
    private LocalDate ipmsAllocationEndDate;

    // ================= RESOURCE INFO =================

    @Column(name = "emp_id")
    private String empId;

    @Column(name = "resource_status")
    private String resourceStatus;

    @Column(name = "onshore_offshore")
    private String onshoreOffshore;

    @Column(name = "pricing_model_coforge")
    private String pricingModelCoforge;

    @Column(name = "wbs_code")
    private String wbsCode;

    @Column(name = "coforge_band")
    private String coforgeBand;

    @Column(name = "hbu")
    private String hbu;

    @Column(name = "ipms_allocation")
    private String ipmsAllocation;

    @Column(name = "gender")
    private String gender;

    @Column(name = "coforge_location")
    private String coforgeLocation;

    // ================= CONCAT / REPORTING =================

    @Column(name = "concat_headcount_number")
    private String concatHeadcountNumber;

    @Column(name = "concat_headcount_new_lob")
    private String concatHeadcountNewLob;

    @Column(name = "month_of_release")
    private String monthOfRelease;

    // ================= EXIT INFO =================

    @Column(name = "exit_status")
    private String exitStatus;

    @Column(name = "reason_for_exit", length = 2000)
    private String reasonForExit;

    @Column(name = "replacement_required")
    private String replacementRequired;

    @Column(name = "remarks", length = 2000)
    private String remarks;

    @Column(name = "replacement_given")
    private String replacementGiven;

    @Column(name = "replacement_id")
    private String replacementId;

    @Column(name = "replacement_name")
    private String replacementName;

    // ================= AUDIT =================

    @Column(name = "is_active")
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
