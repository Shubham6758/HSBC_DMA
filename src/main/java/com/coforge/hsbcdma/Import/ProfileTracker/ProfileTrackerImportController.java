package com.coforge.hsbcdma.Import.ProfileTracker;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ProfileTrackerImportController {

    private final ProfileTrackerImportService service;

    @PostMapping(
            value = "/profile-trackers",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ProfileTrackerImportService.ImportResult> importProfileTrackers(
            @RequestPart("file") MultipartFile file) {

        return ResponseEntity.ok(service.importTrackers(file));
    }
}
