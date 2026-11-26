package com.coforge.hsbcdma.revenuemargin.service.importer;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class BaseExcelImporter {

    protected Workbook openWorkbook(String filePath) throws IOException {

        IOUtils.setByteArrayMaxOverride(
                200000000
        );

        FileInputStream fileInputStream =
                new FileInputStream(filePath);

        return new XSSFWorkbook(fileInputStream);

    }

    protected Sheet getSheet(
            Workbook workbook,
            String sheetName) {

        return workbook.getSheet(sheetName);

    }

    protected int getRowCount(
            Sheet sheet) {

        return sheet.getLastRowNum();

    }

    protected Row getHeaderRow(
            Sheet sheet) {

        return sheet.getRow(0);

    }

}
