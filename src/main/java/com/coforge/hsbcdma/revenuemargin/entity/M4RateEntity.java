package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_m4_rate")
public class M4RateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer ref;

    private String roleRef;

    private String roleTitle;

    private BigDecimal maxDayRateUsd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getRef() {
        return ref;
    }

    public void setRef(Integer ref) {
        this.ref = ref;
    }

    public String getRoleRef() {
        return roleRef;
    }

    public void setRoleRef(String roleRef) {
        this.roleRef = roleRef;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public BigDecimal getMaxDayRateUsd() {
        return maxDayRateUsd;
    }

    public void setMaxDayRateUsd(BigDecimal maxDayRateUsd) {
        this.maxDayRateUsd = maxDayRateUsd;
    }
}