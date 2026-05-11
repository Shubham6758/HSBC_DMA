package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.export.ExcelExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/exports")
public class ExportDownloadController {

    private final ExcelExportService svc;


    private static final DateTimeFormatter FILE_TS_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd_HHmm");

    private String getFileNameWithTimestamp(String fileName) {
        String timestamp = LocalDateTime.now().format(FILE_TS_FORMAT);
        return fileName + timestamp + ".xlsx";
    }


    @GetMapping("/demands/excel")
    public ResponseEntity<InputStreamResource> downloadDemands() {
        ByteArrayInputStream in = svc.exportDemandsExcel();
        return okExcel(in, getFileNameWithTimestamp("Demands"));
    }

    @GetMapping("/profiles/excel")
    public ResponseEntity<InputStreamResource> downloadProfiles() {
        ByteArrayInputStream in = svc.exportProfilesExcel();
        return okExcel(in, getFileNameWithTimestamp("Profiles"));
    }

    @GetMapping("/profile-trackers/excel")
    public ResponseEntity<InputStreamResource> downloadProfileTrackers() {
        ByteArrayInputStream in = svc.exportProfileTrackersExcel();
        return okExcel(in, getFileNameWithTimestamp("ProfileTrackers"));
    }

    @GetMapping("/onboarding/excel")
    public ResponseEntity<InputStreamResource> downloadOnboarding() {
        ByteArrayInputStream in = svc.exportOnboardingExcel();
        return okExcel(in, getFileNameWithTimestamp("Onboarding"));
    }

    @GetMapping("/all")
    public ResponseEntity<InputStreamResource> exportAll() throws Exception {

        ByteArrayInputStream in = svc.exportAllInOneExcel();

        return okExcel(in, getFileNameWithTimestamp("Hsbc_Dma_Report"));
    }



    // helper
    private ResponseEntity<InputStreamResource> okExcel(ByteArrayInputStream in, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
