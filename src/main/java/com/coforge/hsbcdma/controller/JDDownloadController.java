package com.coforge.hsbcdma.controller;


import com.coforge.hsbcdma.service.DemandService.JDDownloadService;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jd")
public class JDDownloadController {

    private final JDDownloadService service;

    public JDDownloadController(JDDownloadService service) {
        this.service = service;
    }

    @GetMapping("/draft/download/{fileName}")
    public ResponseEntity<Resource> downloadDraft(@PathVariable String fileName) {
        return service.downloadDraftJDByFileName(fileName);
    }

    @GetMapping("/demand/download/{fileName}")
    public ResponseEntity<Resource> downloadDemand(@PathVariable String fileName) {
        return service.downloadDemandJDByFileName(fileName);
    }


    @GetMapping("/profile/download/{fileName}")
    public ResponseEntity<Resource> downloadProfile(@PathVariable String fileName) {
        return service.downloadProfileByFileName(fileName);
    }
}

