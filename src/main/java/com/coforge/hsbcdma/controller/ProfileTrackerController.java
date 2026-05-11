package com.coforge.hsbcdma.controller;


import com.coforge.hsbcdma.dto.ProfileTrackerDTO;
import com.coforge.hsbcdma.service.ProfileTrackerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * ProfileController exposes Profile endpoints /profile_tracker and nested under demands.
 * Created-by : pratish.b
 */
@Validated
@RestController
@RequestMapping("/profile_tracker")
public class ProfileTrackerController extends BaseController{

    @Autowired
    private ProfileTrackerService profileService;

    private static final Logger logger = LoggerFactory.getLogger(ProfileTrackerController.class);


    /**
     * List profiles by demand.
     * Created by: pratish.b
     */
    @GetMapping("/profiles/{demandId}")
    public ResponseEntity<ProfileTrackerDTO> getProfileByDemandId(@PathVariable("demandId") String demandId) {
        return ResponseEntity.ok(profileService.getProfileByDemandId(demandId));
    }

    /**
     * Update profile against a demand.
     * Created by: pratish.b
     */
    @PostMapping("/updateProfile")
    public ResponseEntity<?> updateProfileTracker(@Valid @RequestBody ProfileTrackerDTO dto) {

        profileService.updateProfileTracker(dto);
        return success("Profile updated successfully for demandId : "+dto.getDemandId());
    }

    /**
     * Fetch demands based on Start and End date.
     * Created by: pratish.b
     */
    @GetMapping("/retrieveProfiles")
    public ResponseEntity<?> getProfilesOnStartAndEndDate(@RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "dd-MMM-yy") @NotNull(message = "Start Date is required") LocalDate startDate,
                                                          @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "dd-MMM-yy") @NotNull(message = "End Date is required") LocalDate endDate) {


        if (startDate.isAfter(endDate)) {
            return badRequest("Start Date must be on or before End Date");
        }

        logger.info("******* Start date : "+startDate.toString()+" and end date : "+endDate.toString());
        return success(profileService.retrieveProfilesOnStartAndENdDate(startDate, endDate));
    }

    /**
     * DM decision endpoint to accept/reject a profile.
     * Created by: Pooja.I
     */
   /* @PatchMapping("/profiles/{profileId}/decision")
    public ResponseEntity<ProfileTrackerDTO> decide(@PathVariable("profileId") Long profileId, @Valid @RequestBody DecisionDTO decision) {
        return ResponseEntity.ok(profileService.decide(profileId, decision));
    }*/
}
