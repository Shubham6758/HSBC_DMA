package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.M4RateRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.M4RateService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class M4RateImporter extends BaseExcelImporter {

    @Autowired
    private M4RateService m4RateService;
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
        m4RateService.deleteAllRecords();
        readFirstM4Row(sheet);

        List<M4RateRecord> records = readAllRows(sheet);
        rowsRead = records.size();
        m4RateService.saveRecords(records);
        rowsInserted = m4RateService.getRecordCount();
        int rowCount =
                getRowCount(sheet);
        workbook.close();
    }
    private void readFirstM4Row(
            Sheet sheet) {
        Row firstDataRow =
                sheet.getRow(1);
    }
    private List<M4RateRecord> readAllRows(
            Sheet sheet) {

        List<M4RateRecord> records =
                new ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow =
                    sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            M4RateRecord record =
                    new M4RateRecord();

            record.setRef(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(0)
                    )
            );

            record.setRoleRef(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(1)
                    )
            );

            record.setRoleTitle(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(2)
                    )
            );

            record.setMaxDayRateUsd(
                    ExcelCellUtil.getCellValueAsBigDecimal(
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