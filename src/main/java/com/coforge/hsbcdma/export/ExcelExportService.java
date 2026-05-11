package com.coforge.hsbcdma.export;

import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.entity.User;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingRepository;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackRepository;
import com.coforge.hsbcdma.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExcelExportService {

    private final AddDemandRepository demandRepo;
    private final ProfileRepository profileRepo;
    private final ProfileTrackRepository profileTrackerRepo;
    private final OnboardingRepository onboardingRepo;
    private final UserAccountRepository userAccountRepository;



    private String formatLobDemandId(String lob, Long demandId) {
        return lob + "-" + demandId;
    }

    public String getDemandExcelTimestampName(String name) {
        return name + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"));
    }

    // ---------- AddDemand ----------
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportDemandsExcel() {
        List<DemandExportDTO> rows = demandRepo.findAll().stream()
                .map(DemandExportDTO::from)
                .toList();

        List<String> headers = List.of(
                "Demand ID",
                "RR",
                "Subcon RR",
                "LOB",
                "Skill Cluster",
                "Primary Skill",
                "Secondary Skill",
                "Priority",
                "Status",
                "Karat",
                "HBU",
                "Demand Timeline",
                "Demand Type",
                "Demand Location",
                "External/Internal",   // ✅ NEW
                "POD",                 // ✅ NEW
                "Demand Received Date",// ✅ NEW
                "Hiring Manager",
                "Delivery Manager",
                "PM",
                "Sales Spoc",
                "PMO",
                "Band",
                "Experience",
                "P1 Flag Date",
                "Created At",
                "Created By"
        );

        List<Function<DemandExportDTO, Object>> ex = List.of(
                d -> d.demandId(),
                d -> d.rrNumber(),
                d -> d.isSubconRR() == null ? "" : (d.isSubconRR() ? "Yes" : "No"),
                d -> d.lob(),
                d -> Helper.toProperCase(d.skillCluster()),
                d -> Helper.toProperCaseCommaSeparated(d.primarySkills()),
                d -> Helper.toProperCaseCommaSeparated(d.secondarySkills()),
                d -> Helper.toProperCase(d.priority()),
                d -> Helper.toProperCase(d.status()),
                d -> d.karat() == null ? "" : (d.karat() ? "Yes" : "No"),
                d -> d.hbu(),
                d -> Helper.toProperCase(d.demandTimeline()),
                d -> Helper.toProperCase(d.demandType()),
                d -> Helper.toProperCaseCommaSeparated(d.locations()),

                d -> Helper.toProperCase(d.externalInternal()), // ✅ NEW
                d -> Helper.toProperCase(d.pod()),             // ✅ NEW
                d -> d.demandReceivedDate(),                   // ✅ NEW

                d -> Helper.toProperCase(d.hiringManager()),
                d -> Helper.toProperCase(d.deliveryManager()),
                d -> Helper.toProperCase(d.projectManager()),
                d -> Helper.toProperCase(d.salesSpoc()),
                d -> Helper.toProperCase(d.pmo()),
                d -> Helper.toProperCase(d.band()),
                d -> Helper.toProperCase(d.experience()),
                d -> d.p1FlagDate(),
                d -> d.createdAt() != null ? d.createdAt().toLocalDate() : null,
                d -> Helper.toProperCase(d.createdByName())
        );

        String sheetName = getDemandExcelTimestampName("Demands");
        return new ExcelExporter<>(sheetName, headers, ex).export(rows);
    }


    // ---------- Profile ----------
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportProfilesExcel() {

        var profiles = profileRepo.findByIsActiveTrue();

        // collect createdByUserId values from profile table
        var createdByUserIds = profiles.stream()
                .map(Profile::getCreatedByUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // fetch users using userId field (NOT PK id)
        var userMap = userAccountRepository.findByUserIdIn(createdByUserIds).stream()
                .collect(Collectors.toMap(
                        User::getUserId,
                        User::getName,
                        (a, b) -> a
                ));

        var rows = profiles.stream()
                .map(profile -> ProfileExportDTO.from(
                        profile,
                        userMap.getOrDefault(profile.getCreatedByUserId(), "N/A")
                ))
                .toList();

        var headers = List.of(
                "Candidate Name",
                "Email ID",
                "Employee ID",
                "Status",
                "Phone",
                "Exp (yrs)",
                "Skill Cluster",
                "Primary Skills",
                "Secondary Skills",
                "Location",
                "HBU",
                "Created At",
                "Created By"
        );

        var ex = List.<Function<ProfileExportDTO, Object>>of(
                p -> Helper.toProperCase(p.candidateName()),
                p -> p.emailId(),
                p -> p.empId(),
                p -> Helper.toProperCase(p.profileStatus()),                  // or p.profileStatus()
                p -> p.phoneNumber(),
                p -> p.experience(),
                p -> Helper.toProperCase(p.skillCluster()),
                p -> Helper.toProperCaseCommaSeparated(p.primarySkills()),
                p -> Helper.toProperCaseCommaSeparated(p.secondarySkills()),
                p -> Helper.toProperCase(p.location()),
                p -> p.hbu(),
                p -> p.createdAt(),
                p -> Helper.toProperCase(p.createdByUserId())
        );

        String sheetName = getDemandExcelTimestampName("Profiles");
        return new ExcelExporter<>(sheetName, headers, ex).export(rows);
    }


    // ---------- ProfileTracker ----------
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportProfileTrackersExcel() {


        var profileTrackers = profileTrackerRepo.findAll();

        // collect createdByUserId values from profile_tracker table
        var createdByUserIds = profileTrackers.stream()
                .map(ProfileTracker::getCreatedByUserId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // fetch users using userId field (NOT PK id)
        var userMap = userAccountRepository.findByUserIdIn(createdByUserIds).stream()
                .collect(Collectors.toMap(
                        User::getUserId,
                        User::getName,
                        (a, b) -> a
                ));

        var rows = profileTrackers.stream()
                .map(pt -> ProfileTrackerExportDTO.from(
                        pt,
                        userMap.getOrDefault(pt.getCreatedByUserId(), "N/A")
                ))
                .toList();

        List<String> headers = List.of(
                "Demand ID",
                "Candidate Name",
                "Priority",
                "Skill Cluster",
                "Primary Skill",
                "Secondary Skill",
                "LOB",
                "HBU",
                "Location",
                "External/Internal",
                "Hiring Manager",
                "Attached Date",
                "Profile Shared Date",
                "Interview Date",
                "Decision Date",
                "Status",
                "Created At",
                "Created By"
        );

        List<Function<ProfileTrackerExportDTO, Object>> extractors = List.of(
                ProfileTrackerExportDTO::demandId,
                p -> Helper.toProperCase(p.candidateName()),
                p -> Helper.toProperCase(p.priority()),
                p -> Helper.toProperCase(p.skillCluster()),
                p -> Helper.joinPrimarySkills(p.primarySkills()),
                p -> Helper.joinSecondarySkills(p.secondarySkills()),
                ProfileTrackerExportDTO::lob,
                ProfileTrackerExportDTO::hbu,
                p -> Helper.joinLocations(p.location()),
                p -> Helper.toProperCase(p.externalInternal()),
                p -> Helper.toProperCase(p.hiringManager()),
                ProfileTrackerExportDTO::attachedDate,
                ProfileTrackerExportDTO::profileSharedDate,
                ProfileTrackerExportDTO::interviewDate,
                ProfileTrackerExportDTO::decisionDate,
                p -> Helper.toProperCase(p.profileTrackerStatus()),
                p -> p.createdAt() != null ? p.createdAt().toLocalDate() : null,
                p -> Helper.toProperCase(p.createdByUserId())
        );

        String sheetName = getDemandExcelTimestampName("ProfileTracker");
        return new ExcelExporter<>(sheetName, headers, extractors).export(rows);

    }


    // ---------- Onboarding ----------
    @Transactional(readOnly = true)
    public ByteArrayInputStream exportOnboardingExcel() {

        List<OnboardingExportDTO> rows = onboardingRepo.findAll()
                .stream()
                .map(o -> {
                    try {
                        return OnboardingExportDTO.from(o);
                    } catch (Exception e) {
                        System.out.println("Skipping onboarding id: " + o.getId());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toList();

        List<String> headers = List.of(
                "Demand ID",
                "Candidate Name",
                "Profile Type",
                "HBU",
                "Hiring Manager",
                "PMO Spoc",   // ✅ ADDED
                "Band",
                "WBS Type",
                "Offer Date",
                "DOJ",
                "Profile Shared Date",
                "C-Tool ID",
                "BGV Status",
                "PEV Upload Date",
                "VP Tagging Date",
                "Tech Select Date",
                "HSBC Onboard Date",
                "Onboarding Status"
        );

        List<Function<OnboardingExportDTO, Object>> extractors = List.of(
                OnboardingExportDTO::demandId,
                OnboardingExportDTO::candidateName,
                OnboardingExportDTO::profileType,
                OnboardingExportDTO::hbu,
                OnboardingExportDTO::hiringManager,
                OnboardingExportDTO::pmoSpoc,   // ✅ ADDED
                OnboardingExportDTO::band,
                OnboardingExportDTO::wbsType,
                OnboardingExportDTO::offerDate,
                OnboardingExportDTO::doj,
                OnboardingExportDTO::profileSharedDate,
                OnboardingExportDTO::ctoolId,
                OnboardingExportDTO::bgvStatus,
                OnboardingExportDTO::pevUploadDate,
                OnboardingExportDTO::vpTaggingDate,
                OnboardingExportDTO::techSelectDate,
                OnboardingExportDTO::hsbcOnboardDate,
                OnboardingExportDTO::onboardingStatus
        );

        String sheetName = getDemandExcelTimestampName("Onboarding");

        return new ExcelExporter<>(sheetName, headers, extractors).export(rows);
    }



//    private String toProperCase(String value) {
//        if (value == null || value.isBlank()) {
//            return value;
//        }
//
//        return Arrays.stream(value.trim().split("\\s+"))
//                .filter(word -> !word.isBlank())
//                .map(word -> Character.toUpperCase(word.charAt(0)) +
//                        word.substring(1).toLowerCase(Locale.ROOT))
//                .collect(Collectors.joining(" "));
//    }
//
//    private String toProperCaseCommaSeparated(String value) {
//        if (value == null || value.isBlank()) {
//            return value;
//        }
//
//        return Arrays.stream(value.split(","))
//                .map(String::trim)
//                .filter(s -> !s.isEmpty())
//                .map(this::toProperCase)
//                .collect(Collectors.joining(", "));
//    }
//
//
//    private String joinPrimarySkills(Set<PrimarySkills> skills) {
//        if (skills == null || skills.isEmpty()) {
//            return "";
//        }
//
//        return skills.stream()
//                .filter(Objects::nonNull)
//                .map(PrimarySkills::getPrimarySkills)   // change getter if your field name is different
//                .filter(Objects::nonNull)
//                .map(this::toProperCase)
//                .sorted()
//                .collect(Collectors.joining(", "));
//    }
//
//    private String joinSecondarySkills(Set<SecondarySkills> skills) {
//        if (skills == null || skills.isEmpty()) {
//            return "";
//        }
//
//        return skills.stream()
//                .filter(Objects::nonNull)
//                .map(SecondarySkills::getSecondarySkills)   // change getter if your field name is different
//                .filter(Objects::nonNull)
//                .map(this::toProperCase)
//                .sorted()
//                .collect(Collectors.joining(", "));
//    }
//
//    private String joinLocations(Set<Location> locations) {
//        if (locations == null || locations.isEmpty()) {
//            return "";
//        }
//
//        return locations.stream()
//                .filter(Objects::nonNull)
//                .map(Location::getName)   // change getter if your field name is different
//                .filter(Objects::nonNull)
//                .map(this::toProperCase)
//                .sorted()
//                .collect(Collectors.joining(", "));
//    }

    @Transactional(readOnly = true)
    public ByteArrayInputStream exportAllInOneExcel() {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // =====================================================
            // SHEET 1: DEMANDS
            // =====================================================
            var demandRows = demandRepo.findAll()
                    .stream()
                    .map(DemandExportDTO::from)
                    .toList();

            new ExcelExporter<>(
                    getDemandExcelTimestampName("Demands"),
                    demandHeaders(),
                    demandExtractors()
            ).writeSheet(workbook, demandRows);

            // =====================================================
            // SHEET 2: PROFILES
            // =====================================================
            var profiles = profileRepo.findByIsActiveTrue();

            var userMap = userAccountRepository
                    .findByUserIdIn(
                            profiles.stream()
                                    .map(Profile::getCreatedByUserId)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toSet())
                    )
                    .stream()
                    .collect(Collectors.toMap(
                            User::getUserId,
                            User::getName,
                            (a, b) -> a
                    ));

            var profileRows = profiles.stream()
                    .map(p -> ProfileExportDTO.from(
                            p,
                            userMap.getOrDefault(p.getCreatedByUserId(), "N/A")
                    ))
                    .toList();

            new ExcelExporter<>(
                    getDemandExcelTimestampName("Profiles"),
                    profileHeaders(),
                    profileExtractors()
            ).writeSheet(workbook, profileRows);

            // =====================================================
            // SHEET 3: PROFILE TRACKER
            // =====================================================
            var trackers = profileTrackerRepo.findAll();

            var trackerUserMap = userAccountRepository
                    .findByUserIdIn(
                            trackers.stream()
                                    .map(ProfileTracker::getCreatedByUserId)
                                    .filter(Objects::nonNull)
                                    .collect(Collectors.toSet())
                    )
                    .stream()
                    .collect(Collectors.toMap(
                            User::getUserId,
                            User::getName,
                            (a, b) -> a
                    ));

            var trackerRows = trackers.stream()
                    .map(pt -> ProfileTrackerExportDTO.from(
                            pt,
                            trackerUserMap.getOrDefault(pt.getCreatedByUserId(), "N/A")
                    ))
                    .toList();

            new ExcelExporter<>(
                    getDemandExcelTimestampName("ProfileTracker"),
                    profileTrackerHeaders(),
                    profileTrackerExtractors()
            ).writeSheet(workbook, trackerRows);

            // =====================================================
            // SHEET 4: ONBOARDING
            // =====================================================
            var onboardingRows = onboardingRepo.findAll()
                    .stream()
                    .map(OnboardingExportDTO::from)
                    .toList();

            new ExcelExporter<>(
                    getDemandExcelTimestampName("Onboarding"),
                    onboardingHeaders(),
                    onboardingExtractors()
            ).writeSheet(workbook, onboardingRows);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to export excel", e);
        }
    }



    // =====================================================
    // HEADERS & EXTRACTORS (YOUR EXISTING LOGIC)
    // =====================================================
    private List<String> demandHeaders() {
        return List.of(
                "Demand ID", "RR", "Subcon RR", "LOB", "Skill Cluster",
                "Primary Skill", "Secondary Skill", "Priority", "Status",
                "Karat", "HBU", "Demand Timeline", "Demand Type",
                "Demand Location", "External/Internal", "POD",
                "Demand Received Date", "Hiring Manager",
                "Delivery Manager", "PM", "Sales Spoc",
                "PMO", "Band", "Experience",
                "P1 Flag Date", "Created At", "Created By"
        );
    }

    private List<Function<DemandExportDTO, Object>> demandExtractors() {
        return List.of(
                d -> d.demandId(),
                d -> d.rrNumber(),
                d -> d.isSubconRR() == null ? "" : (d.isSubconRR() ? "Yes" : "No"),
                DemandExportDTO::lob,
                d -> Helper.toProperCase(d.skillCluster()),
                d -> Helper.toProperCaseCommaSeparated(d.primarySkills()),
                d -> Helper.toProperCaseCommaSeparated(d.secondarySkills()),
                d -> Helper.toProperCase(d.priority()),
                d -> Helper.toProperCase(d.status()),
                d -> d.karat() == null ? "" : (d.karat() ? "Yes" : "No"),
                DemandExportDTO::hbu,
                DemandExportDTO::demandTimeline,
                DemandExportDTO::demandType,
                d -> Helper.toProperCaseCommaSeparated(d.locations()),
                DemandExportDTO::externalInternal,
                DemandExportDTO::pod,
                DemandExportDTO::demandReceivedDate,
                DemandExportDTO::hiringManager,
                DemandExportDTO::deliveryManager,
                DemandExportDTO::projectManager,
                DemandExportDTO::salesSpoc,
                DemandExportDTO::pmo,
                DemandExportDTO::band,
                DemandExportDTO::experience,
                DemandExportDTO::p1FlagDate,
                d -> d.createdAt() != null ? d.createdAt().toLocalDate() : null,
                DemandExportDTO::createdByName
        );
    }

    private List<String> profileHeaders() {

        return List.of(
                "Candidate Name", "Email ID", "Employee ID", "Phone", "Active", "Experience",

                "Skill Cluster", "Location", "HBU", "External/Internal", "Country",
                "Profile Status", "Origin", "Karat Status", "Source", "Overall Status",

                "Summary", "File Name", "PAN", "Karat Readiness", "LOB Shared",
                "Practice", "Band", "Ageing", "Ageing Range", "Codes", "Code Type",
                "Min Billing Rate", "Project Code",

                "Date Of Submission", "Week Of", "Account Received On", "Status Date",

                "L1 Interview Date", "Current Location", "Official NP",
                "Negotiable NP LWD", "Recruiter",

                "Primary Skills", "Secondary Skills",

                "Created At", "Created By", "Updated At", "Updated By"
        );
    }

    private List<Function<ProfileExportDTO, Object>> profileExtractors() {

        return List.of(

                p -> Helper.toProperCase(p.candidateName()),
                ProfileExportDTO::emailId,
                ProfileExportDTO::empId,
                ProfileExportDTO::phoneNumber,
                p -> p.isActive() != null ? (p.isActive() ? "Yes" : "No") : "",
                ProfileExportDTO::experience,

                p -> Helper.toProperCase(p.skillCluster()),
                p -> Helper.toProperCase(p.location()),
                p -> Helper.toProperCase(p.hbu()),
                p -> Helper.toProperCase(p.externalInternal()),
                p -> Helper.toProperCase(p.country()),
                p -> Helper.toProperCase(p.profileStatus()),
                p -> Helper.toProperCase(p.origin()),
                p -> Helper.toProperCase(p.karatStatus()),
                p -> Helper.toProperCase(p.source()),
                p -> Helper.toProperCase(p.overallStatusRdg()),

                ProfileExportDTO::summary,
                ProfileExportDTO::fileName,
                ProfileExportDTO::panNumber,
                ProfileExportDTO::karatReadiness,
                ProfileExportDTO::lobShared,
                ProfileExportDTO::practice,
                ProfileExportDTO::band,
                ProfileExportDTO::ageing,
                ProfileExportDTO::ageingRange,
                ProfileExportDTO::codes,
                ProfileExportDTO::codeType,
                ProfileExportDTO::minBillingRate,
                ProfileExportDTO::projectCode,

                ProfileExportDTO::dateOfSubmission,
                ProfileExportDTO::weekOf,
                ProfileExportDTO::accountReceivedOn,
                ProfileExportDTO::statusDate,

                ProfileExportDTO::l1InterviewDate,
                ProfileExportDTO::currentLocation,
                ProfileExportDTO::officialNP,
                ProfileExportDTO::negotiableNpLwd,
                ProfileExportDTO::recruiter,

                p -> Helper.toProperCaseCommaSeparated(p.primarySkills()),
                p -> Helper.toProperCaseCommaSeparated(p.secondarySkills()),

                p -> p.createdAt() != null ? p.createdAt().toLocalDate() : null,
                ProfileExportDTO::createdByUserId,
                ProfileExportDTO::updatedAt,
                ProfileExportDTO::updatedByUserId
        );
    }

    private List<String> profileTrackerHeaders() {
        return List.of(
                "Demand ID", "Candidate Name", "Priority",
                "Skill Cluster", "Primary Skill", "Secondary Skill",
                "LOB", "HBU", "Location",
                "External/Internal", "Hiring Manager",
                "Attached Date", "Profile Shared Date",
                "Interview Date", "Decision Date",
                "Status", "Created At", "Created By"
        );
    }

    private List<Function<ProfileTrackerExportDTO, Object>> profileTrackerExtractors() {
        return List.of(
                ProfileTrackerExportDTO::demandId,
                ProfileTrackerExportDTO::candidateName,
                ProfileTrackerExportDTO::priority,
                ProfileTrackerExportDTO::skillCluster,
                p -> Helper.joinPrimarySkills(p.primarySkills()),
                p -> Helper.joinSecondarySkills(p.secondarySkills()),
                ProfileTrackerExportDTO::lob,
                ProfileTrackerExportDTO::hbu,
                p -> Helper.joinLocations(p.location()),
                ProfileTrackerExportDTO::externalInternal,
                ProfileTrackerExportDTO::hiringManager,
                ProfileTrackerExportDTO::attachedDate,
                ProfileTrackerExportDTO::profileSharedDate,
                ProfileTrackerExportDTO::interviewDate,
                ProfileTrackerExportDTO::decisionDate,
                ProfileTrackerExportDTO::profileTrackerStatus,
                ProfileTrackerExportDTO::createdAt,
                ProfileTrackerExportDTO::createdByUserId
        );
    }


    private List<String> onboardingHeaders() {
        return List.of(
                "Demand ID",
                "Candidate Name",
                "Profile Type",
                "HBU",
                "Hiring Manager",
                "PMO Spoc",   // ✅ ADDED
                "Band",
                "WBS Type",
                "Offer Date",
                "DOJ",
                "Profile Shared Date",
                "C-Tool ID",
                "BGV Status",
                "PEV Upload Date",
                "VP Tagging Date",
                "Tech Select Date",
                "HSBC Onboard Date",
                "Onboarding Status"
        );
    }


    private List<Function<OnboardingExportDTO, Object>> onboardingExtractors() {
        return List.of(
                OnboardingExportDTO::demandId,
                OnboardingExportDTO::candidateName,
                OnboardingExportDTO::profileType,
                OnboardingExportDTO::hbu,
                OnboardingExportDTO::hiringManager,
                OnboardingExportDTO::pmoSpoc,   // ✅ ADDED
                OnboardingExportDTO::band,
                OnboardingExportDTO::wbsType,
                OnboardingExportDTO::offerDate,
                OnboardingExportDTO::doj,
                OnboardingExportDTO::profileSharedDate,
                OnboardingExportDTO::ctoolId,
                OnboardingExportDTO::bgvStatus,
                OnboardingExportDTO::pevUploadDate,
                OnboardingExportDTO::vpTaggingDate,
                OnboardingExportDTO::techSelectDate,
                OnboardingExportDTO::hsbcOnboardDate,
                OnboardingExportDTO::onboardingStatus
        );
    }

}