package com.coforge.hsbcdma.entity.Resource;

import com.coforge.hsbcdma.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "resource")
public class Resource extends BaseEntity {

    @Column(name = "coforge_id")
    private String coforgeId;

    @Column(name = "ps_code")
    private String psCode;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "lob")
    private String lob;

    @Column(name = "new_lob")
    private String newLob;

    @Column(name = "coforge_band")
    private String coforgeBand;

    @Column(name = "hbu")
    private String hbu;

    @Column(name = "coforge_location")
    private String coforgeLocation;

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

    @Column(name = "experience")
    private String experience;

    @Column(name = "ctool")
    private String cTool;

    @Column(name = "ctool_location")
    private String cToolLocation;

    @Column(name = "pricing_model_hsbc")
    private String pricingModelHsbc;

    @Column(name = "job_category")
    private String jobCategory;

    @Column(name = "gcb_level")
    private String gcbLevel;

    @Column(name = "currency")
    private String currency;

    @Column(name = "rate")
    private String rate;

    @Column(name = "effective_rate")
    private String effectiveRate;

    @Column(name = "fixed_capacity_rate")
    private String fixedCapacityRate;

    @Column(name = "effective_start_date")
    private String effectiveStartDate;

    @Column(name = "effective_end_date")
    private String effectiveEndDate;

    @Column(name = "resource_status")
    private String resourceStatus;

    @Column(name = "onshore_offshore")
    private String onshoreOffshore;

    @Column(name = "pricing_model_coforge")
    private String pricingModelCoforge;

    @Column(name = "tentative_onboarding_date")
    private String tentativeOnboardingDate;

    @Column(name = "wbs_code")
    private String wbsCode;

    @Column(name = "ipms_allocation")
    private String ipmsAllocation;

    @Column(name = "gender")
    private String gender;

    @Column(name = "ipms_allocation_start_date")
    private String ipmsAllocationStartDate;

    @Column(name = "ipms_allocation_end_date")
    private String ipmsAllocationEndDate;

    @Column(name = "concatenate_for_headcount_number")
    private String concatenateForHeadcountNumber;

    @Column(name = "concatenate_for_headcount_new_lob")
    private String concatenateForHeadcountNewLob;

    @Column(name = "month_of_release")
    private String monthOfRelease;

    @Column(name = "date_of_exit")
    private String dateOfExit;

    @Column(name = "exit_status")
    private String exitStatus;

    @Column(name = "reason_for_exit")
    private String reasonForExit;

    @Column(name = "replacement_required")
    private String replacementRequired;

    @Column(name = "remarks_client_discussion_outcome")
    private String remarksClientDiscussionOutcome;

    @Column(name = "replacement_id")
    private String replacementId;

    @Column(name = "replacement_name")
    private String replacementName;

    @Column(name = "sub_con_vendor")
    private String subConVendor;

    @Column(name = "loc")
    private String loc;

    @Column(name = "lob_check")
    private String lobCheck;

    @Column(name = "coforge_email_id")
    private String coforgeEmailId;

    @Column(name = "hsbc_email_id")
    private String hsbcEmailId;

    @Column(name = "skill_cluster")
    private String skillCluster;

    @Column(name = "sub_cluster")
    private String subCluster;

    @Column(name = "bfs_dpa")
    private String bfsDpa;
}
