package com.coforge.hsbcdma.revenuemargin.controller;

import com.coforge.hsbcdma.revenuemargin.entity.RevenueMarginTmEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RevenueMarginTmRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.List;

@Controller
public class TmExportController {
    @Autowired
    private RevenueMarginTmRepository revenueMarginTmRepository;
    @GetMapping("/tm/export")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<byte[]> exportTm() throws IOException {
//    public String exportTm() throws IOException {
//tymeleafchanges
        List<RevenueMarginTmEntity> records = revenueMarginTmRepository.findAll();
        RevenueMarginTmEntity firstRecord = records.get(0);

        Workbook workbook = new XSSFWorkbook();
        DataFormat dataFormat = workbook.createDataFormat();

        CellStyle currencyStyle = workbook.createCellStyle();

        currencyStyle.setDataFormat(dataFormat.getFormat("$#,##0.00"));

        CellStyle percentStyle =
                workbook.createCellStyle();

        percentStyle.setDataFormat(
                dataFormat.getFormat(
                        "0.00\\%"
                )
        );
        CellStyle totalCurrencyStyle =
                workbook.createCellStyle();

        totalCurrencyStyle.cloneStyleFrom(
                currencyStyle
        );

        CellStyle totalPercentStyle =
                workbook.createCellStyle();

        totalPercentStyle.cloneStyleFrom(
                percentStyle
        );

        CellStyle totalLabelStyle =
                workbook.createCellStyle();

        Font boldFont =
                workbook.createFont();

        boldFont.setBold(true);

        totalLabelStyle.setFont(
                boldFont
        );

        totalCurrencyStyle.setFont(
                boldFont
        );

        totalPercentStyle.setFont(
                boldFont
        );
        Sheet sheet =
                workbook.createSheet(
                        "Revenue Margin TM"
                );
        sheet.createFreezePane(
                0,
                2
        );
        Row headerRow =
                sheet.createRow(0);

        String[] exportColumns = {
                "id",
                "emp_id",
                "psid",
                "name",
                "ctool",
                "coforge_band",
                "gb_gi",
                "bf_level_3",
                "bf_level_4",
                "pricing_model",
                "effective_start_date",
                "effective_end_date",
                "resource_status",
                "country",
                "fte_sub_con",
                "job_category_gcb_level",
                "ctool_rate",
                "m4_rate",
                "effective_rate",
                "apr26_rate",
                "apr26_revenue",
                "apr26_ctc",
                "apr26_margin",
                "may26_rate",
                "may26_revenue",
                "may26_ctc",
                "may26_margin",
                "jun26_rate",
                "jun26_revenue",
                "jun26_ctc",
                "jun26_margin",
                "q1_rev",
                "q1_cost",
                "q1_margin",
                "jul26_rate",
                "jul26_revenue",
                "jul26_ctc",
                "jul26_margin",
                "aug26_rate",
                "aug26_revenue",
                "aug26_ctc",
                "aug26_margin",
                "sep26_working_days",
                "sep26_holidays_leaves",
                "sep26_rate",
                "sep26_revenue",
                "sep26_ctc",
                "sep26_margin",
                "q2_rev",
                "q2_cost",
                "q2_margin",
                "oct26_working_days",
                "oct26_holidays_leaves",
                "oct26_rate",
                "oct26_revenue",
                "oct26_ctc",
                "oct26_margin",

                "nov26_working_days",
                "nov26_holidays_leaves",
                "nov26_rate",
                "nov26_revenue",
                "nov26_ctc",
                "nov26_margin",

                "dec26_working_days",
                "dec26_holidays_leaves",
                "dec26_rate",
                "dec26_revenue",
                "dec26_ctc",
                "dec26_margin",

                "q3_rev",
                "q3_cost",
                "q3_margin",

                "jan27_working_days",
                "jan27_holidays_leaves",
                "jan27_rate",
                "jan27_revenue",
                "jan27_ctc",
                "jan27_margin",

                "feb27_working_days",
                "feb27_holidays_leaves",
                "feb27_rate",
                "feb27_revenue",
                "feb27_ctc",
                "feb27_margin",

                "mar27_working_days",
                "mar27_holidays_leaves",
                "mar27_rate",
                "mar27_revenue",
                "mar27_ctc",
                "mar27_margin",

                "q4_rev",
                "q4_cost",
                "q4_margin",

                "fy2026_27_rev",
                "fy2026_27_cost",
                "fy2026_27_margin"
        };
        for (int i = 0; i < exportColumns.length; i++) {

            headerRow.createCell(i)
                    .setCellValue(
                            exportColumns[i]
                    );
        }
        Row totalsRow =
                sheet.createRow(1);
        totalsRow.createCell(0)
                .setCellValue("TOTALS");
        totalsRow.getCell(0)
                .setCellStyle(
                        totalLabelStyle
                );
        for (int i = 0; i < exportColumns.length; i++) {

            String columnName =
                    exportColumns[i];

            if (columnName.contains("revenue")
                    || columnName.contains("ctc")
                    || columnName.endsWith("_rev")
                    || columnName.endsWith("_cost")) {

                Cell totalCell =
                        totalsRow.createCell(i);

                String excelColumn =
                        CellReference
                                .convertNumToColString(i);

                totalCell.setCellFormula(
                        "SUM("
                                + excelColumn
                                + "3:"
                                + excelColumn
                                + "1056)"
                );

                totalCell.setCellStyle(
                        totalCurrencyStyle
                );
            }
        }
        for (int i = 0; i < exportColumns.length; i++) {

            String columnName =
                    exportColumns[i];

            if (columnName.contains("margin")) {

                Cell marginCell =
                        totalsRow.createCell(i);

                Cell revenueCell =
                        totalsRow.getCell(i - 2);

                Cell costCell =
                        totalsRow.getCell(i - 1);

                String revenueRef =
                        CellReference.convertNumToColString(i - 2)
                                + "2";

                String costRef =
                        CellReference.convertNumToColString(i - 1)
                                + "2";

                marginCell.setCellFormula(
                        "(("
                                + revenueRef
                                + "-"
                                + costRef
                                + ")/"
                                + revenueRef
                                + ")*100"
                );

                marginCell.setCellStyle(
                        totalPercentStyle
                );
            }
        }
        int rowNum = 2;

        for (RevenueMarginTmEntity record : records) {

            Row dataRow =
                    sheet.createRow(rowNum++);

            for (int i = 0; i < exportColumns.length; i++) {

                Object value =
                        getFieldValue(
                                record,
                                exportColumns[i]
                        );

                if (value != null) {

                    Cell cell =
                            dataRow.createCell(i);

                    if (value instanceof BigDecimal) {

                        cell.setCellValue(
                                ((BigDecimal) value)
                                        .doubleValue()
                        );

                        String columnName =
                                exportColumns[i];

                        if (columnName.contains("margin")) {

                            cell.setCellStyle(
                                    percentStyle
                            );

                        } else if (
                                columnName.contains("rate")
                                        || columnName.contains("revenue")
                                        || columnName.contains("ctc")
                                        || columnName.contains("cost")) {

                            cell.setCellStyle(
                                    currencyStyle
                            );
                        }

                    } else if (value instanceof Number) {

                        cell.setCellValue(
                                ((Number) value)
                                        .doubleValue()
                        );

                    } else {

                        cell.setCellValue(
                                value.toString()
                        );
                    }
                }
            }
        }
        for (int i = 0; i < exportColumns.length; i++) {

            sheet.autoSizeColumn(i);
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("RevenueMarginTM.xlsx")
                        .build()
        );

        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
//        Path downloadFile =
//                Paths.get(
//                        System.getProperty("user.home"),
//                        "Downloads",
//                        "RevenueMarginTM.xlsx"
//                );
//
//        try (FileOutputStream out =
//                     new FileOutputStream(
//                             downloadFile.toFile())) {
//            System.out.println("File Location = " + downloadFile.toAbsolutePath());
//            workbook.write(out);
//            System.out.println(
//                    "Excel File Created"
//            );
//        }
//        workbook.close();
        //tymeleafchanges
//        return ResponseEntity.ok("RevenueMarginTM.xlsx exported successfully");
//        return "generic-refresh";
        //tymeleafchanges
    }
    @GetMapping("/tm-revenue/export")
    //tymeleafchanges
    @ResponseBody
    public ResponseEntity<byte[]> exportTmRevenue() throws IOException {
//    public String exportTmRevenue() throws IOException {
            //tymeleafchanges
        List<RevenueMarginTmEntity> records =
                revenueMarginTmRepository.findAll();
        RevenueMarginTmEntity firstRecord =
                records.get(0);
        Workbook workbook =
                new XSSFWorkbook();
        DataFormat dataFormat =
                workbook.createDataFormat();
        CellStyle currencyStyle =
                workbook.createCellStyle();
        currencyStyle.setDataFormat(
                dataFormat.getFormat(
                        "$#,##0.00"
                )
        );
        CellStyle percentStyle =
                workbook.createCellStyle();

        percentStyle.setDataFormat(
                dataFormat.getFormat(
                        "0.00\\%"
                )
        );
        CellStyle totalCurrencyStyle =
                workbook.createCellStyle();

        totalCurrencyStyle.cloneStyleFrom(
                currencyStyle
        );

        CellStyle totalPercentStyle =
                workbook.createCellStyle();

        totalPercentStyle.cloneStyleFrom(
                percentStyle
        );

        CellStyle totalLabelStyle =
                workbook.createCellStyle();

        Font boldFont =
                workbook.createFont();

        boldFont.setBold(true);

        totalLabelStyle.setFont(
                boldFont
        );
        totalCurrencyStyle.setFont(
                boldFont
        );
        totalPercentStyle.setFont(
                boldFont
        );
        Sheet sheet =
                workbook.createSheet(
                        "Revenue TM"
                );
        sheet.createFreezePane(
                0,
                2
        );
        Row headerRow =
                sheet.createRow(0);

        String[] exportColumns = {
                "id",
                "emp_id",
                "psid",
                "name",
                "ctool",
                "coforge_band",
                "gb_gi",
                "bf_level_3",
                "bf_level_4",
                "pricing_model",
                "effective_start_date",
                "effective_end_date",
                "resource_status",
                "country",
                "fte_sub_con",
                "job_category_gcb_level",
                "ctool_rate",
                "m4_rate",
                "effective_rate",
                "apr26_rate",
                "apr26_revenue",
                "may26_rate",
                "may26_revenue",
                "jun26_rate",
                "jun26_revenue",
                "q1_rev",
                "jul26_rate",
                "jul26_revenue",
                "aug26_rate",
                "aug26_revenue",
                "sep26_rate",
                "sep26_revenue",
                "q2_rev",
                "oct26_working_days",
                "oct26_holidays_leaves",
                "oct26_rate",
                "oct26_revenue",

                "nov26_working_days",
                "nov26_holidays_leaves",
                "nov26_rate",
                "nov26_revenue",

                "dec26_working_days",
                "dec26_holidays_leaves",
                "dec26_rate",
                "dec26_revenue",

                "q3_rev",

                "jan27_working_days",
                "jan27_holidays_leaves",
                "jan27_rate",
                "jan27_revenue",

                "feb27_working_days",
                "feb27_holidays_leaves",
                "feb27_rate",
                "feb27_revenue",

                "mar27_working_days",
                "mar27_holidays_leaves",
                "mar27_rate",
                "mar27_revenue",

                "q4_rev",

                "fy2026_27_rev",
        };
        for (int i = 0; i < exportColumns.length; i++) {

            headerRow.createCell(i)
                    .setCellValue(
                            exportColumns[i]
                    );
        }
        Row totalsRow =
                sheet.createRow(1);
        totalsRow.createCell(0)
                .setCellValue("TOTALS");
        totalsRow.getCell(0)
                .setCellStyle(
                        totalLabelStyle
                );
        for (int i = 0; i < exportColumns.length; i++) {

            String columnName =
                    exportColumns[i];

            if (columnName.contains("revenue")
                    || columnName.endsWith("_rev")) {

                Cell totalCell =
                        totalsRow.createCell(i);

                String excelColumn =
                        CellReference
                                .convertNumToColString(i);

                totalCell.setCellFormula(
                        "SUM("
                                + excelColumn
                                + "3:"
                                + excelColumn
                                + "1056)"
                );

                totalCell.setCellStyle(
                        totalCurrencyStyle
                );
            }
        }

        int rowNum = 2;

        for (RevenueMarginTmEntity record : records) {

            Row dataRow =
                    sheet.createRow(rowNum++);

            for (int i = 0; i < exportColumns.length; i++) {

                Object value =
                        getFieldValue(
                                record,
                                exportColumns[i]
                        );

                if (value != null) {

                    Cell cell =
                            dataRow.createCell(i);

                    if (value instanceof BigDecimal) {

                        cell.setCellValue(
                                ((BigDecimal) value)
                                        .doubleValue()
                        );

                        String columnName =
                                exportColumns[i];

                        if (
                                columnName.contains("rate")
                                        || columnName.contains("revenue")) {

                            cell.setCellStyle(
                                    currencyStyle
                            );
                        }

                    } else if (value instanceof Number) {

                        cell.setCellValue(
                                ((Number) value)
                                        .doubleValue()
                        );

                    } else {

                        cell.setCellValue(
                                value.toString()
                        );
                    }
                }
            }
        }

        for (int i = 0; i < exportColumns.length; i++) {

            sheet.autoSizeColumn(i);
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        workbook.write(outputStream);
        workbook.close();

        HttpHeaders headers =
                new HttpHeaders();

        headers.setContentType(
                MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                )
        );

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("RevenueTM.xlsx")
                        .build()
        );

        headers.setCacheControl(
                "no-cache, no-store, must-revalidate"
        );
        headers.setPragma("no-cache");
        headers.setExpires(0);

        return ResponseEntity.ok()
                .headers(headers)
                .body(outputStream.toByteArray());
//        Path downloadFile =
//                Paths.get(System.getProperty("user.home"),"Downloads","RevenueTM.xlsx");
//
//        try (FileOutputStream out = new FileOutputStream(downloadFile.toFile())) {
//            System.out.println("File Location = " + downloadFile.toAbsolutePath());
//            workbook.write(out);}
//        workbook.close();
        //tymeleafchanges
//        return ResponseEntity.ok("RevenueTM.xlsx exported successfully");
//        return "generic-refresh";
        //tymeleafchanges
    }
    @GetMapping("/api/tm/data")
    @ResponseBody
    public List<RevenueMarginTmEntity> getTmData() {

        return revenueMarginTmRepository.findAll();
    }
    private String toGetterName(
            String columnName) {

        StringBuilder getter =
                new StringBuilder("get");

        boolean upperCaseNext = true;

        for (char ch : columnName.toCharArray()) {

            if (ch == '_') {
                upperCaseNext = true;
                continue;
            }

            if (upperCaseNext) {
                getter.append(
                        Character.toUpperCase(ch)
                );
                upperCaseNext = false;
            } else {
                getter.append(ch);
            }
        }

        return getter.toString();
    }
    private Object getFieldValue(
            RevenueMarginTmEntity record,
            String columnName) {

        try {

            String getterName =
                    toGetterName(columnName);

            Method method =
                    RevenueMarginTmEntity.class
                            .getMethod(getterName);

            return method.invoke(record);

        } catch (Exception e) {

            return null;
        }
    }
    private boolean isSummationColumn(
            String columnName) {

        return columnName.contains("revenue")
                || columnName.contains("_rev");
    }
}