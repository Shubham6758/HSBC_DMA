package com.coforge.hsbcdma.service.DailyReportServices;

import com.coforge.hsbcdma.dto.DailyReportDTO.ReportRequestDTO;
import com.coforge.hsbcdma.dto.DailyReportDTO.ReportResponseDTO;
import com.coforge.hsbcdma.dto.DailyReportDTO.ReportRowDTO;
import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyReportService {

    private final OnboardingRepository onboardingRepository;
    private final ProfileTrackRepository profileTrackRepository;





    private ReportResponseDTO getProfileTrackerSummary(
            ReportRequestDTO request) {

        List<ProfileTracker> trackers =
                profileTrackRepository.findAll();

        LocalDate fromDate;
        LocalDate toDate;

        if (request.getFromDate() != null
                && request.getToDate() != null) {

            fromDate = request.getFromDate();
            toDate = request.getToDate();

        } else {

            LocalDate today = LocalDate.now();

            fromDate = today.with(DayOfWeek.MONDAY);
            toDate = today.with(DayOfWeek.SUNDAY);
        }

        trackers = trackers.stream()
                .filter(pt -> {

                    if (pt.getCreatedAt() == null) {
                        return false;
                    }

                    LocalDate createdDate =
                            pt.getCreatedAt().toLocalDate();

                    return !createdDate.isBefore(fromDate)
                            && !createdDate.isAfter(toDate);
                })
                .filter(pt ->
                        matchesProfileTrackerFilters(
                                pt,
                                request.getFilters()))
                .toList();
//        List<ProfileTracker> trackers =
//                profileTrackRepository.findAll();
//
//        trackers = trackers.stream()
//                .filter(pt ->
//                        matchesProfileTrackerFilters(
//                                pt,
//                                request.getFilters()))
//                .toList();

        Set<String> columns = new LinkedHashSet<>();

        Map<String, Map<String, Long>> report =
                new LinkedHashMap<>();

        Map<String, Map<String, Map<String, Long>>> statusBreakdown =
                new LinkedHashMap<>();

        for (ProfileTracker pt : trackers) {

//            String status =
//                    pt.getProfileTrackerStatus() != null
//                            ? pt.getProfileTrackerStatus().getName()
//                            : "NA";
            String reportDate =
                    pt.getCreatedAt()
                            .toLocalDate()
                            .toString();


            String status =
                    pt.getProfileTrackerStatus() != null
                            ? pt.getProfileTrackerStatus().getName()
                            : "NA";

            String column =
                    buildProfileTrackerColumnKey(
                            pt,
                            request.getSelectedColumns());

            if (column.isBlank()) {
                column = "NA";
            }

            columns.add(column);

//            report
//                    .computeIfAbsent(
//                            status,
//                            k -> new LinkedHashMap<>())
            report
                    .computeIfAbsent(
                            reportDate,
                            k -> new LinkedHashMap<>())
                    .merge(
                            column,
                            1L,
                            Long::sum);



            statusBreakdown
                    .computeIfAbsent(
                            reportDate,
                            k -> new LinkedHashMap<>())
                    .computeIfAbsent(
                            status,
                            k -> new LinkedHashMap<>())
                    .merge(
                            column,
                            1L,
                            Long::sum);
        }



        List<ReportRowDTO> rows =
                new ArrayList<>();

        report.forEach((reportDate, values) -> {

            long total =
                    values.values()
                            .stream()
                            .mapToLong(Long::longValue)
                            .sum();

            List<ReportRowDTO> childRows =
                    new ArrayList<>();

            Map<String, Map<String, Long>> dateStatuses =
                    statusBreakdown.get(reportDate);

            if (dateStatuses != null) {

                dateStatuses.forEach((status, statusValues) -> {

                    long childTotal =
                            statusValues.values()
                                    .stream()
                                    .mapToLong(Long::longValue)
                                    .sum();

                    childRows.add(
                            ReportRowDTO.builder()
                                    .status(status)
                                    .values(statusValues)
                                    .total(childTotal)
                                    .build());
                });
            }

            rows.add(
                    ReportRowDTO.builder()
                            .reportDate(reportDate)
                            .values(values)
                            .total(total)
                            .children(childRows)
                            .build());
        });

        return ReportResponseDTO.builder()
                .columns(new ArrayList<>(columns))
                .rows(rows)
                .build();
    }


    private String getProfileTrackerFieldValue(
            ProfileTracker pt,
            String column) {

        switch (column) {

            case "HBU":
                return pt.getDemand() != null &&
                        pt.getDemand().getHbu() != null
                        ? pt.getDemand().getHbu().getHbu()
                        : null;

            case "Hiring Manager":
                return pt.getDemand() != null &&
                        pt.getDemand().getHiringManager() != null
                        ? pt.getDemand().getHiringManager().getHiringManager()
                        : null;

            case "Band":
                return pt.getDemand() != null &&
                        pt.getDemand().getBand() != null
                        ? pt.getDemand().getBand().getBand()
                        : null;

            case "LOB":
                return pt.getDemand() != null &&
                        pt.getDemand().getLob() != null
                        ? pt.getDemand().getLob().getLob()
                        : null;

            case "PMO":
                return pt.getDemand() != null &&
                        pt.getDemand().getPmo() != null
                        ? pt.getDemand().getPmo().getPmo()
                        : null;

            case "Sales SPOC":
                return pt.getDemand() != null &&
                        pt.getDemand().getSalesSpoc() != null
                        ? pt.getDemand().getSalesSpoc().getName()
                        : null;

            case "Project Manager":
                return pt.getDemand() != null &&
                        pt.getDemand().getProjectManager() != null
                        ? pt.getDemand().getProjectManager().getName()
                        : null;

            case "Primary Skill":
                return pt.getProfile() != null &&
                        pt.getProfile().getPrimarySkills() != null
                        ? pt.getProfile()
                        .getPrimarySkills()
                        .stream()
                        .map(skill -> skill.getName())
                        .collect(Collectors.joining(", "))
                        : null;

            case "Secondary Skill":
                return pt.getProfile() != null &&
                        pt.getProfile().getSecondarySkills() != null
                        ? pt.getProfile()
                        .getSecondarySkills()
                        .stream()
                        .map(skill -> skill.getName())
                        .collect(Collectors.joining(", "))
                        : null;

            case "Skill Cluster":
                return pt.getProfile() != null &&
                        pt.getProfile().getSkillCluster() != null
                        ? pt.getProfile().getSkillCluster().getName()
                        : null;

            case "Location":
                return pt.getProfile() != null &&
                        pt.getProfile().getLocation() != null
                        ? pt.getProfile().getLocation().getName()
                        : null;

            default:
                return null;
        }
    }

    private String buildProfileTrackerColumnKey(
            ProfileTracker pt,
            List<String> selectedColumns) {

        List<String> parts = new ArrayList<>();

        if (selectedColumns == null || selectedColumns.isEmpty()) {
            return "NA";
        }





//        for (String column : selectedColumns) {
//
//            switch (column) {
//
//                case "HBU":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getHbu() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getHbu()
//                                        .getHbu());
//                    }
//                    break;
//
//                case "Hiring Manager":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getHiringManager() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getHiringManager()
//                                        .getHiringManager());
//                    }
//                    break;
//
//                case "Band":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getBand() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getBand()
//                                        .getBand());
//                    }
//                    break;
//
//                case "LOB":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getLob() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getLob()
//                                        .getLob());
//                    }
//                    break;
//
//                case "PMO":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getPmo() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getPmo()
//                                        .getPmo());
//                    }
//                    break;
//
//                case "Sales SPOC":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getSalesSpoc() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getSalesSpoc()
//                                        .getName());
//                    }
//                    break;
//
//                case "Project Manager":
//                    if (pt.getDemand() != null
//                            && pt.getDemand().getProjectManager() != null) {
//
//                        parts.add(
//                                pt.getDemand()
//                                        .getProjectManager()
//                                        .getName());
//                    }
//                    break;
//
//                case "Skill Cluster":
//                    if (pt.getProfile() != null
//                            && pt.getProfile().getSkillCluster() != null) {
//
//                        parts.add(
//                                pt.getProfile()
//                                        .getSkillCluster()
//                                        .getName());
//                    }
//                    break;
//
//                case "Location":
//                    if (pt.getProfile() != null
//                            && pt.getProfile().getLocation() != null) {
//
//                        parts.add(
//                                pt.getProfile()
//                                        .getLocation()
//                                        .getName());
//                    }
//                    break;
//
//                case "Primary Skill":
//                    if (pt.getProfile() != null
//                            && pt.getProfile().getPrimarySkills() != null) {
//
//                        parts.add(
//                                pt.getProfile()
//                                        .getPrimarySkills()
//                                        .stream()
//                                        .map(skill -> skill.getName())
//                                        .collect(Collectors.joining(", "))
//                        );
//                    }
//                    break;
//
//                case "Secondary Skill":
//                    if (pt.getProfile() != null
//                            && pt.getProfile().getSecondarySkills() != null) {
//
//                        parts.add(
//                                pt.getProfile()
//                                        .getSecondarySkills()
//                                        .stream()
//                                        .map(skill -> skill.getName())
//                                        .collect(Collectors.joining(", "))
//                        );
//                    }
//                    break;
//
//
//                default:
//                    break;
//            }
//        }


        for (String column : selectedColumns) {

            String value =
                    getProfileTrackerFieldValue(
                            pt,
                            column);

            if (value != null && !value.isBlank()) {
                parts.add(value);
            }
        }

        return parts.isEmpty()
                ? "NA"
                : String.join(" | ", parts);
    }


    private boolean matchesProfileTrackerFilters(
            ProfileTracker pt,
            Map<String, List<String>> filters) {

        if (filters == null || filters.isEmpty()) {
            return true;
        }

        for (Map.Entry<String, List<String>> entry : filters.entrySet()) {

            String column = entry.getKey();

            List<String> selectedValues = entry.getValue();

            String actualValue =
                    getProfileTrackerFieldValue(pt, column);

            if (actualValue == null ||
                    !selectedValues.contains(actualValue)) {
                return false;
            }
        }

        return true;
    }

    public ReportResponseDTO getOnboardingSummary(
            ReportRequestDTO request) {

        if ("PROFILE_TRACKER".equalsIgnoreCase(request.getTrackerType())) {
            return getProfileTrackerSummary(request);
        }

        List<Onboarding> onboardings =
                onboardingRepository.findAll();


        Set<String> columns = new LinkedHashSet<>();

        Map<String, Map<String, Long>> report =
                new LinkedHashMap<>();

        for (Onboarding ob : onboardings) {

            String status =
                    ob.getOnboardingStatus() != null
                            ? ob.getOnboardingStatus().getName()
                            : "NA";

            String column =
                    buildColumnKey(
                            ob,
                            request.getSelectedColumns());

            if (column.isBlank()) {
                column = "NA";
            }

            columns.add(column);

            report
                    .computeIfAbsent(
                            status,
                            k -> new LinkedHashMap<>())
                    .merge(
                            column,
                            1L,
                            Long::sum);
        }

        List<ReportRowDTO> rows =
                new ArrayList<>();

        report.forEach((status, values) -> {

            long total =
                    values.values()
                            .stream()
                            .mapToLong(Long::longValue)
                            .sum();

            rows.add(
                    ReportRowDTO.builder()
                            .status(status)
                            .values(values)
                            .total(total)
                            .build());
        });

        return ReportResponseDTO.builder()
                .columns(new ArrayList<>(columns))
                .rows(rows)
                .build();
    }

    private String buildColumnKey(
            Onboarding ob,
            List<String> selectedColumns) {

        List<String> parts = new ArrayList<>();

        if (selectedColumns == null || selectedColumns.isEmpty()) {
            return "NA";
        }

        for (String column : selectedColumns) {

            switch (column) {

                case "HBU":
                    if (ob.getDemand() != null
                            && ob.getDemand().getHbu() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getHbu()
                                        .getHbu());
                    }
                    break;

                case "Hiring Manager":
                    if (ob.getDemand() != null
                            && ob.getDemand().getHiringManager() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getHiringManager()
                                        .getHiringManager());
                    }
                    break;

                case "Band":
                    if (ob.getDemand() != null
                            && ob.getDemand().getBand() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getBand()
                                        .getBand());
                    }
                    break;

                case "PMO":
                    if (ob.getDemand() != null
                            && ob.getDemand().getPmo() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getPmo()
                                        .getPmo());
                    }
                    break;



                case "LOB":
                    if (ob.getDemand() != null
                            && ob.getDemand().getLob() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getLob()
                                        .getLob());
                    }
                    break;



                case "Sales SPOC":
                    if (ob.getDemand() != null
                            && ob.getDemand().getSalesSpoc() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getSalesSpoc()
                                        .getName());
                    }
                    break;

                case "Project Manager":
                    if (ob.getDemand() != null
                            && ob.getDemand().getProjectManager() != null) {

                        parts.add(
                                ob.getDemand()
                                        .getProjectManager()
                                        .getName());
                    }
                    break;

                case "Skill Cluster":
                    if (ob.getProfile() != null
                            && ob.getProfile().getSkillCluster() != null) {

                        parts.add(
                                ob.getProfile()
                                        .getSkillCluster()
                                        .getName());
                    }
                    break;

                case "Location":
                    if (ob.getProfile() != null
                            && ob.getProfile().getLocation() != null) {

                        parts.add(
                                ob.getProfile()
                                        .getLocation()
                                        .getName());
                    }
                    break;

                case "Primary Skill":
                    if (ob.getProfile() != null
                            && ob.getProfile().getPrimarySkills() != null) {

                        parts.add(
                                ob.getProfile()
                                        .getPrimarySkills()
                                        .stream()
                                        .map(skill -> skill.getName())
                                        .collect(Collectors.joining(", "))
                        );
                    }
                    break;

                case "Secondary Skill":
                    if (ob.getProfile() != null
                            && ob.getProfile().getSecondarySkills() != null) {

                        parts.add(
                                ob.getProfile()
                                        .getSecondarySkills()
                                        .stream()
                                        .map(skill -> skill.getName())
                                        .collect(Collectors.joining(", "))
                        );
                    }
                    break;


                default:
                    break;
            }
        }

        return parts.isEmpty()
                ? "NA"
                : String.join(" | ", parts);
    }
}