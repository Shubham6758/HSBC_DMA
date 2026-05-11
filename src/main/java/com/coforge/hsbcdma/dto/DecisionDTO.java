package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.entity.enums.ProfileStatus;
import jakarta.validation.constraints.NotNull;

/**
 * DecisionDTO used to take DM decision for a profile.
 */
public class DecisionDTO {
    @NotNull
    private ProfileStatus decision; // ACCEPTED or REJECTED

    public ProfileStatus getDecision() { return decision; }
    public void setDecision(ProfileStatus decision) { this.decision = decision; }
}
