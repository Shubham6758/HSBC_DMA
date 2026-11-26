package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.HsbcCentralResourceTrackerRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.HsbcCentralResourceTrackerService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class HsbcCentralResourceTrackerImporter
        extends BaseExcelImporter {

    @Autowired
    private HsbcCentralResourceTrackerService
            hsbcCentralResourceTrackerService;
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
        hsbcCentralResourceTrackerService.deleteAllRecords();
        List<HsbcCentralResourceTrackerRecord> records = readAllRows(sheet);
        System.out.println("%^^%$%$#Records read from Excel#$$^%^% = " + records.size());
        rowsRead = records.size();
        System.out.println("&^$^%$^%&*^^%&Records going to DB = " + records.size());
        hsbcCentralResourceTrackerService.saveRecords(records);
        rowsInserted = hsbcCentralResourceTrackerService.getRecordCount();
        workbook.close();
    }
    private List<HsbcCentralResourceTrackerRecord>
    readAllRows(Sheet sheet) {

        List<HsbcCentralResourceTrackerRecord> records =
                new ArrayList<>();
        System.out.println(
                "Last Row Num = "
                        + sheet.getLastRowNum()
        );
        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow =
                    sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            HsbcCentralResourceTrackerRecord record =
                    new HsbcCentralResourceTrackerRecord();

            record.setKey(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(0)));

            record.setCoforgeId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(1)));

            record.setPsCode(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(2)));

            record.setEmployeeName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(3)));

            record.setLob(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(4)));

            record.setNewLob(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(5)));

            record.setCoforgeBand(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(6)));

            record.setHbu(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(7)));

            record.setCoforgeLocation(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(8)));

            record.setNewLobForOthers(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(9)));

            record.setBusinessFunction(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(10)));

            record.setSubBusinessFunction(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(11)));

            record.setDepartment(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(12)));

            record.setFunctionalMgrHsbc(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(13)));

            record.setPrimarySkill(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(14)));

            record.setExperience(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(15)));

            record.setCtool(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(16)));

            record.setCtoolLocation(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(17)));

            record.setPricingModelHsbc(ExcelCellUtil.getCellValueAsString(currentRow.getCell(18)));

            record.setJobCategory(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(19)));

            record.setGcbLevel(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(20)));

            record.setCurrency(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(21)));

            record.setRate(
                    ExcelCellUtil.getCellValueAsBigDecimal(
                            currentRow.getCell(22)));

            record.setEffectiveRate(
                    ExcelCellUtil.getCellValueAsBigDecimal(
                            currentRow.getCell(23)));

            record.setFixedCapacityRate(
                    ExcelCellUtil.getCellValueAsBigDecimal(
                            currentRow.getCell(24)));

            record.setEffectiveStartDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(25)));

            record.setEffectiveEndDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(26)));

            record.setResourceStatus(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(27)));

            record.setOnshoreOffshore(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(28)));

            record.setPricingModelCoforge(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(29)));

            record.setTentativeOnboardingDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(30)));

            record.setWbsCode(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(31)));

            record.setIpmsAllocation(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(32)));

            record.setGender(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(33)));

            record.setIpmsAllocationStartDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(34)));

            record.setIpmsAllocationEndDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(35)));

            record.setConcatenateForHeadcountNumber(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(36)));

            record.setConcatenateForHeadcountNewLob(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(37)));

            record.setMonthOfRelease(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(38)));

            record.setDateOfExit(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(39)));

            record.setExitStatus(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(40)));

            record.setReasonForExit(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(41)));

            record.setReplacementRequired(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(42)));

            record.setRemarks(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(43)));

            record.setReplacementId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(44)));

            record.setReplacementName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(45)));

            record.setSubConVendor(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(46)));

            record.setLoc(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(47)));

            record.setLobCheck(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(48)));

            record.setCoforgeEmailId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(49)));

            record.setHsbcEmailId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(50)));

            record.setSkillCluster(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(51)));

            record.setSubCluster(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(52)));

            records.add(record);
        }
        System.out.println(
                "%$^%^(*(*)_((*^%Physical Rows Read = "
                        + records.size()
        );
        return records;
    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}