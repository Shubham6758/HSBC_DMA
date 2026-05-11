package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandsDTO.*;
import com.coforge.hsbcdma.service.DemandService.AddDemandDraftService;
import com.coforge.hsbcdma.service.DemandService.ViewDemandDraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/draft")
public class DraftController {
    @Autowired
    private ViewDemandDraftService viewDemandDraftService;

    @Autowired
    private AddDemandDraftService addDemandDraftService;

    @GetMapping("viewdraft")
    public ResponseEntity<List<AddDemandDraftResponseDTO>> getAllDemandDrafts() {
        return ResponseEntity.ok(viewDemandDraftService.getAllDemandDrafts());
    }

    @GetMapping("/viewdraft/{id}")
    public ResponseEntity<AddDemandDraftResponseDTO> getDraftById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                viewDemandDraftService.getDraftById(id)
        );
    }

    @PostMapping(
            value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> create(
            @RequestPart("req") AddDemandDraftCreateRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Long draftId = addDemandDraftService.createDraftWithRrDrafts(request, files);

        return ResponseEntity.ok(Map.of(
                "message", "Draft and RR Draft rows created successfully",
                "draftId", draftId
        ));
    }

    @PutMapping(
            value = "/edit/{draftId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> editDraft(
            @PathVariable Long draftId,
            @RequestPart("req") DemandDraftEditRequest request,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        Long id = addDemandDraftService.editDraftWithRrDrafts(draftId, request, files);

        return ResponseEntity.ok(Map.of(
                "message", "Draft updated successfully",
                "draftId", id
        ));
    }


    @PostMapping(value="/create1",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createDraft(@RequestBody AddDemandDraftOnlyCreateRequest req) {
        Long draftId = addDemandDraftService.createDraftOnly(req);
        return ResponseEntity.ok(Map.of(
                "message", "Draft created successfully",
                "draftId", draftId
        ));
    }


    @PostMapping(value = "/bulk/{draftId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> saveBulk(
            @PathVariable Long draftId,
            @RequestPart("payload") AddDemandRrDraftBulkRequest payload,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        Long id = addDemandDraftService.saveRRDraftsForDraft(draftId, payload, files);
        return ResponseEntity.ok(Map.of("draftId", id, "message", "RR Draft rows saved"));
    }
}
