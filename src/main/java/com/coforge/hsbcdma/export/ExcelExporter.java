package com.coforge.hsbcdma.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ExcelExporter<T> {

    private final String sheetName;
    private final List<String> headers;
    private final List<Function<T, Object>> extractors;

    public ExcelExporter(String sheetName, List<String> headers, List<Function<T, Object>> extractors) {
        if (headers.size() != extractors.size()) {
            throw new IllegalArgumentException("Headers and extractors count must match.");
        }
        this.sheetName = sheetName;
        this.headers = headers;
        this.extractors = extractors;
    }

    public ByteArrayInputStream export(List<T> rows) {
        try (SXSSFWorkbook wb = new SXSSFWorkbook(200);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            wb.setCompressTempFiles(true);
            SXSSFSheet sheet = wb.createSheet(sheetName);
            sheet.trackAllColumnsForAutoSizing();

            // ── Header style ──────────────────────────────────────────────
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.CORNFLOWER_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // ── Header row ────────────────────────────────────────────────
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // ── Data rows ─────────────────────────────────────────────────
            int rowNum = 1;
            for (T item : rows) {
                Row row = sheet.createRow(rowNum++);
                for (int c = 0; c < extractors.size(); c++) {
                    Object val = safeGet(extractors.get(c), item);
                    writeCell(row.createCell(c), val);
                }
            }

            // ── Auto-size columns (disable for >50k rows) ─────────────────
            for (int c = 0; c < headers.size(); c++) {
                sheet.autoSizeColumn(c);
                // cap at 60 chars wide
                sheet.setColumnWidth(c, Math.min(sheet.getColumnWidth(c), 60 * 256));
            }

            // ── Freeze header row ─────────────────────────────────────────
            sheet.createFreezePane(0, 1);

            wb.write(out);
            wb.dispose(); // clean up temp files
            return new ByteArrayInputStream(out.toByteArray());

        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }

    private Object safeGet(Function<T, Object> fn, T item) {
        try { return fn.apply(item); } catch (Exception e) { return null; }
    }

    private void writeCell(Cell cell, Object value) {
        if (value == null)                    { cell.setBlank(); return; }
        if (value instanceof Number n)        { cell.setCellValue(n.doubleValue()); return; }
        if (value instanceof Boolean b)       { cell.setCellValue(b); return; }
        if (value instanceof Date d)          { cell.setCellValue(d); return; }
        if (value instanceof TemporalAccessor){ cell.setCellValue(value.toString()); return; }
        cell.setCellValue(Objects.toString(value, ""));
    }


    // ✅ NEW: add sheet to existing workbook
    public void writeSheet(Workbook workbook, List<T> rows) {

        Sheet sheet = workbook.createSheet(sheetName);

        int rowIndex = 0;
        Row headerRow = sheet.createRow(rowIndex++);
        for (int i = 0; i < headers.size(); i++) {
            headerRow.createCell(i).setCellValue(headers.get(i));
        }

        for (T rowObj : rows) {
            Row row = sheet.createRow(rowIndex++);
            for (int i = 0; i < extractors.size(); i++) {
                Object val = extractors.get(i).apply(rowObj);
                row.createCell(i).setCellValue(
                        val == null ? "" : val.toString()
                );
            }
        }
    }

}