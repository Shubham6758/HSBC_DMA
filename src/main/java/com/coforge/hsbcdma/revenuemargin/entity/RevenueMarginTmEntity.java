package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_revenue_margin_tm")
public class RevenueMarginTmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String empId;
    private String psid;
    private String name;
    private String ctool;
    @Column(name = "coforge_band")
    private String coforgeBand;
    @Column(name = "gb_gi")
    private String gbGi;
    @Column(name = "bf_level_3")
    private String bfLevel3;
    @Column(name = "bf_level_4")
    private String bfLevel4;
    @Column(name = "pricing_model")
    private String pricingModel;
    @Column(name = "effective_start_date")
    private LocalDate effectiveStartDate;
    @Column(name = "effective_end_date")
    private LocalDate effectiveEndDate;
    @Column(name = "resource_status")
    private String resourceStatus;
    private String country;
    @Column(name = "fte_sub_con")
    private String fteSubCon;
    @Column(name = "job_category_gcb_level")
    private String jobCategoryGcbLevel;
    @Column(name = "ctool_rate")
    private BigDecimal ctoolRate;
    @Column(name = "m4_rate")
    private BigDecimal m4Rate;
    @Column(name = "effective_rate")
    private BigDecimal effectiveRate;
    @Column(name = "APR26_RATE")
    private BigDecimal apr26Rate;
    @Column(name = "APR26_REVENUE")
    private BigDecimal apr26Revenue;
    @Column(name = "APR26_CTC")
    private BigDecimal apr26Ctc;
    @Column(name = "APR26_MARGIN")
    private BigDecimal apr26Margin;
    @Column(name = "MAY26_RATE")
    private BigDecimal may26Rate;
    @Column(name = "MAY26_REVENUE")
    private BigDecimal may26Revenue;
    @Column(name = "MAY26_CTC")
    private BigDecimal may26Ctc;
    @Column(name = "MAY26_MARGIN")
    private BigDecimal may26Margin;
    @Column(name = "JUN26_RATE")
    private BigDecimal jun26Rate;
    @Column(name = "JUN26_REVENUE")
    private BigDecimal jun26Revenue;
    @Column(name = "JUN26_CTC")
    private BigDecimal jun26Ctc;
    @Column(name = "JUN26_MARGIN")
    private BigDecimal jun26Margin;

        // Q1

    @Column(name = "Q1_REV")
    private BigDecimal q1Rev;

    @Column(name = "Q1_COST")
    private BigDecimal q1Cost;

    @Column(name = "Q1_MARGIN")
    private BigDecimal q1Margin;

        // JUL 2026

    @Column(name = "JUL26_RATE")
    private BigDecimal jul26Rate;

    @Column(name = "JUL26_REVENUE")
    private BigDecimal jul26Revenue;

    @Column(name = "JUL26_CTC")
    private BigDecimal jul26Ctc;

    @Column(name = "JUL26_MARGIN")
    private BigDecimal jul26Margin;

        // AUG 2026

    @Column(name = "AUG26_RATE")
    private BigDecimal aug26Rate;

    @Column(name = "AUG26_REVENUE")
    private BigDecimal aug26Revenue;

    @Column(name = "AUG26_CTC")
    private BigDecimal aug26Ctc;

    @Column(name = "AUG26_MARGIN")
    private BigDecimal aug26Margin;

    @Column(name = "SEP26_WORKING_DAYS")
    private BigDecimal sep26WorkingDays;

    @Column(name = "SEP26_HOLIDAYS_LEAVES")
    private BigDecimal sep26HolidaysLeaves;

    @Column(name = "SEP26_RATE")
    private BigDecimal sep26Rate;

    @Column(name = "SEP26_REVENUE")
    private BigDecimal sep26Revenue;

    @Column(name = "SEP26_CTC")
    private BigDecimal sep26Ctc;

    @Column(name = "SEP26_MARGIN")
    private BigDecimal sep26Margin;

    @Column(name = "Q2_REV")
    private BigDecimal q2Rev;

    @Column(name = "Q2_COST")
    private BigDecimal q2Cost;

    @Column(name = "Q2_MARGIN")
    private BigDecimal q2Margin;

    // OCT

    @Column(name = "OCT26_WORKING_DAYS")
    private BigDecimal oct26WorkingDays;
    @Column(name = "OCT26_HOLIDAYS_LEAVES")
    private BigDecimal oct26HolidaysLeaves;
    @Column(name = "OCT26_RATE")
    private BigDecimal oct26Rate;
    @Column(name = "OCT26_REVENUE")
    private BigDecimal oct26Revenue;
    @Column(name = "OCT26_CTC")
    private BigDecimal oct26Ctc;
    @Column(name = "OCT26_MARGIN")
    private BigDecimal oct26Margin;

// NOV

    @Column(name = "NOV26_WORKING_DAYS")
    private BigDecimal nov26WorkingDays;
    @Column(name = "NOV26_HOLIDAYS_LEAVES")
    private BigDecimal nov26HolidaysLeaves;
    @Column(name = "NOV26_RATE")
    private BigDecimal nov26Rate;
    @Column(name = "NOV26_REVENUE")
    private BigDecimal nov26Revenue;
    @Column(name = "NOV26_CTC")
    private BigDecimal nov26Ctc;
    @Column(name = "NOV26_MARGIN")
    private BigDecimal nov26Margin;

// DEC

    @Column(name = "DEC26_WORKING_DAYS")
    private BigDecimal dec26WorkingDays;
    @Column(name = "DEC26_HOLIDAYS_LEAVES")
    private BigDecimal dec26HolidaysLeaves;
    @Column(name = "DEC26_RATE")
    private BigDecimal dec26Rate;
    @Column(name = "DEC26_REVENUE")
    private BigDecimal dec26Revenue;
    @Column(name = "DEC26_CTC")
    private BigDecimal dec26Ctc;
    @Column(name = "DEC26_MARGIN")
    private BigDecimal dec26Margin;

// Q3

    @Column(name = "Q3_REV")
    private BigDecimal q3Rev;
    @Column(name = "Q3_COST")
    private BigDecimal q3Cost;
    @Column(name = "Q3_MARGIN")
    private BigDecimal q3Margin;

// JAN

    @Column(name = "JAN27_WORKING_DAYS")
    private BigDecimal jan27WorkingDays;
    @Column(name = "JAN27_HOLIDAYS_LEAVES")
    private BigDecimal jan27HolidaysLeaves;
    @Column(name = "JAN27_RATE")
    private BigDecimal jan27Rate;
    @Column(name = "JAN27_REVENUE")
    private BigDecimal jan27Revenue;
    @Column(name = "JAN27_CTC")
    private BigDecimal jan27Ctc;
    @Column(name = "JAN27_MARGIN")
    private BigDecimal jan27Margin;

// FEB

    @Column(name = "FEB27_WORKING_DAYS")
    private BigDecimal feb27WorkingDays;
    @Column(name = "FEB27_HOLIDAYS_LEAVES")
    private BigDecimal feb27HolidaysLeaves;
    @Column(name = "FEB27_RATE")
    private BigDecimal feb27Rate;
    @Column(name = "FEB27_REVENUE")
    private BigDecimal feb27Revenue;
    @Column(name = "FEB27_CTC")
    private BigDecimal feb27Ctc;
    @Column(name = "FEB27_MARGIN")
    private BigDecimal feb27Margin;

// MAR

    @Column(name = "MAR27_WORKING_DAYS")
    private BigDecimal mar27WorkingDays;
    @Column(name = "MAR27_HOLIDAYS_LEAVES")
    private BigDecimal mar27HolidaysLeaves;
    @Column(name = "MAR27_RATE")
    private BigDecimal mar27Rate;
    @Column(name = "MAR27_REVENUE")
    private BigDecimal mar27Revenue;
    @Column(name = "MAR27_CTC")
    private BigDecimal mar27Ctc;
    @Column(name = "MAR27_MARGIN")
    private BigDecimal mar27Margin;

// Q4

    @Column(name = "Q4_REV")
    private BigDecimal q4Rev;
    @Column(name = "Q4_COST")
    private BigDecimal q4Cost;
    @Column(name = "Q4_MARGIN")
    private BigDecimal q4Margin;

// FY

    @Column(name = "FY2026_27_REV")
    private BigDecimal fy202627Rev;
    @Column(name = "FY2026_27_COST")
    private BigDecimal fy202627Cost;
    @Column(name = "FY2026_27_MARGIN")
    private BigDecimal fy202627Margin;

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

    public String getCtool() {
        return ctool;
    }

    public void setCtool(String ctool) {
        this.ctool = ctool;
    }

    public String getCoforgeBand() {
        return coforgeBand;
    }

    public void setCoforgeBand(String coforgeBand) {
        this.coforgeBand = coforgeBand;
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

    public BigDecimal getEffectiveRate() {
        return effectiveRate;
    }

    public void setEffectiveRate(BigDecimal effectiveRate) {
        this.effectiveRate = effectiveRate;
    }

    public BigDecimal getM4Rate() {
        return m4Rate;
    }

    public void setM4Rate(BigDecimal m4Rate) {
        this.m4Rate = m4Rate;
    }

    public BigDecimal getCtoolRate() {
        return ctoolRate;
    }

    public void setCtoolRate(BigDecimal ctoolRate) {
        this.ctoolRate = ctoolRate;
    }

    public String getJobCategoryGcbLevel() {
        return jobCategoryGcbLevel;
    }

    public void setJobCategoryGcbLevel(String jobCategoryGcbLevel) {
        this.jobCategoryGcbLevel = jobCategoryGcbLevel;
    }

    public String getFteSubCon() {
        return fteSubCon;
    }

    public void setFteSubCon(String fteSubCon) {
        this.fteSubCon = fteSubCon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getResourceStatus() {
        return resourceStatus;
    }

    public void setResourceStatus(String resourceStatus) {
        this.resourceStatus = resourceStatus;
    }

    public LocalDate getEffectiveEndDate() {
        return effectiveEndDate;
    }

    public void setEffectiveEndDate(LocalDate effectiveEndDate) {
        this.effectiveEndDate = effectiveEndDate;
    }

    public LocalDate getEffectiveStartDate() {
        return effectiveStartDate;
    }

    public void setEffectiveStartDate(LocalDate effectiveStartDate) {
        this.effectiveStartDate = effectiveStartDate;
    }

    public String getPricingModel() {
        return pricingModel;
    }

    public void setPricingModel(String pricingModel) {
        this.pricingModel = pricingModel;
    }

    public BigDecimal getApr26Rate() {
        return apr26Rate;
    }

    public void setApr26Rate(BigDecimal apr26Rate) {
        this.apr26Rate = apr26Rate;
    }

    public BigDecimal getApr26Revenue() {
        return apr26Revenue;
    }

    public void setApr26Revenue(BigDecimal apr26Revenue) {
        this.apr26Revenue = apr26Revenue;
    }

    public BigDecimal getApr26Ctc() {
        return apr26Ctc;
    }

    public void setApr26Ctc(BigDecimal apr26Ctc) {
        this.apr26Ctc = apr26Ctc;
    }

    public BigDecimal getApr26Margin() {
        return apr26Margin;
    }

    public void setApr26Margin(BigDecimal apr26Margin) {
        this.apr26Margin = apr26Margin;
    }

    public BigDecimal getMay26Rate() {
        return may26Rate;
    }

    public void setMay26Rate(BigDecimal may26Rate) {
        this.may26Rate = may26Rate;
    }

    public BigDecimal getMay26Revenue() {
        return may26Revenue;
    }

    public void setMay26Revenue(BigDecimal may26Revenue) {
        this.may26Revenue = may26Revenue;
    }

    public BigDecimal getMay26Ctc() {
        return may26Ctc;
    }

    public void setMay26Ctc(BigDecimal may26Ctc) {
        this.may26Ctc = may26Ctc;
    }

    public BigDecimal getMay26Margin() {
        return may26Margin;
    }

    public void setMay26Margin(BigDecimal may26Margin) {
        this.may26Margin = may26Margin;
    }

    public BigDecimal getJun26Rate() {
        return jun26Rate;
    }

    public void setJun26Rate(BigDecimal jun26Rate) {
        this.jun26Rate = jun26Rate;
    }

    public BigDecimal getJun26Revenue() {
        return jun26Revenue;
    }

    public void setJun26Revenue(BigDecimal jun26Revenue) {
        this.jun26Revenue = jun26Revenue;
    }

    public BigDecimal getJun26Ctc() {
        return jun26Ctc;
    }

    public void setJun26Ctc(BigDecimal jun26Ctc) {
        this.jun26Ctc = jun26Ctc;
    }

    public BigDecimal getJun26Margin() {
        return jun26Margin;
    }

    public void setJun26Margin(BigDecimal jun26Margin) {
        this.jun26Margin = jun26Margin;
    }

    public BigDecimal getQ1Rev() {
        return q1Rev;
    }

    public void setQ1Rev(BigDecimal q1Rev) {
        this.q1Rev = q1Rev;
    }

    public BigDecimal getQ1Cost() {
        return q1Cost;
    }

    public void setQ1Cost(BigDecimal q1Cost) {
        this.q1Cost = q1Cost;
    }

    public BigDecimal getQ1Margin() {
        return q1Margin;
    }

    public void setQ1Margin(BigDecimal q1Margin) {
        this.q1Margin = q1Margin;
    }

    public BigDecimal getJul26Rate() {
        return jul26Rate;
    }

    public void setJul26Rate(BigDecimal jul26Rate) {
        this.jul26Rate = jul26Rate;
    }

    public BigDecimal getJul26Revenue() {
        return jul26Revenue;
    }

    public void setJul26Revenue(BigDecimal jul26Revenue) {
        this.jul26Revenue = jul26Revenue;
    }

    public BigDecimal getJul26Ctc() {
        return jul26Ctc;
    }

    public void setJul26Ctc(BigDecimal jul26Ctc) {
        this.jul26Ctc = jul26Ctc;
    }

    public BigDecimal getJul26Margin() {
        return jul26Margin;
    }

    public void setJul26Margin(BigDecimal jul26Margin) {
        this.jul26Margin = jul26Margin;
    }

    public BigDecimal getAug26Rate() {
        return aug26Rate;
    }

    public void setAug26Rate(BigDecimal aug26Rate) {
        this.aug26Rate = aug26Rate;
    }

    public BigDecimal getAug26Revenue() {
        return aug26Revenue;
    }

    public void setAug26Revenue(BigDecimal aug26Revenue) {
        this.aug26Revenue = aug26Revenue;
    }

    public BigDecimal getAug26Ctc() {
        return aug26Ctc;
    }

    public void setAug26Ctc(BigDecimal aug26Ctc) {
        this.aug26Ctc = aug26Ctc;
    }

    public BigDecimal getAug26Margin() {
        return aug26Margin;
    }

    public void setAug26Margin(BigDecimal aug26Margin) {
        this.aug26Margin = aug26Margin;
    }

    public BigDecimal getSep26WorkingDays() {
        return sep26WorkingDays;
    }

    public void setSep26WorkingDays(BigDecimal sep26WorkingDays) {
        this.sep26WorkingDays = sep26WorkingDays;
    }

    public BigDecimal getSep26HolidaysLeaves() {
        return sep26HolidaysLeaves;
    }

    public void setSep26HolidaysLeaves(BigDecimal sep26HolidaysLeaves) {
        this.sep26HolidaysLeaves = sep26HolidaysLeaves;
    }

    public BigDecimal getSep26Rate() {
        return sep26Rate;
    }

    public void setSep26Rate(BigDecimal sep26Rate) {
        this.sep26Rate = sep26Rate;
    }

    public BigDecimal getSep26Revenue() {
        return sep26Revenue;
    }

    public void setSep26Revenue(BigDecimal sep26Revenue) {
        this.sep26Revenue = sep26Revenue;
    }

    public BigDecimal getSep26Ctc() {
        return sep26Ctc;
    }

    public void setSep26Ctc(BigDecimal sep26Ctc) {
        this.sep26Ctc = sep26Ctc;
    }

    public BigDecimal getSep26Margin() {
        return sep26Margin;
    }

    public void setSep26Margin(BigDecimal sep26Margin) {
        this.sep26Margin = sep26Margin;
    }

    public BigDecimal getQ2Rev() {
        return q2Rev;
    }

    public void setQ2Rev(BigDecimal q2Rev) {
        this.q2Rev = q2Rev;
    }

    public BigDecimal getQ2Cost() {
        return q2Cost;
    }

    public void setQ2Cost(BigDecimal q2Cost) {
        this.q2Cost = q2Cost;
    }

    public BigDecimal getQ2Margin() {
        return q2Margin;
    }

    public void setQ2Margin(BigDecimal q2Margin) {
        this.q2Margin = q2Margin;
    }

    public BigDecimal getOct26WorkingDays() {
        return oct26WorkingDays;
    }

    public void setOct26WorkingDays(BigDecimal oct26WorkingDays) {
        this.oct26WorkingDays = oct26WorkingDays;
    }

    public BigDecimal getOct26HolidaysLeaves() {
        return oct26HolidaysLeaves;
    }

    public void setOct26HolidaysLeaves(BigDecimal oct26HolidaysLeaves) {
        this.oct26HolidaysLeaves = oct26HolidaysLeaves;
    }

    public BigDecimal getOct26Rate() {
        return oct26Rate;
    }

    public void setOct26Rate(BigDecimal oct26Rate) {
        this.oct26Rate = oct26Rate;
    }

    public BigDecimal getOct26Revenue() {
        return oct26Revenue;
    }

    public void setOct26Revenue(BigDecimal oct26Revenue) {
        this.oct26Revenue = oct26Revenue;
    }

    public BigDecimal getOct26Ctc() {
        return oct26Ctc;
    }

    public void setOct26Ctc(BigDecimal oct26Ctc) {
        this.oct26Ctc = oct26Ctc;
    }

    public BigDecimal getOct26Margin() {
        return oct26Margin;
    }

    public void setOct26Margin(BigDecimal oct26Margin) {
        this.oct26Margin = oct26Margin;
    }

    public BigDecimal getNov26WorkingDays() {
        return nov26WorkingDays;
    }

    public void setNov26WorkingDays(BigDecimal nov26WorkingDays) {
        this.nov26WorkingDays = nov26WorkingDays;
    }

    public BigDecimal getNov26HolidaysLeaves() {
        return nov26HolidaysLeaves;
    }

    public void setNov26HolidaysLeaves(BigDecimal nov26HolidaysLeaves) {
        this.nov26HolidaysLeaves = nov26HolidaysLeaves;
    }

    public BigDecimal getNov26Rate() {
        return nov26Rate;
    }

    public void setNov26Rate(BigDecimal nov26Rate) {
        this.nov26Rate = nov26Rate;
    }

    public BigDecimal getNov26Revenue() {
        return nov26Revenue;
    }

    public void setNov26Revenue(BigDecimal nov26Revenue) {
        this.nov26Revenue = nov26Revenue;
    }

    public BigDecimal getNov26Ctc() {
        return nov26Ctc;
    }

    public void setNov26Ctc(BigDecimal nov26Ctc) {
        this.nov26Ctc = nov26Ctc;
    }

    public BigDecimal getNov26Margin() {
        return nov26Margin;
    }

    public void setNov26Margin(BigDecimal nov26Margin) {
        this.nov26Margin = nov26Margin;
    }

    public BigDecimal getDec26WorkingDays() {
        return dec26WorkingDays;
    }

    public void setDec26WorkingDays(BigDecimal dec26WorkingDays) {
        this.dec26WorkingDays = dec26WorkingDays;
    }

    public BigDecimal getDec26HolidaysLeaves() {
        return dec26HolidaysLeaves;
    }

    public void setDec26HolidaysLeaves(BigDecimal dec26HolidaysLeaves) {
        this.dec26HolidaysLeaves = dec26HolidaysLeaves;
    }

    public BigDecimal getDec26Rate() {
        return dec26Rate;
    }

    public void setDec26Rate(BigDecimal dec26Rate) {
        this.dec26Rate = dec26Rate;
    }

    public BigDecimal getDec26Revenue() {
        return dec26Revenue;
    }

    public void setDec26Revenue(BigDecimal dec26Revenue) {
        this.dec26Revenue = dec26Revenue;
    }

    public BigDecimal getDec26Ctc() {
        return dec26Ctc;
    }

    public void setDec26Ctc(BigDecimal dec26Ctc) {
        this.dec26Ctc = dec26Ctc;
    }

    public BigDecimal getDec26Margin() {
        return dec26Margin;
    }

    public void setDec26Margin(BigDecimal dec26Margin) {
        this.dec26Margin = dec26Margin;
    }

    public BigDecimal getQ3Rev() {
        return q3Rev;
    }

    public void setQ3Rev(BigDecimal q3Rev) {
        this.q3Rev = q3Rev;
    }

    public BigDecimal getQ3Cost() {
        return q3Cost;
    }

    public void setQ3Cost(BigDecimal q3Cost) {
        this.q3Cost = q3Cost;
    }

    public BigDecimal getQ3Margin() {
        return q3Margin;
    }

    public void setQ3Margin(BigDecimal q3Margin) {
        this.q3Margin = q3Margin;
    }

    public BigDecimal getJan27WorkingDays() {
        return jan27WorkingDays;
    }

    public void setJan27WorkingDays(BigDecimal jan27WorkingDays) {
        this.jan27WorkingDays = jan27WorkingDays;
    }

    public BigDecimal getJan27HolidaysLeaves() {
        return jan27HolidaysLeaves;
    }

    public void setJan27HolidaysLeaves(BigDecimal jan27HolidaysLeaves) {
        this.jan27HolidaysLeaves = jan27HolidaysLeaves;
    }

    public BigDecimal getJan27Rate() {
        return jan27Rate;
    }

    public void setJan27Rate(BigDecimal jan27Rate) {
        this.jan27Rate = jan27Rate;
    }

    public BigDecimal getJan27Revenue() {
        return jan27Revenue;
    }

    public void setJan27Revenue(BigDecimal jan27Revenue) {
        this.jan27Revenue = jan27Revenue;
    }

    public BigDecimal getJan27Ctc() {
        return jan27Ctc;
    }

    public void setJan27Ctc(BigDecimal jan27Ctc) {
        this.jan27Ctc = jan27Ctc;
    }

    public BigDecimal getJan27Margin() {
        return jan27Margin;
    }

    public void setJan27Margin(BigDecimal jan27Margin) {
        this.jan27Margin = jan27Margin;
    }

    public BigDecimal getFeb27WorkingDays() {
        return feb27WorkingDays;
    }

    public void setFeb27WorkingDays(BigDecimal feb27WorkingDays) {
        this.feb27WorkingDays = feb27WorkingDays;
    }

    public BigDecimal getFeb27HolidaysLeaves() {
        return feb27HolidaysLeaves;
    }

    public void setFeb27HolidaysLeaves(BigDecimal feb27HolidaysLeaves) {
        this.feb27HolidaysLeaves = feb27HolidaysLeaves;
    }

    public BigDecimal getFeb27Rate() {
        return feb27Rate;
    }

    public void setFeb27Rate(BigDecimal feb27Rate) {
        this.feb27Rate = feb27Rate;
    }

    public BigDecimal getFeb27Revenue() {
        return feb27Revenue;
    }

    public void setFeb27Revenue(BigDecimal feb27Revenue) {
        this.feb27Revenue = feb27Revenue;
    }

    public BigDecimal getFeb27Ctc() {
        return feb27Ctc;
    }

    public void setFeb27Ctc(BigDecimal feb27Ctc) {
        this.feb27Ctc = feb27Ctc;
    }

    public BigDecimal getFeb27Margin() {
        return feb27Margin;
    }

    public void setFeb27Margin(BigDecimal feb27Margin) {
        this.feb27Margin = feb27Margin;
    }

    public BigDecimal getMar27WorkingDays() {
        return mar27WorkingDays;
    }

    public void setMar27WorkingDays(BigDecimal mar27WorkingDays) {
        this.mar27WorkingDays = mar27WorkingDays;
    }

    public BigDecimal getMar27HolidaysLeaves() {
        return mar27HolidaysLeaves;
    }

    public void setMar27HolidaysLeaves(BigDecimal mar27HolidaysLeaves) {
        this.mar27HolidaysLeaves = mar27HolidaysLeaves;
    }

    public BigDecimal getMar27Rate() {
        return mar27Rate;
    }

    public void setMar27Rate(BigDecimal mar27Rate) {
        this.mar27Rate = mar27Rate;
    }

    public BigDecimal getMar27Revenue() {
        return mar27Revenue;
    }

    public void setMar27Revenue(BigDecimal mar27Revenue) {
        this.mar27Revenue = mar27Revenue;
    }

    public BigDecimal getMar27Ctc() {
        return mar27Ctc;
    }

    public void setMar27Ctc(BigDecimal mar27Ctc) {
        this.mar27Ctc = mar27Ctc;
    }

    public BigDecimal getMar27Margin() {
        return mar27Margin;
    }

    public void setMar27Margin(BigDecimal mar27Margin) {
        this.mar27Margin = mar27Margin;
    }

    public BigDecimal getQ4Rev() {
        return q4Rev;
    }

    public void setQ4Rev(BigDecimal q4Rev) {
        this.q4Rev = q4Rev;
    }

    public BigDecimal getQ4Cost() {
        return q4Cost;
    }

    public void setQ4Cost(BigDecimal q4Cost) {
        this.q4Cost = q4Cost;
    }

    public BigDecimal getQ4Margin() {
        return q4Margin;
    }

    public void setQ4Margin(BigDecimal q4Margin) {
        this.q4Margin = q4Margin;
    }

    public BigDecimal getFy202627Rev() {
        return fy202627Rev;
    }

    public void setFy202627Rev(BigDecimal fy202627Rev) {
        this.fy202627Rev = fy202627Rev;
    }

    public BigDecimal getFy202627Cost() {
        return fy202627Cost;
    }

    public void setFy202627Cost(BigDecimal fy202627Cost) {
        this.fy202627Cost = fy202627Cost;
    }

    public BigDecimal getFy202627Margin() {
        return fy202627Margin;
    }

    public void setFy202627Margin(BigDecimal fy202627Margin) {
        this.fy202627Margin = fy202627Margin;
    }
}