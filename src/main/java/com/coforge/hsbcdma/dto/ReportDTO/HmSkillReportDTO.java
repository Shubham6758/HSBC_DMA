package com.coforge.hsbcdma.dto.ReportDTO;

import lombok.Getter;

@Getter
public class HmSkillReportDTO {

    private String hiringManager;
    private String skill;
    private Long openPositions;
    private Long profilesShared;
    private Long awaitingSlots;
    private Long awaitingFeedback;
    private Long selected;
    private Long rejected;

    public HmSkillReportDTO(Object[] row) {
        this.hiringManager = (String) row[0];
        this.skill = (String) row[1];
        this.openPositions = ((Number) row[2]).longValue();
        this.profilesShared = ((Number) row[3]).longValue();
        this.awaitingSlots = ((Number) row[4]).longValue();
        this.awaitingFeedback = ((Number) row[5]).longValue();
        this.selected = ((Number) row[6]).longValue();
        this.rejected = ((Number) row[7]).longValue();
    }

    // getters
}