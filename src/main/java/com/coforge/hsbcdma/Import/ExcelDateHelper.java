package com.coforge.hsbcdma.Import;


import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

@Slf4j
public final class ExcelDateHelper {

    private ExcelDateHelper() {}

    private static final List<DateTimeFormatter> FORMATTERS = List.of(
            // ✅ ISO / recommended
            DateTimeFormatter.ISO_LOCAL_DATE,              // 2024-01-15
            DateTimeFormatter.ISO_DATE_TIME,               // 2024-01-15T10:30:00

            // ✅ Slash formats
            DateTimeFormatter.ofPattern("dd/MM/yyyy"),
            DateTimeFormatter.ofPattern("d/M/yyyy"),
            DateTimeFormatter.ofPattern("MM/dd/yyyy"),
            DateTimeFormatter.ofPattern("yyyy/MM/dd"),

            // ✅ Dash formats
            DateTimeFormatter.ofPattern("dd-MM-yyyy"),
            DateTimeFormatter.ofPattern("d-M-yyyy"),

            // ✅ Dot formats
            DateTimeFormatter.ofPattern("dd.MM.yyyy"),

            // ✅ Text months
            DateTimeFormatter.ofPattern("dd-MMM-yyyy"),    // 15-Jan-2024
            DateTimeFormatter.ofPattern("dd MMM yyyy"),    // 15 Jan 2024
            DateTimeFormatter.ofPattern("d MMM yyyy")      // 5 Jan 2024
    );

    /** Reads date safely from Excel cell */
    public static LocalDate read(Cell cell) {
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC &&
                    DateUtil.isCellDateFormatted(cell)) {

                Date d = cell.getDateCellValue();
                return d.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
            }

            if (cell.getCellType() == CellType.STRING) {
                return parse(cell.getStringCellValue());
            }

            if (cell.getCellType() == CellType.FORMULA) {
                if (cell.getCachedFormulaResultType() == CellType.NUMERIC) {
                    Date d = DateUtil.getJavaDate(cell.getNumericCellValue());
                    return d.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                } else if (cell.getCachedFormulaResultType() == CellType.STRING) {
                    return parse(cell.getStringCellValue());
                }
            }
        } catch (Exception ex) {
            log.warn("Excel date parsing failed: {}", ex.getMessage());
        }
        return null;
    }

    /** Parse string date using multiple safe formats */
    private static LocalDate parse(String raw) {
        if (raw == null) return null;

        String value = raw.trim();
        if (value.isEmpty()) return null;

        // remove time if user pasted ISO date‑time
        value = value.replaceAll("T.*$", "");

        for (DateTimeFormatter f : FORMATTERS) {
            try {
                return LocalDate.parse(value, f);
            } catch (DateTimeParseException ignored) {}
        }

        log.warn("Unrecognized date format: '{}'", raw);
        return null; // ❗do not fail row
    }
}
