package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileCreateRequestDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfileUpdateRequestDTO;
import com.coforge.hsbcdma.dto.ProfilesDTO.ProfilesFilterRequest;
import com.coforge.hsbcdma.dto.UserMananagementDTO.DecryptPanRequest;
import com.coforge.hsbcdma.service.ProfileServices.ProfileDropdownService;
import com.coforge.hsbcdma.service.ProfileServices.ProfileService;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

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
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws Exception {

//        ProfileCreateRequestDTO dto = new com.fasterxml.jackson.databind.ObjectMapper()
//                .readValue(payload, ProfileCreateRequestDTO.class);

//        ProfileCreateRequestDTO dto = new com.fasterxml.jackson.databind.ObjectMapper()
//                .readValue(payload, ProfileCreateRequestDTO.class);
        System.out.println("Profile Created");
        System.out.println("Profile created again?");
        profileService.createProfile(payload, file);


        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Profile created successfully"));

    }


    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @PathVariable Long id,
            @RequestPart("payload") ProfileUpdateRequestDTO dto,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {

        profileService.updateProfile(id, dto, file);

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
            @RequestParam String action // ACCEPTED / REJECTED
    ) {
        profileService.takeAction(id, action);
        return ResponseEntity.ok("Action applied");
    }
}
