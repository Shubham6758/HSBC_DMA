package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_vm_dump")
public class VmDumpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String resourceId;
    private String resourceName;
    private String ctoolId;
    private String pricingModel;
    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;
    private String jobCategoryGcbLevel;
    @Column(name = "actual_rate")
    private BigDecimal ctoolRate;
    private String functionalManager;
    private String functionalManagerEmailId;
    private String entityManager;
    private String vendorName;

    private String equivalentGcb;
    @Column(name = "legal_entity")
    private String legalEntity;
    private String costCenter;

    private String bfLevel3;
    private String bfLevel4;

    private String baseLocation;
    private String premises;

    private String jobCategory;
    private String operatingUnit;

    private String poNumber;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    private String currency;
    private String unitOfMeasure;

    private String roleActive;
    private String resourceActive;
    private String landedFlag;
    private String onePo;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getResourceId() {return resourceId;}
    public void setResourceId(String resourceId) {this.resourceId = resourceId;}
    public String getResourceName() {return resourceName;}
    public void setResourceName(String resourceName) {this.resourceName = resourceName;}
    public String getCtoolId() {return ctoolId;}
    public void setCtoolId(String ctoolId) {
        this.ctoolId = ctoolId;
    }
    public String getPricingModel() {return pricingModel;}
    public void setPricingModel(String pricingModel) {this.pricingModel = pricingModel;}
    public LocalDate getEffectiveStartDate() {return effectiveStartDate;}
    public void setEffectiveStartDate(LocalDate effectiveStartDate) {this.effectiveStartDate = effectiveStartDate;}
    public LocalDate getEffectiveEndDate() {return effectiveEndDate;}
    public void setEffectiveEndDate(LocalDate effectiveEndDate) {this.effectiveEndDate = effectiveEndDate;}
    public String getJobCategoryGcbLevel() {return jobCategoryGcbLevel;}
    public void setJobCategoryGcbLevel(String jobCategoryGcbLevel) {this.jobCategoryGcbLevel = jobCategoryGcbLevel;}
    public BigDecimal getCtoolRate() {return ctoolRate;}
    public void setCtoolRate(BigDecimal ctoolRate) {this.ctoolRate = ctoolRate;}
    public String getFunctionalManager() {return functionalManager;}
    public void setFunctionalManager(String functionalManager) {this.functionalManager = functionalManager;}
    public String getFunctionalManagerEmailId() {return functionalManagerEmailId;}
    public void setFunctionalManagerEmailId(String functionalManagerEmailId) {
        this.functionalManagerEmailId = functionalManagerEmailId;}
    public String getEntityManager() {return entityManager;}
    public void setEntityManager(String entityManager) {this.entityManager = entityManager;}
    public String getVendorName() {return vendorName;}
    public void setVendorName(String vendorName) {this.vendorName = vendorName;}
    public String getEquivalentGcb() {return equivalentGcb;}
    public void setEquivalentGcb(String equivalentGcb) {this.equivalentGcb = equivalentGcb;}
    public String getLegalEntity() {return legalEntity;}
    public void setLegalEntity(String legalEntity) {this.legalEntity = legalEntity;}
    public String getCostCenter() {return costCenter;}
    public void setCostCenter(String costCenter) {this.costCenter = costCenter;}
    public String getBfLevel3() {return bfLevel3;}
    public void setBfLevel3(String bfLevel3) {this.bfLevel3 = bfLevel3;}
    public String getBfLevel4() {return bfLevel4;}
    public void setBfLevel4(String bfLevel4) {this.bfLevel4 = bfLevel4;}
    public String getBaseLocation() {return baseLocation;}
    public void setBaseLocation(String baseLocation) {this.baseLocation = baseLocation;}
    public String getPremises() {return premises;}
    public void setPremises(String premises) {this.premises = premises;}
    public String getJobCategory() {return jobCategory;}
    public void setJobCategory(String jobCategory) {this.jobCategory = jobCategory;}
    public String getOperatingUnit() {return operatingUnit;}
    public void setOperatingUnit(String operatingUnit) {this.operatingUnit = operatingUnit;}
    public String getPoNumber() {return poNumber;}
    public void setPoNumber(String poNumber) {this.poNumber = poNumber;}
    public LocalDate getContractStartDate() {return contractStartDate;}
    public void setContractStartDate(LocalDate contractStartDate) {this.contractStartDate = contractStartDate;}
    public LocalDate getContractEndDate() {return contractEndDate;}
    public void setContractEndDate(LocalDate contractEndDate) {this.contractEndDate = contractEndDate;}
    public String getCurrency() {return currency;}
    public void setCurrency(String currency) {this.currency = currency;}
    public String getUnitOfMeasure() {return unitOfMeasure;}
    public void setUnitOfMeasure(String unitOfMeasure) {this.unitOfMeasure = unitOfMeasure;}
    public String getRoleActive() {return roleActive;}
    public void setRoleActive(String roleActive) {this.roleActive = roleActive;}
    public String getResourceActive() {return resourceActive;}
    public void setResourceActive(String resourceActive) {this.resourceActive = resourceActive;}
    public String getLandedFlag() {return landedFlag;}
    public void setLandedFlag(String landedFlag) {this.landedFlag = landedFlag;}
    public String getOnePo() {return onePo;}
    public void setOnePo(String onePo) {this.onePo = onePo;}
}