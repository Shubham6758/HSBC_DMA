package com.coforge.hsbcdma.controller;

import com.coforge.hsbcdma.dto.DemandsDTO.PageResponse;
import com.coforge.hsbcdma.dto.ProfileTrackDTO.AttachDemandsToProfileRequest;
import com.coforge.hsbcdma.dto.ProfileTrackDTO.AttachProfilesToDemandRequest;
import com.coforge.hsbcdma.dto.ProfileTrackDTO.EditProfileTrackerRequest;
import com.coforge.hsbcdma.dto.ProfileTrackDTO.ProfileTrackerFilterRequest;
import com.coforge.hsbcdma.service.ProfileTrackerServices.ProfileTrackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile-track")
@RequiredArgsConstructor
public class ProfileTrackController extends BaseController{
    private final ProfileTrackService profileTrackService;

    @PostMapping("/attach/profiles-to-demand")
    public ResponseEntity<?> attachProfilesToDemand(@RequestBody AttachProfilesToDemandRequest request) {
        return success(profileTrackService.attachProfilesToOneDemand(request));
    }

    @PostMapping("/attach/demands-to-profile")
    public ResponseEntity<?> attachDemandsToProfile(@RequestBody AttachDemandsToProfileRequest request) {
        return success(profileTrackService.attachDemandsToOneProfile(request));
    }

    /**
     * edit profile tracker by id
     */
    @PutMapping("/{trackerId}/edit")
    public ResponseEntity<?> editProfileTracker(
            @PathVariable Long trackerId,
            @RequestBody EditProfileTrackerRequest request
    ) {
        return success(profileTrackService.editProfileTracker(trackerId, request));
    }

    /**
     * get profiles by demand Id
     */
    @GetMapping("/{demandId}/profiles")
    public ResponseEntity<?> getProfilesByDemandId(
            @PathVariable Long demandId
    ) {
        return success(profileTrackService.getProfilesByDemandId(demandId));
    }

    /**
     * get all profiles trackers
     */
    @GetMapping("/{profileId}/demands")
    public ResponseEntity<?> getDemandsByProfileId(
            @PathVariable Long profileId
    ) {
        return success(profileTrackService.getDemandsByProfileId(profileId));
    }


    /**
     * get all profile trackers with pagination
     */
    @GetMapping
    public PageResponse getAllProfiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return profileTrackService.getAllProfileTrackers(page, size);
    }

    @PostMapping("/search")
    public ResponseEntity<?> searchByNames(
            @RequestBody ProfileTrackerFilterRequest filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageResponse response = profileTrackService.search(filter, page, size);
       return success(response);
    }

    @GetMapping("/dropdowns")
    public ResponseEntity<?> getProfileTrackerDropdowns() {
        return success(profileTrackService.getDropdowns());
    }

}