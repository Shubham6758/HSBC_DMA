package com.coforge.hsbcdma.export;

import com.coforge.hsbcdma.entity.Profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public record ProfileExportDTO(

        Long id,
        String candidateName,
        String emailId,
        String empId,
        Long phoneNumber,
        Boolean isActive,
        Float experience,

        String skillCluster,
        String location,
        String hbu,
        String externalInternal,
        String country,
        String profileStatus,
        String origin,
        String karatStatus,
        String source,
        String overallStatusRdg,

        String summary,
        String fileName,
        String panNumber,
        String karatReadiness,
        String lobShared,
        String practice,
        String band,
        Integer ageing,
        String ageingRange,
        String codes,
        String codeType,
        Double minBillingRate,
        String projectCode,

        // Dates
        LocalDate dateOfSubmission,
        LocalDate weekOf,
        LocalDate accountReceivedOn,
        LocalDate statusDate,

        // External
        LocalDate l1InterviewDate,
        String currentLocation,
        String officialNP,
        LocalDate negotiableNpLwd,
        String recruiter,

        String primarySkills,
        String secondarySkills,

        LocalDateTime createdAt,
        String createdByUserId,
        LocalDateTime updatedAt,
        String updatedByUserId
) {
    public static ProfileExportDTO from(Profile p, String createdByUsername) {

        return new ProfileExportDTO(
                p.getId(),
                p.getCandidateName(),
                p.getEmailId(),
                p.getEmpId(),
                p.getPhoneNumber(),
                p.getIsActive(),
                p.getExperience(),

                // Masters
                label(p.getSkillCluster()),
                label(p.getLocation()),
                label(p.getHbu()),
                label(p.getExternalInternal()),
                label(p.getCountry()),
                label(p.getProfileStatus()),
                label(p.getOrigin()),
                label(p.getKaratStatus()),
                label(p.getSource()),
                label(p.getOverallStatusRdg()),

                // Normal fields
                p.getSummary(),
                p.getFileName(),
                p.getPanNumber(),
                p.getKaratReadiness(),
                p.getLobShared(),
                p.getPractice(),
                p.getBand(),
                p.getAgeing(),
                p.getAgeingRange(),
                p.getCodes(),
                p.getCodeType(),
                p.getMinBillingRate(),
                p.getProjectCode(),

                // Dates
                p.getDateOfSubmission(),
                p.getWeekOf(),
                p.getAccountReceivedOn(),
                p.getStatusDate(),

                // External
                p.getL1InterviewDate(),
                p.getCurrentLocation(),
                p.getOfficialNP(),
                p.getNegotiableNpLwd(),
                p.getRecruiter(),

                // Skills
                joinLabels(p.getPrimarySkills()),
                joinLabels(p.getSecondarySkills()),

                // Audit
                p.getCreatedAt(),
                createdByUsername,   // ✅ resolved name (important)
                p.getUpdatedAt(),
                p.getUpdatedByUserId()
        );
    }

    // ================= UTIL =================

    private static String label(Object o) {
        if (o == null) return null;
        try {
            Object v = o.getClass().getMethod("getName").invoke(o);
            return v == null ? null : v.toString();
        } catch (Exception e) {
            return o.toString();
        }
    }

    private static String joinLabels(Set<?> set) {
        if (set == null || set.isEmpty()) return null;

        return set.stream()
                .filter(Objects::nonNull)
                .map(ProfileExportDTO::label)
                .filter(Objects::nonNull)
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.joining(", "));
    }
}