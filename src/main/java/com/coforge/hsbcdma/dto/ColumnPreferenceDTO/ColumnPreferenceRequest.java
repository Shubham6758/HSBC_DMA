package com.coforge.hsbcdma.dto.ColumnPreferenceDTO;

import lombok.Data;

import java.util.List;

@Data
public class ColumnPreferenceRequest {

    private String pageName;

    private List<String> selectedColumns;
}
