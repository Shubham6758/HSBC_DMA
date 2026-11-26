package com.coforge.hsbcdma.dto.ReportDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyHiringTrendDTO {

    private String month;

    private Long openDemands;

    private Long profilesShared;

    private Long interviewed;

    private Long selected;

    private Long onboarded;
}