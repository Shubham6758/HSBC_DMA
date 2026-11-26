package com.coforge.hsbcdma.dto.DashboardDTO;

import lombok.Data;

import java.util.List;

@Data
public class DashboardPreferenceRequest {

    private String dashboardName;

    private List<Long> hbuIds;

    private List<Long> hmIds;

    private List<Long> skillIds;

    private List<Long> lobIds;

    private List<Long> priorityIds;
}
