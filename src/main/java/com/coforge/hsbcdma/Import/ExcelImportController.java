package com.coforge.hsbcdma.Import;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
public class ExcelImportController {

    private final ExcelImportService excelImportService;

    @PostMapping("/import/demands")
    public ResponseEntity<DemandImportResponse>
    importDemands(
            @RequestParam("file")
            MultipartFile file) {

        return ResponseEntity.ok(
                excelImportService.importDemands(file)
        );
    }
}

