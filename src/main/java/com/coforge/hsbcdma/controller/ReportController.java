package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.ReportDTO.CombinationCountDTO;
import com.coforge.hsbcdma.dto.ReportDTO.HmSkillReportDTO;
import com.coforge.hsbcdma.service.ReportServices.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/hm-skill")
    public ResponseEntity<List<HmSkillReportDTO>> getReport(
            @RequestParam(required = false) Long hiringManagerId,
            @RequestParam(required = false) Long skillClusterId) {

        return ResponseEntity.ok(
                reportService.getHmSkillReport(hiringManagerId, skillClusterId)
        );
    }


    @GetMapping("/open-count")
    public List<CombinationCountDTO> getOpenCount(
            @RequestParam(required = false) Long hbuId,
            @RequestParam(required = false) Long lobId,
            @RequestParam(required = false) Long hmId,
            @RequestParam(required = false) Long skillId
    ) {
        return reportService.getOpenCount(hbuId, lobId, hmId, skillId);
    }


    @GetMapping("/status")
    public ResponseEntity<List<Map<String, Object>>> getStatus(
            @RequestParam String groupBy,
            @RequestParam(required = false) Long id) {

        if (!groupBy.equalsIgnoreCase("lob") && !groupBy.equalsIgnoreCase("hbu")) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                reportService.getStatus(groupBy.toLowerCase(), id)
        );
    }


}
