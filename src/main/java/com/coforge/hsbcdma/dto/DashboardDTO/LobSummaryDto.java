package com.coforge.hsbcdma.dto.DashboardDTO;

public class LobSummaryDto {

    private String lob;
    private Long openDemand;
    private Long fulfilledDemand;
    private Long profileSharedCount;

    public LobSummaryDto(String lob,
                         Long openDemand,
                         Long fulfilledDemand,
                         Long profileSharedCount) {
        this.lob = lob;
        this.openDemand = openDemand;
        this.fulfilledDemand = fulfilledDemand;
        this.profileSharedCount = profileSharedCount;
    }

    public String getLob() {
        return lob;
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