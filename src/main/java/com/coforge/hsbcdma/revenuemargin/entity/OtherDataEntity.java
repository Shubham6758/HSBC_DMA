package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "tbl_other_data")
public class OtherDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empId;
    private String psid;
    private String name;
    private String coforgeBand;
    private String lob;
    private String gbGi;
    private String bfLevel3;
    private String bfLevel4;
    private String bfLevel5;
    private String pricingModel;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmpId() {
        return empId;
    }

    public void setEmpId(String empId) {
        this.empId = empId;
    }

    public String getPsid() {
        return psid;
    }

    public void setPsid(String psid) {
        this.psid = psid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCoforgeBand() {
        return coforgeBand;
    }

    public void setCoforgeBand(String coforgeBand) {
        this.coforgeBand = coforgeBand;
    }

    public String getLob() {
        return lob;
    }

    public void setLob(String lob) {
        this.lob = lob;
    }

    public String getGbGi() {
        return gbGi;
    }

    public void setGbGi(String gbGi) {
        this.gbGi = gbGi;
    }

    public String getBfLevel3() {
        return bfLevel3;
    }

    public void setBfLevel3(String bfLevel3) {
        this.bfLevel3 = bfLevel3;
    }

    public String getBfLevel4() {
        return bfLevel4;
    }

    public void setBfLevel4(String bfLevel4) {
        this.bfLevel4 = bfLevel4;
    }

    public String getBfLevel5() {
        return bfLevel5;
    }

    public void setBfLevel5(String bfLevel5) {
        this.bfLevel5 = bfLevel5;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
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
}