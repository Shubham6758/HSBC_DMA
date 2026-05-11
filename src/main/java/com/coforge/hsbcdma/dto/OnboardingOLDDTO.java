package com.coforge.hsbcdma.dto;

/**
 * OnboardingDTO used to return onboarding details.
 */
public class OnboardingOLDDTO {
    private Long id;
    private Long demandId;
    private Long profileId;
    private String selectedAt;
    private String notes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getDemandId() { return demandId; }
    public void setDemandId(Long demandId) { this.demandId = demandId; }
    public Long getProfileId() { return profileId; }
    public void setProfileId(Long profileId) { this.profileId = profileId; }
    public String getSelectedAt() { return selectedAt; }
    public void setSelectedAt(String selectedAt) { this.selectedAt = selectedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
