package com.coforge.hsbcdma.export;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class DemandExportConfig {

    public List<ExcelExportService.ColumnDef<DemandExportDTO>> getColumns(Set<String> roles) {

        List<ExcelExportService.ColumnDef<DemandExportDTO>> columns = new ArrayList<>();

        // Common Columns
        columns.add(new ExcelExportService.ColumnDef<>(
                "Demand ID",
                DemandExportDTO::demandId));

        columns.add(new ExcelExportService.ColumnDef<>(
                "LOB",
                DemandExportDTO::lob));

        columns.add(new ExcelExportService.ColumnDef<>(
                "Status",
                DemandExportDTO::status));

        columns.add(new ExcelExportService.ColumnDef<>(
                "Skill Cluster",
                DemandExportDTO::skillCluster));
        columns.add(new ExcelExportService.ColumnDef<>(
                "Business Function",
                DemandExportDTO::businessFunction));
        columns.add(new ExcelExportService.ColumnDef<>(
                "Job Category",
                DemandExportDTO::jobCategory));
        columns.add(new ExcelExportService.ColumnDef<>(
                "Contract Type",
                DemandExportDTO::contractType));
        columns.add(new ExcelExportService.ColumnDef<>(
                "CTool ID",
                DemandExportDTO::ctoolId));

        // ADMIN + PMO
        if (roles.contains("ADMIN")
                || roles.contains("PMO")) {

            columns.add(new ExcelExportService.ColumnDef<>(
                    "RR",
                    DemandExportDTO::rrNumber));

            columns.add(new ExcelExportService.ColumnDef<>(
                    "PMO",
                    DemandExportDTO::pmo));
        }

        // ADMIN + PM + LEADERSHIP
        if (roles.contains("ADMIN")
                || roles.contains("PM")
                || roles.contains("LEADERSHIP")) {

            columns.add(new ExcelExportService.ColumnDef<>(
                    "Hiring Manager",
                    DemandExportDTO::hiringManager));

            columns.add(new ExcelExportService.ColumnDef<>(
                    "Delivery Manager",
                    DemandExportDTO::deliveryManager));
        }

        // ADMIN ONLY
        if (roles.contains("ADMIN")) {

            columns.add(new ExcelExportService.ColumnDef<>(
                    "Created By",
                    DemandExportDTO::createdByName));

            columns.add(new ExcelExportService.ColumnDef<>(
                    "Created At",
                    d -> d.createdAt() != null
                            ? d.createdAt().toLocalDate()
                            : null));
        }

        return columns;
    }
}
