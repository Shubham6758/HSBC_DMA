package com.coforge.hsbcdma.export;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ProfileExportConfig {

    public List<ExcelExportService.ColumnDef<ProfileExportDTO>> getColumns(
            Set<String> roles) {

        List<ExcelExportService.ColumnDef<ProfileExportDTO>> cols =
                new ArrayList<>();

        // ================= RDG =================
        if (hasRole(roles, "RDG")) {

            cols.add(new ExcelExportService.ColumnDef<>("Candidate Name",
                    ProfileExportDTO::candidateName));
            cols.add(new ExcelExportService.ColumnDef<>("Emp Id",
                    ProfileExportDTO::empId));
            cols.add(new ExcelExportService.ColumnDef<>("Experience",
                    ProfileExportDTO::experience));
            cols.add(new ExcelExportService.ColumnDef<>("DOS",
                    ProfileExportDTO::dateOfSubmission));
            cols.add(new ExcelExportService.ColumnDef<>("Week Of",
                    ProfileExportDTO::weekOf));
            cols.add(new ExcelExportService.ColumnDef<>("Status Date",
                    ProfileExportDTO::statusDate));
            cols.add(new ExcelExportService.ColumnDef<>("HBU",
                    ProfileExportDTO::hbu));
            cols.add(new ExcelExportService.ColumnDef<>("Band",
                    ProfileExportDTO::band));
            cols.add(new ExcelExportService.ColumnDef<>("Status",
                    ProfileExportDTO::profileStatus));
            cols.add(new ExcelExportService.ColumnDef<>("Source",
                    ProfileExportDTO::source));
            cols.add(new ExcelExportService.ColumnDef<>("Skill Cluster",
                    ProfileExportDTO::skillCluster));
            cols.add(new ExcelExportService.ColumnDef<>("Location",
                    ProfileExportDTO::location));
            cols.add(new ExcelExportService.ColumnDef<>("Summary",
                    ProfileExportDTO::summary));
            cols.add(new ExcelExportService.ColumnDef<>("Ageing",
                    ProfileExportDTO::ageing));

            return cols;
        }

        // ================= TA =================
        if (hasRole(roles, "TA")) {

            cols.add(new ExcelExportService.ColumnDef<>("Candidate Name",
                    ProfileExportDTO::candidateName));
            cols.add(new ExcelExportService.ColumnDef<>("PAN",
                    ProfileExportDTO::panNumber));
            cols.add(new ExcelExportService.ColumnDef<>("Experience",
                    ProfileExportDTO::experience));
            cols.add(new ExcelExportService.ColumnDef<>("L1 Interview Date",
                    ProfileExportDTO::l1InterviewDate));
            cols.add(new ExcelExportService.ColumnDef<>("Official NP",
                    ProfileExportDTO::officialNP));
            cols.add(new ExcelExportService.ColumnDef<>("Negotiable NP",
                    ProfileExportDTO::negotiableNpLwd));
            cols.add(new ExcelExportService.ColumnDef<>("Recruiter",
                    ProfileExportDTO::recruiter));
            cols.add(new ExcelExportService.ColumnDef<>("Status",
                    ProfileExportDTO::profileStatus));
            cols.add(new ExcelExportService.ColumnDef<>("Primary Skills",
                    ProfileExportDTO::primarySkills));
            cols.add(new ExcelExportService.ColumnDef<>("Location",
                    ProfileExportDTO::location));
            cols.add(new ExcelExportService.ColumnDef<>("HBU",
                    ProfileExportDTO::hbu));
            cols.add(new ExcelExportService.ColumnDef<>("Summary",
                    ProfileExportDTO::summary));
            cols.add(new ExcelExportService.ColumnDef<>("Tech ITRS",
                    ProfileExportDTO::karatReadiness));
            cols.add(new ExcelExportService.ColumnDef<>("Ageing",
                    ProfileExportDTO::ageing));

            return cols;
        }

        // ================= PMO / PM =================
        if (hasRole(roles, "PMO")
                || hasRole(roles, "PM")) {

            cols.add(new ExcelExportService.ColumnDef<>("Candidate Name",
                    ProfileExportDTO::candidateName));
            cols.add(new ExcelExportService.ColumnDef<>("Emp Id",
                    ProfileExportDTO::empId));
            cols.add(new ExcelExportService.ColumnDef<>("Experience",
                    ProfileExportDTO::experience));
            cols.add(new ExcelExportService.ColumnDef<>("Location",
                    ProfileExportDTO::location));
            cols.add(new ExcelExportService.ColumnDef<>("Profile Status",
                    ProfileExportDTO::profileStatus));
            cols.add(new ExcelExportService.ColumnDef<>("Skill Cluster",
                    ProfileExportDTO::skillCluster));
            cols.add(new ExcelExportService.ColumnDef<>("Primary Skills",
                    ProfileExportDTO::primarySkills));
            cols.add(new ExcelExportService.ColumnDef<>("Active Status",
                    p -> p.isActive() != null ? p.isActive() : ""));
            cols.add(new ExcelExportService.ColumnDef<>("Ageing",
                    ProfileExportDTO::ageing));
            cols.add(new ExcelExportService.ColumnDef<>("DOS",
                    ProfileExportDTO::dateOfSubmission));
            cols.add(new ExcelExportService.ColumnDef<>("Week Of",
                    ProfileExportDTO::weekOf));
            cols.add(new ExcelExportService.ColumnDef<>("Status Date",
                    ProfileExportDTO::statusDate));
            cols.add(new ExcelExportService.ColumnDef<>("HBU",
                    ProfileExportDTO::hbu));
            cols.add(new ExcelExportService.ColumnDef<>("Band",
                    ProfileExportDTO::band));
            cols.add(new ExcelExportService.ColumnDef<>("Status",
                    ProfileExportDTO::profileStatus));
            cols.add(new ExcelExportService.ColumnDef<>("Source",
                    ProfileExportDTO::source));
            cols.add(new ExcelExportService.ColumnDef<>("L1 Interview Date",
                    ProfileExportDTO::l1InterviewDate));
            cols.add(new ExcelExportService.ColumnDef<>("Official NP",
                    ProfileExportDTO::officialNP));
            cols.add(new ExcelExportService.ColumnDef<>("Negotiable NP",
                    ProfileExportDTO::negotiableNpLwd));
            cols.add(new ExcelExportService.ColumnDef<>("Recruiter",
                    ProfileExportDTO::recruiter));

            return cols;
        }

        // ================= HBU =================
        if (isHbuUser(roles)) {

            cols.add(new ExcelExportService.ColumnDef<>("Candidate Name",
                    ProfileExportDTO::candidateName));
            cols.add(new ExcelExportService.ColumnDef<>("Emp Id",
                    ProfileExportDTO::empId));
            cols.add(new ExcelExportService.ColumnDef<>("Experience",
                    ProfileExportDTO::experience));
            cols.add(new ExcelExportService.ColumnDef<>("Location",
                    ProfileExportDTO::location));
            cols.add(new ExcelExportService.ColumnDef<>("Profile Status",
                    ProfileExportDTO::profileStatus));
            cols.add(new ExcelExportService.ColumnDef<>("Skill Cluster",
                    ProfileExportDTO::skillCluster));
            cols.add(new ExcelExportService.ColumnDef<>("Primary Skills",
                    ProfileExportDTO::primarySkills));
            cols.add(new ExcelExportService.ColumnDef<>("Active Status",
                    p -> p.isActive() != null ? p.isActive() : ""));
            cols.add(new ExcelExportService.ColumnDef<>("Ageing",
                    ProfileExportDTO::ageing));
            cols.add(new ExcelExportService.ColumnDef<>("DOS",
                    ProfileExportDTO::dateOfSubmission));
            cols.add(new ExcelExportService.ColumnDef<>("Week Of",
                    ProfileExportDTO::weekOf));
            cols.add(new ExcelExportService.ColumnDef<>("Status Date",
                    ProfileExportDTO::statusDate));
            cols.add(new ExcelExportService.ColumnDef<>("HBU",
                    ProfileExportDTO::hbu));
            cols.add(new ExcelExportService.ColumnDef<>("Band",
                    ProfileExportDTO::band));
            cols.add(new ExcelExportService.ColumnDef<>("Source",
                    ProfileExportDTO::source));
            cols.add(new ExcelExportService.ColumnDef<>("L1 Interview Date",
                    ProfileExportDTO::l1InterviewDate));
            cols.add(new ExcelExportService.ColumnDef<>("Official NP",
                    ProfileExportDTO::officialNP));
            cols.add(new ExcelExportService.ColumnDef<>("Negotiable NP",
                    ProfileExportDTO::negotiableNpLwd));
            cols.add(new ExcelExportService.ColumnDef<>("Recruiter",
                    ProfileExportDTO::recruiter));
            cols.add(new ExcelExportService.ColumnDef<>("CV",
                    ProfileExportDTO::fileName));

            return cols;
        }

        // ================= KARAT ADMIN =================
        if (hasRole(roles, "KARAT ADMIN")) {

            cols.add(new ExcelExportService.ColumnDef<>("Candidate Name",
                    ProfileExportDTO::candidateName));
            cols.add(new ExcelExportService.ColumnDef<>("Emp Id",
                    ProfileExportDTO::empId));
            cols.add(new ExcelExportService.ColumnDef<>("Experience",
                    ProfileExportDTO::experience));
            cols.add(new ExcelExportService.ColumnDef<>("Location",
                    ProfileExportDTO::location));
            cols.add(new ExcelExportService.ColumnDef<>("Profile Status",
                    ProfileExportDTO::profileStatus));
            cols.add(new ExcelExportService.ColumnDef<>("Skill Cluster",
                    ProfileExportDTO::skillCluster));
            cols.add(new ExcelExportService.ColumnDef<>("HBU",
                    ProfileExportDTO::hbu));

            cols.add(new ExcelExportService.ColumnDef<>(
                    "Persona",
                    ProfileExportDTO::persona));

            cols.add(new ExcelExportService.ColumnDef<>(
                    "Mentor Buddy",
                    ProfileExportDTO::mentorBuddy));
            cols.add(new ExcelExportService.ColumnDef<>(
                    "Prep Status",
                    ProfileExportDTO::prepStatus));
            cols.add(new ExcelExportService.ColumnDef<>(
                    "Start Date",
                    ProfileExportDTO::startDate));
            cols.add(new ExcelExportService.ColumnDef<>(
                    "Planned End Date",
                    ProfileExportDTO::plannedEndDate));

            cols.add(new ExcelExportService.ColumnDef<>(
                    "Coderpad Result 1",
                    ProfileExportDTO::coderpadResult1));

            cols.add(new ExcelExportService.ColumnDef<>(
                    "Coderpad Result 2",
                    ProfileExportDTO::coderpadResult2));

            cols.add(new ExcelExportService.ColumnDef<>(
                    "Karat Ready Status",
                    ProfileExportDTO::karatReadyStatus));

            cols.add(new ExcelExportService.ColumnDef<>(
                    "Karat Test Date",
                    ProfileExportDTO::karatTestDate));
            cols.add(new ExcelExportService.ColumnDef<>(
                    "Karat Test Result",
                    ProfileExportDTO::karatTestResult));
            cols.add(new ExcelExportService.ColumnDef<>(
                    "Karat Ageing",
                    ProfileExportDTO::karatAgeing));

            cols.add(new ExcelExportService.ColumnDef<>("Summary",
                    ProfileExportDTO::summary));

            return cols;
        }

        // ================= DEFAULT (ALL OTHER ROLES) =================

        return List.of(

                new ExcelExportService.ColumnDef<>("Candidate Name",
                        ProfileExportDTO::candidateName),

                new ExcelExportService.ColumnDef<>("Employee ID",
                        ProfileExportDTO::empId),

                new ExcelExportService.ColumnDef<>("Active",
                        p -> p.isActive() != null
                                ? (p.isActive() ? "Yes" : "No")
                                : ""),

                new ExcelExportService.ColumnDef<>("Experience",
                        ProfileExportDTO::experience),

                new ExcelExportService.ColumnDef<>("Skill Cluster",
                        ProfileExportDTO::skillCluster),

                new ExcelExportService.ColumnDef<>("Location",
                        ProfileExportDTO::location),

                new ExcelExportService.ColumnDef<>("HBU",
                        ProfileExportDTO::hbu),

                new ExcelExportService.ColumnDef<>("External/Internal",
                        ProfileExportDTO::externalInternal),

                new ExcelExportService.ColumnDef<>("Country",
                        ProfileExportDTO::country),

                new ExcelExportService.ColumnDef<>("Profile Status",
                        ProfileExportDTO::profileStatus),

                new ExcelExportService.ColumnDef<>("Origin",
                        ProfileExportDTO::origin),

                new ExcelExportService.ColumnDef<>("Karat Status",
                        ProfileExportDTO::karatStatus),

                new ExcelExportService.ColumnDef<>("Source",
                        ProfileExportDTO::source),

                new ExcelExportService.ColumnDef<>("Overall Status",
                        ProfileExportDTO::overallStatusRdg),

                new ExcelExportService.ColumnDef<>("Summary",
                        ProfileExportDTO::summary),


                new ExcelExportService.ColumnDef<>("Karat Readiness",
                        ProfileExportDTO::karatReadiness),

                new ExcelExportService.ColumnDef<>("LOB Shared",
                        ProfileExportDTO::lobShared),

                new ExcelExportService.ColumnDef<>("Practice",
                        ProfileExportDTO::practice),

                new ExcelExportService.ColumnDef<>("Band",
                        ProfileExportDTO::band),

                new ExcelExportService.ColumnDef<>("Ageing",
                        ProfileExportDTO::ageing),

                new ExcelExportService.ColumnDef<>("Ageing Range",
                        ProfileExportDTO::ageingRange),

                new ExcelExportService.ColumnDef<>("Codes",
                        ProfileExportDTO::codes),

                new ExcelExportService.ColumnDef<>("Code Type",
                        ProfileExportDTO::codeType),

                new ExcelExportService.ColumnDef<>("Min Billing Rate",
                        ProfileExportDTO::minBillingRate),

                new ExcelExportService.ColumnDef<>("Project Code",
                        ProfileExportDTO::projectCode),

                new ExcelExportService.ColumnDef<>("Date Of Submission",
                        ProfileExportDTO::dateOfSubmission),

                new ExcelExportService.ColumnDef<>("Week Of",
                        ProfileExportDTO::weekOf),

                new ExcelExportService.ColumnDef<>("Account Received On",
                        ProfileExportDTO::accountReceivedOn),

                new ExcelExportService.ColumnDef<>("Status Date",
                        ProfileExportDTO::statusDate),

                new ExcelExportService.ColumnDef<>("L1 Interview Date",
                        ProfileExportDTO::l1InterviewDate),

                new ExcelExportService.ColumnDef<>("Current Location",
                        ProfileExportDTO::currentLocation),

                new ExcelExportService.ColumnDef<>("Official NP",
                        ProfileExportDTO::officialNP),

                new ExcelExportService.ColumnDef<>("Negotiable NP/LWD",
                        ProfileExportDTO::negotiableNpLwd),

                new ExcelExportService.ColumnDef<>("Recruiter",
                        ProfileExportDTO::recruiter),

                new ExcelExportService.ColumnDef<>("Primary Skills",
                        ProfileExportDTO::primarySkills),

                new ExcelExportService.ColumnDef<>("Secondary Skills",
                        ProfileExportDTO::secondarySkills),

                new ExcelExportService.ColumnDef<>("Persona",
                        ProfileExportDTO::persona),

                new ExcelExportService.ColumnDef<>("Mentor Buddy",
                        ProfileExportDTO::mentorBuddy),

                new ExcelExportService.ColumnDef<>("Prep Status",
                        ProfileExportDTO::prepStatus),

                new ExcelExportService.ColumnDef<>("Start Date",
                        ProfileExportDTO::startDate),

                new ExcelExportService.ColumnDef<>("Planned End Date",
                        ProfileExportDTO::plannedEndDate),

                new ExcelExportService.ColumnDef<>("Coderpad Result 1",
                        ProfileExportDTO::coderpadResult1),

                new ExcelExportService.ColumnDef<>("Coderpad Result 2",
                        ProfileExportDTO::coderpadResult2),

                new ExcelExportService.ColumnDef<>("Karat Ready Status",
                        ProfileExportDTO::karatReadyStatus),


                new ExcelExportService.ColumnDef<>("Karat Test Date",
                        ProfileExportDTO::karatTestDate),

                new ExcelExportService.ColumnDef<>("Karat Test Result",
                        ProfileExportDTO::karatTestResult),

                new ExcelExportService.ColumnDef<>("Karat Ageing",
                        ProfileExportDTO::karatAgeing),

                new ExcelExportService.ColumnDef<>("Created At",
                        ProfileExportDTO::createdAt),

                new ExcelExportService.ColumnDef<>("Created By",
                        ProfileExportDTO::createdByUserId),

                new ExcelExportService.ColumnDef<>("Updated At",
                        ProfileExportDTO::updatedAt),

                new ExcelExportService.ColumnDef<>("Updated By",
                        ProfileExportDTO::updatedByUserId)
        );
    }

    private boolean hasRole(Set<String> roles, String role) {
        return roles.contains(role);
    }

    private boolean isHbuUser(Set<String> roles) {
        return roles.stream()
                .anyMatch(r -> r.startsWith("HBU-"));
    }
}
