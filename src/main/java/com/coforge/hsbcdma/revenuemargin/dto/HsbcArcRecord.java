package com.coforge.hsbcdma.revenuemargin.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HsbcArcRecord {

    private String pernr;
    private LocalDate zmonth;
    private String recType;
    private String country;
    private String countryKey;
    private String customerName;
    private String customerGrp;
    private String ibu;
    private String subIbu;
    private String ansvh;
    private String band;

    private BigDecimal conversionRate;
    private BigDecimal costToCo;

    private String currency;
    private String location;
    private String deliveryL1;
    private String deliveryL2;
    private String empHorizontal;

    private BigDecimal ctcInUsd;

    private String projectType;
    private String bukrs;
    private String empGroupPrct;
    private String orgeh;
    private String posid;
    private String isActive;
    private String monthYear;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    private String deliveryL0;
    private String wbsElement;

    public String getPernr() {
        return pernr;
    }
    public void setPernr(String pernr) {
        this.pernr = pernr;
    }

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryKey() {
        return countryKey;
    }

    public void setCountryKey(String countryKey) {
        this.countryKey = countryKey;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerGrp() {
        return customerGrp;
    }

    public void setCustomerGrp(String customerGrp) {
        this.customerGrp = customerGrp;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getSubIbu() {
        return subIbu;
    }

    public void setSubIbu(String subIbu) {
        this.subIbu = subIbu;
    }

    public String getAnsvh() {
        return ansvh;
    }

    public void setAnsvh(String ansvh) {
        this.ansvh = ansvh;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getCostToCo() {
        return costToCo;
    }

    public void setCostToCo(BigDecimal costToCo) {
        this.costToCo = costToCo;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDeliveryL1() {
        return deliveryL1;
    }

    public void setDeliveryL1(String deliveryL1) {
        this.deliveryL1 = deliveryL1;
    }

    public String getDeliveryL2() {
        return deliveryL2;
    }

    public void setDeliveryL2(String deliveryL2) {
        this.deliveryL2 = deliveryL2;
    }

    public String getEmpHorizontal() {
        return empHorizontal;
    }

    public void setEmpHorizontal(String empHorizontal) {
        this.empHorizontal = empHorizontal;
    }

    public BigDecimal getCtcInUsd() {
        return ctcInUsd;
    }

    public void setCtcInUsd(BigDecimal ctcInUsd) {
        this.ctcInUsd = ctcInUsd;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getBukrs() {
        return bukrs;
    }

    public void setBukrs(String bukrs) {
        this.bukrs = bukrs;
    }

    public String getEmpGroupPrct() {
        return empGroupPrct;
    }

    public void setEmpGroupPrct(String empGroupPrct) {
        this.empGroupPrct = empGroupPrct;
    }

    public String getOrgeh() {
        return orgeh;
    }

    public void setOrgeh(String orgeh) {
        this.orgeh = orgeh;
    }

    public String getPosid() {
        return posid;
    }

    public void setPosid(String posid) {
        this.posid = posid;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getMonthYear() {
        return monthYear;
    }

    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }

    public String getDeliveryL0() {
        return deliveryL0;
    }

    public void setDeliveryL0(String deliveryL0) {
        this.deliveryL0 = deliveryL0;
    }

    public LocalDate getZmonth() {
        return zmonth;
    }

    public void setZmonth(LocalDate zmonth) {
        this.zmonth = zmonth;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public LocalDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(LocalDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getWbsElement() {
        return wbsElement;
    }

    public void setWbsElement(String wbsElement) {
        this.wbsElement = wbsElement;
    }
}