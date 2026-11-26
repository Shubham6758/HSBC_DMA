package com.coforge.hsbcdma.dto.ProfilesDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DraftProfileResponse {

    private Long id;
    private String candidateName;
    private String emailId;
    private String empId;
    private Boolean isDraft;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}