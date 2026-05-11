package com.coforge.hsbcdma.Import;


import com.coforge.hsbcdma.entity.AddDemand;
import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final ExcelImportHelper excelImportHelper;
    private final AddDemandRepository demandRepository;

    @Transactional
    public int importDemands(MultipartFile file) {
        if (!excelImportHelper.isValidExcelFile(file)) {
            throw new IllegalArgumentException("Please upload a valid .xlsx Excel file.");
        }

        try {
            List<AddDemand> demands = excelImportHelper.parseExcelFile(file.getInputStream());
            demandRepository.saveAll(demands);  // batch save — N+1 safe
            return demands.size();
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file: " + e.getMessage(), e);
        }
    }
}

