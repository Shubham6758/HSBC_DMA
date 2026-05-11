package com.coforge.hsbcdma.audit.History;

import com.coforge.hsbcdma.audit.AuditHistory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
public class HistoryItemDTO {
    private Long auditId;

    private AuditHistory.EntityType entityType; // ADD_DEMAND / PROFILE_TRACKER
    private Long entityId;                      // demand PK or profileTracker PK

    private Long demandId;
    private Long profileId;                     // for attach/update tracker
    private Long profileTrackerId;
    private Long onboardingId;

    private ProfileInfoDTO profileInfoDTO;

    private AuditHistory.Action action;         // UPDATE / ATTACH
    private changedUserDTO changedByUserId;
    private LocalDateTime changedAt;
    private String title;
    private Map<String, Object> diff;           // only when includeDiff=true

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class changedUserDTO{
        private String userId;
        private String username;
    }


    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ProfileInfoDTO {
        private Long profileId;
        private String candidateName;  // change field names based on your Profile entity
        private String emailId;
    }

}
