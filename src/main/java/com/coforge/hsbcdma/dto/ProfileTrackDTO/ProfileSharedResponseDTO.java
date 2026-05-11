package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class ProfileSharedResponseDTO {
    private Long id;
    private Long profileId;
    private String candidateName;
    private LocalDate profileSharedDate;
    private LocalDate attachedDate;
    private Float experience;
    private List<RefDTO> primarySkills;
    private List<RefDTO> secondarySkills;
    private RefDTO hbu;
    private RefDTO profileTrackerStatus;
    private RefDTO evaluationStatus;
    private LocalDateTime createdAt;
}
