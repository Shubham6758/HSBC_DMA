package com.coforge.hsbcdma.Import.ProfileTracker;

import com.coforge.hsbcdma.entity.ProfileTracker;
import com.coforge.hsbcdma.repository.ProfileTrackerRepositories.ProfileTrackRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileTrackerImportService {

    private final ProfileTrackerExcelImportHelper helper;
    private final ProfileTrackRepository repo;

    @Data
    public static class ImportResult {
        private int totalRows;
        private int created;
    }

    @Transactional
    public ImportResult importTrackers(MultipartFile file) {

        if (!helper.isValidExcelFile(file.getContentType())) {
            throw new IllegalArgumentException("Only .xlsx allowed");
        }

        ImportResult result = new ImportResult();

        try (InputStream in = file.getInputStream()) {

            List<ProfileTracker> rows = helper.parseProfileTrackers(in);

            result.setTotalRows(rows.size());

            for (ProfileTracker pt : rows) {
                repo.save(pt);
                result.setCreated(result.getCreated() + 1);
            }

        } catch (Exception e) {
            throw new RuntimeException("IMPORT FAILED: " + e.getMessage(), e);
        }

        return result;
    }
}