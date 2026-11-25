package com.coforge.hsbcdma.dto.ReportDTO;


public interface GenericStatusProjection {

    String getGroupKey();   // lob or hbu name
    String getStatus();     // status name
    Long getCount();        // count
}

