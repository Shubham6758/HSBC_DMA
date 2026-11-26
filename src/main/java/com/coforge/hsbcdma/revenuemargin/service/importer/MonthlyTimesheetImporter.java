package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.MonthlyTimesheetRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.MonthlyTimesheetService;
import com.coforge.hsbcdma.revenuemargin.service.SchemaValidationService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonthlyTimesheetImporter
        extends BaseExcelImporter {

    @Autowired
    private MonthlyTimesheetService monthlyTimesheetService;
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
        monthlyTimesheetService.deleteAllRecords();
        readFirstTimesheetRow(sheet);
        SchemaValidationService schemaValidationService =
                new SchemaValidationService();

        schemaValidationService.printSchema(
                getHeaderRow(sheet)
        );

        java.util.List<MonthlyTimesheetRecord> records = readAllRows(sheet);
        rowsRead = records.size();
        monthlyTimesheetService.saveRecords(records);
        rowsInserted = monthlyTimesheetService.getRecordCount();
        int rowCount =
                getRowCount(sheet);
        workbook.close();
    }
    private void readFirstTimesheetRow(
            Sheet sheet) {

        Row firstDataRow =
                sheet.getRow(1);
    }
    private java.util.List<MonthlyTimesheetRecord> readAllRows(
            Sheet sheet) {

        java.util.List<MonthlyTimesheetRecord> records =
                new java.util.ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow =
                    sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            MonthlyTimesheetRecord record =
                    new MonthlyTimesheetRecord();

            record.setResourceId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(0)
                    )
            );

            record.setTimePeriod(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(3)
                    )
            );

            record.setUnitsConsumed(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(11)
                    )
            );

            record.setVendorRate(
                    ExcelCellUtil.getCellValueAsBigDecimal(
                            currentRow.getCell(16)
                    )
            );
            record.setResourceName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(1)
                    )
            );

            record.setTsStatus(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(2)
                    )
            );

            record.setMon(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(4)
                    )
            );

            record.setTue(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(5)
                    )
            );

            record.setWed(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(6)
                    )
            );

            record.setThu(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(7)
                    )
            );

            record.setFri(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(8)
                    )
            );

            record.setSat(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(9)
                    )
            );

            record.setSun(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(10)
                    )
            );

            record.setConsumptionMonth(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(12)
                    )
            );

            record.setVendorName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(13)
                    )
            );
            record.setContractStartDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(14)
                    )
            );

            record.setContractEndDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(15)
                    )
            );

            record.setCurrency(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(17)
                    )
            );

            record.setUnit(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(18)
                    )
            );

            record.setPoNumber(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(19)
                    )
            );

            record.setPoLineItemNumber(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(20)
                    )
            );

            record.setBaseLocation(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(21)
                    )
            );

            record.setLegalEntity(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(22)
                    )
            );

            record.setJobCategory(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(23)
                    )
            );

            record.setPricingModel(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(24)
                    )
            );

            record.setCostCenter(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(25)
                    )
            );

            record.setPremises(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(26)
                    )
            );

            record.setOperatingUnit(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(27)
                    )
            );
            record.setEffectiveStartDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(28)
                    )
            );

            record.setEffectiveEndDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(29)
                    )
            );

            record.setConsumptionType(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(30)
                    )
            );

            record.setCostActualsLcy(
                    ExcelCellUtil.getCellValueAsBigDecimal(
                            currentRow.getCell(31)
                    )
            );

            record.setExtendedServiceHourFteConsumed(
                    ExcelCellUtil.getCellValueAsInteger(
                            currentRow.getCell(32)
                    )
            );

            record.setExtendedServiceHourLcyAmount(
                    ExcelCellUtil.getCellValueAsBigDecimal(
                            currentRow.getCell(33)
                    )
            );

            record.setStaffType(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(34)
                    )
            );

            record.setEmpClass(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(35)
                    )
            );

            record.setBfLevel1(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(36)
                    )
            );

            record.setBfLevel2(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(37)
                    )
            );

            record.setBusinessFunction(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(38)
                    )
            );

            record.setBusinessSubFunction(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(39)
                    )
            );

            record.setDepartment(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(40)
                    )
            );

            record.setCountry(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(41)
                    )
            );

            record.setRegion(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(42)
                    )
            );

            record.setBillingMonth(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(43)
                    )
            );

            record.setEquivalentGcb(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(44)
                    )
            );

            record.setcToolId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(45)
                    )
            );

            record.setcToolFunctionalManager(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(46)
                    )
            );

            record.setMonth(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(47)
                    )
            );
            records.add(record);

        }
        return records;

    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}