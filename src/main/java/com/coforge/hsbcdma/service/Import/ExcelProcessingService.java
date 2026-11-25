package com.coforge.hsbcdma.service.Import;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExcelProcessingService {

    public ByteArrayInputStream processExcel(MultipartFile file) throws Exception {

        Workbook workbook = new XSSFWorkbook(file.getInputStream());

        Sheet supplyPoolSheet = workbook.getSheet("SupplyPool");
        Sheet trackerSheet = workbook.getSheet("ProfileTracker");

        if (supplyPoolSheet == null || trackerSheet == null) {
            throw new RuntimeException("Both sheets required");
        }

        // ✅ Step 1: Build Map
        Map<String, String> nameToEmpIdMap = new HashMap<>();

        for (Row row : supplyPoolSheet) {
            if (row.getRowNum() == 0) continue;

            String empId = getCellValue(row.getCell(0));
            String name = normalize(getCellValue(row.getCell(1)));

            if (name != null && empId != null) {
                nameToEmpIdMap.put(name, empId);
            }
        }

        // ✅ Step 2: Update ProfileTracker sheet
        for (Row row : trackerSheet) {
            if (row.getRowNum() == 0) continue;

            Cell empCell = row.getCell(0);
            Cell nameCell = row.getCell(1);

            String empId = getCellValue(empCell);
            String name = normalize(getCellValue(nameCell));

            if ((empId == null || empId.isEmpty()) && name != null) {
                String mappedEmpId = nameToEmpIdMap.get(name);

                if (mappedEmpId != null) {
                    if (empCell == null) {
                        empCell = row.createCell(0);
                    }
                    empCell.setCellValue(mappedEmpId);
                }
            }
        }

        // ✅ Step 3: Write updated Excel to output stream
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    // ----------------------------
    private String getCellValue(Cell cell) {
        if (cell == null) return null;

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            default:
                return null;
        }
    }

    private String normalize(String value) {
        if (value == null) return null;
        return value.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}