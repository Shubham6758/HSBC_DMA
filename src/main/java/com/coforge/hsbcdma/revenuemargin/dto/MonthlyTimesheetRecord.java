package com.coforge.hsbcdma.revenuemargin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class MonthlyTimesheetRecord {

    private String resourceId;
    private String resourceName;
    private String tsStatus;

    private LocalDate timePeriod;

    private Integer mon;
    private Integer tue;
    private Integer wed;
    private Integer thu;
    private Integer fri;
    private Integer sat;
    private Integer sun;

    private Integer unitsConsumed;

    private LocalDate consumptionMonth;

    private String vendorName;

    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    private BigDecimal vendorRate;

    private String currency;
    private String unit;

    private String poNumber;
    private String poLineItemNumber;

    private String baseLocation;
    private String legalEntity;

    private String jobCategory;
    private String pricingModel;

    private String costCenter;

    private String premises;
    private String operatingUnit;

    private LocalDate effectiveStartDate;
    private LocalDate effectiveEndDate;

    private String consumptionType;

    private BigDecimal costActualsLcy;

    private Integer extendedServiceHourFteConsumed;

    private BigDecimal extendedServiceHourLcyAmount;

    private String staffType;
    private String empClass;

    private String bfLevel1;
    private String bfLevel2;

    private String businessFunction;
    private String businessSubFunction;

    private String department;
    private String country;
    private String region;

    private LocalDate billingMonth;

    private String equivalentGcb;

    private String cToolId;

    private String cToolFunctionalManager;

    private LocalDate month;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getTsStatus() {
        return tsStatus;
    }

    public void setTsStatus(String tsStatus) {
        this.tsStatus = tsStatus;
    }

    public LocalDate getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(LocalDate timePeriod) {
        this.timePeriod = timePeriod;
    }

    public Integer getMon() {
        return mon;
    }

    public void setMon(Integer mon) {
        this.mon = mon;
    }

    public Integer getTue() {
        return tue;
    }

    public void setTue(Integer tue) {
        this.tue = tue;
    }

    public Integer getWed() {
        return wed;
    }

    public void setWed(Integer wed) {
        this.wed = wed;
    }

    public Integer getThu() {
        return thu;
    }

    public void setThu(Integer thu) {
        this.thu = thu;
    }

    public Integer getFri() {
        return fri;
    }

    public void setFri(Integer fri) {
        this.fri = fri;
    }

    public Integer getSat() {
        return sat;
    }

    public void setSat(Integer sat) {
        this.sat = sat;
    }

    public Integer getSun() {
        return sun;
    }

    public void setSun(Integer sun) {
        this.sun = sun;
    }

    public Integer getUnitsConsumed() {
        return unitsConsumed;
    }

    public void setUnitsConsumed(Integer unitsConsumed) {
        this.unitsConsumed = unitsConsumed;
    }

    public LocalDate getConsumptionMonth() {
        return consumptionMonth;
    }

    public void setConsumptionMonth(LocalDate consumptionMonth) {
        this.consumptionMonth = consumptionMonth;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public BigDecimal getVendorRate() {
        return vendorRate;
    }

    public void setVendorRate(BigDecimal vendorRate) {
        this.vendorRate = vendorRate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getPoLineItemNumber() {
        return poLineItemNumber;
    }

    public void setPoLineItemNumber(String poLineItemNumber) {
        this.poLineItemNumber = poLineItemNumber;
    }

    public String getBaseLocation() {
        return baseLocation;
    }

    public void setBaseLocation(String baseLocation) {
        this.baseLocation = baseLocation;
    }

    public String getLegalEntity() {
        return legalEntity;
    }

    public void setLegalEntity(String legalEntity) {
        this.legalEntity = legalEntity;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getOperatingUnit() {
        return operatingUnit;
    }

    public void setOperatingUnit(String operatingUnit) {
        this.operatingUnit = operatingUnit;
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

    public String getConsumptionType() {
        return consumptionType;
    }

    public void setConsumptionType(String consumptionType) {
        this.consumptionType = consumptionType;
    }

    public BigDecimal getCostActualsLcy() {
        return costActualsLcy;
    }

    public void setCostActualsLcy(BigDecimal costActualsLcy) {
        this.costActualsLcy = costActualsLcy;
    }

    public Integer getExtendedServiceHourFteConsumed() {
        return extendedServiceHourFteConsumed;
    }

    public void setExtendedServiceHourFteConsumed(Integer extendedServiceHourFteConsumed) {
        this.extendedServiceHourFteConsumed = extendedServiceHourFteConsumed;
    }

    public BigDecimal getExtendedServiceHourLcyAmount() {
        return extendedServiceHourLcyAmount;
    }

    public void setExtendedServiceHourLcyAmount(BigDecimal extendedServiceHourLcyAmount) {
        this.extendedServiceHourLcyAmount = extendedServiceHourLcyAmount;
    }

    public String getStaffType() {
        return staffType;
    }

    public void setStaffType(String staffType) {
        this.staffType = staffType;
    }

    public String getEmpClass() {
        return empClass;
    }

    public void setEmpClass(String empClass) {
        this.empClass = empClass;
    }

    public String getBfLevel1() {
        return bfLevel1;
    }

    public void setBfLevel1(String bfLevel1) {
        this.bfLevel1 = bfLevel1;
    }

    public String getBfLevel2() {
        return bfLevel2;
    }

    public void setBfLevel2(String bfLevel2) {
        this.bfLevel2 = bfLevel2;
    }

    public String getBusinessFunction() {
        return businessFunction;
    }

    public void setBusinessFunction(String businessFunction) {
        this.businessFunction = businessFunction;
    }

    public String getBusinessSubFunction() {
        return businessSubFunction;
    }

    public void setBusinessSubFunction(String businessSubFunction) {
        this.businessSubFunction = businessSubFunction;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDate getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(LocalDate billingMonth) {
        this.billingMonth = billingMonth;
    }

    public String getEquivalentGcb() {
        return equivalentGcb;
    }

    public void setEquivalentGcb(String equivalentGcb) {
        this.equivalentGcb = equivalentGcb;
    }

    public String getcToolId() {
        return cToolId;
    }

    public void setcToolId(String cToolId) {
        this.cToolId = cToolId;
    }

    public String getcToolFunctionalManager() {
        return cToolFunctionalManager;
    }

    public void setcToolFunctionalManager(String cToolFunctionalManager) {
        this.cToolFunctionalManager = cToolFunctionalManager;
    }

    public LocalDate getMonth() {
        return month;
    }

    public void setMonth(LocalDate month) {
        this.month = month;
    }
}