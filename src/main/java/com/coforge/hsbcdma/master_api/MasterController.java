package com.coforge.hsbcdma.master_api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/master")
@RequiredArgsConstructor
public class MasterController {

    private final MasterService masterService;

    @PostMapping("/save")
    public ResponseEntity<?> saveMasters(
            @RequestBody MasterRequest request) {

        return ResponseEntity.ok(masterService.saveMasters(request));
    }
}