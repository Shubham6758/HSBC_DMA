package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import lombok.Data;

import java.util.List;
@Data
public class AttachProfilesToDemandRequest {
    private Long demandPkId;
    private List<Long> profileIds;
}
