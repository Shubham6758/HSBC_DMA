package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.RevenueMarginTmRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.RevenueMarginTmService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RevenueMarginTmImporter extends BaseExcelImporter {

    @Autowired
    private RevenueMarginTmService revenueMarginTmService;
    private long rowsRead;
    private long rowsInserted;
    public void importData(
            String filePath,
            String sheetName)
            throws Exception {

        Workbook workbook =
                openWorkbook(filePath);

        Sheet sheet =
                getSheet(workbook, sheetName);
        List<RevenueMarginTmRecord> records = readAllRows(sheet);
        rowsRead = records.size();

        System.out.println(
                "Total Revenue Margin TM DTO Records Read = "
                        + records.size()
        );
        revenueMarginTmService.saveRecords(records);
        rowsInserted = revenueMarginTmService.getRecordCount();

        System.out.println(
                "Revenue Margin TM Database Count = "
                        + revenueMarginTmService.getRecordCount()
        );
        int rowCount =
                getRowCount(sheet);

        System.out.println(
                "Revenue Margin TM Rows Found = "
                        + rowCount
        );

        workbook.close();
    }
    private List<RevenueMarginTmRecord> readAllRows(
            Sheet sheet) {

        List<RevenueMarginTmRecord> records =
                new ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow = sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            RevenueMarginTmRecord record =
                    new RevenueMarginTmRecord();

            record.setEmpId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(0)
                    )
            );

            record.setPsid(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(1)
                    )
            );

            record.setName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(2)
                    )
            );

            record.setCtool(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(3)
                    )
            );

            records.add(record);
        }

        return records;
    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}