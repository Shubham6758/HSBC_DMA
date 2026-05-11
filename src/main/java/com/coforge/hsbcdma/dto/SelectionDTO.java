package com.coforge.hsbcdma.dto;

import jakarta.validation.constraints.NotNull;

/**
 * SelectionDTO used when PMO selects a candidate for a demand.
 */
public class SelectionDTO {
    @NotNull
    private Long profileId;
    private String notes;

    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
