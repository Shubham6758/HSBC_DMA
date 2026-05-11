package com.coforge.hsbcdma.Import.Onboarding;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class OnboardingImportController {

    private final OnboardingImportService service;

    @PostMapping(
            value = "/onboarding",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<OnboardingImportService.ImportResult> importOnboarding(
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(service.importExcel(file));
    }
}