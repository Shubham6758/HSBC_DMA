package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileCreateRequestDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileDraftRequestDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileUpdateRequestDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfilesFilterRequest;
import com.coforge.hsbcdma.dto.UserMananagementDTO.DecryptPanRequest;
import com.coforge.hsbcdma.service.ProfileServices.ProfileDropdownService;
import com.coforge.hsbcdma.service.ProfileServices.ProfileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/profiles")
public class ProfileController extends BaseController {

    private final ProfileService profileService;
    private final ProfileDropdownService dropdownService;

    public ProfileController(ProfileService profileService, ProfileDropdownService dropdownService) {
        this.profileService = profileService;
        this.dropdownService = dropdownService;
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProfile(
            @RequestPart(value = "payload", required = true) ProfileCreateRequestDTO payload,

            @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
            @RequestPart(value = "techIrsFile", required = false) MultipartFile techIrsFile

    ) throws Exception {

//        ProfileCreateRequestDTO dto = new com.fasterxml.jackson.databind.ObjectMapper()
//                .readValue(payload, ProfileCreateRequestDTO.class);

//        ProfileCreateRequestDTO dto = new com.fasterxml.jackson.databind.ObjectMapper()
//                .readValue(payload, ProfileCreateRequestDTO.class);
        System.out.println("Profile Created");
        System.out.println("Profile created again?");
        profileService.createProfile(payload, cvFile, techIrsFile);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Profile created successfully"));

    }


    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestPart(value = "payload", required = false) ProfileUpdateRequestDTO dto,
            @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
            @RequestPart(value = "techIrsFile", required = false) MultipartFile techIrsFile

    ) {
        log.info("***update hit");
        profileService.updateProfile(id, dto, cvFile, techIrsFile);
        log.info("**update done**");
        return ResponseEntity.ok(Map.of("message", "Profile updated successfully"));
    }

    @PostMapping("/search")
    public ResponseEntity<PageResponse> searchProfiles(
            @RequestBody ProfilesFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) throws BadRequestException {
        return ResponseEntity.ok(profileService.searchProfiles(filter, page, size));
    }


    @GetMapping("/dropdowns")
    public ResponseEntity<?> getDropdowns() {
        return success(dropdownService.getProfileDropdowns());
    }

    @GetMapping
    public PageResponse getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long hbuId,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        return profileService.getAllProfiles(page, size, hbuId, sortBy, sortDir);
    }

    @PostMapping(value = "/bulk-upload/ta", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUploadTAProfiles(
            @RequestPart("file") MultipartFile excelFile
    ) {
        return ResponseEntity.ok(profileService.bulkUploadTAProfiles(excelFile));
    }

    @PostMapping(value = "/bulk-upload/rdg", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> bulkUploadRDGProfiles(
            @RequestPart("file") MultipartFile excelFile
    ) {
        return ResponseEntity.ok(profileService.bulkUploadRDGProfiles(excelFile));
    }




    @PostMapping("/decrypt-pan/{profileId}")
    public ResponseEntity<String> decryptPan(@PathVariable Long profileId,@RequestBody DecryptPanRequest request) {

        String pan = profileService.decryptPan(profileId,request.getPassword());

        return ResponseEntity.ok(pan);
    }


    @GetMapping("/pending-with-hbu-count")
    public long getPendingWithHbuCount(
            @RequestParam(required = false) Long hbuId
    ) {
        return profileService.getPendingWithHbuCount(hbuId);
    }

    @PutMapping("/action/{id}")
    public ResponseEntity<?> takeAction(
            @PathVariable Long id,
            @RequestParam(required = false) String action // ACCEPTED / REJECTED
    ) {
        profileService.takeAction(id, action);
        return ResponseEntity.ok("Action applied");
    }


    @PostMapping("/admin/migrate-pan")
    public String migratePan() throws Exception {

        profileService.migrateExistingPanData();

        return "Migration completed";
    }


    // Save draft
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> saveDraft(
            @RequestPart("payload") ProfileDraftRequestDTO dto,
            @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
            @RequestPart(value = "techIrsFile", required = false) MultipartFile techIrsFile
    ) {

        Long draftId = profileService.saveDraft(dto, cvFile, techIrsFile);

        return ResponseEntity.ok(
                Map.of(
                        "message", "Draft saved successfully",
                        "draftId", draftId
                )
        );
    }

    // Update draft
    @PutMapping(value = "/draft/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateDraft(
            @PathVariable Long id,
            @RequestPart("payload") ProfileDraftRequestDTO dto,
            @RequestPart(value = "cvFile", required = false) MultipartFile cvFile,
            @RequestPart(value = "techIrsFile", required = false) MultipartFile techIrsFile
    ) {

        profileService.updateDraft(id, dto, cvFile, techIrsFile);

        return ResponseEntity.ok(
                Map.of("message", "Draft updated successfully")
        );
    }

    // Submit draft
    @PostMapping(
            value = "/draft/{id}/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> submitDraft(

            @PathVariable Long id,

            @RequestPart("payload")
            ProfileDraftRequestDTO dto,

            @RequestPart(value = "cvFile", required = false)
            MultipartFile cvFile,

            @RequestPart(value = "techIrsFile", required = false)
            MultipartFile techIrsFile
    ) {

        profileService.submitDraft(
                id,
                dto,
                cvFile,
                techIrsFile
        );

        return ResponseEntity.ok(
                Map.of("message", "Draft submitted successfully")
        );
    }

    // Get drafts
    @GetMapping("/drafts")
    public ResponseEntity<?> getDrafts() {

        return ResponseEntity.ok(
                profileService.getDrafts()
        );
    }

    @GetMapping("/draft/{id}")
    public ResponseEntity<?> getDraft(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                profileService.getDraft(id)
        );
    }

}
