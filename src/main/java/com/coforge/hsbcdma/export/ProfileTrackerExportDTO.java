package com.coforge.hsbcdma.export;

import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.entity.Location;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.dropdownEntities.PrimarySkills;
import com.coforge.hsbcdma.entity.dropdownEntities.SecondarySkills;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record ProfileTrackerExportDTO(

        String demandId,
        String candidateName,
        String priority,
        String skillCluster,
        Set<PrimarySkills> primarySkills,
        Set<SecondarySkills>secondarySkills,
        String lob,
        String hbu,
        Set<Location> location,
        String externalInternal,
        String hiringManager,
        LocalDate attachedDate,
        LocalDate profileSharedDate,
        LocalDate interviewDate,
        LocalDate decisionDate,
        String profileTrackerStatus,


        LocalDateTime createdAt,
        String createdByUserId
) {
    public static ProfileTrackerExportDTO from(ProfileTracker pt,String name) {
//        return new ProfileTrackerExportDTO(
//                formatDemandId(pt.getDemand()),
//                pt.getProfile() != null ? pt.getProfile().getId() : null,
//                label(pt.getProfileTrackerStatus()),
//                pt.getProfileSharedDate(),
//                pt.getInterviewDate(),
//                pt.getDecisionDate(),
//                pt.getAttachedDate(),
//                pt.getCreatedAt(),
//                name
//        );


        var demand = pt.getDemand();
        var profile = pt.getProfile();
        String prefix = Helper.resolveDemandPrefix(pt.getDemand());
        String formattedDemandId = Helper.formatDemandId(pt.getDemand());
        return new ProfileTrackerExportDTO(
                formattedDemandId,

                // Candidate Name from Profile
                profile != null ? profile.getCandidateName() : null,

                // From Demand
                demand != null && demand.getPriority() != null ? demand.getPriority().getName() : null,
                demand != null && demand.getSkillCluster() != null ? demand.getSkillCluster().getName() : null,
                demand != null ? demand.getPrimarySkills() : null,
                demand != null ? demand.getSecondarySkills() : null,
                demand != null && demand.getLob() != null ? demand.getLob().getLob() : null,
                demand != null ? demand.getHbu().getName() : null,
                demand != null ? demand.getDemandLocations(): null,
                profile != null ? profile.getExternalInternal().getExternalInternal() : null,
                demand != null ? demand.getHiringManager().getHiringManager() : null,

                // From ProfileTracker
                pt.getAttachedDate(),
                pt.getProfileSharedDate(),
                pt.getInterviewDate(),
                pt.getDecisionDate(),
                pt.getProfileTrackerStatus() != null ? pt.getProfileTrackerStatus().getName() : null,
//                pt.getAging()
                pt.getCreatedAt(),
                name
        );

    }

    private static String formatDemandId(AddDemand demand) {
        if (demand == null) {
            return null;
        }

        String lobName = null;

        // If your field is getLob() then replace getLobId() with getLob()
        if (demand.getLob() != null) {
            lobName = demand.getLob().getName();
        }

        Long demandId = demand.getDemandId();

        if (lobName != null && demandId != null) {
            return lobName + "-" + demandId;
        }
        if (demandId != null) {
            return String.valueOf(demandId);
        }
        return lobName;
    }


    private static String label(Object o) {
        if (o == null) return null;
        try { Object v = o.getClass().getMethod("getName").invoke(o); return v == null ? null : v.toString(); }
        catch (Exception e) { return o.toString(); }
    }
}
