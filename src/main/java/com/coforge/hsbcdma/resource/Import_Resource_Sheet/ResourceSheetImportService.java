package com.coforge.hsbcdma.resource.Import_Resource_Sheet;

import com.coforge.hsbcdma.resource.Entity_Resource_Sheet.ResourceData;
import com.coforge.hsbcdma.resource.Repository_Resource_sheet.ResourceDataRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceSheetImportService {

    private final ResourceSheetExcelImportHelper helper;
    private final RowSaver rowSaver;

    @Data
    @AllArgsConstructor
    public static class RowError {
        private int rowIndex;
        private String message;
    }

    @Data
    public static class ImportResult {
        private int totalRows;
        private int created;
        private int deactivated;
        private int failed;
        private int skipped;   // ✅ NEW: unchanged rows skipped
        private List<RowError> errors = new ArrayList<>();
    }

    public ImportResult importResources(MultipartFile file) {

        if (!helper.isValidExcelFile(file)) {
            throw new IllegalArgumentException("Only .xlsx or .csv files are allowed");
        }

        ImportResult result = new ImportResult();

        try (InputStream in = file.getInputStream()) {

            List<ResourceSheetExcelImportHelper.ParsedRow> rows =
                    helper.parseResources(in, file.getOriginalFilename());

            result.setTotalRows(rows.size());

            for (ResourceSheetExcelImportHelper.ParsedRow parsed : rows) {
                try {
                    validateRow(parsed.getData());

                    RowSaveOutcome outcome = rowSaver.save(parsed.getData());

                    if (outcome == RowSaveOutcome.SKIPPED) {
                        result.setSkipped(result.getSkipped() + 1);
                    } else if (outcome == RowSaveOutcome.CREATED) {
                        result.setCreated(result.getCreated() + 1);
                    } else if (outcome == RowSaveOutcome.CREATED_AND_DEACTIVATED) {
                        result.setCreated(result.getCreated() + 1);
                        result.setDeactivated(result.getDeactivated() + 1);
                    }

                } catch (Exception e) {
                    result.getErrors().add(new RowError(parsed.getRowIndex(), e.getMessage()));
                }
            }

            result.setFailed(result.getErrors().size());

        } catch (Exception e) {
            throw new RuntimeException("Resource Sheet import failed", e);
        }

        return result;
    }

    private void validateRow(ResourceData row) {
        List<String> issues = new ArrayList<>();

        if (row.getPsid() == null || row.getPsid().isBlank()) {
            issues.add("PSID is required");
        }

        if (row.getResourceName() == null || row.getResourceName().isBlank()) {
            issues.add("Resource Name is required");
        }

        if (row.getLob() == null || row.getLob().isBlank()) {
            issues.add("LOB is required");
        }

        if (!issues.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", issues));
        }
    }

    enum RowSaveOutcome {
        CREATED,
        CREATED_AND_DEACTIVATED,
        SKIPPED
    }

    @Service
    @RequiredArgsConstructor
    static class RowSaver {

        private final ResourceDataRepository repository;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public RowSaveOutcome save(ResourceData incoming) {

            Optional<ResourceData> existingActiveOpt =
                    repository.findTopByPsidAndIsActiveTrueOrderByUpdatedAtDescCreatedAtDesc(incoming.getPsid());

            // ✅ If there is an active row and nothing changed → skip
            if (existingActiveOpt.isPresent()) {
                ResourceData existingActive = existingActiveOpt.get();

                if (isSameBusinessData(existingActive, incoming)) {
                    return RowSaveOutcome.SKIPPED;
                }

                // ✅ Data changed → deactivate all active rows for that PSID
                List<ResourceData> existingActiveRows =
                        repository.findByPsidAndIsActiveTrueOrderByCreatedAtDesc(incoming.getPsid());

                for (ResourceData oldRow : existingActiveRows) {
                    oldRow.setIsActive(false);
                    repository.save(oldRow);
                }

                incoming.setIsActive(true);
                repository.save(incoming);

                return RowSaveOutcome.CREATED_AND_DEACTIVATED;
            }

            // ✅ No active row exists → create new
            incoming.setIsActive(true);
            repository.save(incoming);
            return RowSaveOutcome.CREATED;
        }

        private boolean isSameBusinessData(ResourceData a, ResourceData b) {
            return sameText(a.getPsid(), b.getPsid())
                    && sameText(a.getResourceName(), b.getResourceName())
                    && sameText(a.getLob(), b.getLob())
                    && sameText(a.getNewLob(), b.getNewLob())
                    && sameText(a.getNewLobForOthers(), b.getNewLobForOthers())
                    && sameText(a.getBusinessFunction(), b.getBusinessFunction())
                    && sameText(a.getSubBusinessFunction(), b.getSubBusinessFunction())
                    && sameText(a.getDepartment(), b.getDepartment())
                    && sameText(a.getFunctionalMgrHsbc(), b.getFunctionalMgrHsbc())
                    && sameText(a.getPrimarySkill(), b.getPrimarySkill())
                    && sameBigDecimal(a.getExperience(), b.getExperience())
                    && sameText(a.getCtool(), b.getCtool())
                    && sameText(a.getCtoolLocation(), b.getCtoolLocation())
                    && sameText(a.getPricingModelHsbc(), b.getPricingModelHsbc())
                    && sameText(a.getJobCategory(), b.getJobCategory())
                    && sameText(a.getGcbLevel(), b.getGcbLevel())
                    && sameText(a.getCurrency(), b.getCurrency())
                    && sameBigDecimal(a.getRate(), b.getRate())
                    && sameBigDecimal(a.getEffectiveRate(), b.getEffectiveRate())
                    && sameBigDecimal(a.getFixedCapacityRate(), b.getFixedCapacityRate())
                    && sameDate(a.getEffectiveStartDate(), b.getEffectiveStartDate())
                    && sameDate(a.getEffectiveEndDate(), b.getEffectiveEndDate())
                    && sameText(a.getEmpId(), b.getEmpId())
                    && sameText(a.getResourceStatus(), b.getResourceStatus())
                    && sameText(a.getOnshoreOffshore(), b.getOnshoreOffshore())
                    && sameText(a.getPricingModelCoforge(), b.getPricingModelCoforge())
                    && sameDate(a.getTentativeOnboardingDate(), b.getTentativeOnboardingDate())
                    && sameText(a.getWbsCode(), b.getWbsCode())
                    && sameText(a.getCoforgeBand(), b.getCoforgeBand())
                    && sameText(a.getHbu(), b.getHbu())
                    && sameText(a.getIpmsAllocation(), b.getIpmsAllocation())
                    && sameText(a.getGender(), b.getGender())
                    && sameText(a.getCoforgeLocation(), b.getCoforgeLocation())
                    && sameDate(a.getIpmsAllocationStartDate(), b.getIpmsAllocationStartDate())
                    && sameDate(a.getIpmsAllocationEndDate(), b.getIpmsAllocationEndDate())
                    && sameText(a.getConcatHeadcountNumber(), b.getConcatHeadcountNumber())
                    && sameText(a.getConcatHeadcountNewLob(), b.getConcatHeadcountNewLob())
                    && sameText(a.getMonthOfRelease(), b.getMonthOfRelease())
                    && sameDate(a.getDateOfExit(), b.getDateOfExit())
                    && sameText(a.getExitStatus(), b.getExitStatus())
                    && sameText(a.getReasonForExit(), b.getReasonForExit())
                    && sameText(a.getReplacementRequired(), b.getReplacementRequired())
                    && sameText(a.getRemarks(), b.getRemarks())
                    && sameText(a.getReplacementGiven(), b.getReplacementGiven())
                    && sameText(a.getReplacementId(), b.getReplacementId())
                    && sameText(a.getReplacementName(), b.getReplacementName());
        }

        private boolean sameText(String a, String b) {
            return normalize(a).equals(normalize(b));
        }

        private String normalize(String s) {
            return s == null ? "" : s.trim();
        }

        private boolean sameBigDecimal(BigDecimal a, BigDecimal b) {
            if (a == null && b == null) return true;
            if (a == null || b == null) return false;
            return a.compareTo(b) == 0;
        }

        private boolean sameDate(LocalDate a, LocalDate b) {
            return Objects.equals(a, b);
        }
    }
}