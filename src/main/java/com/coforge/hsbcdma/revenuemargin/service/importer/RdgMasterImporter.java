package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.RdgMasterRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.RdgMasterService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RdgMasterImporter extends BaseExcelImporter {
    @Autowired
    private RdgMasterService rdgMasterService;
    private long rowsRead;
    private long rowsInserted;
    public void importData(
            String filePath,
            String sheetName)
            throws Exception {

        Workbook workbook = openWorkbook(filePath);

        Sheet sheet = getSheet(workbook, sheetName);
        rdgMasterService.deleteAllRecords();
        List<RdgMasterRecord> records = readAllRows(sheet);
        rowsRead = records.size();
        rdgMasterService.saveRecords(records);
        rowsInserted = rdgMasterService.getRecordCount();
        int rowCount = getRowCount(sheet);
        workbook.close();
    }
    private List<RdgMasterRecord> readAllRows(Sheet sheet) {

        List<RdgMasterRecord> records =
                new ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow = sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            RdgMasterRecord record =
                    new RdgMasterRecord();

            record.setEmployeeNo(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(0)
                    )
            );

            record.setBaseEmployeeNo(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(1)
                    )
            );

            record.setName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(2)
                    )
            );

            record.setEmail(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(3)
                    )
            );

            record.setRoleBand(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(19)
                    )
            );

            record.setIbu(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(45)
                    )
            );

            record.setSubIbu(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(46)
                    )
            );

            record.setCustomerName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(53)
                    )
            );
            record.setEmployeeType(ExcelCellUtil.getCellValueAsString(currentRow.getCell(4)));
            record.setEmployeeSubGroup(ExcelCellUtil.getCellValueAsString(currentRow.getCell(5)));
            record.setTraineeCategory(ExcelCellUtil.getCellValueAsString(currentRow.getCell(6)));
            record.setGender(ExcelCellUtil.getCellValueAsString(currentRow.getCell(7)));
            record.setOnsiteOffshore(ExcelCellUtil.getCellValueAsString(currentRow.getCell(8)));
            record.setCompanyCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(9)));
            record.setCompanyName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(10)));
            record.setCountry(ExcelCellUtil.getCellValueAsString(currentRow.getCell(11)));
            record.setPaCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(12)));
            record.setPaName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(13)));
            record.setPsa(ExcelCellUtil.getCellValueAsString(currentRow.getCell(14)));
            record.setRoleCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(15)));
            record.setRoleDescription(ExcelCellUtil.getCellValueAsString(currentRow.getCell(16)));
            record.setDesignation(ExcelCellUtil.getCellValueAsString(currentRow.getCell(17)));
            record.setDesignationLevel(ExcelCellUtil.getCellValueAsString(currentRow.getCell(18)));
            record.setDis(ExcelCellUtil.getCellValueAsString(currentRow.getCell(20)));
            record.setDisp(ExcelCellUtil.getCellValueAsString(currentRow.getCell(21)));
            record.setClassifier(ExcelCellUtil.getCellValueAsString(currentRow.getCell(22)));
            record.setEmpPractice(ExcelCellUtil.getCellValueAsString(currentRow.getCell(23)));
            record.setEmployeeGroupPractice(ExcelCellUtil.getCellValueAsString(currentRow.getCell(24)));
            record.setSupervisorEmpcode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(25)));
            record.setSupervisorName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(26)));
            record.setCareerManagerCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(27)));
            record.setCareerManagerName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(28)));

            record.setDateOfJoining(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(29)
                    )
            );

            record.setOrganizationalUnit(ExcelCellUtil.getCellValueAsString(currentRow.getCell(30)));
            record.setOrganizationalUnitName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(31)));
            record.setIpmsParentWbsCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(32)));
            record.setIpmsParentWbsName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(33)));
            record.setIpmsWbsCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(34)));
            record.setIpmsWbsName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(35)));
            record.setWbsType(ExcelCellUtil.getCellValueAsString(currentRow.getCell(36)));

            record.setIpmsAllocationFromDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(37)
                    )
            );

            record.setIpmsAllocationToDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(38)
                    )
            );

            record.setForcedAllocation(ExcelCellUtil.getCellValueAsString(currentRow.getCell(39)));
            record.setCostCentre(ExcelCellUtil.getCellValueAsString(currentRow.getCell(40)));
            record.setCostCentreName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(41)));
            record.setProjectPracticeCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(42)));
            record.setProjectPractice(ExcelCellUtil.getCellValueAsString(currentRow.getCell(43)));
            record.setProjectGroupPractice(ExcelCellUtil.getCellValueAsString(currentRow.getCell(44)));
            record.setEmpHorizontal(ExcelCellUtil.getCellValueAsString(currentRow.getCell(47)));
            record.setProjectHorizontal(ExcelCellUtil.getCellValueAsString(currentRow.getCell(48)));
            record.setCostGroup(ExcelCellUtil.getCellValueAsString(currentRow.getCell(49)));
            record.setCostGroupDesc(ExcelCellUtil.getCellValueAsString(currentRow.getCell(50)));
            record.setBuCostGroup(ExcelCellUtil.getCellValueAsString(currentRow.getCell(51)));
            record.setCustomerCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(52)));
            record.setAccountCustomerGroup(ExcelCellUtil.getCellValueAsString(currentRow.getCell(54)));
            record.setPrevEmployeeCode(ExcelCellUtil.getCellValueAsString(currentRow.getCell(55)));

            record.setPrevDoj(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(56)
                    )
            );

            record.setContractEndDate(
                    ExcelCellUtil.getCellValueAsDate(
                            currentRow.getCell(57)
                    )
            );

            record.setSkillArea(ExcelCellUtil.getCellValueAsString(currentRow.getCell(58)));
            record.setSkillCluster(ExcelCellUtil.getCellValueAsString(currentRow.getCell(59)));
            record.setSubCluster(ExcelCellUtil.getCellValueAsString(currentRow.getCell(60)));
            record.setProvisionalIbu(ExcelCellUtil.getCellValueAsString(currentRow.getCell(61)));
            record.setProvisionalSubBu(ExcelCellUtil.getCellValueAsString(currentRow.getCell(62)));
            record.setBilledUnbilled(ExcelCellUtil.getCellValueAsString(currentRow.getCell(63)));
            record.setItBpsHc(ExcelCellUtil.getCellValueAsString(currentRow.getCell(64)));
            record.setGeo(ExcelCellUtil.getCellValueAsString(currentRow.getCell(65)));
            record.setLocation(ExcelCellUtil.getCellValueAsString(currentRow.getCell(66)));
            record.setOrgKey(ExcelCellUtil.getCellValueAsString(currentRow.getCell(67)));

            records.add(record);
        }

        return records;
    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}