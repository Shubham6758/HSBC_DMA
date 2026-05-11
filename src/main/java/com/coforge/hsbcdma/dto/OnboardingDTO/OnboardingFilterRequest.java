package com.coforge.hsbcdma.dto.OnboardingDTO;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class OnboardingFilterRequest {

    // -------- From Profile --------
    private String candidateName;          // LIKE (profiles.candidate_name)
    private String empId;                  // LIKE (profiles.emp_id)
    private String externalInternal;       // LIKE (external_internal.external_internal)

    // -------- From AddDemand --------
    private Long demandId;                 // equals (add_demands.demand_id - numeric business id)
    private String lob;                    // LIKE (lob.lob)
    private String band;                   // LIKE (band.band)
    private String hiringManager;          // LIKE (hiring_manager.<see note>)
    private String hbu;                    // LIKE (hbu.hbu)
    private String pmoSpoc;                // LIKE (pmo_spoc.pmo_spoc)

    // -------- From ProfileTracker --------
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate profileSharedDate;   // equals
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate profileSharedDateFrom; // optional range
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate profileSharedDateTo;   // optional range

    // -------- From Onboarding (self) --------
    private String onboardingStatus;       // LIKE (onboarding_status.onboarding_status)
    private String bgvStatus;              // LIKE (bgv_status.bgv_status)
    private String wbsType;                // LIKE (wbs_type.wbs_type)

    private Long ctoolId;                  // equals

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate offerDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoining;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoiningFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfJoiningTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate pevUploadDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate pevUploadDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate pevUploadDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate vpTagging;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate vpTaggingFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate vpTaggingTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate techSelectDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate techSelectDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate techSelectDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hsbcOnboardingDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hsbcOnboardingDateFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hsbcOnboardingDateTo;
}