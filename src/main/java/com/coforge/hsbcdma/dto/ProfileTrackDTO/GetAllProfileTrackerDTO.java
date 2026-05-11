package com.coforge.hsbcdma.dto.ProfileTrackDTO;

import com.coforge.hsbcdma.dto.DemandsDTO.RefDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class GetAllProfileTrackerDTO {

    private Long id;

    private LocalDateTime createdAt;
    private String createdByUserId;

    private LocalDateTime updatedAt;
    private String updatedByUserId;

    private LocalDate profileSharedDate;
    private LocalDate interviewDate;
    private LocalDate attachedDate;
    private LocalDate decisionDate;

    private RefDTO evaluationStatus;

    private RefDTO profileTrackerStatus;

    private DemandMiniDTO demand;
    private ProfileMiniDTO profile;



    @Data
    public static class DemandMiniDTO {
        private Long id;

        private Boolean flag;

        private Long demandId;
        private String displayDemandId;

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
    }


    @Data
    public static class ProfileMiniDTO {
        private Long id;

        private String candidateName;
        private String emailId;
        private String empId;
        private Long phoneNumber;
        private Boolean isActive;
        private Float experience;

        private RefDTO skillCluster;
        private RefDTO location;
        private RefDTO hbu;
        private RefDTO externalInternal;

        private String summary;
        private String fileName;

        private List<RefDTO> primarySkills;
        private List<RefDTO> secondarySkills;

        private String createdByUserId;
        //    private String createdByName;
        private String updatedByUserId;
//    private String updatedByName;

        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }


}
