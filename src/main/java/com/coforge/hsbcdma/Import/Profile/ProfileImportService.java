package com.coforge.hsbcdma.Import.Profile;

import com.coforge.hsbcdma.entity.Profile;
import com.coforge.hsbcdma.repository.ProfileRepositories.ProfileRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileImportService {

    private final ProfileExcelImportHelper helper;
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
        private int updated;  // will always be 0 now
        private List<RowError> errors = new ArrayList<>();
    }

    public ImportResult importProfiles(MultipartFile file) {

        if (!helper.isValidExcelFile(file.getContentType()))
            throw new IllegalArgumentException("Only .xlsx allowed");

        ImportResult result = new ImportResult();

        try (InputStream in = file.getInputStream()) {

            List<Profile> rows = helper.parseProfiles(in);
            result.setTotalRows(rows.size());

            int rowIndex = 1;

            for (Profile p : rows) {
                try {

                    validateRow(p);

                    rowSaver.save(p);   // ✅ ONLY INSERT

                    result.setCreated(result.getCreated() + 1);

                } catch (Exception e) {
                    result.getErrors().add(new RowError(rowIndex, e.getMessage()));
                }

                rowIndex++;
            }

        } catch (Exception e) {
            throw new RuntimeException("Profile import failed", e);
        }

        return result;
    }

    private void validateRow(Profile p) {

        List<String> issues = new ArrayList<>();

        if (p.getCandidateName() == null || p.getCandidateName().isBlank())
            issues.add("Candidate Name is required");

        // ✅ ✅ CORE RULE
        if ((p.getEmpId() == null || p.getEmpId().isBlank()) &&
                (p.getPanNumber() == null || p.getPanNumber().isBlank())) {

            issues.add("Either EmpId or PAN Number is required");
        }

        if (p.getCountry() == null)
            issues.add("Country is required");

        if (!issues.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", issues));
        }
    }

    // ✅ ROW SAVER (INSERT ONLY)
    @Service
    @RequiredArgsConstructor
    static class RowSaver {

        private final ProfileRepository repo;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void save(Profile incoming) {

            // ✅ ALWAYS INSERT
            repo.save(incoming);
        }
    }
}