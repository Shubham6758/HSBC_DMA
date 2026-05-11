package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DashboardDTO.DashboardCountDto;
import com.coforge.hsbcdma.dto.DashboardDTO.HbuSummaryDto;
import com.coforge.hsbcdma.dto.DashboardDTO.LobSummaryDto;
import com.coforge.hsbcdma.dto.DashboardDTO.PrioritySummaryDto;
import com.coforge.hsbcdma.service.DashboardServices.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/lob-summary")
    public ResponseEntity<List<LobSummaryDto>> getLobSummary(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        return ResponseEntity.ok(
                dashboardService.getLobSummary(fromDate, toDate)
        );
    }
    @GetMapping("/hbu-summary")
    public ResponseEntity<List<HbuSummaryDto>> getHbuSummary(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        return ResponseEntity.ok(
                dashboardService.getHbuSummary(fromDate, toDate)
        );
    }
    @GetMapping("/priority-summary")
    public ResponseEntity<PrioritySummaryDto> getPrioritySummary(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        PrioritySummaryDto response =
                dashboardService.getPrioritySummary(fromDate, toDate);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard-count")
    public DashboardCountDto getDashboardCount(
            @RequestParam LocalDate fromDate,
            @RequestParam LocalDate toDate) {

        return dashboardService.getDashboardCounts(fromDate, toDate);
    }
}

