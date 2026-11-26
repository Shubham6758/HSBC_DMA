package com.coforge.hsbcdma.dto.DailyReportDTO;


import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class ReportRowDTO {

    private String reportDate;

    private String status;

    private Map<String, Long> values;

    private Long total;

    private List<ReportRowDTO> children;
}
