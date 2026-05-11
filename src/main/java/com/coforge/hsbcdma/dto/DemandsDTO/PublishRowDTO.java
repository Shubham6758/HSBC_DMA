package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;

// Step 2 response item
@Data
@AllArgsConstructor
public class PublishRowDTO {
    private String tempId;
    private Long demandId;          // real generated demand_id (100+)
    private String displayDemandId; // "MSS-100"
    private Long rrNumber;
}
