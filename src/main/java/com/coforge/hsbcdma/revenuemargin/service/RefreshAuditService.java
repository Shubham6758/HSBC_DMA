package com.coforge.hsbcdma.revenuemargin.service;

import com.coforge.hsbcdma.revenuemargin.entity.RefreshAuditEntity;
import com.coforge.hsbcdma.revenuemargin.repository.RefreshAuditRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshAuditService {

    @Autowired
    private RefreshAuditRepository refreshAuditRepository;
    public void saveRefreshAudit(
            String sourceName,
            long rowsRead,
            long rowsInserted,
            long durationSeconds,
            String status,
            String message) {

        RefreshAuditEntity audit =
                new RefreshAuditEntity();

        audit.setRefreshTime(
                java.time.LocalDateTime.now()
        );

        audit.setSourceName(
                sourceName
        );

        audit.setRowsRead((int) rowsRead);
        audit.setRowsInserted((int) rowsInserted);
        audit.setWarningsCount(0);

        audit.setStatus(
                status
        );

        audit.setDurationSeconds(
                durationSeconds
        );

        audit.setMessage(
                message
        );

        refreshAuditRepository.save(
                audit
        );
    }

    public RefreshAuditEntity
    getLatestRefresh(
            String sourceName) {

        return refreshAuditRepository
                .findTopBySourceNameOrderByRefreshTimeDesc(
                        sourceName
                );
    }
    public RefreshAuditEntity getVmDumpRefresh() {return getLatestRefresh("VM Dump");}
    public RefreshAuditEntity getArcRefresh() {return getLatestRefresh("ARC");}
    public RefreshAuditEntity getRdgRefresh() {return getLatestRefresh("RDG Master");}
    public RefreshAuditEntity getM4Refresh() {return getLatestRefresh("M4 Rate");}
    public RefreshAuditEntity getTimesheetRefresh() {return getLatestRefresh("Monthly Timesheet");}
    public RefreshAuditEntity getOtherDataRefresh() {return getLatestRefresh("Other Data");}
    public RefreshAuditEntity getCentralResourceTrackerRefresh() {return getLatestRefresh("Central Resource Tracker");}
    public RefreshAuditEntity getTmRefresh() {return getLatestRefresh("T&M Revenue");}
}
