package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DailyReportDTO.ReportRequestDTO;
import com.coforge.hsbcdma.dto.DailyReportDTO.ReportResponseDTO;
import com.coforge.hsbcdma.service.DailyReportServices.DailyReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class DailyReportController {

    private final DailyReportService reportService;

    @PostMapping("/dailyReports")
    public ResponseEntity<ReportResponseDTO> getReport(
            @RequestBody ReportRequestDTO request) {

        return ResponseEntity.ok(
                reportService.getOnboardingSummary(request));
    }
}
