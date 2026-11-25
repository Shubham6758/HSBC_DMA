package com.coforge.hsbcdma.dto.ReportDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class CombinationCountDTO {

    private String hbu;
    private String lob;
    private String hiringManager;
    private String skill;

    private Long openCount;
    private Long profileShared;
    private Long selected;
    private Long rejected;
}


