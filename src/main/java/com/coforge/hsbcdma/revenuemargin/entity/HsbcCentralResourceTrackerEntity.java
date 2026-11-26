package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_hsbc_central_resource_tracker")
public class HsbcCentralResourceTrackerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "resource_key")
    private String key;
    private String coforgeId;
    private String psCode;
    private String employeeName;
    private String lob;
    private String newLob;
    private String coforgeBand;
    private String hbu;
    private String coforgeLocation;
    private String newLobForOthers;
    private String businessFunction;
    private String subBusinessFunction;
    private String department;
    private String functionalMgrHsbc;
    @Column(length = 2000)
    private String primarySkill;
    private String experience;
    private String ctool;
    private String ctoolLocation;
    @Column(name = "pricing_model_hsbc")
    private String pricingModelHsbc;
    private String jobCategory;
    private String gcbLevel;
    private String currency;

    private BigDecimal rate;
    private BigDecimal effectiveRate;
    private BigDecimal fixedCapacityRate;

    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;

    private String resourceStatus;
    private String onshoreOffshore;
    private String pricingModelCoforge;

    private LocalDate tentativeOnboardingDate;

    private String wbsCode;
    private String ipmsAllocation;
    private String gender;

    private LocalDate ipmsAllocationStartDate;
    private LocalDate ipmsAllocationEndDate;

    private String concatenateForHeadcountNumber;
    private String concatenateForHeadcountNewLob;

    private String monthOfRelease;

    private LocalDate dateOfExit;

    private String exitStatus;
    private String reasonForExit;
    private String replacementRequired;
    private String remarks;
    private String replacementId;
    private String replacementName;
    private String subConVendor;
    private String loc;
    private String lobCheck;
    private String coforgeEmailId;
    private String hsbcEmailId;
    private String skillCluster;
    private String subCluster;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCoforgeId() {
        return coforgeId;
    }

    public void setCoforgeId(String coforgeId) {
        this.coforgeId = coforgeId;
    }

    public String getPsCode() {
        return psCode;
    }

    public void setPsCode(String psCode) {
        this.psCode = psCode;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public String getNewLob() {
        return newLob;
    }

    public void setNewLob(String newLob) {
        this.newLob = newLob;
    }

    public String getCoforgeBand() {
        return coforgeBand;
    }

    public void setCoforgeBand(String coforgeBand) {
        this.coforgeBand = coforgeBand;
    }

    public String getHbu() {
        return hbu;
    }

    public void setHbu(String hbu) {
        this.hbu = hbu;
    }

    public String getCoforgeLocation() {
        return coforgeLocation;
    }

    public void setCoforgeLocation(String coforgeLocation) {
        this.coforgeLocation = coforgeLocation;
    }

    public String getNewLobForOthers() {
        return newLobForOthers;
    }

    public void setNewLobForOthers(String newLobForOthers) {
        this.newLobForOthers = newLobForOthers;
    }

    public String getBusinessFunction() {
        return businessFunction;
    }

    public void setBusinessFunction(String businessFunction) {
        this.businessFunction = businessFunction;
    }

    public String getSubBusinessFunction() {
        return subBusinessFunction;
    }

    public void setSubBusinessFunction(String subBusinessFunction) {
        this.subBusinessFunction = subBusinessFunction;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFunctionalMgrHsbc() {
        return functionalMgrHsbc;
    }

    public void setFunctionalMgrHsbc(String functionalMgrHsbc) {
        this.functionalMgrHsbc = functionalMgrHsbc;
    }

    public String getPrimarySkill() {
        return primarySkill;
    }

    public void setPrimarySkill(String primarySkill) {
        this.primarySkill = primarySkill;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCtool() {
        return ctool;
    }

    public void setCtool(String ctool) {
        this.ctool = ctool;
    }

    public String getCtoolLocation() {
        return ctoolLocation;
    }

    public void setCtoolLocation(String ctoolLocation) {
        this.ctoolLocation = ctoolLocation;
    }

    public String getPricingModelHsbc() {return pricingModelHsbc;}
    public void setPricingModelHsbc(String pricingModelHsbc) {this.pricingModelHsbc = pricingModelHsbc;}

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getGcbLevel() {
        return gcbLevel;
    }

    public void setGcbLevel(String gcbLevel) {
        this.gcbLevel = gcbLevel;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(BigDecimal effectiveRate) {
        this.effectiveRate = effectiveRate;
    }

    public BigDecimal getFixedCapacityRate() {
        return fixedCapacityRate;
    }

    public void setFixedCapacityRate(BigDecimal fixedCapacityRate) {
        this.fixedCapacityRate = fixedCapacityRate;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public String getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public String getOnshoreOffshore() {
        return onshoreOffshore;
    }

    public void setOnshoreOffshore(String onshoreOffshore) {
        this.onshoreOffshore = onshoreOffshore;
    }

    public String getPricingModelCoforge() {
        return pricingModelCoforge;
    }

    public void setPricingModelCoforge(String pricingModelCoforge) {
        this.pricingModelCoforge = pricingModelCoforge;
    }

    public LocalDate getTentativeOnboardingDate() {
        return tentativeOnboardingDate;
    }

    public void setTentativeOnboardingDate(LocalDate tentativeOnboardingDate) {
        this.tentativeOnboardingDate = tentativeOnboardingDate;
    }

    public String getWbsCode() {
        return wbsCode;
    }

    public void setWbsCode(String wbsCode) {
        this.wbsCode = wbsCode;
    }

    public String getIpmsAllocation() {
        return ipmsAllocation;
    }

    public void setIpmsAllocation(String ipmsAllocation) {
        this.ipmsAllocation = ipmsAllocation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getIpmsAllocationStartDate() {
        return ipmsAllocationStartDate;
    }

    public void setIpmsAllocationStartDate(LocalDate ipmsAllocationStartDate) {
        this.ipmsAllocationStartDate = ipmsAllocationStartDate;
    }

    public LocalDate getIpmsAllocationEndDate() {
        return ipmsAllocationEndDate;
    }

    public void setIpmsAllocationEndDate(LocalDate ipmsAllocationEndDate) {
        this.ipmsAllocationEndDate = ipmsAllocationEndDate;
    }

    public String getConcatenateForHeadcountNumber() {
        return concatenateForHeadcountNumber;
    }

    public void setConcatenateForHeadcountNumber(String concatenateForHeadcountNumber) {
        this.concatenateForHeadcountNumber = concatenateForHeadcountNumber;
    }

    public String getConcatenateForHeadcountNewLob() {
        return concatenateForHeadcountNewLob;
    }

    public void setConcatenateForHeadcountNewLob(String concatenateForHeadcountNewLob) {
        this.concatenateForHeadcountNewLob = concatenateForHeadcountNewLob;
    }

    public String getMonthOfRelease() {
        return monthOfRelease;
    }

    public void setMonthOfRelease(String monthOfRelease) {
        this.monthOfRelease = monthOfRelease;
    }

    public LocalDate getDateOfExit() {
        return dateOfExit;
    }

    public void setDateOfExit(LocalDate dateOfExit) {
        this.dateOfExit = dateOfExit;
    }

    public String getExitStatus() {
        return exitStatus;
    }

    public void setExitStatus(String exitStatus) {
        this.exitStatus = exitStatus;
    }

    public String getReasonForExit() {
        return reasonForExit;
    }

    public void setReasonForExit(String reasonForExit) {
        this.reasonForExit = reasonForExit;
    }

    public String getReplacementRequired() {
        return replacementRequired;
    }

    public void setReplacementRequired(String replacementRequired) {
        this.replacementRequired = replacementRequired;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getReplacementId() {
        return replacementId;
    }

    public void setReplacementId(String replacementId) {
        this.replacementId = replacementId;
    }

    public String getReplacementName() {
        return replacementName;
    }

    public void setReplacementName(String replacementName) {
        this.replacementName = replacementName;
    }

    public String getSubConVendor() {
        return subConVendor;
    }

    public void setSubConVendor(String subConVendor) {
        this.subConVendor = subConVendor;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLobCheck() {
        return lobCheck;
    }

    public void setLobCheck(String lobCheck) {
        this.lobCheck = lobCheck;
    }

    public String getCoforgeEmailId() {
        return coforgeEmailId;
    }

    public void setCoforgeEmailId(String coforgeEmailId) {
        this.coforgeEmailId = coforgeEmailId;
    }

    public String getHsbcEmailId() {
        return hsbcEmailId;
    }

    public void setHsbcEmailId(String hsbcEmailId) {
        this.hsbcEmailId = hsbcEmailId;
    }

    public String getSkillCluster() {
        return skillCluster;
    }

    public void setSkillCluster(String skillCluster) {
        this.skillCluster = skillCluster;
    }

    public String getSubCluster() {
        return subCluster;
    }

    public void setSubCluster(String subCluster) {
        this.subCluster = subCluster;
    }
}