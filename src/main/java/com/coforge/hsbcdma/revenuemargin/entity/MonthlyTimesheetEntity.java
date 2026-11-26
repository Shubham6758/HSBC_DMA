package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_monthly_timesheet")
public class MonthlyTimesheetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "resource_id")
    private String resourceId;
    @Column(name = "resource_name")
    private String resourceName;
    @Column(name = "ts_status")
    private String tsStatus;
    @Column(name = "time_period")
    private LocalDate timePeriod;

    private Integer mon;
    private Integer tue;
    private Integer wed;
    private Integer thu;
    private Integer fri;
    private Integer sat;
    private Integer sun;
    @Column(name = "units_consumed")
    private Integer unitsConsumed;
    @Column(name = "consumption_month")
    private LocalDate consumptionMonth;
    @Column(name = "vendor_name")
    private String vendorName;
    @Column(name = "contract_start_date")
    private LocalDate contractStartDate;
    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;
    @Column(name = "vendor_rate")
    private BigDecimal vendorRate;

    private String currency;
    private String unit;
    @Column(name = "po_number")
    private String poNumber;
    @Column(name = "po_line_item_number")
    private String poLineItemNumber;
    @Column(name = "base_location")
    private String baseLocation;
    @Column(name = "legal_entity")
    private String legalEntity;
    @Column(name = "job_category")
    private String jobCategory;
    @Column(name = "pricing_model")
    private String pricingModel;
    @Column(name = "cost_center")
    private String costCenter;

    private String premises;
    @Column(name = "operating_unit")
    private String operatingUnit;
    @Column(name = "effective_start_date")
    private LocalDate effectiveStartDate;
    @Column(name = "effective_end_date")
    private LocalDate effectiveEndDate;
    @Column(name = "consumption_type")
    private String consumptionType;
    @Column(name = "cost_actuals_lcy")
    private BigDecimal costActualsLcy;
    @Column(name = "extended_service_hour_fte_consumed")
    private Integer extendedServiceHourFteConsumed;
    @Column(name = "extended_service_hour_lcy_amount")
    private BigDecimal extendedServiceHourLcyAmount;
    @Column(name = "staff_type")
    private String staffType;
    @Column(name = "emp_class")
    private String empClass;
    @Column(name = "bf_level1")
    private String bfLevel1;
    @Column(name = "bf_level2")
    private String bfLevel2;
    @Column(name = "business_function")
    private String businessFunction;
    @Column(name = "business_sub_function")
    private String businessSubFunction;

    private String department;
    private String country;
    private String region;
    @Column(name = "billing_month")
    private LocalDate billingMonth;
    @Column(name = "equivalent_gcb")
    private String equivalentGcb;
    @Column(name = "ctool_id")
    private String cToolId;
    @Column(name = "ctool_functional_manager")
    private String cToolFunctionalManager;

    private LocalDate month;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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