package com.coforge.hsbcdma.revenuemargin.dto;

import java.math.BigDecimal;

public class M4RateRecord {

    private Integer ref;

    private String roleRef;

    private String roleTitle;

    private BigDecimal maxDayRateUsd;

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