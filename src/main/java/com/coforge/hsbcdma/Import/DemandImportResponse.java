package com.coforge.hsbcdma.Import;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DemandImportResponse {

    private int successCount;

    private int failedCount;

    private List<String> errors;
}