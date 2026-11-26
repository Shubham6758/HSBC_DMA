package com.coforge.hsbcdma.dto.ReportDTO;

import lombok.Getter;

@Getter
public class HmSkillReportDTO {

    private String hiringManager;
    private String skill;
    private String hbu;
    private String lob;

    private Long openPositions;
    private Long profilesShared;
    private Long awaitingSlots;
    private Long awaitingFeedback;
    private Long selected;
    private Long rejected;

    public HmSkillReportDTO(Object[] row) {
        this.hiringManager = (String) row[0];
        this.skill = (String) row[1];
        this.hbu = (String) row[2];
        this.lob = (String) row[3];
        this.openPositions = ((Number) row[4]).longValue();
        this.profilesShared = ((Number) row[5]).longValue();
        this.awaitingSlots = ((Number) row[6]).longValue();
        this.awaitingFeedback = ((Number) row[7]).longValue();
        this.selected = ((Number) row[8]).longValue();
        this.rejected = ((Number) row[9]).longValue();
    }
    // getters
}