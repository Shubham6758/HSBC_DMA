package com.coforge.hsbcdma.revenuemargin.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ExcelCellUtil {

    public static String getCellValueAsString(
            Cell cell) {

        if (cell == null) {
            return "";
        }

        if (cell.getCellType() ==
                CellType.NUMERIC) {

            BigDecimal value =
                    BigDecimal.valueOf(
                            cell.getNumericCellValue()
                    );

            return value.stripTrailingZeros()
                    .toPlainString();
        }

        return cell.toString().trim();

    }
    public static BigDecimal getCellValueAsBigDecimal(
            Cell cell) {

        if (cell == null) {
            return BigDecimal.ZERO;
        }

        if (cell.getCellType() ==
                CellType.NUMERIC) {

            return BigDecimal.valueOf(
                    cell.getNumericCellValue()
            );
        }

        try {

            return new BigDecimal(
                    cell.toString().trim()
            );

        } catch (Exception ex) {

            return BigDecimal.ZERO;

        }

    }
    public static LocalDate getCellValueAsDate(
            Cell cell) {

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() ==
                CellType.NUMERIC) {

            Date date =
                    cell.getDateCellValue();

            return date.toInstant()
                    .atZone(
                            ZoneId.systemDefault()
                    )
                    .toLocalDate();
        }

        return null;

    }
    public static LocalDateTime getCellValueAsDateTime(
            Cell cell) {

        if (cell == null) {
            return null;
        }

        if (cell.getCellType() == CellType.NUMERIC) {

            Date date =
                    cell.getDateCellValue();

            return date.toInstant()
                    .atZone(
                            ZoneId.systemDefault()
                    )
                    .toLocalDateTime();
        }

        return null;
    }
    public static Integer getCellValueAsInteger(
            Cell cell) {

        if (cell == null) {
            return 0;
        }

        if (cell.getCellType() ==
                CellType.NUMERIC) {

            return (int) cell.getNumericCellValue();
        }

        try {

            return Integer.parseInt(
                    cell.toString().trim()
            );

        } catch (Exception ex) {

            return 0;

        }

    }
}

