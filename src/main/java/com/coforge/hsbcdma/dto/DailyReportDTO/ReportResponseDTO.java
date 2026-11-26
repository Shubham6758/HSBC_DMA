package com.coforge.hsbcdma.dto.DailyReportDTO;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReportResponseDTO {

    private List<String> columns;

    private List<ReportRowDTO> rows;
}