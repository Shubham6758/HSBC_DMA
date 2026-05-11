package com.coforge.hsbcdma.Import.Onboarding;

import com.coforge.hsbcdma.entity.Onboarding;
import com.coforge.hsbcdma.repository.OnboardingRepositories.OnboardingRepository;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingImportService {

    private final OnboardingExcelImportHelper helper;
    private final OnboardingRepository repo;

    @Data
    public static class ImportResult {
        private int totalRows;
        private int created;
    }

    @Transactional // ✅ FULL ROLLBACK
    public ImportResult importExcel(MultipartFile file) {

        ImportResult res = new ImportResult();

        try (InputStream in = file.getInputStream()) {

            List<Onboarding> rows = helper.parse(in);

            res.setTotalRows(rows.size());

            for (Onboarding ob : rows) {
                repo.save(ob); // ✅ fail-fast
                res.setCreated(res.getCreated() + 1);
            }

        } catch (Exception e) {
            throw new RuntimeException("IMPORT FAILED: " + e.getMessage(), e);
        }

        return res;
    }
}