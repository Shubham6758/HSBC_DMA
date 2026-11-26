package com.coforge.hsbcdma.revenuemargin.controller;

import com.coforge.hsbcdma.revenuemargin.dto.RefreshResponse;
import com.coforge.hsbcdma.revenuemargin.service.HsbcCentralResourceTrackerService;
import com.coforge.hsbcdma.revenuemargin.service.RefreshAuditService;
import com.coforge.hsbcdma.revenuemargin.service.VmDumpService;
import com.coforge.hsbcdma.revenuemargin.service.importer.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;

@Controller
public class SourceDataController {

    @Autowired
    private VmDumpImporter vmDumpImporter;

    @Autowired
    private VmDumpService vmDumpService;

    @Autowired
    private RdgMasterImporter rdgMasterImporter;

    @Autowired
    private HsbcArcImporter hsbcArcImporter;

    @Autowired
    private MonthlyTimesheetImporter monthlyTimesheetImporter;

    @Autowired
    private OtherDataImporter otherDataImporter;
    @Autowired
    private M4RateImporter m4RateImporter;
    @Autowired
    private HsbcCentralResourceTrackerImporter hsbcCentralResourceTrackerImporter;
    @Autowired
    private HsbcCentralResourceTrackerService hsbcCentralResourceTrackerService;
    @Autowired
    private RefreshAuditService refreshAuditService;

    @PostMapping("/rdg/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshRdg(@RequestParam("file") MultipartFile file) throws Exception {
//    public String refreshRdg(@RequestParam("file") MultipartFile file,Model model) throws Exception {
        //tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        File uploadedFile =
                new File(uploadedFilePath);

        uploadedFile.getParentFile().mkdirs();

        file.transferTo(uploadedFile);
        rdgMasterImporter.importData(
                uploadedFilePath,
                "RDG Master File"
        );
//tymeleafchanges
//        model.addAttribute("status", "Success");
//        model.addAttribute("rowsLoaded", "Loaded");
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "RDG Master",
                (int) rdgMasterImporter.getRowsRead(),
                (int) rdgMasterImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );

        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        rdgMasterImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);
//        return ResponseEntity.ok("RDG Master refresh completed in " + durationSeconds
//                        + " seconds");
//        return "generic-refresh";
        //tymeleafchanges
    }
    @PostMapping("/arc/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshArc(@RequestParam("file") MultipartFile file) throws Exception {
//    public String refreshArc(@RequestParam("file") MultipartFile file,Model model) throws Exception {
//tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        File uploadedFile =
                new File(uploadedFilePath);

        uploadedFile.getParentFile().mkdirs();

        file.transferTo(uploadedFile);
        hsbcArcImporter.importData(
                uploadedFilePath,
                "HSBC-ARC"
        );
//tymeleafchanges
//        model.addAttribute("status", "Success");
//        model.addAttribute("rowsLoaded", "Loaded");
//tymeleafchanges
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "ARC",
                (int) hsbcArcImporter.getRowsRead(),
                (int) hsbcArcImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );

        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        hsbcArcImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);
        //tymeleafchanges
//        return ResponseEntity.ok("ARC refresh completed in " + durationSeconds +" seconds");
//        return "generic-refresh";
        //tymeleafchanges
    }

    @PostMapping("/timesheet/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshTimesheet(@RequestParam("file") MultipartFile file) throws Exception {
//    public String refreshTimesheet(@RequestParam("file") MultipartFile file,Model model) throws Exception {
        //tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        File uploadedFile =
                new File(uploadedFilePath);

        uploadedFile.getParentFile().mkdirs();

        file.transferTo(uploadedFile);
        monthlyTimesheetImporter.importData(
                uploadedFilePath,
                "Monthly Timesheet Dump"
        );
//tymeleafchanges
//        model.addAttribute("status", "Success");
//        model.addAttribute("rowsLoaded", "Loaded");
        //tymeleafchanges
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "Monthly Timesheet",
                (int) monthlyTimesheetImporter.getRowsRead(),
                (int) monthlyTimesheetImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );
        //tymeleafchanges

        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        monthlyTimesheetImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);
//        return ResponseEntity.ok("Monthly Timesheet refresh completed in " + durationSeconds + " seconds");
//        return "generic-refresh";
        //tymeleafchanges
    }

    @PostMapping("/other/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshOther(@RequestParam("file") MultipartFile file)
            throws Exception {
//    public String refreshOther(@RequestParam("file") MultipartFile file,Model model) throws Exception {
        //tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        File uploadedFile =
                new File(uploadedFilePath);

        uploadedFile.getParentFile().mkdirs();

        file.transferTo(uploadedFile);
        otherDataImporter.importData(
                uploadedFilePath,
                "Other Data"
        );
//tymeleafchanges
//        model.addAttribute("status", "Success");
//        model.addAttribute("rowsLoaded", "Loaded");
        //tymeleafchanges
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "Other Data",
                (int) otherDataImporter.getRowsRead(),
                (int) otherDataImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );
        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        otherDataImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);

        //tymeleafchanges
//        return ResponseEntity.ok("Other Data refresh completed in " + durationSeconds + " seconds");
//        return "generic-refresh";
        //tymeleafchanges
    }
    @PostMapping("/m4/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshM4(@RequestParam("file") MultipartFile file) throws Exception {
//    public String refreshM4(@RequestParam("file") MultipartFile file,Model model) throws Exception {
        //tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        File uploadedFile =
                new File(uploadedFilePath);

        uploadedFile.getParentFile().mkdirs();

        file.transferTo(uploadedFile);
        m4RateImporter.importData(
                uploadedFilePath,
                "M4 Rate Sheet"
        );
        //tymeleafchanges
//        model.addAttribute("status", "Success");
//        model.addAttribute("rowsLoaded", "Loaded");
        //tymeleafchanges
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "M4 Rate",
                (int) m4RateImporter.getRowsRead(),
                (int) m4RateImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );

        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        m4RateImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);
        //tymeleafchanges
//        return ResponseEntity.ok("M4 Rate refresh completed in " + durationSeconds + " seconds");
//        return "generic-refresh";
        //tymeleafchanges
    }
    @PostMapping("/vm/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshVm(@RequestParam("file") MultipartFile file) throws Exception {
//    public String refreshVm(@RequestParam("file") MultipartFile file,Model model) throws Exception {
        //tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();
        File uploadedFile =
                new File(uploadedFilePath);
        uploadedFile.getParentFile().mkdirs();
        file.transferTo(uploadedFile);
//        model.addAttribute(
//                "status",
//                "Processing..."
//        );

        vmDumpImporter.importData(uploadedFilePath,"VM Dump");
        //tymeleafchanges
//        model.addAttribute("rowsLoaded",vmDumpService.getRecordCount());
//        model.addAttribute("status","Success");
        //tymeleafchanges
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "VM Dump",
                (int) vmDumpImporter.getRowsRead(),
                (int) vmDumpImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );
        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        vmDumpImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);
        //tymeleafchanges
//        return ResponseEntity.ok("Vendor Management Dump refresh completed in " + durationSeconds + " seconds");
//                return "vm-refresh";
        //tymeleafchanges
    }
    @PostMapping("/resource-tracker/refresh")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<RefreshResponse> refreshResourceTracker(@RequestParam("file") MultipartFile file) throws Exception {
//    public String refreshResourceTracker(@RequestParam("file") MultipartFile file,Model model) throws Exception {
            //tymeleafchanges
        long startTime =
                System.currentTimeMillis();
        long endTime;
        long durationSeconds;
        String uploadedFilePath =
                "/data/temp/" + System.currentTimeMillis()
                        + "_" + file.getOriginalFilename();

        File uploadedFile =
                new File(uploadedFilePath);

        uploadedFile.getParentFile().mkdirs();

        file.transferTo(uploadedFile);
//        model.addAttribute(
//                "status",
//                "Processing..."
//        );

        hsbcCentralResourceTrackerImporter.importData(
                uploadedFilePath,
                "ResourceSheet"
        );

//        model.addAttribute("rowsLoaded",hsbcCentralResourceTrackerService.getRecordCount());
//        model.addAttribute("status","Success");
        endTime = System.currentTimeMillis();
        durationSeconds = (endTime - startTime) / 1000;
        refreshAuditService.saveRefreshAudit(
                "Central Resource Tracker",
                (int) hsbcCentralResourceTrackerImporter.getRowsRead(),
                (int) hsbcCentralResourceTrackerImporter.getRowsInserted(),
                durationSeconds,
                "SUCCESS",
                "Refresh Completed"
        );
        RefreshResponse response =
                new RefreshResponse(
                        LocalDateTime.now().toString(),
                        durationSeconds,
                        vmDumpImporter.getRowsInserted()
                );

        return ResponseEntity.ok(response);
//        return ResponseEntity.ok("Central Resource Tracker refresh completed in " + durationSeconds
//                        + " seconds");
//        return "generic-refresh";
    }
}