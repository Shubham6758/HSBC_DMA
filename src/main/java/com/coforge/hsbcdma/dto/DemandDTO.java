package com.coforge.hsbcdma.dto;

import com.coforge.hsbcdma.entity.enums.DemandStatus;
import com.coforge.hsbcdma.entity.enums.Externality;
import jakarta.validation.constraints.NotBlank;

/**
 * DemandDTO used to transfer Demand data.
 */
public class DemandDTO {
    private Long id;
    @NotBlank
    private String demandBusinessId;
    private String rr;
    private String lob;
    private String hiringManager;
    private String skillCluster;
    private String primarySkill;
    private String secondarySkill;
    private Externality externality;
    private DemandStatus status;
    private String dateProfileShared; // ISO string

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDemandBusinessId() { return demandBusinessId; }
    public void setDemandBusinessId(String demandBusinessId) { this.demandBusinessId = demandBusinessId; }
    public String getRr() { return rr; }
    public void setRr(String rr) { this.rr = rr; }
    public String getLob() { return lob; }
    public void setLob(String lob) { this.lob = lob; }
    public String getHiringManager() { return hiringManager; }
    public void setHiringManager(String hiringManager) { this.hiringManager = hiringManager; }
    public String getSkillCluster() { return skillCluster; }
    public void setSkillCluster(String skillCluster) { this.skillCluster = skillCluster; }
    public String getPrimarySkill() { return primarySkill; }
    public void setPrimarySkill(String primarySkill) { this.primarySkill = primarySkill; }
    public String getSecondarySkill() { return secondarySkill; }
    public void setSecondarySkill(String secondarySkill) { this.secondarySkill = secondarySkill; }
    public Externality getExternality() { return externality; }
    public void setExternality(Externality externality) { this.externality = externality; }
    public DemandStatus getStatus() { return status; }
    public void setStatus(DemandStatus status) { this.status = status; }
    public String getDateProfileShared() { return dateProfileShared; }
    public void setDateProfileShared(String dateProfileShared) { this.dateProfileShared = dateProfileShared; }
}
