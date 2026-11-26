package com.coforge.hsbcdma.Import;


import com.coforge.hsbcdma.repository.DemandRepositories.AddDemandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ExcelImportService {

    private final ExcelImportHelper excelImportHelper;
    private final AddDemandRepository demandRepository;

    @Transactional
    public DemandImportResponse importDemands(
            MultipartFile file) {

        if (!excelImportHelper.isValidExcelFile(file)) {

            throw new IllegalArgumentException(
                    "Please upload a valid .xlsx Excel file."
            );
        }

        try {

            return excelImportHelper.parseExcelFile(
                    file.getInputStream()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Error reading Excel file: "
                            + e.getMessage(),
                    e
            );
        }
    }
}

