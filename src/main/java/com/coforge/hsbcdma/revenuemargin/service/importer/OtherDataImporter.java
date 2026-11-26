package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.OtherDataRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.OtherDataService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OtherDataImporter extends BaseExcelImporter {
    @Autowired
    private OtherDataService otherDataService;
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
        otherDataService.deleteAllRecords();
        readFirstOtherDataRow(sheet);

        List<OtherDataRecord> records = readAllRows(sheet);
        rowsRead = records.size();
        otherDataService.saveRecords(records);
        rowsInserted = otherDataService.getRecordCount();
        int rowCount =
                getRowCount(sheet);

        System.out.println(
                "Other Data Rows Found = "
                        + rowCount
        );

        workbook.close();

    }
    private void readFirstOtherDataRow(Sheet sheet) {

        Row firstDataRow = sheet.getRow(1);

        System.out.println("Emp ID = " + firstDataRow.getCell(0));
        System.out.println("PSID = " + firstDataRow.getCell(1));
        System.out.println("Name = " + firstDataRow.getCell(2));
        System.out.println("Contract Start Date = " + firstDataRow.getCell(10));
        System.out.println("Contract End Date = " + firstDataRow.getCell(11));
    }
    private List<OtherDataRecord> readAllRows(Sheet sheet) {

        List<OtherDataRecord> records =
                new ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow = sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            OtherDataRecord record =
                    new OtherDataRecord();

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

            record.setCoforgeBand(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(3)
                    )
            );

            record.setLob(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(4)
                    )
            );

            record.setGbGi(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(5)
                    )
            );

            record.setBfLevel3(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(6)
                    )
            );

            record.setBfLevel4(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(7)
                    )
            );

            record.setBfLevel5(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(8)
                    )
            );

            record.setPricingModel(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(9)
                    )
            );

            record.setContractStartDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(10)
                    )
            );

            record.setContractEndDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(11)
                    )
            );

            records.add(record);
        }

        return records;
    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}