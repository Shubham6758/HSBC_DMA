package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
@Builder
public class DemandSharedResponseDTO {
    private Long id;
    private String demand;
    private LocalDate attachedDate;
    private List<RefDTO> primarySkills;
    private List<RefDTO> secondarySkills;
    private RefDTO hbu;
    private RefDTO profileTrackerStatus;
    private RefDTO evaluationStatus;
    private RefDTO demandStatus;
    private LocalDateTime createdAt;
}
