package com.coforge.hsbcdma.dto.DashboardDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    private List<LobSummaryDto> lobSummary;
    private List<HbuSummaryDto> hbuSummary;
}