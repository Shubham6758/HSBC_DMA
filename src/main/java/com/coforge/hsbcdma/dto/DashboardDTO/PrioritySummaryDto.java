package com.coforge.hsbcdma.dto.DashboardDTO;

public class PrioritySummaryDto {

    private Long p0Count;
    private Long p1Count;
    private Long p2Count;

    public PrioritySummaryDto(Long p0Count, Long p1Count, Long p2Count) {
        this.p0Count = p0Count;
        this.p1Count = p1Count;
        this.p2Count = p2Count;
    }

    public Long getP0Count() { return p0Count; }
    public Long getP1Count() { return p1Count; }
    public Long getP2Count() { return p2Count; }
}
