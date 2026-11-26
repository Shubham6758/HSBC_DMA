package com.coforge.hsbcdma.dto.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DemandFilterCountDTO {

    private Long totalDemands;
    private Long openCount;
    private Long closedCount;
    private Long rejectedCount;
}