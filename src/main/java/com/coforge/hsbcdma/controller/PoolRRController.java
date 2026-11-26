package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.PoolRRDTO.BulkUploadResponse;
import com.coforge.hsbcdma.dto.PoolRRDTO.PoolRRBulkRequest;
import com.coforge.hsbcdma.dto.PoolRRDTO.PoolRRCreateRequest;
import com.coforge.hsbcdma.dto.PoolRRDTO.PoolRRUpdateRequest;
import com.coforge.hsbcdma.service.PoolRRServices.PoolRRService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pool-rr")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PoolRRController {

    private final PoolRRService poolRRService;

    // ✅ CREATE
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody @Valid PoolRRCreateRequest request
    ) {

        poolRRService.create(request);

        return ResponseEntity.ok(
                Map.of("message", "RR created successfully")
        );
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody PoolRRUpdateRequest request
    ) {

        poolRRService.update(id, request);

        return ResponseEntity.ok(
                Map.of("message", "RR updated successfully")
        );
    }

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<?> getAll() {

        return ResponseEntity.ok(
                poolRRService.getAll()
        );
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                poolRRService.getById(id)
        );
    }

    // ✅ BULK UPLOAD API
    @PostMapping("/bulk")
    public ResponseEntity<BulkUploadResponse> bulkUpload(
            @RequestBody List<PoolRRBulkRequest> requests
    ) {

        BulkUploadResponse response =
                poolRRService.bulkUpload(requests);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadExcel(
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                poolRRService.uploadExcel(file)
        );
    }

}