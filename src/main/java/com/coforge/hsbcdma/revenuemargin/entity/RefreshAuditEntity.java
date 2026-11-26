package com.coforge.hsbcdma.revenuemargin.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_refresh_audit")
public class RefreshAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private java.time.LocalDateTime refreshTime;

    private String sourceName;

    private Integer rowsRead;

    private Integer rowsInserted;

    private Integer warningsCount;

    private String status;

    private Long durationSeconds;

    private String message;

    public Long getId() {
        return id;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public Integer getRowsRead() {
        return rowsRead;
    }

    public void setRowsRead(Integer rowsRead) {
        this.rowsRead = rowsRead;
    }

    public Integer getRowsInserted() {
        return rowsInserted;
    }

    public void setRowsInserted(Integer rowsInserted) {
        this.rowsInserted = rowsInserted;
    }

    public Integer getWarningsCount() {
        return warningsCount;
    }

    public void setWarningsCount(Integer warningsCount) {
        this.warningsCount = warningsCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public java.time.LocalDateTime getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(
            java.time.LocalDateTime refreshTime) {
        this.refreshTime = refreshTime;
    }
}