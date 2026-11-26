package com.coforge.hsbcdma.revenuemargin.service.importer;

import com.coforge.hsbcdma.revenuemargin.dto.ColumnSchema;
import com.coforge.hsbcdma.revenuemargin.dto.VmDumpRecord;
import com.coforge.hsbcdma.revenuemargin.service.ExcelCellUtil;
import com.coforge.hsbcdma.revenuemargin.service.SchemaValidationService;
import com.coforge.hsbcdma.revenuemargin.service.VmDumpService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VmDumpImporter extends BaseExcelImporter {

    @Autowired
    private VmDumpService vmDumpService;
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
        vmDumpService.deleteAllRecords();
        printHeaders(sheet);
        Row headerRow = getHeaderRow(sheet);

        SchemaValidationService schemaValidationService =
                new SchemaValidationService();

        schemaValidationService.printSchema(
                headerRow
        );
        List<String> headers =
                schemaValidationService
                        .getHeaders(headerRow);
        List<ColumnSchema> schema =
                schemaValidationService
                        .getSchema(headerRow);

        System.out.println(
                "First Schema Column"
        );

        System.out.println(
                "Column Number = "
                        + schema.get(0)
                        .getColumnNumber()
        );

        System.out.println(
                "Column Name = "
                        + schema.get(0)
                        .getColumnName()
        );
        boolean schemaValid =
                schemaValidationService
                        .validateMinimumSchema(
                                headers
                        );

        System.out.println(
                "Schema Valid = "
                        + schemaValid
        );
        Row firstDataRow =
                sheet.getRow(1);
        VmDumpRecord record =
                new VmDumpRecord();

        record.setResourceId(
                ExcelCellUtil.getCellValueAsString(
                        firstDataRow.getCell(0)
                )
        );

        record.setResourceName(
                ExcelCellUtil.getCellValueAsString(
                        firstDataRow.getCell(1)
                )
        );

        record.setCToolId(
                ExcelCellUtil.getCellValueAsString(
                        firstDataRow.getCell(2)
                )
        );

        System.out.println(
                "DTO Resource ID = "
                        + record.getResourceId()
        );

        System.out.println(
                "DTO Resource Name = "
                        + record.getResourceName()
        );

        System.out.println(
                "DTO cTool ID = "
                        + record.getCToolId()
        );
        System.out.println(
                "First Resource ID = "
                        + firstDataRow.getCell(0)
        );

        System.out.println(
                "First Resource Name = "
                        + firstDataRow.getCell(1)
        );
        headers.forEach(header ->
                System.out.println(
                        "[" + header + "]"
                ));
        List<VmDumpRecord> records = readAllRows(sheet);
        rowsRead = records.size();

        System.out.println(
                "Total DTO Records Read = "
                        + records.size()
        );
        vmDumpService.saveRecords(records);
        rowsInserted = vmDumpService.getRecordCount();

        System.out.println(
                "Database Count = "
                        + vmDumpService.getRecordCount()
        );
        int rowCount =
                getRowCount(sheet);

        System.out.println(
                "VM Dump Rows Found = "
                        + rowCount);

        workbook.close();

    }
    private void printHeaders(Sheet sheet) {

        Row headerRow = getHeaderRow(sheet);

        System.out.println("Headers Found:");

        headerRow.forEach(cell ->
                System.out.println(cell.toString()));
    }
    private List<VmDumpRecord> readAllRows(
            Sheet sheet) {

        List<VmDumpRecord> records =
                new ArrayList<>();

        for (int rowNumber = 1;
             rowNumber <= sheet.getLastRowNum();
             rowNumber++) {

            Row currentRow =
                    sheet.getRow(rowNumber);

            if (currentRow == null) {
                continue;
            }

            VmDumpRecord record =
                    new VmDumpRecord();

            record.setResourceId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(0)
                    )
            );

            record.setResourceName(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(1)
                    )
            );

            record.setCToolId(
                    ExcelCellUtil.getCellValueAsString(
                            currentRow.getCell(2)
                    )
            );
            record.setFunctionalManager(ExcelCellUtil.getCellValueAsString(currentRow.getCell(3)));
            record.setFunctionalManagerEmailId(ExcelCellUtil.getCellValueAsString(currentRow.getCell(4)));
            record.setEntityManager(ExcelCellUtil.getCellValueAsString(currentRow.getCell(5)));
            record.setVendorName(ExcelCellUtil.getCellValueAsString(currentRow.getCell(6)));
            record.setEquivalentGcb(ExcelCellUtil.getCellValueAsString(currentRow.getCell(7)));
            record.setLegalEntity(ExcelCellUtil.getCellValueAsString(currentRow.getCell(8)));
            record.setCostCenter(ExcelCellUtil.getCellValueAsString(currentRow.getCell(9)));
            record.setBfLevel3(ExcelCellUtil.getCellValueAsString(currentRow.getCell(10)));
            record.setBfLevel4(ExcelCellUtil.getCellValueAsString(currentRow.getCell(11)));
            record.setBaseLocation(ExcelCellUtil.getCellValueAsString(currentRow.getCell(12)));
            record.setPremises(ExcelCellUtil.getCellValueAsString(currentRow.getCell(13)));
            record.setJobCategory(ExcelCellUtil.getCellValueAsString(currentRow.getCell(14)));
            record.setOperatingUnit(ExcelCellUtil.getCellValueAsString(currentRow.getCell(15)));
            record.setPoNumber(ExcelCellUtil.getCellValueAsString(currentRow.getCell(16)));
            record.setPricingModel(ExcelCellUtil.getCellValueAsString(currentRow.getCell(17)));
            record.setContractStartDate(ExcelCellUtil.getCellValueAsDate(currentRow.getCell(18)));
            record.setContractEndDate(ExcelCellUtil.getCellValueAsDate(currentRow.getCell(19)));
            record.setCtoolRate(ExcelCellUtil.getCellValueAsBigDecimal(currentRow.getCell(20)));
            record.setCurrency(ExcelCellUtil.getCellValueAsString(currentRow.getCell(21)));
            record.setUnitOfMeasure(ExcelCellUtil.getCellValueAsString(currentRow.getCell(22)));
            record.setEffectiveStartDate(ExcelCellUtil.getCellValueAsDate(currentRow.getCell(23)));
            record.setEffectiveEndDate(ExcelCellUtil.getCellValueAsDate(currentRow.getCell(24)));
            record.setRoleActive(ExcelCellUtil.getCellValueAsString(currentRow.getCell(25)));
            record.setResourceActive(ExcelCellUtil.getCellValueAsString(currentRow.getCell(26)));
            record.setLandedFlag(ExcelCellUtil.getCellValueAsString(currentRow.getCell(27)));
            record.setOnePo(ExcelCellUtil.getCellValueAsString(currentRow.getCell(28)));
            record.setJobCategoryGcbLevel(ExcelCellUtil.getCellValueAsString(currentRow.getCell(29)));

            records.add(record);

        }

        return records;

    }
    public long getRowsRead() {return rowsRead;}
    public long getRowsInserted() {return rowsInserted;}
}
