package com.coforge.hsbcdma.resource.Import_Resource_Sheet;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource-data")
@RequiredArgsConstructor
public class ResourceSheetImportController {

    private final ResourceSheetImportService service;

    @PostMapping(value = "/bulk-upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResourceSheetImportService.ImportResult> bulkUpload(
            @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.importResources(file));
    }
}
