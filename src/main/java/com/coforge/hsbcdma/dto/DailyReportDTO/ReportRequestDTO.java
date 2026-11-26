package com.coforge.hsbcdma.dto.DailyReportDTO;


import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class ReportRequestDTO {

    private String trackerType;

    private List<String> selectedColumns;

    private LocalDate fromDate;

    private LocalDate toDate;

    private Map<String, List<String>> filters;
}