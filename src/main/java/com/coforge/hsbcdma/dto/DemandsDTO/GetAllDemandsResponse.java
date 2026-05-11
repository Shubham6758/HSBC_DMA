package com.coforge.hsbcdma.dto.DemandsDTO;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllDemandsResponse {

    // BaseEntity id
    private Long id;

    private Boolean flag;

    // Demand Id / RR / file
//    @JsonIgnore
    private Long demandId;

    private String displayDemandId;

//    @JsonProperty("demandId")
//    public String getDemandIdWithLob() {
//        String lobVal = (lob != null && lob.getName() != null) ? lob.getName() : "";
//        return lobVal.isBlank() ? String.valueOf(demandId) : lobVal + "-" + demandId;
//    }

    private Long rrNumber;
    private String fileName;

    // Other scalar fields
    private String experience;
    private String remark;
    private LocalDate demandReceivedDate;


    private List<RefDTO> primarySkills;
    private List<RefDTO> secondarySkills;
    private List<RefDTO> demandLocations;

    private RefDTO hbu;
    private RefDTO hbuSpoc;

    private RefDTO band;
    private RefDTO priority;
    private RefDTO lob;
    private RefDTO demandType;
    private RefDTO demandTimeline;
    private RefDTO externalInternal;
    private RefDTO status;
    private RefDTO pod;

    private RefDTO pmo;
    private RefDTO pmoSpoc;
    private RefDTO salesSpoc;

    private RefDTO hiringManager;
    private RefDTO deliveryManager;

    private RefDTO skillCluster;
    private RefDTO projectManager;
    private Boolean karatFlag;
    private Boolean isSubcon;

    public void computeDisplayDemandId() {
        String lobVal = (lob != null && lob.getName() != null) ? lob.getName() : "";
        this.displayDemandId = lobVal.isBlank()
                ? String.valueOf(demandId)
                : lobVal + "-" + demandId;
    }


//    private List<ProfileTrackerSummaryDTO> profileShared;
//
//    @Data
//    @Builder
//    public static class ProfileTrackerSummaryDTO {
//        private Long id;
//        private Long profileId;
//        private String candidateName;
//        private LocalDate profileSharedDate;
//        private LocalDate attachedDate;
//        private Float experience;
//        private Set<PrimarySkills> primarySkills;
//        private Set<SecondarySkills> secondarySkills;
//        private RefDTO hbu;
//        private LocalDateTime createdAt;
//    }
}