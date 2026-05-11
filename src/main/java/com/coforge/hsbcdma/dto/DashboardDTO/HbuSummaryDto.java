package com.coforge.hsbcdma.dto.DashboardDTO;

public class HbuSummaryDto {

    private String hbu;
    private Long openDemand;
    private Long fulfilledDemand;
    private Long profileSharedCount;

    public HbuSummaryDto(String hbu,
                         Long openDemand,
                         Long fulfilledDemand,
                         Long profileSharedCount) {
        this.hbu = hbu;
        this.openDemand = openDemand;
        this.fulfilledDemand = fulfilledDemand;
        this.profileSharedCount = profileSharedCount;
    }

    public String getHbu() {
        return hbu;
    }

    public Long getOpenDemand() {
        return openDemand;
    }

    public Long getFulfilledDemand() {
        return fulfilledDemand;
    }

    public Long getProfileSharedCount() {
        return profileSharedCount;
    }
}