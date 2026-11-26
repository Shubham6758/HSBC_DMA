package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.HsbcArcRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.HsbcArcService;
import com.coforge.hsbcdma.revenuemargin.service.SchemaValidationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HsbcArcImporter extends BaseExcelImporter {

    @Autowired
    private HsbcArcService hsbcArcService;
    private long rowsRead;
    private long rowsInserted;
    public void importData(
            String filePath,
            String sheetName)
            throws Exception {

        Workbook workbook =
                openWorkbook(filePath);

        workbook.sheetIterator().forEachRemaining(
                sheet ->
                        System.out.println(
                                "Sheet = "
                                        + sheet.getSheetName()
                        )
        );
        Sheet sheet =
                getSheet(workbook, sheetName);
        hsbcArcService.deleteAllRecords();
        readFirstArcRow(sheet);

        SchemaValidationService schemaValidationService =
                new SchemaValidationService();

        schemaValidationService.printSchema(
                getHeaderRow(sheet)
        );

        List<HsbcArcRecord> records = readAllRows(sheet);
        rowsRead = records.size();
        hsbcArcService.saveRecords(records);
        rowsInserted = hsbcArcService.getRecordCount();
        int rowCount =
                getRowCount(sheet);
        workbook.close();
    }
    private void readFirstArcRow(
            Sheet sheet) {

        Row firstDataRow =
                sheet.getRow(1);
        HsbcArcRecord record =
                new HsbcArcRecord();

        record.setPernr(
                ExcelCellUtil.getCellValueAsString(
                        firstDataRow.getCell(0)
                )
        );

        record.setCostToCo(
                ExcelCellUtil.getCellValueAsBigDecimal(
                        firstDataRow.getCell(12)
                )
        );

        record.setCtcInUsd(
                ExcelCellUtil.getCellValueAsBigDecimal(
                        firstDataRow.getCell(18)
                )
        );
    }
    private List<HsbcArcRecord> readAllRows(
            Sheet sheet) {

        List<HsbcArcRecord> records =
                new ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow =
                    sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            HsbcArcRecord record =
                    new HsbcArcRecord();

            record.setPernr(ExcelCellUtil.getCellValueAsString(currentRow.getCell(0)));
            record.setZmonth(ExcelCellUtil.getCellValueAsDate(currentRow.getCell(1)));
            record.setRecType(ExcelCellUtil.getCellValueAsString(currentRow.getCell(2)));
            record.setCountry(ExcelCellUtil.getCellValueAsString(currentRow.getCell(3)));
            record.setCountryKey(ExcelCellUtil.getCellValueAsString(currentRow.getCell(4)));
            record.setCustomerName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(5)));
            record.setCustomerGrp(ExcelCellUtil.getCellValueAsString(currentRow.getCell(6)));
            record.setIbu(ExcelCellUtil.getCellValueAsString(currentRow.getCell(7)));
            record.setSubIbu(ExcelCellUtil.getCellValueAsString(currentRow.getCell(8)));
            record.setAnsvh(ExcelCellUtil.getCellValueAsString(currentRow.getCell(9)));
            record.setBand(ExcelCellUtil.getCellValueAsString(currentRow.getCell(10)));
            record.setConversionRate(ExcelCellUtil.getCellValueAsBigDecimal(currentRow.getCell(11)));
            record.setCostToCo(ExcelCellUtil.getCellValueAsBigDecimal(currentRow.getCell(12)));
            record.setCurrency(ExcelCellUtil.getCellValueAsString(currentRow.getCell(13)));
            record.setLocation(ExcelCellUtil.getCellValueAsString(currentRow.getCell(14)));
            record.setDeliveryL1(ExcelCellUtil.getCellValueAsString(currentRow.getCell(15)));
            record.setDeliveryL2(ExcelCellUtil.getCellValueAsString(currentRow.getCell(16)));
            record.setEmpHorizontal(ExcelCellUtil.getCellValueAsString(currentRow.getCell(17)));
            record.setCtcInUsd(ExcelCellUtil.getCellValueAsBigDecimal(currentRow.getCell(18)));
            record.setProjectType(ExcelCellUtil.getCellValueAsString(currentRow.getCell(19)));
            record.setBukrs(ExcelCellUtil.getCellValueAsString(currentRow.getCell(20)));
            record.setEmpGroupPrct(ExcelCellUtil.getCellValueAsString(currentRow.getCell(21)));
            record.setOrgeh(ExcelCellUtil.getCellValueAsString(currentRow.getCell(22)));
            record.setPosid(ExcelCellUtil.getCellValueAsString(currentRow.getCell(23)));
            record.setIsActive(ExcelCellUtil.getCellValueAsString(currentRow.getCell(24)));
            record.setMonthYear(ExcelCellUtil.getCellValueAsString(currentRow.getCell(25)));
            record.setCreatedOn(ExcelCellUtil.getCellValueAsDateTime(currentRow.getCell(26)));
            record.setModifiedOn(ExcelCellUtil.getCellValueAsDateTime(currentRow.getCell(27)));
            record.setDeliveryL0(ExcelCellUtil.getCellValueAsString(currentRow.getCell(28)));
            record.setWbsElement(ExcelCellUtil.getCellValueAsString(currentRow.getCell(29)));

            records.add(record);

        }
        return records;
    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}