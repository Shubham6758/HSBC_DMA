package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.service.TalentDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/talent_dashboard")
public class TalentDashboardController extends BaseController {

    @Autowired
    private TalentDashboardService talentDashboardService;

    private static final Logger logger = LoggerFactory.getLogger(TalentDashboardController.class);

    @GetMapping("/overview")
    public ResponseEntity<?> overview(){

        return success(talentDashboardService.getTalentOverviewDetails());
    }
}