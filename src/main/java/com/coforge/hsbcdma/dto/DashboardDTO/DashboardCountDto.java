package com.coforge.hsbcdma.dto.DashboardDTO;


public class DashboardCountDto {

    private Long totalDemands;
    private Long openCount;
    private Long closedCount;
    private Long rejectedCount;

    public DashboardCountDto(Long totalDemands,
                             Long openCount,
                             Long closedCount,
                             Long rejectedCount) {
        this.totalDemands = totalDemands;
        this.openCount = openCount;
        this.closedCount = closedCount;
        this.rejectedCount = rejectedCount;
    }

    public Long getTotalDemands() { return totalDemands; }
    public Long getOpenCount() { return openCount; }
    public Long getClosedCount() { return closedCount; }
    public Long getRejectedCount() { return rejectedCount; }
}
