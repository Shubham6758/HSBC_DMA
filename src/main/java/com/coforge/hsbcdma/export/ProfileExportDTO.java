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
//        String emailId,
        String empId,
//        Long phoneNumber,
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
        String negotiableNpLwd,
        String recruiter,

        String primarySkills,
        String secondarySkills,
//      karat fields
        String persona,
        String mentorBuddy,
        String prepStatus,
        LocalDate startDate,
        LocalDate plannedEndDate,
        String coderpadResult1,
        String coderpadResult2,
        String karatReadyStatus,
        LocalDate karatTestDate,
        String karatTestResult,
        Integer karatAgeing,

        LocalDateTime createdAt,
        String createdByUserId,
        LocalDateTime updatedAt,
        String updatedByUserId
) {
    public static ProfileExportDTO from(Profile p, String createdByUsername) {

        return new ProfileExportDTO(
                p.getId(),
                p.getCandidateName(),
//                p.getEmailId(),
                p.getEmpId(),
//                p.getPhoneNumber(),
                p.getIsActive(),
                p.getExperience(),

                // Masters
                p.getSkillCluster() != null ? label(p.getSkillCluster()) : "",
                p.getLocation() != null ? label(p.getLocation()) : "",
                p.getHbu() != null ? label(p.getHbu()) : "",
                p.getExternalInternal() != null ? label(p.getExternalInternal()) : "",
                p.getCountry() != null ? label(p.getCountry()) : "",
                p.getProfileStatus() != null ? label(p.getProfileStatus()) : "",
                p.getOrigin() != null ? label(p.getOrigin()) : "",
                p.getKaratStatus() != null ? label(p.getKaratStatus()) : "",
                p.getSource() != null ? label(p.getSource()) : "",
                p.getOverallStatusRdg() != null ? label(p.getOverallStatusRdg()) : "",

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

                p.getPersona() != null ? label(p.getPersona()) : "",
                p.getMentorBuddy(),
                p.getPrepStatus() != null ? label(p.getPrepStatus()) : "",
                p.getStartDate(),
                p.getPlannedEndDate(),
                p.getCoderpadResult1() != null ? label(p.getCoderpadResult1()) : "",
                p.getCoderpadResult2() != null ? label(p.getCoderpadResult2()) : "",
                p.getKaratReadyStatus() != null ? label(p.getKaratReadyStatus()) : "",
                p.getKaratTestDate(),

                p.getKaratTestResult() != null ? label(p.getKaratTestResult()) : "",
                p.getKaratAgeing(),
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