package com.coforge.hsbcdma.revenuemargin.controller;

import com.coforge.hsbcdma.revenuemargin.dto.RefreshResponse;
import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import com.coforge.hsbcdma.revenuemargin.service.RefreshAuditService;
import com.coforge.hsbcdma.revenuemargin.service.RevenueMarginTmBuildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
//tymeleafimports


@Controller
public class RevenueMarginController {
    @Autowired
    private RevenueMarginTmBuildService revenueMarginTmBuildService;
    @Autowired
    private RevenueMarginTmRepository
            revenueMarginTmRepository;
    @Autowired
    private RefreshAuditService refreshAuditService;
    @GetMapping("/tm/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshTm(Model model) {
    //tymeleafchanges
//    public String refreshTm(Model model) {
        //tymeleafchanges
        long startTime = System.currentTimeMillis();
        revenueMarginTmRepository.addNewResources();
        revenueMarginTmBuildService.enrichData();
        long endTime = System.currentTimeMillis();
        long durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "T&M Revenue",
                0,
                0,
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );
        //tymeleafchanges
//        model.addAttribute("status","Success");
//        model.addAttribute("durationSeconds",durationSeconds);
//        model.addAttribute("rowsLoaded","Completed in "+ durationSeconds
//                        + " seconds");
//        return "generic-refresh";
        //tymeleafchanges
        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        0
                );

        return ResponseEntity.ok(response);
//        return ResponseEntity.ok("{\"status\":\"SUCCESS\",\"durationSeconds\":"
//                        + durationSeconds + "}");
        //tymeleafchanges
    }
}
