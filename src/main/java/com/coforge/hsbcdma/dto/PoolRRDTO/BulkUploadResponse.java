package com.coforge.hsbcdma.dto.PoolRRDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BulkUploadResponse {

    private int totalRecords;

    private int successCount;

    private int failedCount;

    private List<String> failedRows;
}
