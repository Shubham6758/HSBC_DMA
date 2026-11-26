package com.coforge.hsbcdma.dto.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCountDto {

    private Long totalDemands;
    private Long openCount;
    private Long demandFulfilledCount;
    private Long onboardedCount;
    private Long totalProfileCount;
    private Long internalProfileCount;
    private Long externalProfileCount;
    private Long selectionRejectionPendingCount;
}
