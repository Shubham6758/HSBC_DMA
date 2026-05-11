package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class EditProfileTrackerRequest {
    // All optional; only provided fields will be updated
    private LocalDate profileSharedDate;
    private LocalDate interviewDate;
    private LocalDate decisionDate;
    private Long evaluationStatusId;
    private Long profileTrackerStatusId;
}
