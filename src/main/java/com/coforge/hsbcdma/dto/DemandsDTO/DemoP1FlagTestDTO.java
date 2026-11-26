package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo DTO for testing P1 Flag Date logic transitions
 * Used to demonstrate priority changes and their impact on P1FlagDate
 */
@Data
@NoArgsConstructor
public class DemoP1FlagTestDTO {
    
    private String oldPriority;  // P0, P1, P2, or null for creation
    private String newPriority;  // P0, P1, or P2
    
    public DemoP1FlagTestDTO(String oldPriority, String newPriority) {
        this.oldPriority = oldPriority;
        this.newPriority = newPriority;
    }
}
