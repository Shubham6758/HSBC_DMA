
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

            @RequestParam(required = false) List<Long> hmIds,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) List<Long> hbuIds,
            @RequestParam(required = false) List<Long> lobIds
    ) {

        System.out.println("HM IDS = " + hmIds);
        System.out.println("SKILL IDS = " + skillIds);
        System.out.println("HBU IDS = " + hbuIds);
        System.out.println("LOB IDS = " + lobIds);

        return ResponseEntity.ok(
                reportService.getHmSkillReport(
                        hmIds,
                        skillIds,
                        hbuIds,
                        lobIds
                )
        );
    }


    @GetMapping("/open-count")
    public List<CombinationCountDTO> getOpenCount(
            @RequestParam(required = false) List<Long> hbuIds,
            @RequestParam(required = false) List<Long> lobIds,
            @RequestParam(required = false) List<Long> hmIds,
            @RequestParam(required = false) List<Long> skillIds,
            @RequestParam(required = false) List<Long> priorityIds


    ) {
        return reportService.getOpenCount(hbuIds, lobIds, hmIds, skillIds,priorityIds);
    }

    @GetMapping("/open-count/all")
    public ResponseEntity<List<CombinationCountDTO>>
    getalllhmhbu(
            @RequestParam(
                    name = "parameters",
                    required = false
            )
            List<String> parameters
    ) {

        return ResponseEntity.ok(
                reportService.getallhmhbu(parameters)
        );
    }
//    @GetMapping("/open-count")
//    public List<CombinationCountDTO> getOpenCount(
//
//            @RequestParam(required = false) List<Long> hbuIds,
//
//            @RequestParam(required = false) List<Long> lobIds,
//
//            @RequestParam(required = false) List<Long> hmIds,
//
//            @RequestParam(required = false) List<Long> skillIds,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate fromDate,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate toDate
//
//    ) {
//        return reportService.getOpenCount(
//                hbuIds,
//                lobIds,
//                hmIds,
//                skillIds,
//                fromDate,
//                toDate
//        );
//    }

//    @GetMapping("/demand-aging")
//    public DemandAgingDTO getDemandAging(
//
//            @RequestParam(required = false)
//            List<Long> hbuIds,
//
//            @RequestParam(required = false)
//            List<Long> lobIds,
//
//            @RequestParam(required = false)
//            List<Long> hmIds,
//
//            @RequestParam(required = false)
//            List<Long> skillIds,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate fromDate,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate toDate
//    ) {
//
//        return reportService.getDemandAgingReport(
//                hbuIds,
//                lobIds,
//                hmIds,
//                skillIds,
//                fromDate,
//                toDate
//        );
//    }
//
//    @GetMapping("/monthly-hiring-trend")
//    public List<MonthlyHiringTrendDTO> getMonthlyHiringTrend(
//
//            @RequestParam(required = false)
//            List<Long> hbuIds,
//
//            @RequestParam(required = false)
//            List<Long> lobIds,
//
//            @RequestParam(required = false)
//            List<Long> hmIds,
//
//            @RequestParam(required = false)
//            List<Long> skillIds,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate fromDate,
//
//            @RequestParam(required = false)
//            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//            LocalDate toDate
//    ) {
//
//        return reportService.getMonthlyHiringTrend(
//                hbuIds,
//                lobIds,
//                hmIds,
//                skillIds,
//                fromDate,
//                toDate
//        );
//    }

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

 