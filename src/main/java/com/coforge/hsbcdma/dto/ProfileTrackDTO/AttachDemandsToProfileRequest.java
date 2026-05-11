package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import lombok.Data;

import java.util.List;
@Data
public class AttachDemandsToProfileRequest {
    private Long profilePkId;
    private List<Long> demandIds;
}
