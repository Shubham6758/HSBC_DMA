package com.coforge.hsbcdma.revenuemargin.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditDTO {

    private Object vmAudit;
    private Object arcAudit;
    private Object rdgAudit;
    private Object m4Audit;
    private Object timesheetAudit;
    private Object otherDataAudit;
    private Object resourceTrackerAudit;
    private Object tmAudit;
}
