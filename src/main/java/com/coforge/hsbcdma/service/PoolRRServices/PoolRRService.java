package com.coforge.hsbcdma.service.PoolRRServices;

import com.coforge.hsbcdma.dto.PoolRRDTO.BulkUploadResponse;
import com.coforge.hsbcdma.dto.PoolRRDTO.PoolRRBulkRequest;
import com.coforge.hsbcdma.dto.PoolRRDTO.PoolRRCreateRequest;
import com.coforge.hsbcdma.dto.PoolRRDTO.PoolRRUpdateRequest;
import com.coforge.hsbcdma.entity.PoolRR;
import com.coforge.hsbcdma.repository.PoolRRRepositories.PoolRRRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
//@RequiredArgsConstructor
@AllArgsConstructor
public class PoolRRService {

    private final PoolRRRepository poolRRRepository;

    public PoolRR create(PoolRRCreateRequest request) {

        validateCreateRequest(request);

        PoolRR entity = new PoolRR();

        entity.setRr(request.getRr().trim().toUpperCase());
        entity.setSkill(request.getSkill());
        entity.setBand(request.getBand());
        entity.setLocation(request.getLocation());
        entity.setFlag(request.getFlag());

        entity.setCreatedByUserId(getCurrentUserId());

        return poolRRRepository.save(entity);
    }


    public PoolRR update(Long id, PoolRRUpdateRequest request) {

        PoolRR entity = poolRRRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PoolRR not found with id " + id));
        validateUpdateRequest(request);

        if (request.getRr() != null) {
            entity.setRr(request.getRr().trim().toUpperCase());
        }
        if (request.getSkill() != null) {
            entity.setSkill(request.getSkill());
        }
        if (request.getBand() != null) {
            entity.setBand(request.getBand());
        }
        if (request.getLocation() != null) {
            entity.setLocation(request.getLocation());
        }
        if (request.getFlag() != null) {
            entity.setFlag(request.getFlag());
        }

        entity.setUpdatedByUserId(getCurrentUserId());

        return poolRRRepository.save(entity);
    }

    public BulkUploadResponse bulkUpload(
            List<PoolRRBulkRequest> requests
    ) {

        List<PoolRR> entities = new ArrayList<>();

        List<String> failedRows = new ArrayList<>();
        Set<String> rrSet = new HashSet<>();

        int successCount = 0;

        for (PoolRRBulkRequest request : requests) {

            try {

                // ✅ RR Validation
                if (request.getRr() == null ||
                        request.getRr().trim().isEmpty()) {

                    failedRows.add("RR missing");
                    continue;
                }

                // ✅ Skill Validation
                if (request.getSkill() == null ||
                        request.getSkill().trim().isEmpty()) {

                    failedRows.add(
                            request.getRr() + " -> Skill missing"
                    );

                    continue;
                }

                // ✅ Band Validation
                if (request.getBand() == null) {

                    failedRows.add(
                            request.getRr() + " -> Band missing"
                    );

                    continue;
                }

                // ✅ Location Validation
                if (request.getLocation() == null ||
                        request.getLocation().trim().isEmpty()) {

                    failedRows.add(
                            request.getRr() + " -> Location missing"
                    );

                    continue;
                }

                validateRrNumber(request.getRr());
                validatePlainText(request.getSkill(), "Skill", 100);
                validatePlainText(request.getLocation(), "Location", 100);
                // ✅ Normalize RR
                String rrKey =
                        request.getRr()
                                .trim()
                                .toUpperCase();

// ✅ Check Duplicate Inside Current Excel Upload
                if (rrSet.contains(rrKey)) {

                    failedRows.add(
                            rrKey + " -> Duplicate RR in Excel"
                    );

                    continue;
                }

// ✅ Check Duplicate Already In Database
                boolean exists =
                        poolRRRepository.existsByRrIgnoreCase(rrKey);

                if (exists) {

                    failedRows.add(
                            rrKey + " -> Duplicate RR in Database"
                    );

                    continue;
                }

// ✅ Add RR To Set ONLY AFTER VALIDATION
                rrSet.add(rrKey);

                // ✅ Create Entity
                PoolRR entity = new PoolRR();

                entity.setRr(rrKey);
                entity.setSkill(request.getSkill());
                entity.setBand(request.getBand());
                entity.setLocation(request.getLocation());

                // ✅ User Tracking
                entity.setCreatedByUserId(getCurrentUserId());

                entities.add(entity);

                successCount++;

            } catch (Exception e) {

                failedRows.add(
                        request.getRr() + " -> " + e.getMessage()
                );
            }
        }
        // ✅ Save All Valid Rows
        poolRRRepository.saveAll(entities);
        return new BulkUploadResponse(
                requests.size(),
                successCount,
                failedRows.size(),
                failedRows
        );
    }


    public List<PoolRR> getAll() {
        return poolRRRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    public PoolRR getById(Long id) {
        return poolRRRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PoolRR not found with id " + id));
    }

    private String getCurrentUserId() {
        var auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || !auth.isAuthenticated()) return null;

        return auth.getName(); // userId
    }

    public Map<String, Object> uploadExcel(MultipartFile file) {

        Map<String, Object> response = new HashMap<>();

        List<PoolRR> successList = new ArrayList<>();
        List<String> errorList = new ArrayList<>();
        Set<String> rrSet = new HashSet<>();

        try {

            InputStream inputStream = file.getInputStream();

            Workbook workbook = new XSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {

                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                try {

                    String rr = getCellValue(row.getCell(0));

                    if (rr != null) {
                        rr = rr.trim();
                    }

                    String skill =
                            getCellValue(row.getCell(1));

                    Float band =
                            getFloatCellValue(row.getCell(2));

                    String location =
                            getCellValue(row.getCell(3));

                    // Empty RR Check
                    if (rr == null || rr.isEmpty()) {

                        errorList.add(
                                "Row " + (i + 1)
                                        + ": RR is empty"
                        );

                        continue;
                    }

                    validateRrNumber(rr);
                    validatePlainText(skill, "Skill", 100);
                    validatePlainText(location, "Location", 100);

                    // Normalize RR
                    String rrKey = rr.trim().toUpperCase();

                    if (rrSet.contains(rrKey)) {

                        errorList.add(
                                "Row " + (i + 1)
                                        + ": Duplicate RR in Excel -> "
                                        + rrKey
                        );

                        continue;
                    }

                    // Duplicate Check In Database
                    boolean exists =
                            poolRRRepository.existsByRrIgnoreCase(rrKey);

                    if (exists) {

                        errorList.add(
                                "Row " + (i + 1)
                                        + ": Duplicate RR -> "
                                        + rrKey
                        );

                        continue;
                    }

                    PoolRR entity = new PoolRR();

                    entity.setRr(rrKey);
                    entity.setSkill(skill);
                    entity.setBand(band);
                    entity.setLocation(location);

                    successList.add(
                            poolRRRepository.save(entity)
                    );

                } catch (Exception e) {

                    errorList.add(
                            "Row " + (i + 1)
                                    + ": "
                                    + e.getMessage()
                    );
                }
            }

            workbook.close();

            response.put("successCount",
                    successList.size());

            response.put("errorCount",
                    errorList.size());

            response.put("savedData",
                    successList);

            response.put("errors",
                    errorList);

            return response;

        } catch (Exception e) {

            response.put(
                    "message",
                    "Failed to upload excel"
            );

            response.put(
                    "error",
                    e.getMessage()
            );

            return response;
        }
    }

    private String getCellValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {

            case STRING ->
                    cell.getStringCellValue();

            case NUMERIC ->
                    String.valueOf(
                            (long) cell.getNumericCellValue()
                    );

            case BOOLEAN ->
                    String.valueOf(
                            cell.getBooleanCellValue()
                    );

            default -> null;
        };
    }

    private Float getFloatCellValue(Cell cell) {

        if (cell == null) {
            return null;
        }

        try {

            return (float)
                    cell.getNumericCellValue();

        } catch (Exception e) {

            try {

                return Float.parseFloat(
                        cell.getStringCellValue()
                );

            } catch (Exception ex) {

                return null;
            }
        }
    }


    // ------------------------------------------------------
// Input validation for Pool RR
// ------------------------------------------------------

    private void validateCreateRequest(PoolRRCreateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Pool RR request is required");
        }

        validateRrNumber(request.getRr());
        validatePlainText(request.getSkill(), "Skill", 100);
        validatePlainText(request.getLocation(), "Location", 100);
    }

    private void validateUpdateRequest(PoolRRUpdateRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Pool RR update request is required");
        }

        if (request.getRr() != null) {
            validateRrNumber(request.getRr());
        }

        if (request.getSkill() != null) {
            validatePlainText(request.getSkill(), "Skill", 100);
        }

        if (request.getLocation() != null) {
            validatePlainText(request.getLocation(), "Location", 100);
        }
    }

    private void validateRrNumber(String rr) {

        if (rr == null || rr.trim().isEmpty()) {
            throw new IllegalArgumentException("RR Number is required");
        }

        String value = rr.trim();

        if (containsUnsafeInput(value)) {
            throw new IllegalArgumentException("HTML or script tags are not allowed in RR Number");
        }

        if (value.length() > 50) {
            throw new IllegalArgumentException("RR Number cannot be more than 50 characters");
        }

        /*
         * Allowed RR examples:
         * RR123
         * RR-123
         * RR_123
         * RR/123
         */
        if (!value.matches("^[a-zA-Z0-9/_-]+$")) {
            throw new IllegalArgumentException(
                    "Invalid RR Number. Only letters, numbers, /, _ and - are allowed"
            );
        }
    }

    private void validatePlainText(String value, String fieldName, int maxLength) {

        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        String cleanValue = value.trim();

        if (containsUnsafeInput(cleanValue)) {
            throw new IllegalArgumentException("HTML or script tags are not allowed in " + fieldName);
        }

        if (cleanValue.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot be more than " + maxLength + " characters");
        }
    }

    private boolean containsUnsafeInput(String value) {

        if (value == null) {
            return false;
        }

        String lower = value.toLowerCase(Locale.ROOT);

        return lower.contains("<")
                || lower.contains(">")
                || lower.contains("&lt;")
                || lower.contains("&gt;")
                || lower.contains("script")
                || lower.contains("svg")
                || lower.contains("javascript:")
                || lower.contains("onerror")
                || lower.contains("onload")
                || lower.contains("onclick")
                || lower.contains("onmouseover")
                || lower.contains("alert(");
    }
}
