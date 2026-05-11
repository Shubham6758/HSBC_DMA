package com.coforge.hsbcdma.export;

import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.entity.ProfileTracker;

import java.time.LocalDate;

public record OnboardingExportDTO(

        String demandId,
        String candidateName,
        String profileType,
        String hbu,
        String hiringManager,
        String pmoSpoc,
        String band,
        String wbsType,
        LocalDate offerDate,
        LocalDate doj,
        LocalDate profileSharedDate,
        Long ctoolId,
        String bgvStatus,
        LocalDate pevUploadDate,
        LocalDate vpTaggingDate,
        LocalDate techSelectDate,
        LocalDate hsbcOnboardDate,
        String onboardingStatus
) {
    public static OnboardingExportDTO from(Onboarding o) {
        ProfileTracker pt = o.getProfileTracker();
//        return new OnboardingExportDTO(
//                o.getId(),
//                o.getProfileTracker() != null ? o.getProfileTracker().getId() : null,
//                label(o.getWbsType()),
//                o.getOfferDate(),
//                o.getDateOfJoining(),
//                o.getCtoolId(),
//                label(o.getBgvStatus()),
//                o.getPevUploadDate(),
//                o.getVpTagging(),
//                o.getTechSelectDate(),
//                o.getHsbcOnboardingDate(),
//                label(o.getOnboardingStatus()),
//                o.getDemand() != null ? o.getDemand().getId() : null,
//                o.getDemand() != null ? o.getDemand().getDemandId() : null,
//                o.getProfile() != null ? o.getProfile().getId() : null
//        );
        String prefix = Helper.resolveDemandPrefix(o.getDemand());
        String formattedDemandId = Helper.formatDemandId(o.getDemand());

        return new OnboardingExportDTO(
//                Helper.formatDemandId(o.getDemand()),
                formattedDemandId,
                pt != null && pt.getProfile() != null ? pt.getProfile().getCandidateName() : "",
                pt != null && pt.getProfile() != null && pt.getProfile().getExternalInternal() != null
                        ? pt.getProfile().getExternalInternal().getName() : "",
                pt != null && pt.getDemand() != null && pt.getDemand().getHbu() != null
                        ? pt.getDemand().getHbu().getName() : "",
                pt != null && pt.getDemand() != null ? pt.getDemand().getHiringManager().getName() : "",
//                pt != null && pt.getDemand() != null ? pt.getDemand().getPmoSpoc() : "",
                pt != null && pt.getDemand() != null && pt.getDemand().getPmo() != null
                        ? pt.getDemand().getPmo().getName() : "",
                pt != null && pt.getDemand() != null && pt.getDemand().getBand() != null
                        ? pt.getDemand().getBand().getBand() : "",
                o.getWbsType() != null ? o.getWbsType().getName() : "",



                o.getOfferDate(),
                o.getDateOfJoining(),
                pt != null ? pt.getProfileSharedDate() : null,
                o.getCtoolId(),
                o.getBgvStatus() != null ? o.getBgvStatus().getName() : "",
                o.getPevUploadDate(),
                o.getVpTagging(),
                o.getTechSelectDate(),
                o.getHsbcOnboardingDate(),
                o.getOnboardingStatus() != null ? o.getOnboardingStatus().getName() : ""
        );

    }

    private static String label(Object o) {
        if (o == null) return null;
        try { Object v = o.getClass().getMethod("getName").invoke(o); return v == null ? null : v.toString(); }
        catch (Exception e) { return o.toString(); }
    }
}
