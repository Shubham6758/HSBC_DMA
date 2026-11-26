package com.coforge.hsbcdma.resource.Import_Resource_Sheet;

import com.coforge.hsbcdma.Import.ExcelColumnHelper;
import com.coforge.hsbcdma.Import.ExcelDateHelper;
import com.coforge.hsbcdma.resource.Entity_Resource_Sheet.ResourceData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class ResourceSheetExcelImportHelper {

    public static final String EXCEL_TYPE =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Data
    @AllArgsConstructor
    public static class ParsedRow {
        private int rowIndex;
        private ResourceData data;
    }

    public boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();

        return EXCEL_TYPE.equals(contentType) ||
                (fileName != null && fileName.toLowerCase().endsWith(".xlsx"))
                || fileName.toLowerCase().endsWith(".csv");
    }

    public List<ParsedRow> parseResources(InputStream in, String fileName) {

        List<ParsedRow> result = new ArrayList<>();

        try {
            if (fileName != null && fileName.toLowerCase().endsWith(".csv")) {
                return parseCSV(in);
            }

            // ✅ Excel (.xlsx)
            try (Workbook workbook = new XSSFWorkbook(in)) {
                Sheet sheet = workbook.getSheetAt(0);

                Map<String, Integer> headerMap = ExcelColumnHelper.getHeaderIndexMap(sheet);

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue;
                    if (isRowEmpty(row)) continue;

                    ResourceData r = new ResourceData();

                    r.setPsid(getString(ExcelColumnHelper.getCell(row, headerMap, "PSID")));
                    r.setResourceName(getString(ExcelColumnHelper.getCell(row, headerMap, "Resource Name")));
                    r.setLob(getString(ExcelColumnHelper.getCell(row, headerMap, "LOB")));
                    r.setNewLob(getString(ExcelColumnHelper.getCell(row, headerMap, "New LOB")));
                    r.setNewLobForOthers(getString(ExcelColumnHelper.getCell(row, headerMap, "New LOB For Others")));
                    r.setBusinessFunction(getString(ExcelColumnHelper.getCell(row, headerMap, "Business Function")));
                    r.setSubBusinessFunction(getString(ExcelColumnHelper.getCell(row, headerMap, "Sub Business Function")));
                    r.setDepartment(getString(ExcelColumnHelper.getCell(row, headerMap, "Department")));
                    r.setFunctionalMgrHsbc(getString(ExcelColumnHelper.getCell(row, headerMap, "FunctionalMgr - HSBC")));
                    r.setPrimarySkill(getString(ExcelColumnHelper.getCell(row, headerMap, "Primary Skill")));
                    r.setExperience(getBigDecimal(ExcelColumnHelper.getCell(row, headerMap, "Experience")));
                    r.setCtool(getString(ExcelColumnHelper.getCell(row, headerMap, "cTool")));
                    r.setCtoolLocation(getString(ExcelColumnHelper.getCell(row, headerMap, "cTool Location")));
                    r.setPricingModelHsbc(getString(ExcelColumnHelper.getCell(row, headerMap, "Pricing Model - HSBC")));
                    r.setJobCategory(getString(ExcelColumnHelper.getCell(row, headerMap, "Job Category")));
                    r.setGcbLevel(getString(ExcelColumnHelper.getCell(row, headerMap, "GCB Level")));
                    r.setCurrency(getString(ExcelColumnHelper.getCell(row, headerMap, "Currency")));
                    r.setRate(getBigDecimal(ExcelColumnHelper.getCell(row, headerMap, "Rate")));
                    r.setEffectiveRate(getBigDecimal(ExcelColumnHelper.getCell(row, headerMap, "Effective Rate")));
                    r.setFixedCapacityRate(getBigDecimal(ExcelColumnHelper.getCell(row, headerMap, "Fixed Capacity Rate")));
                    r.setEffectiveStartDate(ExcelDateHelper.read(ExcelColumnHelper.getCell(row, headerMap, "Effective Start Date")));
                    r.setEffectiveEndDate(ExcelDateHelper.read(ExcelColumnHelper.getCell(row, headerMap, "Effective End Date")));
                    r.setEmpId(getString(ExcelColumnHelper.getCell(row, headerMap, "Emp ID")));
                    r.setResourceStatus(getString(ExcelColumnHelper.getCell(row, headerMap, "Resource Status")));
                    r.setOnshoreOffshore(getString(ExcelColumnHelper.getCell(row, headerMap, "Onshore/Offshore")));
                    r.setPricingModelCoforge(getString(ExcelColumnHelper.getCell(row, headerMap, "Pricing Model - Coforge")));
                    r.setTentativeOnboardingDate(ExcelDateHelper.read(ExcelColumnHelper.getCell(row, headerMap, "Tentative Onboarding Date")));
                    r.setWbsCode(getString(ExcelColumnHelper.getCell(row, headerMap, "WBS Code")));
                    r.setCoforgeBand(getString(ExcelColumnHelper.getCell(row, headerMap, "Coforge Band")));
                    r.setHbu(getString(ExcelColumnHelper.getCell(row, headerMap, "HBU")));
                    r.setIpmsAllocation(getString(ExcelColumnHelper.getCell(row, headerMap, "IPMS Allocation")));
                    r.setGender(getString(ExcelColumnHelper.getCell(row, headerMap, "Gender")));
                    r.setCoforgeLocation(getString(ExcelColumnHelper.getCell(row, headerMap, "Coforge Location")));
                    r.setIpmsAllocationStartDate(ExcelDateHelper.read(ExcelColumnHelper.getCell(row, headerMap, "IPMS Allocation Start Date")));
                    r.setIpmsAllocationEndDate(ExcelDateHelper.read(ExcelColumnHelper.getCell(row, headerMap, "IPMS Allocation End Date")));
                    r.setConcatHeadcountNumber(getString(ExcelColumnHelper.getCell(row, headerMap, "Concatenate for Headcount Number")));
                    r.setConcatHeadcountNewLob(getString(ExcelColumnHelper.getCell(row, headerMap, "Concatenate for Headcount - New LOB")));
                    r.setMonthOfRelease(getString(ExcelColumnHelper.getCell(row, headerMap, "Month of Release")));
                    r.setDateOfExit(ExcelDateHelper.read(ExcelColumnHelper.getCell(row, headerMap, "Date of Exit")));
                    r.setExitStatus(getString(ExcelColumnHelper.getCell(row, headerMap, "Exit Status")));
                    r.setReasonForExit(getString(ExcelColumnHelper.getCell(row, headerMap, "Reason for Exit")));
                    r.setReplacementRequired(getString(ExcelColumnHelper.getCell(row, headerMap, "Replacement Required")));
                    r.setRemarks(getString(ExcelColumnHelper.getCell(row, headerMap, "Remarks - client discussion outcome; Specify name of client talked to")));
                    r.setReplacementGiven(getString(ExcelColumnHelper.getCell(row, headerMap, "Replacement Given (Y/N )")));
                    r.setReplacementId(getString(ExcelColumnHelper.getCell(row, headerMap, "Replacement Id")));
                    r.setReplacementName(getString(ExcelColumnHelper.getCell(row, headerMap, "Replacement Name")));
                    r.setIsActive(true);

                    result.add(new ParsedRow(row.getRowNum() + 1, r));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("File parse failed: " + e.getMessage(), e);
        }

        return result;
    }

    private List<ParsedRow> parseCSV(InputStream in) {

        List<ParsedRow> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new RuntimeException("Empty CSV file");
            }

            String[] headers = headerLine.split(",");

            Map<String, Integer> headerMap = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerMap.put(headers[i].trim().toLowerCase(), i);
            }

            String line;
            int rowIndex = 1;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] values = line.split(",", -1); // keep empty trailing columns

                ResourceData r = new ResourceData();

                // Ignore "Key" intentionally
                r.setPsid(getCsvValue(values, headerMap, "psid"));
                r.setResourceName(getCsvValue(values, headerMap, "resource name"));
                r.setLob(getCsvValue(values, headerMap, "lob"));
                r.setNewLob(getCsvValue(values, headerMap, "new lob"));
                r.setNewLobForOthers(getCsvValue(values, headerMap, "new lob for others"));
                r.setBusinessFunction(getCsvValue(values, headerMap, "business function"));
                r.setSubBusinessFunction(getCsvValue(values, headerMap, "sub business function"));
                r.setDepartment(getCsvValue(values, headerMap, "department"));
                r.setFunctionalMgrHsbc(getCsvValue(values, headerMap, "functionalmgr - hsbc"));
                r.setPrimarySkill(getCsvValue(values, headerMap, "primary skill"));

                r.setExperience(getCsvBigDecimal(values, headerMap, "experience"));
                r.setCtool(getCsvValue(values, headerMap, "ctool"));
                r.setCtoolLocation(getCsvValue(values, headerMap, "ctool location"));
                r.setPricingModelHsbc(getCsvValue(values, headerMap, "pricing model - hsbc"));
                r.setJobCategory(getCsvValue(values, headerMap, "job category"));
                r.setGcbLevel(getCsvValue(values, headerMap, "gcb level"));
                r.setCurrency(getCsvValue(values, headerMap, "currency"));
                r.setRate(getCsvBigDecimal(values, headerMap, "rate"));
                r.setEffectiveRate(getCsvBigDecimal(values, headerMap, "effective rate"));
                r.setFixedCapacityRate(getCsvBigDecimal(values, headerMap, "fixed capacity rate"));

                r.setEffectiveStartDate(getCsvDate(values, headerMap, "effective start date"));
                r.setEffectiveEndDate(getCsvDate(values, headerMap, "effective end date"));

                r.setEmpId(getCsvValue(values, headerMap, "emp id"));
                r.setResourceStatus(getCsvValue(values, headerMap, "resource status"));
                r.setOnshoreOffshore(getCsvValue(values, headerMap, "onshore/offshore"));
                r.setPricingModelCoforge(getCsvValue(values, headerMap, "pricing model - coforge"));
                r.setTentativeOnboardingDate(getCsvDate(values, headerMap, "tentative onboarding date"));
                r.setWbsCode(getCsvValue(values, headerMap, "wbs code"));
                r.setCoforgeBand(getCsvValue(values, headerMap, "coforge band"));
                r.setHbu(getCsvValue(values, headerMap, "hbu"));
                r.setIpmsAllocation(getCsvValue(values, headerMap, "ipms allocation"));
                r.setGender(getCsvValue(values, headerMap, "gender"));
                r.setCoforgeLocation(getCsvValue(values, headerMap, "coforge location"));

                r.setIpmsAllocationStartDate(getCsvDate(values, headerMap, "ipms allocation start date"));
                r.setIpmsAllocationEndDate(getCsvDate(values, headerMap, "ipms allocation end date"));

                r.setConcatHeadcountNumber(getCsvValue(values, headerMap, "concatenate for headcount number"));
                r.setConcatHeadcountNewLob(getCsvValue(values, headerMap, "concatenate for headcount - new lob"));

                r.setMonthOfRelease(getCsvValue(values, headerMap, "month of release"));
                r.setDateOfExit(getCsvDate(values, headerMap, "date of exit"));
                r.setExitStatus(getCsvValue(values, headerMap, "exit status"));
                r.setReasonForExit(getCsvValue(values, headerMap, "reason for exit"));
                r.setReplacementRequired(getCsvValue(values, headerMap, "replacement required"));
                r.setRemarks(getCsvValue(values, headerMap, "remarks - client discussion outcome; specify name of client talked to"));
                r.setReplacementGiven(getCsvValue(values, headerMap, "replacement given (y/n )"));
                r.setReplacementId(getCsvValue(values, headerMap, "replacement id"));
                r.setReplacementName(getCsvValue(values, headerMap, "replacement name"));

                r.setIsActive(true);

                result.add(new ParsedRow(rowIndex++, r));
            }

        } catch (Exception e) {
            throw new RuntimeException("CSV parse failed: " + e.getMessage(), e);
        }

        return result;
    }

    private String getCsvValue(String[] values, Map<String, Integer> headerMap, String key) {

        Integer index = headerMap.get(key.toLowerCase());
        if (index == null || index >= values.length) {
            return null;
        }

        String value = values[index];
        if (value == null) return null;

        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private BigDecimal getCsvBigDecimal(String[] values, Map<String, Integer> headerMap, String key) {
        String value = getCsvValue(values, headerMap, key);
        if (value == null || value.isBlank()) return null;

        try {
            value = value.replaceAll(",", "").trim();
            return new BigDecimal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate getCsvDate(String[] values, Map<String, Integer> headerMap, String key) {
        String value = getCsvValue(values, headerMap, key);
        if (value == null || value.isBlank()) return null;

        try {
            // Reuse your supported formats via helper-like logic
            return parseDateFlexible(value);
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDate parseDateFlexible(String raw) {
        if (raw == null || raw.isBlank()) return null;

        List<java.time.format.DateTimeFormatter> formatters = List.of(
                java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("d-M-yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("d/M/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("dd-MMM-yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"),
                java.time.format.DateTimeFormatter.ofPattern("d MMM yyyy")
        );

        for (var formatter : formatters) {
            try {
                return LocalDate.parse(raw.trim(), formatter);
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    private String getString(Cell cell) {
        if (cell == null) return null;

        DataFormatter formatter = new DataFormatter();
        String value = formatter.formatCellValue(cell);

        if (value == null) return null;

        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private BigDecimal getBigDecimal(Cell cell) {
        String value = getString(cell);
        if (value == null || value.isBlank()) return null;

        try {
            value = value.replaceAll(",", "").trim();
            return new BigDecimal(value);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = new DataFormatter().formatCellValue(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
}