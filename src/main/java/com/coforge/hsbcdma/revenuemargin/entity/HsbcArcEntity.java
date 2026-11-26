package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_hsbc_arc")
public class HsbcArcEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String pernr;
    private LocalDate zmonth;
    private String recType;
    private String country;
    private String countryKey;
    private String customerName;
    @Column(name = "customer_grp")
    private String customerGrp;
    private String ibu;
    private String subIbu;
    private String ansvh;
    private String band;

    private BigDecimal conversionRate;
    @Column(name = "cost_to_co")
    private BigDecimal costToCo;
    //@Convert(converter = ArcEncryptionConverter.class)
    //private String costToCo;
    //@Transient
    //private BigDecimal decryptedCostToCo;
    private String currency;
    private String location;
    @Column(name = "delivery_l1")
    private String deliveryL1;
    @Column(name = "delivery_l2")
    private String deliveryL2;
    private String empHorizontal;
    @Column(name = "ctc_in_usd")
    private BigDecimal ctcInUsd;
    //@Convert(converter = ArcEncryptionConverter.class)
    //private String ctcInUsd;
    //@Transient
    //private BigDecimal decryptedCtcInUsd;
    private String projectType;
    private String bukrs;
    @Column(name = "emp_group_prct")
    private String empGroupPrct;
    private String orgeh;
    private String posid;
    private String isActive;
    private String monthYear;
    private LocalDateTime createdOn;
    private LocalDateTime modifiedOn;
    @Column(name = "delivery_l0")
    private String deliveryL0;
    @Column(name = "wbs_element")
    private String wbsElement;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPernr() {
        return pernr;
    }

    public void setPernr(String pernr) {
        this.pernr = pernr;
    }

    public LocalDate getZmonth() {
        return zmonth;
    }

    public void setZmonth(LocalDate zmonth) {
        this.zmonth = zmonth;
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

    public BigDecimal getCostToCo() {return costToCo;}
    public void setCostToCo(BigDecimal costToCo) {this.costToCo = costToCo;}
    //public String getCostToCo() {return costToCo;}
    //public BigDecimal getCostToCo() {if (costToCo == null) {return null;}
    //    return new BigDecimal(costToCo);}
    //public void setCostToCo(BigDecimal costToCo) {
    //    this.costToCo = costToCo == null
    //                    ? null
    //                    : costToCo.toPlainString();}

    //public void setCostToCo(String costToCo) {this.costToCo = costToCo;}

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

    public BigDecimal getCtcInUsd() {return ctcInUsd;}
    public void setCtcInUsd(BigDecimal ctcInUsd) {this.ctcInUsd = ctcInUsd;}

//    public String getCtcInUsd() {return ctcInUsd;}
//    public void setCtcInUsd(String ctcInUsd) {this.ctcInUsd = ctcInUsd;}

//    public BigDecimal getCtcInUsd() {
//        if (ctcInUsd == null) {return null;}
//        return new BigDecimal(ctcInUsd);}

//    public void setCtcInUsd(BigDecimal ctcInUsd) {
//        this.ctcInUsd = ctcInUsd == null
//                        ? null
//                        : ctcInUsd.toPlainString();}

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

    public String getDeliveryL0() {
        return deliveryL0;
    }

    public void setDeliveryL0(String deliveryL0) {
        this.deliveryL0 = deliveryL0;
    }

    public String getWbsElement() {
        return wbsElement;
    }

    public void setWbsElement(String wbsElement) {
        this.wbsElement = wbsElement;
    }
}