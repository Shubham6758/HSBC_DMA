package com.coforge.hsbcdma.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TalentDashboardDTO {

    Integer totalDemands;
    Integer openPositions;
    Integer closedPositions;
    Integer onHold;
    Integer abandoned;
    Integer fulfilled;
    Integer OnBoardingInProgress;
    Integer profileShared;
    Integer rejected;
    Integer softSelect;
    Integer candidateResigned;
    Integer duplicateDemand;
}
