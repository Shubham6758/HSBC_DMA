package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Demo Response DTO for P1 Flag Date logic test
 * Shows the expected behavior for different priority transitions
 */
@Data
@NoArgsConstructor
public class DemoP1FlagResponseDTO {
    
    private String oldPriority;
    private String newPriority;
    private String transition;                  // Description of the priority transition
    private String expectedFlagBehavior;        // What should happen to P1FlagDate
    private String scenario;                    // Business context
    private boolean success;
    private String message;
}
