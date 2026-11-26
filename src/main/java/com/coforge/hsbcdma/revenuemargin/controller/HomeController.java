package com.coforge.hsbcdma.revenuemargin.controller;

import com.coforge.hsbcdma.revenuemargin.dto.AuditDTO;
import com.coforge.hsbcdma.revenuemargin.service.RefreshAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private RefreshAuditService refreshAuditService;

//    @GetMapping("/")
//    public String home(Model model) {
//
//        model.addAttribute(
//                "vmAudit",
//                refreshAuditService.getVmDumpRefresh()
//        );
//
//        model.addAttribute(
//                "arcAudit",
//                refreshAuditService.getArcRefresh()
//        );
//
//        model.addAttribute(
//                "rdgAudit",
//                refreshAuditService.getRdgRefresh()
//        );
//
//        model.addAttribute(
//                "m4Audit",
//                refreshAuditService.getM4Refresh()
//        );
//
//        model.addAttribute(
//                "timesheetAudit",
//                refreshAuditService.getTimesheetRefresh()
//        );
//
//        model.addAttribute(
//                "otherDataAudit",
//                refreshAuditService.getOtherDataRefresh()
//        );
//
//        model.addAttribute(
//                "resourceTrackerAudit",
//                refreshAuditService.getCentralResourceTrackerRefresh()
//        );
//
//        model.addAttribute(
//                "tmAudit",
//                refreshAuditService.getTmRefresh()
//        );
//
//        return "index";
//    }

    @GetMapping("/dashboard-audit")
    public AuditDTO getDashboardAudit() {

        return AuditDTO.builder()
                .vmAudit(refreshAuditService.getVmDumpRefresh())
                .arcAudit(refreshAuditService.getArcRefresh())
                .rdgAudit(refreshAuditService.getRdgRefresh())
                .m4Audit(refreshAuditService.getM4Refresh())
                .timesheetAudit(refreshAuditService.getTimesheetRefresh())
                .otherDataAudit(refreshAuditService.getOtherDataRefresh())
                .resourceTrackerAudit(refreshAuditService.getCentralResourceTrackerRefresh())
                .tmAudit(refreshAuditService.getTmRefresh())
                .build();
    }
}