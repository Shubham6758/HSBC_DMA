package com.coforge.hsbcdma.Import;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.HashMap;
import java.util.Map;

public class ExcelColumnHelper {

    // ✅ HEADER MAP
    public static Map<String, Integer> getHeaderIndexMap(Sheet sheet) {

        Map<String, Integer> headerMap = new HashMap<>();

        Row headerRow = sheet.getRow(0);

        for (Cell cell : headerRow) {

            String header = cell.getStringCellValue();

            if (header != null && !header.trim().isEmpty()) {
                headerMap.put(header.trim().toLowerCase(), cell.getColumnIndex());
            }
        }

        return headerMap;
    }

    // ✅ GET CELL
    public static Cell getCell(Row row, Map<String, Integer> headerMap, String columnName) {

        Integer index = headerMap.get(columnName.toLowerCase());

        if (index == null) return null;

        return row.getCell(index);
    }
}